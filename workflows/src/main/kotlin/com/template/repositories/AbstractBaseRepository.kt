package com.template.repositories

import net.corda.core.serialization.SingletonSerializeAsToken

abstract class AbstractBaseRepository: SingletonSerializeAsToken() {

    companion object {
        const val COUNT_ALL = "COUNT(*)"
    }

}