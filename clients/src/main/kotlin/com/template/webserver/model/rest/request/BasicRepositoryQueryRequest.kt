package com.template.webserver.model.rest.request

import com.template.repositories.RepositoryQueryParams

data class BasicRepositoryQueryRequest(
    override val startPage: Int,
    override val pageSize: Int,
    override val searchTermList: List<RepositoryQueryParams.SearchTerm>,
    override val sortFieldList: List<RepositoryQueryParams.SortTerm>
) : AbstractRepositoryQueryRequest()
