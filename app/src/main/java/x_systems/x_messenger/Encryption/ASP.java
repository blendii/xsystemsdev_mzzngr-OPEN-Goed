package x_systems.x_messenger.Encryption;

        import android.util.Base64;

        import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.io.OutputStreamWriter;
        import java.nio.charset.Charset;
        import java.security.SecureRandom;
        import java.security.spec.KeySpec;

        import javax.crypto.SecretKey;
        import javax.crypto.SecretKeyFactory;
        import javax.crypto.spec.PBEKeySpec;
        import javax.crypto.spec.SecretKeySpec;
        import org.spongycastle.crypto.engines.AESFastEngine;
        import org.spongycastle.crypto.io.CipherInputStream;
        import org.spongycastle.crypto.io.CipherOutputStream;
        import org.spongycastle.crypto.modes.CBCBlockCipher;
        import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
        import org.spongycastle.crypto.params.KeyParameter;
        import org.spongycastle.crypto.params.ParametersWithIV;



public class ASP {
    private static Charset DefaultCharset = Charset.forName("ISO-8859-1");
    private static String AESMODE = "AES";
    private static String IV = "1234567891234567";
    private static String SALT = "12345678";
    public static SecretKey getSecretEncryptionKey(String Password) throws Exception{
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
        KeySpec keySpec = new PBEKeySpec(Password.toCharArray(), SALT.getBytes(), 1000, 256);
        SecretKeyFactory keyFactory = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA1");
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static void EncryptFile(File inFile){
        try
        {
            SecretKey key = getSecretEncryptionKey("hello");
            PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
            // Random iv

            SecureRandom rng = new SecureRandom();
            byte[] ivBytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8};


            byte[] data = GetFileContent(inFile);
            String tow = Base64.encodeToString(data, Base64.DEFAULT);
            cipher.init(true, (org.spongycastle.crypto.CipherParameters)new ParametersWithIV(new KeyParameter(key.getEncoded()), ivBytes));
            FileOutputStream fos = new FileOutputStream(inFile);
            CipherOutputStream stream = new CipherOutputStream(fos, cipher);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(stream));
            bw.write(tow);
            bw.close();
            fos.close();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    public static void DecryptFile(File inFile){
        try
        {
            SecretKey key = getSecretEncryptionKey("hello");
            PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
            // Random iv

            SecureRandom rng = new SecureRandom();
            byte[] ivBytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8};

            cipher.init(false, (org.spongycastle.crypto.CipherParameters)new ParametersWithIV(new KeyParameter(key.getEncoded()), ivBytes));
            FileInputStream fis = new FileInputStream(inFile);
            CipherInputStream stream = new CipherInputStream(fis, cipher);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String output = "";
            String temp;
            while((temp = br.readLine()) != null){
                output += temp;
            }

            br.close();
            fis.close();
            FileOutputStream fos = new FileOutputStream(inFile);
            fos.write(Base64.decode(output, Base64.DEFAULT));
            fos.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    public static String DecryptFileToMemory(File inFile){
        try
        {
            SecretKey key = getSecretEncryptionKey("hello");
            PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
            // Random iv

            SecureRandom rng = new SecureRandom();
            byte[] ivBytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8};

            cipher.init(false, (org.spongycastle.crypto.CipherParameters)new ParametersWithIV(new KeyParameter(key.getEncoded()), ivBytes));
            FileInputStream fis = new FileInputStream(inFile);
            CipherInputStream stream = new CipherInputStream(fis, cipher);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String output = "";
            String temp;
            while((temp = br.readLine()) != null){
                output += temp;
            }

            br.close();
            fis.close();
            return output;

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;

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
}

