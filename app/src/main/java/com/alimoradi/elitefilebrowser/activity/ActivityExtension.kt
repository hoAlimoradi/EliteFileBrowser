package com.alimoradi.elitefilebrowser.activity

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.IdRes
import com.alimoradi.elitefilebrowser.addAccount.Convertor
import com.alimoradi.elitefilebrowser.addAccount.PersonSignalProtocol
import com.alimoradi.elitefilebrowser.addAccount.PersonSignalProtocolAsJsonData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.whispersystems.libsignal.IdentityKeyPair
import org.whispersystems.libsignal.SignalProtocolAddress
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyRecord
import java.util.ArrayList

object ActivityExtension {

    fun <T : View> Activity.bind(@IdRes res: Int): Lazy<T> {
        @Suppress("UNCHECKED_CAST")
        return lazy(LazyThreadSafetyMode.NONE) { findViewById<T>(res) }
    }

    fun Activity.showKeyboard(et: EditText) {
        et.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)
    }

    fun Activity.convertPersonSignalProtocolToPersonSignalProtocolAsJsonData(input: PersonSignalProtocol) : PersonSignalProtocolAsJsonData {

        val gson = Gson()

        val identityKeyPairByteArray: ByteArray = input.identityKeyPair!!.serialize()
        val identityKeyPairEncodedString = Convertor().byteArrayToString(identityKeyPairByteArray)
        Log.e("test: ", " identityKeyPairEncodedString  $identityKeyPairEncodedString")

        Log.e("test: ", " bob.preKeys!!.size" + input.preKeys!!.size)
        //var preKeysListString = ArrayList<String>()
        val preKeysListString: ArrayList<String> = ArrayList()
        val bobPreKeysSize = input.preKeys!!.size

        for (counter in 0 until 100) {
            preKeysListString.add(Convertor().byteArrayToString(input.preKeys!![counter].serialize()))
        }

        val signedPreKeyByteArray: ByteArray = input.signedPreKey!!.serialize()
        val signedPreKeyPairEncodedString = Convertor().byteArrayToString(signedPreKeyByteArray)

        Log.e("test: ", " signedPreKeyByteArray  $signedPreKeyByteArray")

        val result =  PersonSignalProtocolAsJsonData (
            name = input.name!!,
            deviceId = input.deviceId,
            signedPreKeyId = input.signedPreKeyId,
            registrationId = input.registrationId,
            identityKeyPair = identityKeyPairEncodedString,
            preKeys = gson.toJson(preKeysListString) ,
            signedPreKey = signedPreKeyPairEncodedString ,
            address = gson.toJson(input.address)
        )
        Log.e("test: ", "    ___________________________")
        Log.e("test: ", " name  ${result.name}")
        Log.e("test: ", " deviceId  ${result.deviceId}")
        Log.e("test: ", " signedPreKeyId  ${result.signedPreKeyId}")
        Log.e("test: ", " registrationId  ${result.registrationId}")
        Log.e("test: ", " preKeys  ${result.preKeys}")
        Log.e("test: ", " signedPreKey  ${result.signedPreKey}")
        Log.e("jafar: ", " address  ${result.address}")
        val x = gson.toJson(result)
        Log.e("test: ", "    ___________________________" + x)

        return result
    }

    fun Activity.convertPersonSignalProtocolAsJsonDataToPersonSignalProtocol(input: PersonSignalProtocolAsJsonData) : PersonSignalProtocol {

        var resultPersonSignalProtocol = PersonSignalProtocol()
        val gson = Gson()
        resultPersonSignalProtocol.name = input.name
        resultPersonSignalProtocol.deviceId = input.deviceId
        resultPersonSignalProtocol.signedPreKeyId = input.signedPreKeyId
        resultPersonSignalProtocol.registrationId = input.registrationId

        val identityKeyPairByteArrayFromString: ByteArray =  Convertor().stringToByteArray(input.identityKeyPair)
        val identityKeyPairFromByteArray =  IdentityKeyPair(identityKeyPairByteArrayFromString)
        //Log.e("test: ", " identityKeyPairEncodedString  $identityKeyPairEncodedString")

        resultPersonSignalProtocol.identityKeyPair = identityKeyPairFromByteArray

        val preKeysListOfPreKeyRecord: ArrayList<PreKeyRecord> = ArrayList()
        val preKeysListOfString: ArrayList<String> = gson.fromJson(input.preKeys, object : TypeToken<List<String>>() {}.getType())

        for (counter in 0 until 100) {
            // preKeysListOfByteArray.add(Convertor().stringToByteArrayTo(preKeysListOfString[counter]))
            preKeysListOfPreKeyRecord.add(PreKeyRecord(Convertor().stringToByteArray(preKeysListOfString[counter])))
        }

        resultPersonSignalProtocol.preKeys = preKeysListOfPreKeyRecord

        val signedPreKeyFromString = SignedPreKeyRecord(Convertor().stringToByteArray(input.signedPreKey))
        resultPersonSignalProtocol.signedPreKey = signedPreKeyFromString

        val signalProtocolAddress: SignalProtocolAddress = gson.fromJson(input.address, SignalProtocolAddress::class.java)
        resultPersonSignalProtocol.address = signalProtocolAddress

        return resultPersonSignalProtocol
    }
}
