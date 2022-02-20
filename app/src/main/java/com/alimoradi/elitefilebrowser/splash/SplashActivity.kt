package com.alimoradi.elitefilebrowser.splash

import android.Manifest.permission.*
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alimoradi.elitefilebrowser.R
import com.alimoradi.elitefilebrowser.addAccount.Convertor
import com.alimoradi.elitefilebrowser.addAccount.PersonSignalProtocol
import com.alimoradi.elitefilebrowser.addAccount.PersonSignalProtocolAsJsonData
import com.alimoradi.elitefilebrowser.authentication.AuthenticationModule
import com.alimoradi.elitefilebrowser.biometric.BiometricAuthenticator
import com.alimoradi.elitefilebrowser.data.AppPreferences
import com.alimoradi.elitefilebrowser.main.MainActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_splash.*
import java.io.File


class SplashActivity : AppCompatActivity() {
    private lateinit var biometricAuthenticator: BiometricAuthenticator

    var lock =  true

    val PERMISSION_REQUEST_CODE = 2296
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val authenticationManager =  AuthenticationModule(this).createAuthenticationManager()
        checkPermission()
        requestPermission()



        Log.e(" ", "The value of our pref is: ${AppPreferences.firstRun}")
        if (!AppPreferences.firstRun) {
            AppPreferences.firstRun = true
            createMySignalData()
        }

        biometricAuthenticator =
            BiometricAuthenticator.instance(this, object : BiometricAuthenticator.Listener {
                override fun onNewMessage(message: String) {
                    Log.e("onNewMessage---------", message)
                      if (message == "true") {
                          Log.e("  ist ok ---------", message)
                          //startActivity(Intent(applicationContext, SteganographyActivity::class.java))
                          //startActivity(Intent(applicationContext, AddAccountActivity::class.java))
                          startActivity(Intent(applicationContext, MainActivity::class.java))
                          finish()
                      } else {
                          Log.e("", "not ok show dialog---------" + message)
                          biometricAuthenticator.cancelAuthentication()
                      }
                }
            })
        // Initial states
        biometricAuthenticator.isStrongAuthenticationEnabled = true
        biometricAuthenticator.isWeakAuthenticationEnabled = true
        biometricAuthenticator.isDeviceCredentialAuthenticationEnabled = true
        //biometricAuthenticator.showNegativeButton = true
        //biometricAuthenticator.showAuthenticationConfirmation = true

        start.setOnClickListener {
           //biometricAuthenticator.authenticateWithoutCrypto(this)
           // startActivity(Intent(applicationContext, MainActivity::class.java))
            if (authenticationManager.isAuthenticationEnable()) {
                biometricAuthenticator.authenticateWithoutCrypto(this)
            } else {
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }
        }

