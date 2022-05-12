package com.template.webserver.model.rest.request.lender

data class CreateLenderAccountRequest(
    val lenderCode: String,
    val email: String,
    val name: String,
    val active: Boolean,
    val participants: List<String>
)
