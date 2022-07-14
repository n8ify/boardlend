package com.template.webserver.model.rest.request.lender

data class UpdateLenderAccountRequest(
    val lenderCode: String,
    val email: String?,
    val name: String?,
    val active: Boolean?
)
