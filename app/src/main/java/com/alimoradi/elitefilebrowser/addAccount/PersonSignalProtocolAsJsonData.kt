package com.alimoradi.elitefilebrowser.addAccount

data class PersonSignalProtocolAsJsonData (
    var name:String,
    var deviceId: Int,
    var signedPreKeyId: Int,
    var registrationId: Int,
    var identityKeyPair:String,
    var preKeys:String,
    var signedPreKey: String,
    var address:String
    )

