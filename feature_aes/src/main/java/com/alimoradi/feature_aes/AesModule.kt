package com.alimoradi.feature_aes

import com.alimoradi.feature_base64.Base64Module
import com.alimoradi.feature_aes.internal.AesBase64ManagerImpl
import com.alimoradi.feature_aes.internal.AesManagerImpl

class AesModule {

    fun createAesManager(): AesManager {
        return AesManagerImpl()
    }

    fun createAesBase64Manager(): AesBase64Manager {
        val aesManager = createAesManager()
        val base64Manager = Base64Module().createBase64Manager()
        return AesBase64ManagerImpl(
            aesManager,
            base64Manager
        )
    }
}
