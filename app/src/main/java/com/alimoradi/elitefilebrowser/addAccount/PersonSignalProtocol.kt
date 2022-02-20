package com.alimoradi.elitefilebrowser.addAccount

import android.util.Log
import org.whispersystems.libsignal.IdentityKeyPair
import org.whispersystems.libsignal.InvalidKeyException
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyRecord
import org.whispersystems.libsignal.SignalProtocolAddress
import org.whispersystems.libsignal.util.KeyHelper
import java.lang.StringBuilder
import java.util.*

class PersonSignalProtocol {
    var name: String? = null
    var deviceId = 0
    var signedPreKeyId = 0
    var registrationId = 0
    var identityKeyPair: IdentityKeyPair? = null
    var preKeys: List<PreKeyRecord>? = null
    var signedPreKey: SignedPreKeyRecord? = null
    var address: SignalProtocolAddress? = null


    @Throws(InvalidKeyException::class)
    fun createNewPerson(): PersonSignalProtocol {
        val person = PersonSignalProtocol()
        val nameTemp = generateName()
        val deviceIdTemp = generateDeviceID()
        val signedPreKeyIdTemp = 3
        val identityKeyPairTemp = KeyHelper.generateIdentityKeyPair()
        val registrationIdTemp = KeyHelper.generateRegistrationId(false)
        val preKeysTemp = KeyHelper.generatePreKeys(1, 100)
        val signedPreKeyTemp = KeyHelper.generateSignedPreKey(identityKeyPairTemp, signedPreKeyIdTemp)
        val addressTemp = SignalProtocolAddress(nameTemp, deviceIdTemp)

        person.name = nameTemp
        person.deviceId = deviceIdTemp
        person.signedPreKeyId = signedPreKeyIdTemp
        person.identityKeyPair =identityKeyPairTemp
        person.registrationId = registrationIdTemp
        person.preKeys = preKeysTemp
        person.signedPreKey = signedPreKeyTemp
        person.address = addressTemp
        return person
    }

    fun generateName(): String {
        val SALTCHARS = "1234567890"
        val salt = StringBuilder()
        val rnd = Random()
        while (salt.length < 18) { // length of the random string.
            val index = (rnd.nextFloat() * SALTCHARS.length).toInt()
            salt.append(SALTCHARS[index])
        }
        val saltStr = salt.toString()
        //Log.e("test: ", " Person saltStr $saltStr")
        return saltStr
    }

    fun generateDeviceID(): Int {
        val rnd = Random()
        val digits = CharArray(5)
        digits[0] = (rnd.nextInt(9) + '1'.toInt()).toChar()
        for (i in 1 until digits.size) {
            digits[i] = (rnd.nextInt(10) + '0'.toInt()).toChar()
        }
        val digitsString = String(digits)
        val digitsLong = digitsString.toLong()
        val digitsInt = digitsString.toInt()
/*        Log.e("test: ", " Person digitsString $digitsString")
        Log.e("test: ", " Person digitsLong $digitsLong")
        Log.e("test: ", " Person digitsInt $digitsInt")*/
        return digitsInt
    }
}