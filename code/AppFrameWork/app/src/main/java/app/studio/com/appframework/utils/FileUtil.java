package app.studio.com.appframework.utils;

import java.io.Closeable;
import java.io.IOException;

import app.studio.com.appframework.component.log.Logger;

/**
 * Created by Administrator on 2016/3/29 0029.
 */
public final class FileUtil {
    private static final String TAG = "FileUtil";

    public static void closeStream(Closeable closeable) {
        if (null == closeable) {
            return;
        }

        try {
            closeable.close();
        } catch (IOException e) {
            Logger.w(TAG, "close stream failed, closeable: " + closeable, e);
        }
    }

}
