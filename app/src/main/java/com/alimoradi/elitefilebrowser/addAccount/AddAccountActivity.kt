package com.alimoradi.elitefilebrowser.addAccount

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alimoradi.elitefilebrowser.R
import com.alimoradi.elitefilebrowser.data.AppPreferences
import com.alimoradi.elitefilebrowser.file_provider.FileProvider
import com.alimoradi.elitefilebrowser.main.ApplicationGraph
import com.alimoradi.feature_aes.*
import com.alimoradi.feature_base64.Base64Module
import com.alimoradi.file_api.SignalFileExtension
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_add_acount.*
import org.json.JSONException
import org.json.JSONObject
import org.whispersystems.libsignal.*
import org.whispersystems.libsignal.protocol.CiphertextMessage
import org.whispersystems.libsignal.protocol.PreKeySignalMessage
import org.whispersystems.libsignal.state.PreKeyBundle
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyRecord
import org.whispersystems.libsignal.state.impl.InMemorySignalProtocolStore
import java.io.*
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.*
import kotlin.collections.ArrayList


class AddAccountActivity : AppCompatActivity() {
    var currentPath: String? = null
    var fileExtension: String = "txt"
    private lateinit var personSignalProtocolAsJsonList: ArrayList<PersonSignalProtocolAsJsonData>
    private lateinit var userAdapter: UserAdapter
    val base64Module = Base64Module().createBase64Manager()
    val fileCreatorManager = ApplicationGraph.getFileCreatorManager()
    val fileProviderRootManager = ApplicationGraph.getFileProviderRootManager()
    var alice = PersonSignalProtocol()
    var bob = PersonSignalProtocol()
    var bobFromQrCode = PersonSignalProtocol()
    var recipientSelectedFromList: PersonSignalProtocol? = null

    val aesManager = AesModule().createAesManager()
    val secretKey = aesManager.generateKey(AesKeySize.KEY_256)

    //QR Code Scanner Object
    private var qrScan: IntentIntegrator? = null


    private fun getRootPath(): String {
        return fileProviderRootManager.getFileRootPath(FileProvider.Local)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_acount)

        val extras = intent.extras

        if (extras != null) {
            currentPath = extras.getString("EXTRA_PATH")
            fileExtension = extras.getString("EXTRA_FILE_EXTENTION")!!
            //Log.e("jafar", " currentPath " + currentPath)
        }
        Log.e("jafar: ", " fileExtension  " + fileExtension)

        //encryptFileByAES()

        if (fileExtension == ".esenc") {
            encryptButton.visibility = View.GONE
            decryptButton.visibility = View.VISIBLE
        } else {
            encryptButton.visibility = View.VISIBLE
            decryptButton.visibility = View.GONE
        }

        qrCodeImageView.setOnClickListener {
            qrScan?.initiateScan()
        }

        backImageView.setOnClickListener {
            this.finish()
        }

