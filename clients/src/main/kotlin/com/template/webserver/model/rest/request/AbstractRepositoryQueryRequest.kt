package com.template.webserver.model.rest.request

import com.template.repositories.RepositoryQueryParams


abstract class AbstractRepositoryQueryRequest {

    abstract val startPage: Int
    abstract val pageSize: Int
    abstract val sortFieldList: List<RepositoryQueryParams.SortTerm>
    abstract val searchTermList: List<RepositoryQueryParams.SearchTerm>


    fun toRepositoryQueryParams() : RepositoryQueryParams {
        return RepositoryQueryParams(
            startPage = this.startPage,
            pageSize = this.pageSize,
            searchTerms = this.searchTermList,
            sortOrders = this.sortFieldList
        )
    }

}