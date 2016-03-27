package app.studio.com.appframework.component.log;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * 打印日志<BR>
 * 日志打印功能主要提供打印接口，其中支持模块打印到不同文件功能
 *
 * @author ssw
 * @version [Platform v1.0, 2015-7-21]
 */
public class LogWriter
{
    /**
     * 日志文件名后缀
     */
    private static final String LOG_EXTENSIONS = ".log";

    /**
     * 文件格式
     */
    private DateFormat fileFormat;

    /**
     * 打印文件的文件名排序
     */
    private final Comparator<File> c = new Comparator<File>()
    {
        public int compare(File f1, File f2)
        {
            return String.CASE_INSENSITIVE_ORDER.compare(f1.getName(),
                    f2.getName());
        }
    };

    /**
     * 当前打印的文件
     */
    private File current;

    /**
     * 当前打印的文件路径
     */
    private File currentDirectory;

    /**
     * 文件数量限制，默认值为2
     */
    private int fileAmount = 2;

    /**
     * 文件大小限制，默认值为1024*1024 1M大小
     */
    private long maxSize = 1048576;

    /**
     * 打印实体对象
     */
    private PrintWriter writer = null;

    /**
     * 构造打印任务的写对象
     *
     * @param directory  文件路径
     * @param fileAmount 文件数量限制
     * @param maxSize    文件大小限制
     */
    public LogWriter(File directory, int fileAmount, long maxSize)
    {
        currentDirectory = directory;
        this.fileAmount = fileAmount <= 0 ? this.fileAmount : fileAmount;
        this.maxSize = (maxSize <= 0) ? this.maxSize : maxSize;
        fileFormat =
                new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        initialize();
    }

    /**
     * 日志打印对象的初始化
     *
     * @return 返回
     */
    public synchronized boolean initialize()
    {
        if (!currentDirectory.exists())
        {
            boolean isMkdirsSucc = currentDirectory.mkdirs();
        }
        createWriter();
        return null != writer;
    }

    /**
     * 创建打印对象
     */
    public void createWriter()
    {
        try
        {
            File newFile =
                    new File(currentDirectory, fileFormat.format(new Date())
                            + LOG_EXTENSIONS);

            if (!newFile.exists())
            {
                boolean isCreateSucc = newFile.createNewFile();
            }

            ArrayList<File> historyLogs = getHistoryLogs();
            for (File file : historyLogs)
            {
                if (file.length() < maxSize)
                {
                    current = file;
                    if (!newFile.equals(file))
                    {
                        if (newFile.delete())
                        {
                            historyLogs.remove(newFile);
                        }
                    }
                    break;
                }
            }

            writer = new PrintWriter(new FileOutputStream(current, true), true);
            //打印开始日志
            printBegin();
            int size;
            while ((size = historyLogs.size()) > fileAmount)
            {
                if (!historyLogs.get(size - 1).equals(current)
                        && historyLogs.get(size - 1).delete())
                {
                    historyLogs.remove(size - 1);
                }
                else if (!historyLogs.get(0).equals(current)
                        && historyLogs.get(0).delete())
                {
                    historyLogs.remove(0);
                }
                else
                {
                    break;
                }
            }
        }
        catch (Exception e)
        {
            Log.e("LogWriter", "print log to file failed", e);
        }
    }

    /**
     * 获取已经存在的打印文件列表
     *
     * @return
     */
    private ArrayList<File> getHistoryLogs()
    {
        ArrayList<File> historyLogs;
        File[] fs = currentDirectory.listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String filename)
            {
                return filename.contains(LOG_EXTENSIONS);
            }
        });
        if (fs != null && fs.length != 0)
        {
            historyLogs = new ArrayList<File>(Arrays.asList(fs));
        }
        else
        {
            historyLogs = new ArrayList<File>();
        }
        Collections.sort(historyLogs, c);

        return historyLogs;
    }

    /**
     * 打开始日志
     */
    private void printBegin()
    {
        StringBuilder sbr = new StringBuilder();

        //输入起始内容
        sbr.append("Begin Time:");
        sbr = addCurrentTime(sbr);

        println(sbr.toString());
    }

    /**
     * 开放的打印信息的接口
     * [功能详细描述]
     *
     * @param msg 需要打印的信息
     */
    public void println(String msg)
    {
        if (null == writer)
        {
            initialize();
        }
        else
        {
            writer.println(msg);
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
        sbr.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(System.currentTimeMillis()));
        return sbr;
    }

    /**
     * 关闭打印写对象
     */
    public synchronized void close()
    {
        if (null != writer)
        {
            writer.close();
        }
    }

    /**
     * 判断当前文件是否怎在
     *
     * @return 返回值
     */
    public boolean isCurrentExist()
    {
        return null != current && current.exists();
    }

    /**
     * 文件未达到最大容量
     *
     * @return 文件是否未达到最大容量
     */
    public boolean isCurrentAvailable()
    {
        return null != current && current.length() < maxSize;
    }

    /**
     * 日志超过最大容量后，处理日志文件,生成一个新的；超过文件数目需要删除
     *
     * @return 新建结果
     */
    public boolean reCreateWriter()
    {
        writer.close();
        writer = null;
        createWriter();
        return null != writer;
    }

    /**
     * 清理除当前文件之外的其他日志文件
     *
     * @return 返回值
     */
    public boolean deleteTheEarliest()
    {
        for (File file : getHistoryLogs())
        {
            if (!file.equals(current) && file.delete())
            {
                return true;
            }
        }
        return false;
    }
}
