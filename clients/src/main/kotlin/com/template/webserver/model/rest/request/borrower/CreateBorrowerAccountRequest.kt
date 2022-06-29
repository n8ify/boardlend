package com.template.webserver.model.rest.request.borrower

data class CreateBorrowerAccountRequest(
    val borrowerCode: String,
    val email: String,
    val tel: String,
    val name: String,
    val tier: String,
    val active: Boolean,
    val participantLenderCode: String
)