       /*
        val docsFolder = File(Environment.getExternalStorageDirectory().toString() + "/Documents")
        var isPresent = true
        if (!docsFolder.exists()) {
            isPresent = docsFolder.mkdir()
        }
        if (isPresent) {
            val file = File(docsFolder.absolutePath, "test.txt")
        } else {
            // Failure
        }
*/
        val docsFolder = File(Environment.getExternalStorageDirectory().toString() + "/Documents")
        docsFolder.absoluteFile
    }

    private fun checkPermission(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result: Int =
                ContextCompat.checkSelfPermission(this, MANAGE_EXTERNAL_STORAGE)

            val result2: Int =
                ContextCompat.checkSelfPermission(this, INTERNET)

            val result3: Int =
                ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)

            val result4: Int =
                ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)

            val result5: Int =
                ContextCompat.checkSelfPermission(this, ACCESS_MEDIA_LOCATION)

            val result6: Int =
                ContextCompat.checkSelfPermission(this, REQUEST_INSTALL_PACKAGES)
            result ==  PackageManager.PERMISSION_GRANTED &&
                    result2 == PackageManager.PERMISSION_GRANTED &&
                    result3 == PackageManager.PERMISSION_GRANTED &&
                    result4 == PackageManager.PERMISSION_GRANTED &&
                    result5 == PackageManager.PERMISSION_GRANTED &&
                    result6 == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivityForResult(intent, 2296)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, 2296)
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(
                this,
                arrayOf(MANAGE_EXTERNAL_STORAGE, INTERNET, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, ACCESS_MEDIA_LOCATION, REQUEST_INSTALL_PACKAGES),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }





    private fun createMySignalData() {
        val mySignalDataProtocolAsListWithOnlyOneElement =  ArrayList<PersonSignalProtocolAsJsonData>()

        val mySignalData = PersonSignalProtocol().createNewPerson()
        val gson = Gson()

        val identityKeyPairByteArray: ByteArray = mySignalData.identityKeyPair!!.serialize()
        val identityKeyPairEncodedString = Convertor().byteArrayToString(identityKeyPairByteArray)
        Log.e("test: ", " identityKeyPairEncodedString  $identityKeyPairEncodedString")

        Log.e("test: ", " bob.preKeys!!.size" + mySignalData.preKeys!!.size)
        //var preKeysListString = ArrayList<String>()
        val preKeysListString: ArrayList<String> = ArrayList()
        val bobPreKeysSize = mySignalData.preKeys!!.size

        for (counter in 0 until 100) {
            preKeysListString.add(Convertor().byteArrayToString(mySignalData.preKeys!![counter].serialize()))
        }

        val signedPreKeyByteArray: ByteArray = mySignalData.signedPreKey!!.serialize()
        val signedPreKeyPairEncodedString = Convertor().byteArrayToString(signedPreKeyByteArray)

        Log.e("test: ", " signedPreKeyByteArray  $signedPreKeyByteArray")

        val myPersonSignalProtocolAsJsonData  =  PersonSignalProtocolAsJsonData (
            name = mySignalData.name!!,
            deviceId = mySignalData.deviceId,
            signedPreKeyId = mySignalData.signedPreKeyId,
            registrationId = mySignalData.registrationId,
            identityKeyPair = identityKeyPairEncodedString,
            preKeys = gson.toJson(preKeysListString) ,
            signedPreKey = signedPreKeyPairEncodedString ,
            address = gson.toJson(mySignalData.address)
        )

        Log.e("my: ", "    ___________________________")
        Log.e("my: ", " name  ${myPersonSignalProtocolAsJsonData.name}")
        Log.e("my: ", " deviceId  ${myPersonSignalProtocolAsJsonData.deviceId}")
        Log.e("my: ", " signedPreKeyId  ${myPersonSignalProtocolAsJsonData.signedPreKeyId}")
        Log.e("my: ", " registrationId  ${myPersonSignalProtocolAsJsonData.registrationId}")
        Log.e("my: ", " preKeys  ${myPersonSignalProtocolAsJsonData.preKeys}")
        Log.e("my: ", " signedPreKey  ${myPersonSignalProtocolAsJsonData.signedPreKey}")
        Log.e("my: ", " address  ${myPersonSignalProtocolAsJsonData.address}")



        mySignalDataProtocolAsListWithOnlyOneElement.clear()
        mySignalDataProtocolAsListWithOnlyOneElement.add(myPersonSignalProtocolAsJsonData)

        AppPreferences.setMySignalProtocolData(mySignalDataProtocolAsListWithOnlyOneElement)

        Log.e("my: ", "    ___________________________" + gson.toJson(myPersonSignalProtocolAsJsonData))
       // AppPreferences.mySignalProtocolData = mySignalDataJson

    }

    /*private fun createMySignalData() {

        val mySignalData = PersonSignalProtocol().createNewPerson()
        val gson = Gson()

        val identityKeyPairByteArray: ByteArray = mySignalData.identityKeyPair!!.serialize()
        val identityKeyPairEncodedString = Convertor().byteArrayToString(identityKeyPairByteArray)
        Log.e("test: ", " identityKeyPairEncodedString  $identityKeyPairEncodedString")

        Log.e("test: ", " bob.preKeys!!.size" + mySignalData.preKeys!!.size)
        //var preKeysListString = ArrayList<String>()
        val preKeysListString: ArrayList<String> = ArrayList()
        val bobPreKeysSize = mySignalData.preKeys!!.size

        for (counter in 0 until 100) {
            preKeysListString.add(Convertor().byteArrayToString(mySignalData.preKeys!![counter].serialize()))
        }

        val signedPreKeyByteArray: ByteArray = mySignalData.signedPreKey!!.serialize()
        val signedPreKeyPairEncodedString = Convertor().byteArrayToString(signedPreKeyByteArray)

        Log.e("test: ", " signedPreKeyByteArray  $signedPreKeyByteArray")

        val myPersonSignalProtocolAsJsonData  =  PersonSignalProtocolAsJsonData (
            name = mySignalData.name!!,
            deviceId = mySignalData.deviceId,
            signedPreKeyId = mySignalData.signedPreKeyId,
            registrationId = mySignalData.registrationId,
            identityKeyPair = identityKeyPairEncodedString,
            preKeys = gson.toJson(preKeysListString) ,
            signedPreKey = signedPreKeyPairEncodedString ,
            address = gson.toJson(mySignalData.address)
        )

        Log.e("my: ", "    ___________________________")
        Log.e("my: ", " name  ${myPersonSignalProtocolAsJsonData.name}")
        Log.e("my: ", " deviceId  ${myPersonSignalProtocolAsJsonData.deviceId}")
        Log.e("my: ", " signedPreKeyId  ${myPersonSignalProtocolAsJsonData.signedPreKeyId}")
        Log.e("my: ", " registrationId  ${myPersonSignalProtocolAsJsonData.registrationId}")
        Log.e("my: ", " preKeys  ${myPersonSignalProtocolAsJsonData.preKeys}")
        Log.e("my: ", " signedPreKey  ${myPersonSignalProtocolAsJsonData.signedPreKey}")
        Log.e("my: ", " address  ${myPersonSignalProtocolAsJsonData.address}")
        val mySignalDataJson = gson.toJson(myPersonSignalProtocolAsJsonData)
        Log.e("my: ", "    ___________________________" + mySignalDataJson)
        AppPreferences.mySignalProtocolData = mySignalDataJson

    }
*/

}