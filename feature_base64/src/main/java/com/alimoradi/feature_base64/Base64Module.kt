package com.alimoradi.feature_base64

import com.alimoradi.feature_base64.internal.Base64ManagerImpl

class Base64Module {

    fun createBase64Manager(): Base64Manager {
        return Base64ManagerImpl()
    }
}
