package il.co.inmanage.meshulam_sdk.utils;

import android.annotation.SuppressLint;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class BaseEncryption {

    private static BaseEncryption baseEncryption;

//    static {
//        System.loadLibrary("ndkMethods");
//    }

    private BaseEncryption() {
    }

    @SuppressWarnings("JniMissingFunction")
//    public static native String encryptionKey();

    private static String key = "meinshmaulnaamge";

    public static BaseEncryption getInstance() {
        if (baseEncryption == null) {
            baseEncryption = new BaseEncryption();
        }
        return baseEncryption;
    }

//    public static boolean isInitialized() {
//        return !encryptionKey().isEmpty();
//    }

    public String encryptAesString(String input) throws Exception {
        @SuppressLint("GetInstance")
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        String e = key;
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(input.getBytes("UTF-8"));
        return Base64.encodeToString(encrypted, Base64.NO_WRAP);
    }
    public String decryptAesString(String input) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] data = Base64.decode(input, Base64.NO_WRAP);
        byte[] decryptedBytes = cipher.doFinal(data);
        return new String(decryptedBytes);
    }

    public static String getSHA256(String str){
        MessageDigest messageDigest;
        String encodestr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodestr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodestr;
    }

    private static String byte2Hex(byte[] bytes){
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length()==1){
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }


}
