package app.studio.com.appframework.component.log;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 打印日志<BR>
 * 日志打印功能主要提供打印接口，其中支持模块打印到不同文件功能
 *
 * @author ssw
 * @version [Platform v1.0, 2015-7-21]
 */
public class LogTask
{
    /**
     * SD卡可存储的最小空间要求：50M
     */
    private static final long MIX_SIZE = 50 * 1024 * 1024;

    /**
     * 打印次数记录，主要为了提高效率，不用每次都去检查外存设备空间是否满足要求
     */
    private static int printCount = 0;

    /**
     * 再次进行外村设备空间检查的打印次数
     */
    private static final int PRINT_COUNT = 200;

    /**
     * 打印标记
     */
    private static final String TAG = "LogTask";

    /**
     * 消息队列
     */
    private final BlockingQueue<String> queue =
            new LinkedBlockingQueue<String>();

    /**
     * 标记当前打印任务是否启动
     */
    private volatile boolean started;

    /**
     * 打印日志的线程
     */
    private volatile Thread logWorkerThread;

    /**
     * 打印日志的写对象
     */
    private LogWriter logWriter = null;

    /**
     * 构造打印任务
     *
     * @param filePath   文件保存路径
     * @param fileAmount 文件数量限制
     * @param maxSize    文件大小限制
     */
    public LogTask(String filePath, int fileAmount, long maxSize)
    {
        this.logWriter = new LogWriter(new File(filePath), fileAmount, maxSize);
    }

    /**
     * 启动打印任务
     *
     * @param logType 打印任务类型
     */
    public synchronized void start(String logType)
    {
        if (null == logWorkerThread)
        {
            logWorkerThread =
                    new Thread(new LogRunnable(), "Log Task Thread - " + logType);
        }
        if (started || !logWriter.initialize())
        {
            return;
        }
        Log.v(TAG, "Log Task instance is starting ...");
        started = true;
        logWorkerThread.start();
        Log.v(TAG, "Log Task instance is started");
    }

    /**
     * 停止打印任务
     */
    public synchronized void stop()
    {
        Log.v(TAG, "Log Task instance is stopping...");
        started = false;
        if (null != logWorkerThread)
        {
            logWorkerThread.interrupt();
            logWorkerThread = null;
        }
        queue.clear();
        logWriter.close();
        Log.v(TAG, "Log Task instance is stopped");
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     *
     * @author 盛兴亚
     * @version [RCS Client V100R001C03, 2012-2-15]
     */
    private final class LogRunnable implements Runnable
    {
        public LogRunnable()
        {
        }

        private void dealMsg()
        throws InterruptedException
        {
            String msg = null;
            while (started && !Thread.currentThread().isInterrupted())
            {
                msg = queue.take();
                synchronized (logWriter)
                {
                    if (isExternalMemoryAvailable(MIX_SIZE))
                    {
                        //判断当前的打印文件是否存在
                        if (!logWriter.isCurrentExist())
                        {
                            Log.i(TAG, "LogWriter initialize");
                            if (!logWriter.initialize())
                            {
                                continue;
                            }
                        }
                        else if (!logWriter.isCurrentAvailable())
                        {
                            Log.i(TAG, "current is reCreateWriter...");
                            if (!logWriter.reCreateWriter())
                            {
                                continue;
                            }
                        }
                        logWriter.println(msg);
                    }
                    else
                    {
                        logWriter.deleteTheEarliest();
                        Log.e(TAG, "can't log into sdcard.");
                    }
                }
            }
        }

        public void run()
        {
            try
            {
                dealMsg();
            }
            catch (InterruptedException e)
            {
                Log.e(TAG, Thread.currentThread().toString(), e);
            }
            catch (RuntimeException e)
            {
                Log.e(TAG, Thread.currentThread().toString(), e);
                logWorkerThread =
                        new Thread(new LogRunnable(), "Log Task Thread");

            }
            finally
            {
                Log.v(TAG, "Log Task Thread is terminated.");
                started = false;
            }
        }
    }

    /**
     * 判断当前日志是否满足设备的空间要求
     *
     * @param size 最小空间大小要求
     * @return 返回值
     */
    private static boolean isExternalMemoryAvailable(long size)
    {
        //规格要求：
        //无存储卡，不记录日志
        //存储卡剩余空间《 50M，不记录日志。
        //存储卡剩余空间>=50M，日志文件总占用空间不超过4M。
        if (printCount > 0 && printCount < PRINT_COUNT)
        {
            //打印PRINT_COUNT次数之内，不去检查空间满足情况
            printCount++;
            return true;
        }
        else
        {
            long availableMemory = getAvailableExternalMemorySize();
            if (size > availableMemory)
            {
                //表明已经存在存储外设空间
                printCount = 0;
                return false;
            }
            else
            {
                //满足存储设备的空间需要，开始循环
                printCount = 1;
                return true;
            }
        }
    }

    /**
     * 获取设备可用的存储空间大小
     */
    private static long getAvailableExternalMemorySize()
    {
        if (externalMemoryAvailable())
        {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        }
        else
        {
            return -1;
        }
    }

    /**
     * 获取设备是否存在外设存储卡
     */
    private static boolean externalMemoryAvailable()
    {
        return Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 打印日志任务是否启动
     *
     * @return 返回值
     */
    public boolean isStarted()
    {
        return started;
    }

    /**
     * 打印内容拼接
     *
     * @param level      打印优先级
     * @param tag        打印标记
     * @param msg        打印信息
     * @param id         PID
     * @param methodName 方法名
     * @param fileName   文件名
     * @param lineNumber 代码行数
     * @param type       日志分类
     */
    public void write(
            String level, String tag, String msg, long id,
            String methodName, String fileName, int lineNumber, String type
    )
    {
        StringBuilder sbr = new StringBuilder();
        //时间
        addCurrentTime(sbr);

        //级别
        sbr.append(" [").append(level).append(']');

        //进程和线程
        sbr.append(" [")
                .append(android.os.Process.myPid())
                .append("|")
                .append(id)
                .append(']');

        //        sbr.append(" [")
        //                .append(fileName)
        //                .append("|")
        //                .append(lineNumber)
        //                .append(']');
        //类名方法名
        sbr.append(" [")
                .append(fileName)
                .append("|")
                .append(methodName)
                .append(']');

        //消息非空的时候输入
        if (msg != null && !msg.equals(""))
        {
            sbr.append("  ").append(tag).append(": ").append(msg);
        }
        write(sbr.toString());
    }

    /**
     * 打印信息写入队列
     *
     * @param msg 打印信息
     */
    public void write(String msg)
    {
        if (started)
        {
            try
            {
                queue.put(msg);
            }
            catch (InterruptedException e)
            {
                Log.e("LogCache", "", e);
            }
        }
    }

    /**
     * 添加时间
     *
     * @param sbr StringBuilder待添加
     * @return 添加过后的StringBuilder
     */
    private StringBuilder addCurrentTime(StringBuilder sbr)
    {
        if (null == sbr)
        {
            return null;
        }
        //添加时间,格式:YYYY-MM-DD HH-MM-SS.mmm
        sbr.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date()));
        return sbr;
    }
}
