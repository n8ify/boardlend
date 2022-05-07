package com.template.webserver.constants.enums

enum class ResponseCode(val description: String) {
    S00000("Completed"),
    E00000("Unknown Internal Server Error"),
    E00001("Borrower account cannot be create")
}