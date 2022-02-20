package com.alimoradi.elitefilebrowser.addAccount;

import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectOutputStreamHelper {

    public void run(java.io.File inputFile) {

       /* int aesKeyEncryptedBySignal = 5;
        String data2 = "This is programiz";*/

        int aesKeyEncryptedBySignal = 5;
        String data2 = "This is programiz";

        try {
            FileOutputStream file = new FileOutputStream(inputFile);
            ObjectOutputStream output = new ObjectOutputStream(file);

            // Writing to the file using ObjectOutputStream
            output.writeInt(aesKeyEncryptedBySignal);
            output.writeObject(data2);

            FileInputStream fileStream = new FileInputStream(inputFile);
            // Creating an object input stream
            ObjectInputStream objStream = new ObjectInputStream(fileStream);

            //Using the readInt() method
            Log.e("jafar", "Integer data :" + objStream.readInt());

            // Using the readObject() method
            Log.e("jafar", "String data: " + objStream.readObject());

            output.close();
            objStream.close();
        } catch (Exception e) {
            Log.e("jafar", "Exception   :" + e.getMessage());
            e.getStackTrace();
        }
    }


    public void run(java.io.File inputFile,
                    byte[] aesKeyEncryptedBySignal,
                    byte[] fileEncryptedByAES) {

       /* int aesKeyEncryptedBySignal = 5;
        String data2 = "This is programiz";*/


        try {
            FileOutputStream file = new FileOutputStream(inputFile);
            ObjectOutputStream output = new ObjectOutputStream(file);

            // Writing to the file using ObjectOutputStream
            //output.writeInt(aesKeyEncryptedBySignal);
            output.writeObject(aesKeyEncryptedBySignal);
            output.writeObject(fileEncryptedByAES);

            FileInputStream fileStream = new FileInputStream(inputFile);
            // Creating an object input stream
            ObjectInputStream objStream = new ObjectInputStream(fileStream);

            //Using the readInt() method
            byte[] retrievedSecretKeyByteArray = (byte[]) objStream.readObject();
            byte[] retrievedEncryptedFileByteArray = (byte[]) objStream.readObject();
            Log.e("jafar", " ConcatKeyWithEncryptedFile retrievedSecretKeyByteArray   :" + new Convertor().byteArrayToString(retrievedSecretKeyByteArray));
            Log.e("jafar", " ConcatKeyWithEncryptedFile retrievedEncryptedFileByteArray  : " + new Convertor().byteArrayToString(retrievedEncryptedFileByteArray));
            //Log.e("jafar" , "Integer data :" + objStream.readInt());

            // Using the readObject() method
           /* Log.e("jafar" , " ConcatKeyWithEncryptedFile first data :" + objStream.readObject());
            Log.e("jafar" ,  " ConcatKeyWithEncryptedFile second data: " + objStream.readObject());*/

            output.close();
            objStream.close();
        } catch (Exception e) {
            Log.e("jafar", "Exception   :" + e.getMessage());
            e.getStackTrace();
        }
    }

    public java.io.File concatKeyWithEncryptedFile(java.io.File inputFile,
                                                   byte[] aesKeyEncryptedBySignal,
                                                   byte[] fileEncryptedByAES,
                                                   String fileExtension) {

        //java.io.File concatFile = inputFile;
        try {
            FileOutputStream file = new FileOutputStream(inputFile);
            ObjectOutputStream output = new ObjectOutputStream(file);

            // Writing to the file using ObjectOutputStream
            output.writeObject(aesKeyEncryptedBySignal);
            output.writeObject(fileEncryptedByAES);
            output.writeObject(fileExtension);

            output.close();

        } catch (Exception e) {
            Log.e("jafar", "concatKeyWithEncryptedFile   :" + e.getMessage());
            e.getStackTrace();
        }
        return inputFile;
    }

    public ExtractKeyWithEncryptedFileModel extractKeyWithEncryptedFile(java.io.File inputFile) {

        ExtractKeyWithEncryptedFileModel extractKeyWithEncryptedFileModel = new ExtractKeyWithEncryptedFileModel();
        try {
            FileInputStream fileStream = new FileInputStream(inputFile);
            // Creating an object input stream
            ObjectInputStream objStream = new ObjectInputStream(fileStream);

            //Using the readInt() method
            byte[] retrievedSecretKeyByteArray = (byte[]) objStream.readObject();
            byte[] retrievedEncryptedFileByteArray = (byte[]) objStream.readObject();
            String fileExtension = (String) objStream.readObject();

            extractKeyWithEncryptedFileModel.setAesKeyEncryptedBySignal(retrievedSecretKeyByteArray);
            extractKeyWithEncryptedFileModel.setFileEncryptedByAES(retrievedEncryptedFileByteArray);
            extractKeyWithEncryptedFileModel.setFileExtension(fileExtension);

            Log.e("jafar", " ConcatKeyWithEncryptedFile retrievedSecretKeyByteArray   :" + new Convertor().byteArrayToString(retrievedSecretKeyByteArray));
            Log.e("jafar", " ConcatKeyWithEncryptedFile retrievedEncryptedFileByteArray  : " + new Convertor().byteArrayToString(retrievedEncryptedFileByteArray));
            //Log.e("jafar" , "Integer data :" + objStream.readInt());
            objStream.close();

        } catch (Exception e) {
            Log.e("jafar", "Exception   :" + e.getMessage());
            e.getStackTrace();
        }
        return extractKeyWithEncryptedFileModel;
    }

    /* int aesKeyEncryptedBySignal = 5;  ObjectOutputStreamHelper
        String data2 = "This is programiz";*/
}
