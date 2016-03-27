package app.studio.com.appframework.component.log;

import android.os.Environment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 打印日志<BR>
 * 日志打印功能主要提供打印接口，其中支持模块打印到不同文件功能
 *
 * @author ssw
 * @version [Platform v1.0, 2015-7-21]
 */
public class Logger
{

    /**
     * 本地日志打印优先级; 调用系统 Log.v.
     */
    public static final int VERBOSE = 2;

    /**
     * 本地日志打印优先级; 调用系统  Log.d.
     */
    public static final int DEBUG = 3;

    /**
     * 本地日志打印优先级; 调用系统  Log.i.
     */
    public static final int INFO = 4;

    /**
     * 本地日志打印优先级; 调用系统 Log.w.
     */
    public static final int WARN = 5;

    /**
     * 本地日志打印优先级; 调用系统 Log.e.
     */
    public static final int ERROR = 6;

    /**
     * 普通打印标示
     */
    public static final String LOG_TYPE_COMMON = "log_common";

    /**
     * voip模块专用打印标示
     */
    public static final String LOG_TYPE_VOIP = "log_voip";

    /**
     * 打印标签
     */
    private static final String TAG = "Logger";

    /**
     * 当前打印优先级别
     */
    private static int currentLevel = VERBOSE;

    /**
     * 日志打印的目录管理Map
     * 默认基础打印和VOIP打印   初始化路径
     */
    private static Map<String, String> logDirMap =
            new HashMap<String, String>()
            {
                {
                    put(LOG_TYPE_COMMON, Environment.getExternalStorageDirectory()
                            .getPath() + "/McsPlatform/log/common/");

                    put(LOG_TYPE_VOIP, Environment.getExternalStorageDirectory()
                            .getPath() + "/McsPlatform/log/voip/");
                }
            };

    /**
     * 增加操作 解决Map结构的多线程冲突问题
     */
    private static final int ADD_OPERATION = 1;

    /**
     * 删除操作 解决Map结构的多线程冲突问题
     */
    private static final int DEL_OPERATION = 2;

    /**
     * 修改操作 解决Map结构的多线程冲突问题
     */
    private static final int MOD_OPERATION = 3;

    /**
     * 清楚操作 解决Map结构的多线程冲突问题
     */
    private static final int CLE_OPERATION = 4;

    /**
     * 日志打印的任务对象管理
     */
    private static Map<String, LogTask> logTaskMap =
            new HashMap<String, LogTask>();

    /**
     * 普通打印文件路径，可以调用方法进行设置
     */
    private static String logCommonDir =
            Environment.getExternalStorageDirectory().getPath()
                    + "/log/common/";

    /**
     * 日志打印开关
     */
    private static boolean isLoggerable = true;

    /**
     * LogCat是否打印
     */
    private static boolean isLogCatable = true;

    /**
     * 打印文件数量限制数
     */
    private static int fileAmount = 5;

    /**
     * 文件大小限制1024*1024
     */
    private static long fileMaxSize = 1048576;

    /**
     * 默认构造函数
     */
    private Logger()
    {
    }

    /**
     * 设置普通打印的路径
     *
     * @param commonDir 全路径名称
     */
    public static void setLogCommonDir(String commonDir)
    {
        logCommonDir = commonDir;
        opLogDirMap(LOG_TYPE_COMMON, commonDir, MOD_OPERATION);
    }

    /**
     * 增加打印对象的方法，可以支持特定模块的特定打印路径
     *
     * @param logTypeName 增加的打印类型
     * @param logSavaPath 增加打印类型的存储路径
     */
    public static void addLogType(String logTypeName, String logSavaPath)
    {
        if (logDirMap != null && logDirMap.containsKey(logTypeName))
        {
            opLogDirMap(logTypeName, logSavaPath, MOD_OPERATION);
        }
        else if (logDirMap != null && !logDirMap.containsKey(logTypeName))
        {
            opLogDirMap(logTypeName, logSavaPath, ADD_OPERATION);
        }
    }

