package app.studio.com.appframework.component.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import library.sswwm.com.component.log.Logger;

public class UpgradeDbUtil
{
    private static final String TAG = "UpgradeDbUtil";

    /**
     * 数据库存结构保存xml的路径
     */
    private static final String XML_DIR = "sql/";

    /**
     * 数据库存升级操作的实例
     */
    private static UpgradeDbUtil instance;

    /**
     * 当前操作的上下文对象
     */
    private static Context mContext;

    /**
     * 全局数据库存操作实例
     *
     * @param context 当前操作的上下文对象
     * @return UpgradeDbUtil实例
     */

    public static synchronized UpgradeDbUtil getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new UpgradeDbUtil();
            mContext = context;
        }
        return instance;
    }

    /**
     * 创建表结构
     *
     * @param fileName 文件名称
     * @param db       数据库存操作实例
     * @return true表示创建成功 false 表示创建失败
     */
    public boolean createTableFromXml(String fileName, SQLiteDatabase db)
    {
        boolean isSuccess = false;
        try
        {
            String dbxml = DBUtils.getFromAssets(XML_DIR + fileName, mContext);
            DatabaseInfo info = new DatabaseInfo();
            DbInfoHandler handler = new DbInfoHandler();
            handler.doParse(info, dbxml);
            addTable(info, db, dbxml);
            isSuccess = true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Logger.i(TAG, "==创建数据库失败==" + ex.toString());
        }
        return isSuccess;
    }

    /**
     * 升级数据库表结构
     *
     * @param fileName 数据库xml文件名称
     * @param db       数据库操作实例
     * @param version  旧的数据库版本号
     * @return true 表示升级成功,false 表示升级失败
     */
    public boolean upgradeTableFromXml(String fileName, SQLiteDatabase db, String version)
    {
        boolean isSuccess = false;
        Cursor cursor = db.query(DatabaseInfo.GlobalDbVer.TABLE_NAME, null, null, null, null, null, null, null);
        String oldVersion = cursor.getString(cursor.getColumnIndex(DatabaseInfo.GlobalDbVer.TABLE_DB_VER));
        String oldDesc = cursor.getString(cursor.getColumnIndex(DatabaseInfo.GlobalDbVer.TABLE_DESC));
        String oldId = cursor.getString(cursor.getColumnIndex(DatabaseInfo.GlobalDbVer.TABLE_ID));
        if (cursor != null)
        {
            cursor.close();
        }
        //在这里做数据库的版本号信息比较
        if (oldVersion == null || oldDesc == null)
        {
            try
            {
                isSuccess = createTableFromXml(fileName, db);
            }
            catch (Exception ex)
            {
                isSuccess = false;
                Logger.i(TAG, "==创建数据库失败==" + ex.toString());
            }
        }
        else
        {
            if (version.equals(oldVersion))
            {
                isSuccess = true;
                Logger.i(TAG, "数据库版本一致或者当前的数据库存版本号比之前的低不需要升级");
            }
            // ***END***  [修改数据库存的版本号比对] zhouxin 2012-9-6 modify
            else
            {
                String snsDataXml;
                try
                {
                    Logger.i(TAG, "数据库升级开始时间:" + System.currentTimeMillis());
                    String dbxml = DBUtils.getFromAssets(XML_DIR + fileName, mContext);
                    DatabaseInfo info = new DatabaseInfo();
                    DbInfoHandler handler = new DbInfoHandler();
                    handler.doParse(info, dbxml);
                    DatabaseInfo oldinfo = new DatabaseInfo();
                    handler.doParse(oldinfo, oldDesc);
                    compareDbInfo(oldinfo, info, db);
                    db.update(DatabaseInfo.GlobalDbVer.TABLE_NAME, setValues(version, dbxml), DatabaseInfo.GlobalDbVer.TABLE_ID + "=?", new String[]{String.valueOf(oldId)});
                    isSuccess = true;
                    Logger.i(TAG, "数据库升级结束时间:" + System.currentTimeMillis());
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    isSuccess = false;
                    Logger.i(TAG, "==数据版本升级失败：==" + ex.toString());
                }
            }
        }
        return isSuccess;
    }


    /**
     * [用于添加表信息]<BR>
     * [功能详细描述]
     *
     * @param baseInfo Database实体类
     * @param db       数据库操作实例
     * @param desc     描述信息
     */
    private void addTable(DatabaseInfo baseInfo, SQLiteDatabase db, String desc)
    {
        Log.e(TAG, "首次创建表结构开始时间：" + System.currentTimeMillis());
        for (DatabaseInfo.Table table : baseInfo.getListTable())
        {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("CREATE TABLE ");
            stringBuffer.append(table.getName());
            stringBuffer.append(" ( ");

            List<DatabaseInfo.Field> listFiled = table.getListFiled();
            int size = listFiled.size();
            for (int i = 0; i < size; i++)
            {
                DatabaseInfo.Field field = listFiled.get(i);
                stringBuffer.append(field.getName());
                stringBuffer.append(" ");
                stringBuffer.append(field.getType());
                stringBuffer.append(" ");
                stringBuffer.append(field.getObligatory());
                if (i != size - 1)
                {
                    stringBuffer.append(", ");
                }
                else
                {
                    stringBuffer.append(");");
                }
            }
            Log.i(TAG, "create table sql is : " + stringBuffer.toString());
            db.execSQL(stringBuffer.toString());
        }
        db.insert(DatabaseInfo.GlobalDbVer.TABLE_NAME, null, setValues(baseInfo.getVersion(), desc));
        Log.e(TAG, "首次创建表结构结束时间：" + System.currentTimeMillis());
    }

    /**
     * 比对当前数据库存的版本号是否一致
     *
     * @param oldVerSion 旧的数据版本号
     * @param newVersion 新的数据库版本号
     * @return true 表示一致 false 表示不一致
     */
    public boolean compareDBVersion(String oldVerSion, String newVersion)
    {
        return newVersion.equals(oldVerSion);
    }

    /**
     * 比对数据库的版本信息
     *
     * @param oldDatabase 旧的数据库存版本信息
     * @param newDatabase 新的数据库存版本信息
     * @param db          数据库实例
     */
    public void compareDbInfo(DatabaseInfo oldDatabase, DatabaseInfo newDatabase, SQLiteDatabase db)
    {
        Log.e(TAG, "比对表结构开始时间: " + System.currentTimeMillis());
        List<DatabaseInfo.Table> listAddTable = new ArrayList<>();
        List<DatabaseInfo.Table> listUpdateTable = new ArrayList<>();
        for (DatabaseInfo.Table table : newDatabase.getListTable())
        {
            boolean isExitTableName = false;
            for (DatabaseInfo.Table oldTab : oldDatabase.getListTable())
            {
                if (table.getName().equals(oldTab.getName()))
                {
                    DatabaseInfo.Table updateTable = new DatabaseInfo.Table();
                    updateTable.setName(table.getName());
                    isExitTableName = true;
                    List<DatabaseInfo.Field> updateFieldList = new ArrayList<>();
                    for (DatabaseInfo.Field field : table.getListFiled())
                    {
                        boolean isExitField = false;
                        for (DatabaseInfo.Field oldField : oldTab.getListFiled())
                        {
                            if (oldField.getName().equals(field.getName()))
                            {
                                isExitField = true;
                            }
                        }
                        if (!isExitField)
                        {
                            updateFieldList.add(field);
                        }
                    }
                    if (updateFieldList != null && updateFieldList.size() > 0)
                    {
                        updateTable.setListFiled(updateFieldList);
                        listUpdateTable.add(updateTable);
                    }
                }
            }
            if (!isExitTableName)
            {
                listAddTable.add(table);
            }
        }
        Log.e(TAG, "比对表结构结束时间:" + System.currentTimeMillis());
        if (listAddTable != null && listAddTable.size() > 0)
        {
            // 在这里做新增表的操作
            addTableStructure(listAddTable, db);
        }
        if (listUpdateTable != null && listUpdateTable.size() > 0)
        {
            // 在这里做新增列的操作
            addColumn(listUpdateTable, db);
        }
    }

    /**
     * 添加新的表结构
     *
     * @param listAddTable 需要添加的新的表结构
     */
    private void addTableStructure(List<DatabaseInfo.Table> listAddTable, SQLiteDatabase db)
    {
        Log.e(TAG, "==比对完成后对新增表结构开始时间==" + System.currentTimeMillis());
        for (DatabaseInfo.Table table : listAddTable)
        {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("CREATE TABLE ");
            stringBuffer.append(table.getName());
            stringBuffer.append(" ( ");
            List<DatabaseInfo.Field> listFiled = table.getListFiled();
            int size = listFiled.size();
            for (int i = 0; i < size; i++)
            {
                DatabaseInfo.Field field = listFiled.get(i);
                stringBuffer.append(field.getName());
                stringBuffer.append(" ");
                stringBuffer.append(field.getType());
                stringBuffer.append(" ");
                stringBuffer.append(field.getObligatory());
                if (i != size - 1)
                {
                    stringBuffer.append(", ");
                }
                else
                {
                    stringBuffer.append(");");
                }
            }
            Log.i(TAG, "addTableStructure sql is :" + stringBuffer.toString());
            db.execSQL(stringBuffer.toString());
        }
        Log.e(TAG, "比对完成后对新增表结构结束时间:" + System.currentTimeMillis());
    }

    /**
     * 做新增列操作
     *
     * @param listUpdateTable 需要更新的列表
     * @param db              数据库操作实例
     */
    private void addColumn(List<DatabaseInfo.Table> listUpdateTable, SQLiteDatabase db)
    {
        Log.e(TAG, "比对完后对新增字段开始时间：" + System.currentTimeMillis());
        for (DatabaseInfo.Table table : listUpdateTable)
        {
            for (DatabaseInfo.Field field : table.getListFiled())
            {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("ALTER TABLE ");
                stringBuffer.append(table.getName());
                stringBuffer.append(" ADD COLUMN ");
                stringBuffer.append(field.getName());
                stringBuffer.append(" ");
                stringBuffer.append(field.getType());
                stringBuffer.append(" ");
                stringBuffer.append(field.getObligatory());
                Log.i(TAG, " addColumn sql is :" + stringBuffer.toString());
                db.execSQL(stringBuffer.toString());
            }
        }
        Log.e(TAG, "比对完后对新增字段结束时间：" + System.currentTimeMillis());
    }


    /**
     * 封装全局数据库版本对象
     *
     * @return ContentValues
     */
    private ContentValues setValues(String version, String desc)
    {
        ContentValues values = new ContentValues();
        values.put(DatabaseInfo.GlobalDbVer.TABLE_DB_VER, version);
        values.put(DatabaseInfo.GlobalDbVer.TABLE_DESC, desc);
        return values;
    }

}
