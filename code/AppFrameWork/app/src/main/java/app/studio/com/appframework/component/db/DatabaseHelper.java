package app.studio.com.appframework.component.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import library.sswwm.com.component.log.Logger;

public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * 打印log信息时传入的标志
     */
    private static final String TAG = "DatabaseHelper";

    /**
     * 数据库名称
     */
    private static final String DATABASE_NAME_STR = "common";

    /**
     * 数据库后缀名
     */
    private static final String DATABASE_NAME_SUFFIX = ".db";

    /**
     * 数据库的版本号
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * 跟随版本发本的数据库版本号，用于做数据库版本号的对比
     */
    private static final String VERSION = "1.00.000";

    /**
     * 数据库xml文件的名称
     */
    private static final String DB_FILE_NAME = "database.xml";

    /**
     * 数据库升级实例
     */
    private static UpgradeDbUtil mUpgradeDbutil = null;

    /**
     * 数据库表操作对象
     */
    private static DatabaseHelper sSingleton = null;

    /**
     * 当前的用户ID
     */
    private static String currentUserID = "";

    /**
     * 构造器创建数据库
     *
     * @param context 上下文
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME_STR + DATABASE_NAME_SUFFIX, null, DATABASE_VERSION);
        Logger.i(TAG, "init DatabaseHelper()");
    }

    /**
     * 带有UserId的DatabaseHelper构造方法
     *
     * @param context Context对象
     * @param userId  用户ID
     */
    private DatabaseHelper(Context context, String userId) {
        super(context, DATABASE_NAME_STR + "_" + userId + DATABASE_NAME_SUFFIX, null, DATABASE_VERSION);
        Logger.i(TAG, "init DatabaseHelper()  userSysId : " + userId);
    }

    /**
     * 获取DatabaseHelper对象
     *
     * @param context 上下文
     * @return DatabaseHelper对象
     */
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sSingleton == null) {
            mUpgradeDbutil = UpgradeDbUtil.getInstance(context);
            sSingleton = new DatabaseHelper(context);
        }
        return sSingleton;
    }

    /**
     * 带有userId的databaseHelper对象的创建<BR>
     * 分库用到的databaseHelper(添加数据库切换时对原有数据库的关闭动作)
     *
     * @param context Context
     * @param userId  用户ID
     * @return DatabaseHelper对象
     */
    public static synchronized DatabaseHelper getInstance(Context context, String userId) {
        Logger.d(TAG, "new DatabaseHelper");
        mUpgradeDbutil = UpgradeDbUtil.getInstance(context);
        if (sSingleton != null) {
            if (currentUserID.equals(userId)) {
                return sSingleton;
            } else {
                sSingleton.close();
            }
        }
        sSingleton = new DatabaseHelper(context, userId);
        currentUserID = userId;
        return sSingleton;
    }

    /**
     * 创建数据库
     *
     * @param db SQLiteDatabase对象
     * @see SQLiteOpenHelper#onCreate(SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.d(TAG, "DatabaseHelper on Create()");
        try {
            mUpgradeDbutil.createTableFromXml(DB_FILE_NAME, db);

        } catch (Exception e) {
            Logger.e(TAG, "Create table : ", e);
        }
    }

    /**
     * 版本更新
     *
     * @param db         SQLiteDatabase对象
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     * @see SQLiteOpenHelper#onUpgrade(SQLiteDatabase,
     * int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mUpgradeDbutil.upgradeTableFromXml(DB_FILE_NAME, db, VERSION);
    }
}