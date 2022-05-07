package com.template.webserver.model.rest.request.borrower


data class UpdateBorrowerAccountRequest(
    val linearId: String,
    val email: String? = null,
    val name: String? = null,
    val tier: String? = null,
    val active: Boolean? = null,
    val participants: List<String>,
    val version: Int
)