package x_systems.x_messenger.Encryption;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.*;


public class AES256 {
    private static Charset DefaultCharset = Charset.forName("ISO-8859-1");
    private static String AESMODE = "AES/CBC/PKCS5Padding";
    private static String IV = "1234567891234567";
    private static String SALT = "12345678";

    public static SecretKey getSecretEncryptionKey(String Password) throws Exception{
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        KeySpec keySpec = new PBEKeySpec(Password.toCharArray(), SALT.getBytes(), 5, 256);
        SecretKeyFactory keyFactory = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA1");
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    private static String  toBinary(byte[] hash) {
        BigInteger bl = new BigInteger(hash);
        return bl.toString(2);
    }

    private static byte[] fromBinary(String hash) {
        BigInteger bl = new BigInteger(hash, 2);
        return bl.toByteArray();
    }

    public static String decryptedFileRead(File file, String password) throws Exception{
        SecretKey secretKey = getSecretEncryptionKey(password);
        Cipher aesCipher = Cipher.getInstance(AESMODE);
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
        System.out.println("DECRYPTIONAES START");
        InputStream ip = new FileInputStream(file);

        aesCipher.init(Cipher.DECRYPT_MODE, secretKey);
        CipherInputStream is = new CipherInputStream(ip, aesCipher);

        BufferedReader br = new BufferedReader(new InputStreamReader(is));


        String output = "";
        String buff;
        while(( buff = br.readLine() )!= null){
            output += buff;
        }
        br.close();
        System.out.println("DECRYPTIONAES DONE");

        return output;
    }

    public static void decryptFile(File file, String password) throws Exception{
        SecretKey secretKey = getSecretEncryptionKey(password);
        Cipher aesCipher = Cipher.getInstance(AESMODE);
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
        System.out.println("DECRYPTIONAES START");
        InputStream ip = new FileInputStream(file);

        aesCipher.init(Cipher.DECRYPT_MODE, secretKey);
        CipherInputStream is = new CipherInputStream(ip, aesCipher);
        System.out.println("DECRYPTIONAES DONE");

        BufferedReader br = new BufferedReader(new InputStreamReader(is));


        String output = "";
        String buff;
        while(( buff = br.readLine() )!= null){
            output += buff;
        }

        br.close();

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(Base64.decode(output, Base64.DEFAULT));
        fos.close();
    }

    public static void encryptedFileWrite(File file, String Text, String password) throws Exception{
        SecretKey secretKey = getSecretEncryptionKey(password);
        Cipher aesCipher = Cipher.getInstance(AESMODE);
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        OutputStream ip = new FileOutputStream(file);
        CipherOutputStream is = new CipherOutputStream(ip, aesCipher);
        BufferedWriter br = new BufferedWriter(new OutputStreamWriter(is));
        br.write(Text);
        br.close();
    }

    public static byte[] GetFileContent(File f){
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            byte[] holder = new byte[fis.available()];
            fis.read(holder);
            fis.close();
            return holder;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static void encryptFile(File file, String password) throws Exception{
        SecretKey secretKey = getSecretEncryptionKey(password);
        Cipher aesCipher = Cipher.getInstance(AESMODE);
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);



        String toEnc = Base64.encodeToString(GetFileContent(file), Base64.DEFAULT);
        System.out.println("FIRST ENCODED: " + file.getName());
        OutputStream ip = new FileOutputStream(file);
        CipherOutputStream is = new CipherOutputStream(ip, aesCipher);
        BufferedWriter br = new BufferedWriter(new OutputStreamWriter(is));

        br.write(toEnc);
        br.close();
    }

    public static void changePassword(File[] files , String oldPassword, String newPassword){
        try {
            for(File file : files){
                encryptedFileWrite(file, decryptedFileRead(file, oldPassword), newPassword);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] GetBytesFromString(String Input){
        return Base64.decode(Input, Base64.DEFAULT);
    }

    public static String GetStringFromBytes(byte[] Input){
        return Base64.encodeToString(Input, Base64.DEFAULT);
    }

    public static String encryptText(String plainText, String password) throws Exception{
        // AES defaults to AES/ECB/PKCS5Padding in Java 7
        SecretKey secretKey = getSecretEncryptionKey(password);
        Cipher aesCipher = Cipher.getInstance(AESMODE);
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] byteCipherText = aesCipher.doFinal(plainText.getBytes(DefaultCharset));
        return new String(byteCipherText, DefaultCharset);
    }

    public static String decryptText(String byteCipherText, String password) throws Exception {
        SecretKey secretKey = getSecretEncryptionKey(password);
        Cipher aesCipher = Cipher.getInstance(AESMODE);
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
        aesCipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] bytePlainText = aesCipher.doFinal(byteCipherText.getBytes(DefaultCharset));
        return new String(bytePlainText);
    }
}


