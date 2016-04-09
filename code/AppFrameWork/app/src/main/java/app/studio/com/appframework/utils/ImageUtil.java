package app.studio.com.appframework.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import app.studio.com.appframework.component.log.Logger;

/**
 * Created by yupeng on 4/6/16.
 */
public class ImageUtil
{
    private static final String TAG = "ImageUtil";

    /**
     * 等比例放大缩小图片
     *
     * @param srcBitmap 原图
     * @param scale     缩放比例.
     *                  小于1表示缩小;等于1表示不变;大于1表示放大
     * @return 缩放后的图片
     */
    public static Bitmap zoom(Bitmap srcBitmap, double scale)
    {
        if (null == srcBitmap)
        {
            return null;
        }

        if (scale == 1.0)
        {
            return srcBitmap;
        }

        float scaleWidth = (float) (srcBitmap.getWidth() * scale);
        float scaleHeight = (float) (srcBitmap.getHeight() * scale);

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.postScale(scaleWidth, scaleHeight);

        try
        {
            return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), scaleMatrix, true);
        }
        catch (OutOfMemoryError e)
        {
            Logger.w(TAG, "zoom image failed, Out of memory error.", e);
            return srcBitmap;
        }
    }

    /**
     * 裁剪图片,将原图转换成指定的宽和高的目标图
     * 1.
     *
     * @param srcBitmap  原图
     * @param destWidth  目标图片的宽度
     * @param destHeight 目标图片的高度
     * @return 裁剪后的图片
     */
    public static Bitmap crop(Bitmap srcBitmap, float destWidth, float destHeight)
    {
        if (null == srcBitmap)
        {
            return null;
        }

        float srcWidth = srcBitmap.getWidth();
        float srcHeight = srcBitmap.getHeight();

        // 裁剪原图,转换成目标图的等比例图片
        float tmpWidth, tmpHeight;
        // 原图的宽度大于高度(换算成比例后)
        if (srcWidth > (destWidth / destHeight) * srcHeight)
        {
            tmpWidth = (destWidth / destHeight) * srcHeight;
            tmpHeight = srcHeight;
        }
        // 原图的宽度小于高度(换算成比例后)
        else if (srcWidth < (destWidth / destHeight) * srcHeight)
        {
            tmpWidth = srcWidth;
            tmpHeight = (destHeight / destWidth) * srcWidth;
        }
        // 原图的宽度等于高度(换算成比例后)
        else
        {
            tmpWidth = srcWidth;
            tmpHeight = srcHeight;
        }

        Bitmap tmpBitmap = null;
        try
        {
            // 裁剪出中间部分
            tmpBitmap = Bitmap.createBitmap(srcBitmap, (int) ((srcWidth - tmpWidth) / 2), (int) ((srcHeight - tmpHeight) / 2), (int) tmpWidth, (int) tmpHeight, null, false);
            // 缩放得到最终图
            return zoom(tmpBitmap, tmpWidth / srcWidth);
        }
        catch (OutOfMemoryError e)
        {
            Logger.w(TAG, "crop image failed, Out of memory error.", e);
            return srcBitmap;
        }
        finally
        {
            if (null != tmpBitmap && !tmpBitmap.isRecycled())
            {
                tmpBitmap.recycle();
            }
        }

    }
}
