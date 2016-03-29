package app.studio.com.appframework.utils.cryptor;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import app.studio.com.appframework.component.log.Logger;

/**
 * AES加解密
 * 对称加密算法（秘钥一样）
 */
public final class AES {

    private static final String TAG = "AES";
    private static final byte[] RAW_KEY_DEFAULT = {1, 2, 3, 4, 5, 6, 7, 8};

    public static String encrypt(String input) {
        if (null == input) {
            return null;
        }
        return new String(encrypt(input.getBytes()));
    }


    public static byte[] encrypt(byte[] input) {
        return encrypt(input, RAW_KEY_DEFAULT);
    }

    public static byte[] encrypt(byte[] input, byte[] rawKey) {
        if (null == input || null == rawKey) {
            return null;
        }

        SecretKey secretKey = new SecretKeySpec(RAW_KEY_DEFAULT, "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal();
        } catch (NoSuchAlgorithmException e) {
            Logger.w(TAG, "encrypt, NoSuchAlgorithmException");
        } catch (NoSuchPaddingException e) {
            Logger.w(TAG, "encrypt, NoSuchPaddingException");
        } catch (IllegalBlockSizeException e) {
            Logger.w(TAG, "encrypt, IllegalBlockSizeException");
        } catch (BadPaddingException e) {
            Logger.w(TAG, "encrypt, BadPaddingException");
        } catch (InvalidKeyException e) {
            Logger.w(TAG, "encrypt, InvalidKeyException");
        }
        return null;
    }

    public static String decrypt(String input) {
        if (null == input) {
            return null;
        }
        return new String(decrypt(input.getBytes()));
    }

    public static byte[] decrypt(byte[] input) {
        return decrypt(input, RAW_KEY_DEFAULT);
    }

    public static byte[] decrypt(byte[] input, byte[] rawKey) {
        if (null == input || null == rawKey) {
            return null;
        }

        SecretKey secretKey = new SecretKeySpec(RAW_KEY_DEFAULT, "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal();
        } catch (NoSuchAlgorithmException e) {
            Logger.w(TAG, "encrypt, NoSuchAlgorithmException");
        } catch (NoSuchPaddingException e) {
            Logger.w(TAG, "encrypt, NoSuchPaddingException");
        } catch (IllegalBlockSizeException e) {
            Logger.w(TAG, "encrypt, IllegalBlockSizeException");
        } catch (BadPaddingException e) {
            Logger.w(TAG, "encrypt, BadPaddingException");
        } catch (InvalidKeyException e) {
            Logger.w(TAG, "encrypt, InvalidKeyException");
        }
        return null;
    }

}
