package com.alimoradi.elitefilebrowser.addAccount;

import android.util.Base64;

public class Convertor {
    public String byteArrayToString(byte [] array){
        String saveThis = Base64.encodeToString(array, Base64.DEFAULT);
        return saveThis;
    }

    public byte[] stringToByteArray(String stringFromSharedPrefs){
        byte[] array = Base64.decode(stringFromSharedPrefs, Base64.DEFAULT);
        return array;
    }

}
