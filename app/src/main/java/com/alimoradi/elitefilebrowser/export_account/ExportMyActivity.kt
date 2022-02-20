package com.alimoradi.elitefilebrowser.export_account

import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alimoradi.elitefilebrowser.R
import com.alimoradi.elitefilebrowser.addAccount.Convertor
import com.alimoradi.elitefilebrowser.addAccount.PersonSignalProtocol
import com.alimoradi.elitefilebrowser.addAccount.PersonSignalProtocolAsJsonData
import com.alimoradi.elitefilebrowser.data.AppPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_expoert_my.*

import com.google.zxing.*
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import org.whispersystems.libsignal.IdentityKeyPair
import org.whispersystems.libsignal.SignalProtocolAddress
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyRecord
import java.util.ArrayList

class ExportMyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expoert_my)
   /*     val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.encodeBitmap(AppPreferences.mySignalProtocolData, BarcodeFormat.QR_CODE, 512, 512)
        myAccountQrCodeImageView.setImageBitmap(bitmap)*/
        //myAccountQrCodeImageView.setImageBitmap(getQrCodeBitmap(AppPreferences.mySignalProtocolData))
        try {

        }catch (e: Exception) {

        }
    }

    fun getQrCodeBitmap(qrCodeContent: String): Bitmap {
        val size = 68 //pixels
        val hints = hashMapOf<EncodeHintType, Int>().also { it[EncodeHintType.MARGIN] = 1 } // Make the QR code buffer border narrower
        val bits = QRCodeWriter().encode(qrCodeContent, BarcodeFormat.QR_CODE, size, size)
        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    }

/*
    private fun readMySignalData(): PersonSignalProtocol {
        val result = PersonSignalProtocol().createNewPerson()
        val gson = Gson()
        val mySignalDataJson = AppPreferences.mySignalProtocolData
        val myPersonSignalProtocolAsJsonData = gson.fromJson(mySignalDataJson, PersonSignalProtocolAsJsonData::class.java)

        result.name = myPersonSignalProtocolAsJsonData.name
        result.deviceId = myPersonSignalProtocolAsJsonData.deviceId
        result.signedPreKeyId = myPersonSignalProtocolAsJsonData.signedPreKeyId
        result.registrationId = myPersonSignalProtocolAsJsonData.registrationId

        val identityKeyPairByteArrayFromString: ByteArray =  Convertor().stringToByteArrayTo(myPersonSignalProtocolAsJsonData.identityKeyPair)
        val identityKeyPairFromByteArray =  IdentityKeyPair(identityKeyPairByteArrayFromString)

        result.identityKeyPair = identityKeyPairFromByteArray

        val preKeysListOfPreKeyRecord: ArrayList<PreKeyRecord> = ArrayList()
        val preKeysListOfString: ArrayList<String> = gson.fromJson(myPersonSignalProtocolAsJsonData.preKeys, object : TypeToken<List<String>>() {}.getType())

        for (counter in 0 until 100) {
            // preKeysListOfByteArray.add(Convertor().stringToByteArrayTo(preKeysListOfString[counter]))
            preKeysListOfPreKeyRecord.add(PreKeyRecord(Convertor().stringToByteArrayTo(preKeysListOfString[counter])))
        }

        result.preKeys = preKeysListOfPreKeyRecord

        val signedPreKeyFromString = SignedPreKeyRecord(Convertor().stringToByteArrayTo(myPersonSignalProtocolAsJsonData.signedPreKey))
        result.signedPreKey = signedPreKeyFromString

        val signalProtocolAddress: SignalProtocolAddress = gson.fromJson(myPersonSignalProtocolAsJsonData.address, SignalProtocolAddress::class.java)
        result.address = signalProtocolAddress

        return result
    }*/
}