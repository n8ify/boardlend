package com.template.webserver.services

import com.template.domain.TrxReference
import com.template.flows.GetStateStatusFlow
import com.template.webserver.NodeRPCConnection
import com.template.webserver.exceptions.PartyNotFoundException
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateRef
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.messaging.startFlow
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.builder
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.PersistentStateRef
import net.corda.core.utilities.getOrThrow
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.support.PropertiesLoaderUtils
import javax.annotation.PostConstruct


abstract class AbstractBaseService {

    private val logger = LoggerFactory.getLogger(AbstractBaseService::class.java)

    @Autowired
    protected lateinit var rpc: NodeRPCConnection

    private val partyIdentitiesMap by lazy { HashMap<String, String>() }

    @PostConstruct
    fun init() {
        val properties = PropertiesLoaderUtils.loadAllProperties("party_identities.yaml")
        properties.forEach { entry ->
            partyIdentitiesMap[entry.key as String] = entry.value as String
        }
        logger.info("Party identities : $partyIdentitiesMap")
    }

    fun obtainPartyByName(name: String) : Party {
        val partyName = partyIdentitiesMap[name]  ?: throw PartyNotFoundException("Cannot parse name for \"${name}\"")
        return rpc.proxy.wellKnownPartyFromX500Name(CordaX500Name.parse(partyName))!!
    }

    fun inquiryStateStatus(trxReference: TrxReference): Vault.StateStatus {
        return rpc.proxy.startFlow(::GetStateStatusFlow, trxReference).returnValue.getOrThrow()
    }

    fun validateIsConsumedState(trxReference: TrxReference) {
        val isConsumed = inquiryStateStatus(trxReference) == Vault.StateStatus.CONSUMED
        if (isConsumed) {
            throw IllegalStateException("State \"$trxReference\" is already consumed")
        }
    }

}