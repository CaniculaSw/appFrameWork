package app.studio.com.appframework.component.bitmap.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;

/**
 * Created by yupeng on 4/9/16.
 */
public class Utils
{
    private static final long INITIALCRC = 0xFFFFFFFFFFFFFFFFL;
    private static final long POLY64REV = 0x95AC9329AC4BC9B5L;
    private static long[] sCrcTable = new long[256];

    static
    {
        //参考 http://bioinf.cs.ucl.ac.uk/downloads/crc64/crc64.c
        long part;
        for (int i = 0; i < 256; i++)
        {
            part = i;
            for (int j = 0; j < 8; j++)
            {
                long x = ((int) part & 1) != 0 ? POLY64REV : 0;
                part = (part >> 1) ^ x;
            }
            sCrcTable[i] = part;
        }
    }

    /**
     * 获取bitmap的字节大小
     *
     * @param bitmap
     * @return
     */
    public static int getBitmapSize(Bitmap bitmap)
    {
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    public static byte[] makeKey(String httpUrl)
    {
        return getBytes(httpUrl);
    }

    public static byte[] getBytes(String in)
    {
        byte[] result = new byte[in.length() * 2];
        int output = 0;
        for (char ch : in.toCharArray())
        {
            result[output++] = (byte) (ch & 0xFF);
            result[output++] = (byte) (ch >> 8);
        }
        return result;
    }

    public static final long crc64Long(byte[] buffer)
    {
        long crc = INITIALCRC;
        for (int k = 0, n = buffer.length; k < n; ++k)
        {
            crc = sCrcTable[(((int) crc) ^ buffer[k]) & 0xff] ^ (crc >> 8);
        }
        return crc;
    }

    public static boolean isSameKey(byte[] key, byte[] buffer)
    {
        int n = key.length;
        if (buffer.length < n)
        {
            return false;
        }
        for (int i = 0; i < n; ++i)
        {
            if (key[i] != buffer[i])
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取可以使用的缓存目录
     *
     * @param context
     * @param uniqueName 目录名称
     * @return
     */
    public static File getDiskCacheDir(Context context, String uniqueName)
    {
        final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ?
                getExternalCacheDir(context).getPath() : context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 获取程序外部的缓存目录
     *
     * @param context
     * @return
     */
    public static File getExternalCacheDir(Context context)
    {
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }
}
