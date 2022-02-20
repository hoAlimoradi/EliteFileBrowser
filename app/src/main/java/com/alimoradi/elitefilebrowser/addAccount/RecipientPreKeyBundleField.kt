package com.alimoradi.elitefilebrowser.addAccount

import org.whispersystems.libsignal.IdentityKey
import org.whispersystems.libsignal.ecc.ECPublicKey

data class RecipientPreKeyBundleField(
    var name: String,
    var deviceId: Int,
    var signedPreKeyId:Int,
    var registrationId: Int,
    var addressDeviceId: Int,
    var preKeysId: Int,
    var preKeysKeyPairPublicKey:  ByteArray,
    var signedPreKeyKeyPairPublicKey: ECPublicKey,
    var signedPreKeySignature: ByteArray,
    var identityKeyPairPublicKey: ByteArray
)
