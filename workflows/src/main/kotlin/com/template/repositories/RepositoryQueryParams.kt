package com.template.repositories

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class RepositoryQueryParams(val startPage: Int = 1, val pageSize: Int = 100,
                                 val searchTerms: List<SearchTerm> = emptyList(),
                                 val sortOrders: List<SortTerm> = emptyList()) {


    @CordaSerializable
    data class SearchTerm(val searchTermFieldName: String, val searchTermParam: Any?)

    @CordaSerializable
    data class SortTerm(val sortFieldName: String, val sortOrder: SortOrder)

    @CordaSerializable
    enum class SortOrder { ASC, DESC }

    fun getSearchTermsAsMap(vararg additionalParams: Pair<String, Any?>): MutableMap<String, Any?> {
        return (searchTerms.associate { it.searchTermFieldName to it.searchTermParam }  + additionalParams)
            .toMutableMap()
    }

}
