package app.studio.com.appframework.utils.cryptor;

/**
 * Base64编解码
 */
public final class Base64 {
    public static String encode(byte[] input) {
        if (null == input) {
            return null;
        }
        return android.util.Base64.encodeToString(input, android.util.Base64.DEFAULT);
    }


    public static byte[] decode(String input) {
        if (null == input) {
            return null;
        }
        return android.util.Base64.decode(input, android.util.Base64.DEFAULT);
    }
}
