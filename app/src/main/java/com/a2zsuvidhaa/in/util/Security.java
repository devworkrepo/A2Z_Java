package com.a2zsuvidhaa.in.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Security {

    private byte[] keyValue;
    public Security(String key){
        keyValue = key.getBytes();
    }

    public String encrypt(String input){
        byte[] crypted = null;
        try{
            SecretKeySpec skey = new SecretKeySpec(keyValue, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(input.getBytes());
        }catch(Exception e){
           e.getStackTrace();
        }
        return new String(Base64.encodeBase64(crypted));
    }

    public String decrypt(String input){
        byte[] output = null;
        try{
            SecretKeySpec skey = new SecretKeySpec(keyValue, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            output = cipher.doFinal(Base64.decodeBase64(input.getBytes()));
        }catch(Exception e){
            e.getStackTrace();
        }
        return new String(output);
    }

}