    /**
     * 同步操作打印目录的MAP表
     *
     * @param logType 打印类型
     * @param dir     打印类型对应的存储路径
     * @param type    操作类型
     */
    private static synchronized void opLogDirMap(
            String logType, String dir,
            int type
    )
    {
        switch (type)
        {
            case ADD_OPERATION:
                if (logDirMap != null)
                {
                    logDirMap.put(logType, dir);
                }
                break;
            case DEL_OPERATION:
                if (logDirMap != null)
                {
                    logDirMap.remove(logType);
                }
                break;
            case MOD_OPERATION:
                if (logDirMap != null)
                {
                    logDirMap.remove(logType);
                    logDirMap.put(logType, dir);
                }
                break;
            case CLE_OPERATION:
                if (logDirMap != null)
                {
                    logDirMap.clear();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 同步操作打印任务的MAP表
     *
     * @param logType 打印类型
     * @param task    打印类型对应的任务
     * @param type    操作类型
     */
    private static synchronized void opLogTaskMap(
            String logType, LogTask task,
            int type
    )
    {
        switch (type)
        {
            case ADD_OPERATION:
                if (logTaskMap != null)
                {
                    logTaskMap.put(logType, task);
                }
                break;
            case DEL_OPERATION:
                if (logTaskMap != null)
                {
                    logTaskMap.remove(logType);
                }
                break;
            case MOD_OPERATION:
                if (logTaskMap != null)
                {
                    logTaskMap.remove(logType);
                    logTaskMap.put(logType, task);
                }
                break;
            case CLE_OPERATION:
                if (logTaskMap != null)
                {
                    logTaskMap.clear();
                }
                break;
            default:
                break;
        }
    }

    /**
     * VERBOSE优先级别打印日志，默认是正常打印类型的日志
     *
     * @param tag 打印TAG
     * @param msg 打印信息
     * @return 返回
     */
    public static int v(String tag, String msg)
    {
        return v(tag, msg, LOG_TYPE_COMMON);
    }

    /**
     * VERBOSE优先级别打印日志
     *
     * @param tag     打印TAG
     * @param msg     打印信息
     * @param logType 指定打印类型  内部默认支持（LOG_TYPE_COMMON、LOG_TYPE_VOIP）
     * @return 返回
     */
    public static int v(String tag, String msg, String logType)
    {
        return println(VERBOSE, tag, msg, logType);
    }

    /**
     * VERBOSE优先级别打印日志, 默认是正常打印类型的日志
     *
     * @param tag 打印TAG
     * @param msg 打印信息
     * @param tr  异常堆栈
     * @return 返回
     */
    public static int v(String tag, String msg, Throwable tr)
    {
        return v(tag, msg, tr, LOG_TYPE_COMMON);
    }

    /**
     * VERBOSE优先级别打印日志, 默认是正常打印类型的日志
     *
     * @param tag 打印TAG
     * @param tr  异常堆栈
     * @return 返回
     */
    public static int v(String tag, Throwable tr)
    {
        return println(VERBOSE, tag, getStackTraceString(tr), LOG_TYPE_COMMON);
    }

    /**
     * VERBOSE优先级别打印日志
     *
     * @param tag     打印TAG
     * @param msg     打印信息
     * @param tr      异常堆栈
     * @param logType 指定打印类型  内部默认支持（LOG_TYPE_COMMON、LOG_TYPE_VOIP）
     * @return 返回
     */
    public static int v(String tag, String msg, Throwable tr, String logType)
    {
        return println(VERBOSE,
                tag,
                msg + '\n' + getStackTraceString(tr),
                logType);
    }

    /**
     * DEBUG优先级别打印日志，默认是正常打印类型的日志
     *
     * @param tag 打印TAG
     * @param msg 打印信息
     * @return 返回
     */
    public static int d(String tag, String msg)
    {
        return d(tag, msg, LOG_TYPE_COMMON);
    }

    /**
     * DEBUG优先级别打印日志，默认是正常打印类型的日志<BR>
     *
     * @param moduleName 模块名称
     * @param flowName   流程名称
     * @param className  类名
     * @param desc       描述信息
     * @return 返回
     */
    public static int d(
            String moduleName, String flowName, String className,
            String desc
    )
    {
        StringBuilder msg = new StringBuilder("|");
        msg.append(moduleName).append("|");
        msg.append(flowName).append("|");
        msg.append(className).append("|");
        msg.append(desc).append("|");
        return d(className, msg.toString(), LOG_TYPE_COMMON);
    }

    /**
     * DEBUG优先级别打印日志
     *
     * @param tag     打印TAG
     * @param msg     打印信息
     * @param logType 指定打印类型  内部默认支持（LOG_TYPE_COMMON、LOG_TYPE_VOIP）
     * @return 返回
     */
    public static int d(String tag, String msg, String logType)
    {
        return println(DEBUG, tag, msg, logType);
    }

    /**
     * DEBUG优先级别打印日志, 默认是正常打印类型的日志
     *
     * @param tag 打印TAG
     * @param msg 打印信息
     * @param tr  异常堆栈
     * @return 返回
     */
    public static int d(String tag, String msg, Throwable tr)
    {
        return d(tag, msg, tr, LOG_TYPE_COMMON);
    }

    /**
     * DEBUG优先级别打印日志, 默认是正常打印类型的日志
     *
     * @param tag 打印TAG
     * @param tr  异常堆栈
     * @return 返回
     */
    public static int d(String tag, Throwable tr)
    {
        return println(DEBUG, tag, getStackTraceString(tr), LOG_TYPE_COMMON);
    }

    /**
     * DEBUG优先级别打印日志
     *
     * @param tag     打印TAG
     * @param msg     打印信息
     * @param tr      异常堆栈
     * @param logType 指定打印类型  内部默认支持（LOG_TYPE_COMMON、LOG_TYPE_VOIP）
     * @return 返回
     */
    public static int d(String tag, String msg, Throwable tr, String logType)
    {
        return println(DEBUG,
                tag,
                msg + '\n' + getStackTraceString(tr),
                logType);
    }

    /**
     * INFO优先级别打印日志，默认是正常打印类型的日志
     *
     * @param tag 打印TAG
     * @param msg 打印信息
     * @return 返回
     */
    public static int i(String tag, String msg)
    {
        return i(tag, msg, LOG_TYPE_COMMON);
    }

    /**
     * INFO优先级别打印日志
     *
     * @param tag     打印TAG
     * @param msg     打印信息
     * @param logType 指定打印类型  内部默认支持（LOG_TYPE_COMMON、LOG_TYPE_VOIP）
     * @return 返回
     */
    public static int i(String tag, String msg, String logType)
    {
        return println(INFO, tag, msg, logType);
    }

    /**
     * INFO优先级别打印日志, 默认是正常打印类型的日志
     *
     * @param tag 打印TAG
     * @param msg 打印信息
     * @param tr  异常堆栈
     * @return 返回
     */
    public static int i(String tag, String msg, Throwable tr)
    {
        return i(tag, msg, tr, LOG_TYPE_COMMON);
    }

    /**
     * INFO优先级别打印日志, 默认是正常打印类型的日志
     *
     * @param tag 打印TAG
     * @param tr  异常堆栈
     * @return 返回
     */
    public static int i(String tag, Throwable tr)
    {
        return println(INFO, tag, getStackTraceString(tr), LOG_TYPE_COMMON);
    }

    /**
     * INFO优先级别打印日志
     *
     * @param tag     打印TAG
     * @param msg     打印信息
     * @param tr      异常堆栈
     * @param logType 指定打印类型  内部默认支持（LOG_TYPE_COMMON、LOG_TYPE_VOIP）
     * @return 返回
     */
    public static int i(String tag, String msg, Throwable tr, String logType)
    {
        return println(INFO, tag, msg + '\n' + getStackTraceString(tr), logType);
    }

    /**
     * WARN优先级别打印日志，默认是正常打印类型的日志
     *
     * @param tag 打印TAG
     * @param msg 打印信息
     * @return 返回
     */
    public static int w(String tag, String msg)
    {
        return w(tag, msg, LOG_TYPE_COMMON);
    }

    /**
     * WARN优先级别打印日志
     *
     * @param tag     打印TAG
     * @param msg     打印信息
     * @param logType 指定打印类型  内部默认支持（LOG_TYPE_COMMON、LOG_TYPE_VOIP）
     * @return 返回
     */
    public static int w(String tag, String msg, String logType)
    {
        return println(WARN, tag, msg, logType);
    }

    /**
     * WARN优先级别打印日志, 默认是正常打印类型的日志
     *
     * @param tag 打印TAG
     * @param msg 打印信息
     * @param tr  异常堆栈
     * @return 返回
     */
    public static int w(String tag, String msg, Throwable tr)
    {
        return w(tag, msg, tr, LOG_TYPE_COMMON);
    }

    /**
     * WARN优先级别打印日志, 默认是正常打印类型的日志
     *
     * @param tag 打印TAG
     * @param tr  异常堆栈
     * @return 返回
     */
    public static int w(String tag, Throwable tr)
    {
        return println(WARN, tag, getStackTraceString(tr), LOG_TYPE_COMMON);
    }

    /**
     * WARN优先级别打印日志
     *
     * @param tag     打印TAG
     * @param msg     打印信息
     * @param tr      异常堆栈
     * @param logType 指定打印类型  内部默认支持（LOG_TYPE_COMMON、LOG_TYPE_VOIP）
     * @return 返回
     */
    public static int w(String tag, String msg, Throwable tr, String logType)
    {
        return println(WARN, tag, msg + '\n' + getStackTraceString(tr), logType);
    }

    /**
     * ERROR优先级别打印日志，默认是正常打印类型的日志
     *
     * @param tag 打印TAG
     * @param msg 打印信息
     * @return 返回
     */
    public static int e(String tag, String msg)
    {
        return e(tag, msg, LOG_TYPE_COMMON);
    }

    /**
     * ERROR优先级别打印日志
     *
     * @param tag     打印TAG
     * @param msg     打印信息
     * @param logType 指定打印类型  内部默认支持（LOG_TYPE_COMMON、LOG_TYPE_VOIP）
     * @return 返回
     */
    public static int e(String tag, String msg, String logType)
    {
        return println(ERROR, tag, msg, logType);
    }

    /**
     * ERROR优先级别打印日志, 默认是正常打印类型的日志
     *
     * @param tag 打印TAG
     * @param msg 打印信息
     * @param tr  异常堆栈
     * @return 返回
     */
    public static int e(String tag, String msg, Throwable tr)
    {
        return e(tag, msg, tr, LOG_TYPE_COMMON);
    }

    /**
     * ERROR优先级别打印日志, 默认是正常打印类型的日志
     *
     * @param tag 打印TAG
     * @param tr  异常堆栈
     * @return 返回
     */
    public static int e(String tag, Throwable tr)
    {
        return println(ERROR, tag, getStackTraceString(tr), LOG_TYPE_COMMON);
    }

    /**
     * ERROR优先级别打印日志
     *
     * @param tag     打印TAG
     * @param msg     打印信息
     * @param tr      异常堆栈
     * @param logType 指定打印类型  内部默认支持（LOG_TYPE_COMMON、LOG_TYPE_VOIP）
     * @return 返回
     */
    public static int e(String tag, String msg, Throwable tr, String logType)
    {
        return println(ERROR,
                tag,
                msg + '\n' + getStackTraceString(tr),
                logType);
    }

    /**
     * 获取异常的打印信息
     *
     * @param tr 异常
     * @return
     */
    private static String getStackTraceString(Throwable tr)
    {
        return android.util.Log.getStackTraceString(tr);
    }

    /**
     * 打印实体方法
     *
     * @param level   打印优先级
     * @param tag     打印标签
     * @param msg     打印信息
     * @param logType 打印类型
     * @return 返回
     */
    private static synchronized int println(
            int level, String tag, String msg,
            String logType
    )
    {
        int result = 0;
        if (!isLoggable(level))
        {
            return result;
        }
        else if (isLoggable(level) && isLogCatable())
        {
            result = android.util.Log.println(level, tag, msg);
        }

        if (!Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED))
        {
            android.util.Log.e(TAG, "SD Card is unavailable.");
            return result;
        }

        LogTask task;
        String logTypePath;

        if (logType != null)
        {
            //检查当前打印类型是否已配置路径
            logTypePath = logDirMap.get(logType);
        }
        else
        {
            logTypePath = null;
        }

        if (logTypePath != null)
        {
            task = logTaskMap.get(logType);
            if (task == null)
            {
                task = new LogTask(logTypePath, fileAmount, fileMaxSize);
                opLogTaskMap(logType, task, ADD_OPERATION);
            }
        }
        else
        {
            //传入的打印类型没有配置过，则当成普通打印进行
            task = logTaskMap.get(LOG_TYPE_COMMON);
            if (task == null)
            {
                task = new LogTask(logCommonDir, fileAmount, fileMaxSize);
                opLogTaskMap(LOG_TYPE_COMMON, task, ADD_OPERATION);
            }
        }

        if (!task.isStarted())
        {
            task.start(logType);
        }

        if (isLoggable(level))
        {
            long id = Thread.currentThread().getId();
            StackTraceElement element =
                    Thread.currentThread().getStackTrace()[5];
            String methodName = element.getMethodName();
            int lineNumber = element.getLineNumber();
            String fileName = element.getFileName();
            task.write(levelString(level),
                    tag,
                    msg,
                    id,
                    methodName,
                    fileName,
                    lineNumber,
                    logType);
        }
        return result;
    }

    /**
     * 对当前打印优先级设置，判断是否需要对信息进行打印
     *
     * @param level 打印优先级
     * @return
     */
    private static boolean isLoggable(int level)
    {
        return (level >= currentLevel) && isLoggerable;
    }

    /**
     * 判断当前是否需要打印Logcat
     *
     * @return 返回是否进行logcat打印
     */
    private static boolean isLogCatable()
    {
        return isLogCatable;
    }

    /**
     * 打印信息优先级字符
     *
     * @param level 优先级
     * @return 返回字符串
     */
    private static String levelString(int level)
    {
        switch (level)
        {
            case Logger.VERBOSE:
                return "Ver";
            case Logger.DEBUG:
                return "Deb";
            case Logger.INFO:
                return "Inf";
            case Logger.WARN:
                return "War";
            case Logger.ERROR:
                return "Err";
            default:
                return "Default";
        }
    }

    /**
     * 停止指定类型的打印任务
     *
     * @param logType 指定打印类型  内部默认支持（LOG_TYPE_COMMON、LOG_TYPE_VOIP）
     */
    public static void stopLog(String logType)
    {
        i(TAG, "logger stopLog logType = " + logType);
        LogTask task = logTaskMap.get(logType);
        if (task != null)
        {
            task.stop();
        }
        opLogTaskMap(logType, task, DEL_OPERATION);
    }

    /**
     * 停止所有打印任务
     */
    public static void stopLog()
    {
        i(TAG, "logger stopLog");
        Iterator it = logTaskMap.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            Object value = entry.getValue();
            LogTask task = (LogTask) value;
            task.stop();
        }
        opLogTaskMap(null, null, CLE_OPERATION);
    }

    /**
     * 设置文件日志最大数
     *
     * @param fileamount 文件数 当前默认 5个
     */
    public static void setFileAmount(int fileamount)
    {
        fileAmount = fileamount;
    }

    /**
     * 设置单个打印文件的大小
     *
     * @param fileMaxsize 文件大小 当前默认1024*1024
     */
    public static void setFileMaxSize(long fileMaxsize)
    {
        fileMaxSize = fileMaxsize;
    }

    /**
     * 设置日志打印开关项，默认值为TRUE
     *
     * @param isLogable 开关值
     */
    public static void setLogable(boolean isLogable)
    {
        Logger.isLoggerable = isLogable;
    }

    /**
     * 设置打印级别
     *
     * @param level 打印级别
     *              VERBOSE = 1;
     *              DEBUG = 2;
     *              INFO = 3;
     *              WARN = 4;
     *              ERROR = 5;
     */
    public static void setLogLevel(int level)
    {
        if (level > ERROR)
        {
            currentLevel = ERROR;
        }
        else if (level < VERBOSE)
        {
            currentLevel = VERBOSE;
        }
        else
        {
            currentLevel = level;
        }
    }

    /**
     * 设置Logcat日志是否打印
     *
     * @param isLogcatable logcat打印标记
     */
    public static void setLogCatable(boolean isLogcatable)
    {
        isLogCatable = isLogcatable;
    }
}
