package com.alimoradi.elitefilebrowser.addAccount;


public class ExtractKeyWithEncryptedFileModel {
    private byte[] aesKeyEncryptedBySignal;
    private byte[] fileEncryptedByAES;
    private String fileExtension;
   // private FileOpenManagerAndroid.FileTypeModelENUM fileExtension;

    public byte[] getAesKeyEncryptedBySignal() {
        return aesKeyEncryptedBySignal;
    }

    public void setAesKeyEncryptedBySignal(byte[] aesKeyEncryptedBySignal) {
        this.aesKeyEncryptedBySignal = aesKeyEncryptedBySignal;
    }

    public byte[] getFileEncryptedByAES() {
        return fileEncryptedByAES;
    }

    public void setFileEncryptedByAES(byte[] fileEncryptedByAES) {
        this.fileEncryptedByAES = fileEncryptedByAES;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }
}