        encryptButton.setOnClickListener {
            if (userAdapter.selectedItemPosition == -1) {
                Toast.makeText(applicationContext, "لطفا برای رمزکردن یکی از مخاطبین را لیست انتخاب کنید", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            AppPreferences.getUserSignalProtocolDataList()?.let {
                recipientSelectedFromList = convertPersonSignalProtocolAsJsonDataToPersonSignalProtocol(
                    it[userAdapter.selectedItemPosition ]
                )
                concatKeyWithEncryptedFile()
            }


        }

        decryptButton.setOnClickListener {

            if (userAdapter.selectedItemPosition == -1) {
                Toast.makeText(applicationContext, "لطفا برای بازگشایی فایل یکی از مخاطبین را لیست انتخاب کنید", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            AppPreferences.getUserSignalProtocolDataList()?.let {
                recipientSelectedFromList = convertPersonSignalProtocolAsJsonDataToPersonSignalProtocol(
                    it[userAdapter.selectedItemPosition ]
                )

                try {
                    extractKeyWithEncryptedFile()
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, "در کلید رمزگشایی عدم تطابق رخ داده ", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

        }



        //alice = readMySignalData()

        personSignalProtocolAsJsonList = ArrayList()

        AppPreferences.getUserSignalProtocolDataList()?.let {
            personSignalProtocolAsJsonList = it
        }


        userAdapter = UserAdapter(this,
            personSignalProtocolAsJsonList/*, UserAdapter.OnClickListener { position ->
                AppPreferences.getUserSignalProtocolDataList()?.let {
                    recipientSelectedFromList = convertPersonSignalProtocolAsJsonDataToPersonSignalProtocol(
                        it[position]
                    )
                    Toast.makeText(applicationContext, "${recipientSelectedFromList!!.name}", Toast.LENGTH_SHORT).show()
                }
                }*/

        )
        accountRecyclerView.layoutManager = LinearLayoutManager(this)
        accountRecyclerView.adapter = userAdapter

        //Initialize the Scan Object
        qrScan = IntentIntegrator(this)



        addContactDirectValue.setOnClickListener {
            /*addInfo(
                nameInput = null,
                storeInput = null,
                preKeyInput = null,
                addressInput = null
            )*/

            createTestUser()
        }

        //convertPersonSignalProtocolToPersonSignalProtocolAsJsonData()

        //
        try {
            //test()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: UntrustedIdentityException) {
            e.printStackTrace()
        } catch (e: LegacyMessageException) {
            e.printStackTrace()
        } catch (e: InvalidMessageException) {
            e.printStackTrace()
        } catch (e: NoSessionException) {
            e.printStackTrace()
        } catch (e: DuplicateMessageException) {
            e.printStackTrace()
        } catch (e: InvalidVersionException) {
            e.printStackTrace()
        } catch (e: InvalidKeyIdException) {
            e.printStackTrace()
        }

    }
    fun convertPersonSignalProtocolToPersonSignalProtocolAsJsonData(input: PersonSignalProtocol) : PersonSignalProtocolAsJsonData {

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

    fun convertPersonSignalProtocolAsJsonDataToPersonSignalProtocol(input: PersonSignalProtocolAsJsonData) : PersonSignalProtocol {

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
        val preKeysListOfString: ArrayList<String>  = gson.fromJson(input.preKeys, object : TypeToken<List<String>>() {}.getType())

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

    private fun readMySignalData(): PersonSignalProtocol {
        var mySignalDataProtocolAsListWithOnlyOneElement =
            ArrayList<PersonSignalProtocolAsJsonData>()

        AppPreferences.getMySignalProtocolData()?.let {
            mySignalDataProtocolAsListWithOnlyOneElement = it

        }
        val result = PersonSignalProtocol().createNewPerson()
        val gson = Gson()
        val myPersonSignalProtocolAsJsonData = mySignalDataProtocolAsListWithOnlyOneElement.first()

        /*val myPersonSignalProtocolAsJsonData =
            gson.fromJson(mySignalDataJson, PersonSignalProtocolAsJsonData::class.java)*/

        result.name = myPersonSignalProtocolAsJsonData.name
        result.deviceId = myPersonSignalProtocolAsJsonData.deviceId
        result.signedPreKeyId = myPersonSignalProtocolAsJsonData.signedPreKeyId
        result.registrationId = myPersonSignalProtocolAsJsonData.registrationId

        val identityKeyPairByteArrayFromString: ByteArray =
            Convertor().stringToByteArray(myPersonSignalProtocolAsJsonData.identityKeyPair)
        val identityKeyPairFromByteArray = IdentityKeyPair(identityKeyPairByteArrayFromString)

        result.identityKeyPair = identityKeyPairFromByteArray

        val preKeysListOfPreKeyRecord: ArrayList<PreKeyRecord> = ArrayList()
        val preKeysListOfString: ArrayList<String> = gson.fromJson(
            myPersonSignalProtocolAsJsonData.preKeys,
            object : TypeToken<List<String>>() {}.getType()
        )

        for (counter in 0 until 100) {
            preKeysListOfPreKeyRecord.add(
                PreKeyRecord(
                    Convertor().stringToByteArray(
                        preKeysListOfString[counter]
                    )
                )
            )
        }

        result.preKeys = preKeysListOfPreKeyRecord

        val signedPreKeyFromString =
            SignedPreKeyRecord(Convertor().stringToByteArray(myPersonSignalProtocolAsJsonData.signedPreKey))
        result.signedPreKey = signedPreKeyFromString

        val signalProtocolAddress: SignalProtocolAddress = gson.fromJson(
            myPersonSignalProtocolAsJsonData.address,
            SignalProtocolAddress::class.java
        )
        result.address = signalProtocolAddress

        return result
    }

    private fun createUserFromQrCode(
        nameInput: String,
        deviceIdInput: Int,
        signedPreKeyIdInput: Int,
        registrationIdAddressInput: Int,
        identityKeyPairInput: String,
        preKeysInput: String,
        signedPreKeyInput: String,
        addressInput: String
    ) {

        val gson = Gson()

        Log.e(" : ", "    _____________createUserFromQrCode______________")
        val jafar = PersonSignalProtocolAsJsonData(
            name = nameInput,
            deviceId = deviceIdInput,
            signedPreKeyId = signedPreKeyIdInput,
            registrationId = registrationIdAddressInput,
            identityKeyPair = identityKeyPairInput,
            preKeys = preKeysInput,
            signedPreKey = signedPreKeyInput,
            address = addressInput
        )

        /* var personSignalProtocolFromQrCode = PersonSignalProtocol()
         personSignalProtocolFromQrCode.name = jafar.name
         personSignalProtocolFromQrCode.deviceId = jafar.deviceId
         personSignalProtocolFromQrCode.signedPreKeyId = jafar.signedPreKeyId
         personSignalProtocolFromQrCode.registrationId = jafar.registrationId

         val identityKeyPairByteArrayFromString: ByteArray =  Convertor().stringToByteArrayTo(jafar.identityKeyPair)
         val identityKeyPairFromByteArray =  IdentityKeyPair(identityKeyPairByteArrayFromString)

         personSignalProtocolFromQrCode.identityKeyPair = identityKeyPairFromByteArray

         val preKeysListOfPreKeyRecord: ArrayList<PreKeyRecord> = ArrayList()
         val preKeysListOfString: ArrayList<String>  = gson.fromJson(jafar.preKeys, object : TypeToken<List<String>>() {}.getType())

         for (counter in 0 until 100) {
             // preKeysListOfByteArray.add(Convertor().stringToByteArrayTo(preKeysListOfString[counter]))
             preKeysListOfPreKeyRecord.add(PreKeyRecord(Convertor().stringToByteArrayTo(preKeysListOfString[counter])))
         }

         personSignalProtocolFromQrCode.preKeys = preKeysListOfPreKeyRecord

         val signedPreKeyFromString = SignedPreKeyRecord(Convertor().stringToByteArrayTo(jafar.signedPreKey))
         personSignalProtocolFromQrCode.signedPreKey = signedPreKeyFromString

         val signalProtocolAddress: SignalProtocolAddress = gson.fromJson(jafar.address, SignalProtocolAddress::class.java)
         personSignalProtocolFromQrCode.address = signalProtocolAddress*/

        val x = gson.toJson(jafar)
        Log.e("personSignalProtocolFromQrCode: ", x)
        refreshList(element = jafar)
        /* personSignalProtocolAsJsonList.add(jafar)
         AppPreferences.setUserSignalProtocolDataList(personSignalProtocolAsJsonList)
         userAdapter.notifyDataSetChanged()*/
    }

    private fun createTestUser() {
        val newUser = PersonSignalProtocol().createNewPerson()

        //convert PersonSignalProtocol  to PersonSignalProtocolAsJsonData
        val gson = Gson()

        val identityKeyPairByteArray: ByteArray = newUser.identityKeyPair!!.serialize()
        val identityKeyPairEncodedString = Convertor().byteArrayToString(identityKeyPairByteArray)
        Log.e("test: ", " identityKeyPairEncodedString  $identityKeyPairEncodedString")

        Log.e("test: ", " bob.preKeys!!.size" + newUser.preKeys!!.size)
        //var preKeysListString = ArrayList<String>()
        val preKeysListString: ArrayList<String> = ArrayList()
        val bobPreKeysSize = newUser.preKeys!!.size

        for (counter in 0 until 100) {
            preKeysListString.add(Convertor().byteArrayToString(newUser.preKeys!![counter].serialize()))
        }

        val signedPreKeyByteArray: ByteArray = newUser.signedPreKey!!.serialize()
        val signedPreKeyPairEncodedString = Convertor().byteArrayToString(signedPreKeyByteArray)

        Log.e("jafar: ", "    ___________________________")
        val jafar = PersonSignalProtocolAsJsonData(
            name = newUser.name!!,
            deviceId = newUser.deviceId,
            signedPreKeyId = newUser.signedPreKeyId,
            registrationId = newUser.registrationId,
            identityKeyPair = identityKeyPairEncodedString,
            preKeys = gson.toJson(preKeysListString),
            signedPreKey = signedPreKeyPairEncodedString,
            address = gson.toJson(newUser.address)
        )

        val x = gson.toJson(jafar)
        Log.e("jafar: ", x)
        refreshList(element = jafar)
        /*       personSignalProtocolAsJsonList.add(jafar)
               userAdapter.notifyDataSetChanged()
               AppPreferences.setUserSignalProtocolDataList(personSignalProtocolAsJsonList)*/
    }

    private fun refreshList(element: PersonSignalProtocolAsJsonData) {
        personSignalProtocolAsJsonList.add(element)
        userAdapter.notifyDataSetChanged()
        AppPreferences.setUserSignalProtocolDataList(personSignalProtocolAsJsonList)
    }

    private fun addInfo(
        nameInput: String?,
        deviceIdInput: String?,
        signedPreKeyIdInput: String?,
        registrationIdAddressInput: String?,
        identityKeyPairInput: String?,
        preKeysInput: String?,
        signedPreKeyInput: String?,
        addressInput: String?
    ) {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.add_account_item, null)
        val name = v.findViewById<EditText>(R.id.name)
        val deviceId = v.findViewById<EditText>(R.id.deviceId)
        val signedPreKeyId = v.findViewById<EditText>(R.id.signedPreKeyId)
        val registrationId = v.findViewById<EditText>(R.id.registrationId)
        val identityKeyPair = v.findViewById<EditText>(R.id.identityKeyPair)
        val preKeys = v.findViewById<EditText>(R.id.preKeys)
        val signedPreKey = v.findViewById<EditText>(R.id.signedPreKey)
        val address = v.findViewById<EditText>(R.id.address)

        name.setText(nameInput)
        deviceId.setText(deviceIdInput)
        signedPreKeyId.setText(signedPreKeyIdInput)
        registrationId.setText(registrationIdAddressInput)
        identityKeyPair.setText(identityKeyPairInput)
        preKeys.setText(preKeysInput)
        signedPreKey.setText(signedPreKeyInput)
        address.setText(addressInput)

        val addDialog = AlertDialog.Builder(this)

        addDialog.setView(v)
        addDialog.setPositiveButton("Ok") { dialog, _ ->

            Toast.makeText(this, "Adding User Information Success", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        addDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()

        }
        addDialog.create()
        addDialog.show()


    }

    //Getting the scan results
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {

            //Check to see if QR Code has nothing in it
            if (result.contents == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show()
            } else {
                //QR Code contains some data
                try {

                    //Convert the QR Code Data to JSON
                    val obj = JSONObject(result.contents)
                    //Set up the TextView Values using the data from JSON
                    Log.e("متن ", obj.toString())


                    val signedPreKeyId = obj.getString("signedPreKeyId")
                    val registrationId = obj.getString("registrationId")

                    val preKeys = obj.getString("preKeys")
                    val signedPreKey = obj.getString("signedPreKey")

                    val address = obj.getString("address")
                    val name = obj.getString("name")
                    val deviceId = obj.getString("deviceId")
                    val identityKeyPair = obj.getString("identityKeyPair")
                    /* addInfo(
                         nameInput = name,
                         storeInput = store,
                         preKeyInput = preKey,
                         addressInput = address
                     )*/

                    if (isInteger(deviceId) and isInteger(signedPreKeyId) and isInteger(
                            registrationId
                        )
                    ) {
                        createUserFromQrCode(
                            nameInput = name,
                            deviceIdInput = deviceId.toInt(),
                            signedPreKeyIdInput = signedPreKeyId.toInt(),
                            registrationIdAddressInput = registrationId.toInt(),
                            identityKeyPairInput = identityKeyPair,
                            preKeysInput = preKeys,
                            signedPreKeyInput = signedPreKey,
                            addressInput = address
                        )
                    } else {
                        // توست
                    }


                } catch (e: JSONException) {
                    Toast.makeText(this, result.contents, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    @Throws(
        InvalidKeyException::class,
        UntrustedIdentityException::class,
        LegacyMessageException::class,
        InvalidMessageException::class,
        NoSessionException::class,
        DuplicateMessageException::class,
        InvalidVersionException::class,
        InvalidKeyIdException::class
    )
    fun test() {
        /*Person ALICE = new Person("+6281111111111", 111, 2);
        Person BOB = new Person("+6282222222222", 112, 5);*/
        /*val ALICE = Person(generateName(), generateDeviceID(), 2)
        val bob = Person(generateName(), generateDeviceID(), 5)*/
        val randomPassword =
            "Hello world!, hello world!, hello world!, hello world!, hello world!, hello world! haha"


        val ALICE_MSG =
            encryptAESRandomPasswordByRecipientSignalPublicKey(alice, bobFromQrCode, randomPassword)
        val msg = decryptAESRandomPasswordBySenderSignalPublicKey(
            alice,
            bobFromQrCode,
            PreKeySignalMessage(ALICE_MSG.serialize())
        )
        Log.e("test: ", "-------------- encrypted -------------")
        Log.e("test: ", String(ALICE_MSG.serialize()))
        Log.e("test: ", "-------------- decrypted -------------")
        Log.e("test: ", String(msg))
        // ----------------------------------
    }

    @Throws(UntrustedIdentityException::class, InvalidKeyException::class)
    fun encryptAESRandomPasswordByRecipientSignalPublicKey(
        sender: PersonSignalProtocol,
        recipient: PersonSignalProtocol,
        randomPassword: String
    ): CiphertextMessage {

        val protocolStore =
            InMemorySignalProtocolStore(sender.identityKeyPair, sender.registrationId)

        val sessionBuilder = SessionBuilder(protocolStore, recipient.address)

        val preKeyBundle = PreKeyBundle(
            recipient.registrationId,
            recipient.address!!.deviceId,
            recipient.preKeys!!.last().id,
            recipient.preKeys!!.last().keyPair.publicKey,
            recipient.signedPreKey!!.getId(),
            recipient.signedPreKey!!.getKeyPair().getPublicKey(),
            recipient.signedPreKey!!.getSignature(),
            recipient.identityKeyPair!!.getPublicKey()
        )
/*        Log.e("test: ", "-------------- encrypt data recipient  -------------")
        Log.e("test: ", "  registrationId " + recipient.registrationId)
        Log.e("test: ", "  address.getDeviceId() " + recipient.address!!.deviceId)
        Log.e("test: ", "  preKeys.last().getId() " + recipient.preKeys!!.last().id)
        Log.e(
            "test: ",
            "  preKeys.last().getKeyPair().getPublicKey() " + String(recipient.preKeys!!.last().keyPair.publicKey.serialize())
        )
        Log.e("test: ", "  signedPreKey.getId() " + recipient.signedPreKey!!.id)
        Log.e(
            "test: ",
            "  signedPreKey.getKeyPair().getPublicKey() " + recipient.signedPreKey!!.keyPair.publicKey
        )
        Log.e("test: ", "  signedPreKey.getSignature() " + recipient.signedPreKey!!.signature)
        Log.e("test: ", "  identityKeyPair.getPublicKey() " + recipient.identityKeyPair!!.publicKey)*/

        sessionBuilder.process(preKeyBundle)
        val cipher = SessionCipher(protocolStore, recipient.address)
        Log.e("test: ", "  address " + recipient.address)

        return cipher.encrypt(Convertor().stringToByteArray(randomPassword))
        //return cipher.encrypt(randomPassword.toByteArray(StandardCharsets.UTF_8))
    }


    @Throws(
        UntrustedIdentityException::class,
        InvalidKeyException::class,
        NoSessionException::class,
        DuplicateMessageException::class,
        InvalidMessageException::class,
        LegacyMessageException::class,
        InvalidKeyIdException::class
    )
    fun decryptAESRandomPasswordBySenderSignalPublicKey(
        sender: PersonSignalProtocol,
        recipient: PersonSignalProtocol,
        message: PreKeySignalMessage?
    ): ByteArray {
        val protocolStore =
            InMemorySignalProtocolStore(recipient.identityKeyPair, recipient.registrationId)

        protocolStore.storePreKey(recipient.preKeys!!.last().getId(), recipient.preKeys!!.last())
        protocolStore.storeSignedPreKey(recipient.signedPreKey!!.getId(), recipient.signedPreKey)
        val sessionBuilder = SessionBuilder(protocolStore, sender.address)
        val preKeyBundle = PreKeyBundle(
            sender.registrationId,
            sender.address!!.getDeviceId(),
            sender.preKeys!!.first().getId(),
            sender.preKeys!!.first().getKeyPair().getPublicKey(),
            sender.signedPreKey!!.getId(),
            sender.signedPreKey!!.getKeyPair().getPublicKey(),
            sender.signedPreKey!!.getSignature(),
            sender.identityKeyPair!!.getPublicKey()
        )
        sessionBuilder.process(preKeyBundle)
        val cipher = SessionCipher(protocolStore, sender.address)
        //val enc = cipher.encrypt("Hi hi!!".toByteArray())
        return cipher.decrypt(message)
    }

    fun isInteger(str: String?) = str?.toIntOrNull()?.let { true } ?: false

    //encrypt file
    /*@Throws(Exception::class)
    fun generateSecretKey(): SecretKey? {
        val secureRandom = SecureRandom()
        val keyGenerator = KeyGenerator.getInstance("AES")
        //generate a key with secure random
        keyGenerator?.init(128, secureRandom)
        return keyGenerator?.generateKey()
    }*/


    fun saveFile(fileData: ByteArray) {
        /*val file = File(path)
        val bos = BufferedOutputStream(FileOutputStream(file, false))
        bos.write(fileData)
        bos.flush()
        bos.close()*/
        val uuid = UUID.randomUUID().toString()
        val fileName = "EliteFile"+uuid+ "esenc"
        val parentPath = if (currentPath == null) {
            getRootPath()
        } else {
            currentPath
        }
        fileCreatorManager.createWithByteArrayContent(parentPath!!, fileName, fileData)

    }

    /*  @Throws(Exception::class)
      fun encrypt(yourKey: SecretKey, fileData: ByteArray): ByteArray {
          val data = yourKey.getEncoded()
          val skeySpec = SecretKeySpec(data, 0, data.size, "AES")
          val cipher = Cipher.getInstance("AES", "BC")
          cipher.init(
              Cipher.ENCRYPT_MODE,
              skeySpec,
              IvParameterSpec(ByteArray(cipher.getBlockSize()))
          )
          return cipher.doFinal(fileData)
      }*/

    fun concatKeyWithEncryptedFile1() {

        val ioFile = java.io.File(currentPath)
        // ConcatKeyWithEncryptedFile().run(ioFile)
        val encryptedFile = encryptFileByAES()
        /*injectSignalEncryptedKeyAndFileEncryptedWithAES(
            containerFile = ioFile,
            aesKeyEncryptedBySignal = secretKey,
            fileEncryptedByAES = encryptedFile
        )*/

        /*ObjectOutputStreamHelper()
            .run(ioFile, secretKey, encryptedFile)*/

        //تو مبدا که می خواد رمز کنه
        val mergedFile =
            ObjectOutputStreamHelper().concatKeyWithEncryptedFile(ioFile, secretKey, encryptedFile, fileExtension)

        //تو مقصد اینجوری باید باز کنیم
        val extractKeyWithEncryptedFileModel =
            ObjectOutputStreamHelper().extractKeyWithEncryptedFile(mergedFile)
        decryptFileByAES(
            secretKey = extractKeyWithEncryptedFileModel.aesKeyEncryptedBySignal,
            outputEncrypted = extractKeyWithEncryptedFileModel.fileEncryptedByAES
        )
    }


    /**
     *   تو مبدا که می خواد رمز کنه
     */
    // این متد واسه وقتیه که بخواییم رمز کنیم فایلی که بخواییم رمز کنیم رو که داریم
    // کلید الگوریتم متقارن رو هم ساختیم اول میدیم با سیگنال کلید رمز بشه بعد فایلی که با همون کلید رمز کردیم رو کنار کلید رمز شده ترکیب می کنیم
    fun concatKeyWithEncryptedFile() {
        val ioFile = java.io.File(currentPath)

        // فایلو رمز کردیم با همون کلید که تولید شده
        val encryptedFile = encryptFileByAES()


        // حالا کلیدو متقارن رو هم رمز می کنیم
        val secretKeyEncryptedByRecipientSignalPublicKeySelectedInList =
            encryptAESSecretKeyExistInRamByRecipientSignalPublicKeySelectedInList()

        //دوتا بایت رمز شده ینی بایت فایل و بایت کلید رو مرج می کنیم
        // تو مبدا که می خواد رمز کنه
        val mergedFile = ObjectOutputStreamHelper().concatKeyWithEncryptedFile(
            ioFile,
            secretKeyEncryptedByRecipientSignalPublicKeySelectedInList,
            encryptedFile,
            fileExtension
        )

        //فایل رمز شده رو در همین مسری که فایل هست با پسورد مد نظر سیو می کنیم
        val uuid = UUID.randomUUID().toString()
        val fileName = "EliteFile"+uuid+ ".esenc"
        val parentPath = if (currentPath == null) {
            getRootPath()
        } else {
            currentPath
        }
        fileCreatorManager.createWithByteArrayContent(getRootPath(), fileName, mergedFile.readBytes())
        Toast.makeText(this, "  فایل در دایکتوری روت ساخته شد  " + fileName  , Toast.LENGTH_LONG).show()
    }

    // وقتی بخواهیم در مبدا فایلی رو رمز کنیم با کلیدی متقارنی که در لحظه تولید شده و در رم هست رو با سیگنال رمز کنیم تا در فایل ارسالی مرج بشه
    @Throws(
        InvalidKeyException::class,
        UntrustedIdentityException::class,
        LegacyMessageException::class,
        InvalidMessageException::class,
        NoSessionException::class,
        DuplicateMessageException::class,
        InvalidVersionException::class,
        InvalidKeyIdException::class
    )
    fun encryptAESSecretKeyExistInRamByRecipientSignalPublicKeySelectedInList(): ByteArray {

        val secretKeyString = Convertor().byteArrayToString(secretKey)

        // سندر خودم هستم که می خوام فایل رو رمز کنم
        val sender = readMySignalData()
        var recipient =  PersonSignalProtocol()
        recipientSelectedFromList?.let {
            recipient = it
        }
        val secretKeyStringEncrypted = encryptAESRandomPasswordByRecipientSignalPublicKey(
            sender,
            recipient,
            secretKeyString
        )

 /*       Log.e(
            "test: ",
            "secretKeyStringEncrypted : " + String(secretKeyStringEncrypted.serialize())
        )
        Log.e("test: ", "--------------   -------------")*/

        return secretKeyStringEncrypted.serialize()
    }
    /**
     ***********************************************************************************************۸۸
     */


    /**
     *   تو مقصد اینجوری باید باز کنیم
     */

    private fun extractKeyWithEncryptedFile() {
        val mergedFile = java.io.File(currentPath)

        //   بایت ها رو از فایل مرجی که قرار دیکریپت کنیم استخراچ می کنیم
        val extractKeyWithEncryptedFileModel =
            ObjectOutputStreamHelper().extractKeyWithEncryptedFile(mergedFile)

        Log.e("jafar: ", "--------------   -------------")
        Log.e("jafar: ", "-------------- extractKeyWithEncryptedFileModel.fileExtension  -------------"  + extractKeyWithEncryptedFileModel.fileExtension)
        // حال باید کلیدی متقارنی که با سیگنال رمز شده بود رو دیکرپیت کنیم
        val retrievedOriginalSecretKey = decryptAESSecretKeyExistInMergedFileBySenderSignalPublicKey(
            secretKeyEncryptedByteArray = extractKeyWithEncryptedFileModel.aesKeyEncryptedBySignal)

        //فایل رو رمزکشایی می کنیم
        val resultFileByteArray =  decryptFileByAES(
            secretKey = retrievedOriginalSecretKey,
            outputEncrypted = extractKeyWithEncryptedFileModel.fileEncryptedByAES
        )


        //save
        val uuid = UUID.randomUUID().toString()
        val fileName = "eliteFile"+ uuid  +  extractKeyWithEncryptedFileModel.fileExtension


        Log.e("jafar: ", "--------------  fileName -------------" + fileName)
        Log.e("jafar: ", "--------------   -------------")

        val parentPath = if (currentPath == null) {
            getRootPath()
        } else {
            currentPath
        }
        fileCreatorManager.createWithByteArrayContent( getRootPath(), fileName, resultFileByteArray)
        Toast.makeText(this, "  فایل در دایکتوری روت ساخته شد  " + fileName  , Toast.LENGTH_LONG).show()
        Log.e("jafar: ", "--------------  parentPath!! -------------" +  getRootPath() )
        Log.e("jafar: ", "--------------  parentPath!! -------------" + fileName )
    }



    // وقتی بخواهیم فایلی رو باز کنیم اول باید کلید متقارن رو از داخل فایل استخراج و با سیگنال باز کنیم
    // با این متد داریم مقدار کلید رمز شده رو استخراج می کنیم که بدیمش به الگوریتم تا با اون فایل رو برامون باز کنه
    @Throws(
        InvalidKeyException::class,
        UntrustedIdentityException::class,
        LegacyMessageException::class,
        InvalidMessageException::class,
        NoSessionException::class,
        DuplicateMessageException::class,
        InvalidVersionException::class,
        InvalidKeyIdException::class
    )
    fun decryptAESSecretKeyExistInMergedFileBySenderSignalPublicKey(
        secretKeyEncryptedByteArray: ByteArray
    ): ByteArray {

        val recipient: PersonSignalProtocol = readMySignalData()
        var sender =  PersonSignalProtocol()
        recipientSelectedFromList?.let {
            sender = it
        }
        val secretKey = decryptAESRandomPasswordBySenderSignalPublicKey(
            sender = sender,
            recipient = recipient,
            message = PreKeySignalMessage(secretKeyEncryptedByteArray)
        )
      /*  Log.e(
            "test: ",
            "-------------- decryptAESSecretKeyExistInMergedFileBySenderSignalPublicKey -------------"
        )
        Log.e(
            "test: ",
            "secretKeyStringEncryptedByteArray : " + String(secretKeyEncryptedByteArray)
        )*/
        Log.e("test: ", "--------------   -------------")
        // ----------------------------------

        return secretKey
    }


    fun injectSignalEncryptedKeyAndFileEncryptedWithAES(
        containerFile: File?,
        aesKeyEncryptedBySignal: ByteArray,
        fileEncryptedByAES: ByteArray
    ) {

        /*    val aesKeyEncryptedBySignal = 5
            val data2 = "This is programiz"*/
        try {
            val file = FileOutputStream(containerFile)
            val output = ObjectOutputStream(file)

            // Writing to the file using ObjectOutputStream
            val aesKeyEncryptedBySignalEncodedString =
                Convertor().byteArrayToString(aesKeyEncryptedBySignal)
            val fileEncryptedByAESEncodedString = Convertor().byteArrayToString(fileEncryptedByAES)

            output.writeObject(aesKeyEncryptedBySignalEncodedString)
            output.writeObject(fileEncryptedByAESEncodedString)

            val fileStream = FileInputStream(containerFile)
            // Creating an object input stream
            val objStream = ObjectInputStream(fileStream)

            //Using the readInt() method
            Log.e("jafar", " data 0 :" + objStream.readObject())
            Log.e("jafar", " data 1 :" + objStream.readObject())

            // Using the readObject() method
            val firstData = objStream.readObject()
            val secondData = objStream.readObject()

            var retrievedSecretKeyByteArray: ByteArray =
                Convertor().stringToByteArray(objStream.readObject() as String)
            var retrievedEncryptedFileByteArray: ByteArray =
                Convertor().stringToByteArray(objStream.readObject() as String)
            val result =
                decryptFileByAES(
                    secretKey = retrievedSecretKeyByteArray,
                    outputEncrypted = retrievedEncryptedFileByteArray
                )
            Log.e("jafar", "result data: " + result.decodeToString())


            output.close()
            objStream.close()
        } catch (e: java.lang.Exception) {
            Log.e("jafar", "Exception injectSignalEncryptedKeyAndFileEncryptedWithAES  :" + e)
            e.stackTrace
        }
    }


    // رمز کردن فایل با کلید متقارن
    fun encryptFileByAES(): ByteArray {
        val aesCrypter = aesManager.getAesCrypter(
            AesOpMode.CRYPT,
            AesMode.ECB,
            AesPadding.PKCS5,
            secretKey
        )

        val ioFile = java.io.File(currentPath) //ioFile.readBytes()
        val selectedFileEncrypted = aesCrypter.crypt(ioFile.readBytes())
        //Log.e("jafar: ", "  outputEncrypted " + selectedFileEncrypted)

        /*val aesCrypter = aesManager.getAesCrypter(
            AesOpMode.CRYPT,
            AesMode.ECB,
            AesPadding.PKCS5,
            secretKey
        )
        val fileShouldBe =
            "Hello world!, hello world!, hello world!, hello world!, hello world!, hello world! haha"
        val ioFile = java.io.File(currentPath) //ioFile.readBytes()
        val outputEncrypted = aesCrypter.crypt(fileShouldBe.encodeToByteArray())
        Log.e("jafar: ", "  outputEncrypted " + outputEncrypted)*/
        return selectedFileEncrypted
    }

    fun decryptFileByAES(secretKey: ByteArray, outputEncrypted: ByteArray): ByteArray {

        val aesDecrypter = aesManager.getAesCrypter(
            AesOpMode.DECRYPT,
            AesMode.ECB,
            AesPadding.PKCS5,
            secretKey
        )
        val outputDecrypted = aesDecrypter.crypt(outputEncrypted)
        //Log.e("jafar: ", "  output decryptFileByAES" + outputDecrypted.decodeToString())
        return outputDecrypted
    }

    /*fun onFileCreationConfirmed(fileName: String) {
        var currentPath: String? = null
        val parentPath = if (currentPath == null) {
            getRootPath()
        } else {
            currentPath
        }
        fileCreatorManager.create(parentPath!!, fileName)
    }*/
}