package app.studio.com.appframework.component.db;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteTransactionListener;
import android.net.Uri;

import java.util.ArrayList;

import library.sswwm.com.component.log.Logger;

public abstract class BaseContentProvider extends ContentProvider implements
        SQLiteTransactionListener
{
    /**
     * 打印log信息时传入的标志
     */
    private static final String TAG = "BaseContentProvider";

    /**
     * 数据库
     */
    protected SQLiteDatabase mDb;

    /**
     * 数据库操作帮助类
     */
    private SQLiteOpenHelper mOpenHelper;

    /**
     * 有改动需要notifyChange的标记
     */
    private volatile boolean mNotifyChange;

    /**
     * 各线程是否在批处理的标记
     */
    private final ThreadLocal<Boolean> mApplyingBatch = new ThreadLocal<Boolean>();

    /**
     * 创建provider
     * @return boolean 创建是否成功
     * @see ContentProvider#onCreate()
     */
    @Override
    public boolean onCreate()
    {
        Logger.d(TAG, "BaseContentProvider create");
        Context context = getContext();
        mOpenHelper = getDatabaseHelper(context);
        return true;
    }

    /**
     * 抽象方法，用于获取SQLiteOpenHelper
     * @param context Context
     * @return SQLiteOpenHelper
     */
    protected abstract SQLiteOpenHelper getDatabaseHelper(Context context);

    /**
     * 抽象方法，事物中对insert的处理，用于子类继承
     *
     * @param uri    通用资源标志符 代表要操作的数据
     * @param values ContentValues 要插入的值
     * @return 插入后的uri描述
     */
    protected abstract Uri insertInTransaction(Uri uri, ContentValues values);

    /**
     * 抽象方法，事物中对update的处理，用于子类继承
     *
     * @param uri           通用资源标志符 代表要操作的数据
     * @param values        要更新的值
     * @param selection     更新条件
     * @param selectionArgs String[] 更新条件参数值
     * @return count 返回更新的条数
     */
    protected abstract int updateInTransaction(Uri uri, ContentValues values, String selection,
                                               String[] selectionArgs);

    /**
     * 抽象方法，事物中对delete的处理，用于子类继承
     *
     * @param uri           通用资源标志符 代表要操作的数据
     * @param selection     删除的条件
     * @param selectionArgs 删除的条件参数值
     * @return 返回删除的条数
     * @see ContentProvider#delete(Uri,
     * String, String[])
     */
    protected abstract int deleteInTransaction(Uri uri, String selection, String[] selectionArgs);

    /**
     * 当有数据内容变动时，通知变动的抽象方法，用于子类继承
     */
    protected abstract void notifyChange();

    /**
     * 返回一个SQLiteOpenHelper
     * @return SQLiteOpenHelper对象
     */
    protected SQLiteOpenHelper getDatabaseHelper()
    {
        return mOpenHelper;
    }

    protected void setDatabaseHelper(SQLiteOpenHelper openHelper)
    {
        mOpenHelper = openHelper;
    }

    /**
     * 判断是否在执行批处理
     *
     * @return 是否在执行批处理
     */
    private boolean applyingBatch()
    {
        return mApplyingBatch.get() != null && mApplyingBatch.get();
    }

    /**
     * 插入方法
     * 向数据库中插入一条数据
     * @param uri    通用资源标志符 代表要操作的数据
     * @param values ContentValues 插入的数据值
     * @return 插入后的uri描述
     * @see ContentProvider#insert(Uri, ContentValues)
     */
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        Uri result = null;
        if (!applyingBatch())
        {
            mDb = mOpenHelper.getWritableDatabase();
            mDb.beginTransactionWithListener(this);
            try
            {
                result = insertInTransaction(uri, values);
                // 操作成功标记有数据改动
                if (result != null)
                {
                    mNotifyChange = true;
                    mDb.setTransactionSuccessful();
                }
            }
            finally
            {
                mDb.endTransaction();
            }
            onEndTransaction();
        }
        else
        {
            result = insertInTransaction(uri, values);
            if (result != null)
            {
                mNotifyChange = true;
            }
        }
        return result;
    }

    /**
     * 批量插入方法
     * 向数据库中插入多条数据
     *
     * @param uri    通用资源标志符 代表要操作的数据
     * @param values 需要插入的数据数组
     * @return 插入数据的数量
     * @see ContentProvider#bulkInsert(Uri, ContentValues[])
     */
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        int numValues = values.length;
        mDb = mOpenHelper.getWritableDatabase();
        mDb.beginTransactionWithListener(this);
        try
        {
            for (int i = 0; i < numValues; i++)
            {
                Uri result = insertInTransaction(uri, values[i]);
                // 操作成功标记有数据改动
                if (result != null)
                {
                    mNotifyChange = true;
                }
                // 每插入一条记录都尝试一次让行
                mDb.yieldIfContendedSafely();
            }
            mDb.setTransactionSuccessful();
        }
        finally
        {
            mDb.endTransaction();
        }

        onEndTransaction();
        return numValues;
    }

    /**
     * 更新方法
     * 更新数据库中的数据
     *
     * @param uri           通用资源标志符 代表要操作的数据
     * @param values        更新的值
     * @param selection     更新的条件
     * @param selectionArgs 更新的条件值
     * @return 更新条目的数量
     * @see ContentProvider
     * #update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        int count = 0;
        boolean applyingBatch = applyingBatch();
        // 没有批处理执行时开始一个事物
        if (!applyingBatch)
        {
            mDb = mOpenHelper.getWritableDatabase();
            mDb.beginTransactionWithListener(this);
            try
            {
                count = updateInTransaction(uri, values, selection, selectionArgs);
                // 操作成功标记有数据改动
                if (count > 0)
                {
                    mNotifyChange = true;
                }
                mDb.setTransactionSuccessful();
            }
            finally
            {
                mDb.endTransaction();
            }

            onEndTransaction();
        }
        // 有批处理在执行时不需要另外开启事物
        else
        {
            count = updateInTransaction(uri, values, selection, selectionArgs);
            // 操作成功标记有数据改动
            if (count > 0)
            {
                mNotifyChange = true;
            }
        }

        return count;
    }

    /**
     * 删除方法<BR>
     * 删除数据库中的数据
     *
     * @param uri           通用资源标志符 代表要操作的数据
     * @param selection     删除的条件语句
     * @param selectionArgs 删除的条件值
     * @return 删除的条数
     * @see ContentProvider#delete(Uri, String, String[])
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        int count = 0;
        boolean applyingBatch = applyingBatch();
        // 没有批处理执行时开始一个事物
        if (!applyingBatch)
        {
            mDb = mOpenHelper.getWritableDatabase();
            mDb.beginTransactionWithListener(this);
            try
            {
                count = deleteInTransaction(uri, selection, selectionArgs);
                // 操作成功标记有数据改动
                if (count > 0)
                {
                    mNotifyChange = true;
                }
                mDb.setTransactionSuccessful();
            }
            finally
            {
                mDb.endTransaction();
            }

            onEndTransaction();
        }
        // 有批处理在执行时不需要另外开启事物
        else
        {
            count = deleteInTransaction(uri, selection, selectionArgs);
            // 操作成功标记有数据改动
            if (count > 0)
            {
                mNotifyChange = true;
            }
        }
        return count;
    }

    /**
     * 批量处理方法
     * 集中处理一批操作
     *
     * @param operations 需要执行的操作数组
     * @return 返回的操作结果数组
     * @throws OperationApplicationException OperationApplicationException
     * @see ContentProvider#applyBatch(ArrayList)
     */
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException
    {
        mDb = mOpenHelper.getWritableDatabase();
        mDb.beginTransactionWithListener(this);
        try
        {
            // 标记当前线程为正在执行批量处理
            mApplyingBatch.set(true);
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++)
            {
                final ContentProviderOperation operation = operations.get(i);
                results[i] = operation.apply(this, results, i);
            }
            mDb.setTransactionSuccessful();
            return results;
        }
        finally
        {
            // 标记当前线程不在执行批量处理
            mApplyingBatch.set(false);
            mDb.endTransaction();
            onEndTransaction();
        }
    }

    /**
     * 事物开始时回调给监听者的方法
     *
     * @see SQLiteTransactionListener#onBegin()
     */
    public void onBegin()
    {
        onBeginTransaction();
    }

    /**
     * 事物执行成功时回调给监听者的方法
     *
     * @see SQLiteTransactionListener#onCommit()
     */
    public void onCommit()
    {
        beforeTransactionCommit();
    }

    public void onRollback()
    {
    }

    /**
     * 事物开始时的操作
     */
    protected void onBeginTransaction()
    {
    }

    /**
     * 事物执行成功是的操作
     */
    protected void beforeTransactionCommit()
    {
    }

    /**
     * 事物执行结束后的操作
     */
    protected void onEndTransaction()
    {
        if (mNotifyChange)
        {
            mNotifyChange = false;
            notifyChange();
        }
    }
}
