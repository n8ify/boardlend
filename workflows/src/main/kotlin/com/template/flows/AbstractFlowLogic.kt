package com.template.flows

import net.corda.core.flows.FlowLogic
import net.corda.core.utilities.ProgressTracker
import java.time.Instant
import java.util.*

abstract class AbstractFlowLogic<T> : FlowLogic<T>() {

    private val flowRunId by lazy { UUID.randomUUID().toString() }
    private var flowStartTime = Instant.now()

    protected abstract val flowName: String
    protected val notary by lazy { serviceHub.networkMapCache.notaryIdentities.first() }

    protected fun setCurrentProgressTracker(nextStep: ProgressTracker.Step) {
        val now = Instant.now()
        val previousStep : ProgressTracker.Step? = progressTracker?.run { steps.getOrNull(steps.lastIndexOf(currentStep) - 1) }

        logger.info("[$flowName][$flowRunId] Time Usage (\"${previousStep?.label ?: "Initial"}\" -> \"${progressTracker?.currentStep?.label}\") : ${now.toEpochMilli() - flowStartTime.toEpochMilli()} ms")

        progressTracker?.currentStep = nextStep
        flowStartTime = now
    }

}