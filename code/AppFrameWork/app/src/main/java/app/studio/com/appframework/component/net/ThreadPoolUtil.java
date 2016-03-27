/*
 * 文件名: ThreadUtil.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: l00124251
 * 创建时间:2013-6-29
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package app.studio.com.appframework.component.net;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工具类。<BR><BR>
 * @version [RCS Client V100R001C03, 2013-6-29]
 */
public class ThreadPoolUtil
{
    /**
     * 默认的初始化线程数
     */
    private static final int CORE_THREAD_COUNT = 5;
    
    /**
     * 默认的最大线程数
     */
    private static final int MAX_THREAD_COUNT = 10;
    
    /**
     * 默认的最大等待队列
     */
    private static final int MAX_QUEUE_COUT = 60;
    
    /**
     * 默认的队列大小
     */
    private static final int QUEUE_SIZE = 50;
    
    /**
     * 线程池对象
     */
    private static ThreadPoolExecutor tpe;
    
    /**
     * 私有构造方法，不许构造该对象
     */
    private ThreadPoolUtil()
    {
        
    }
    
    /**
     * 
     * 初始化线程池方法
     * @param coreThreadCount 线程池初始化线程个数
     * @param maxThreadCount 线程池最大的线程数
     * @param queueSize 队列长度
     */
    public static void init(int coreThreadCount, int maxThreadCount,
        int queueSize)
    {
        tpe =
            new ThreadPoolExecutor(coreThreadCount, maxThreadCount, MAX_QUEUE_COUT,
                TimeUnit.MILLISECONDS, new TaskQueue<Runnable>(queueSize));
    }
    
    /**
     * 
     * 执行任务方法。
     * 执行该方法前，请先初始化线程池，如果没有初始化线程池，则会默认为核心线程为10个，最大线程为50个，队列为50
     * 若核心线程都在执行，队列已满，且已达到最大线程，则添加不会成功
     * @param task 传入任务
     * @return 是否添加成功
     */
    public static boolean execute(ThreadTask task)
    {
        if (tpe == null)
        {
            //如果没有初始化，传入默认初始化参数
            init(CORE_THREAD_COUNT, MAX_THREAD_COUNT, QUEUE_SIZE);
        }
        try
        {
            tpe.execute(task);
        }
        catch (RejectedExecutionException e)
        {
            return false;
        }
        
        return true;
    }
    
    /**
     * 
     * 关闭线程池。<BR><BR>
     * @param shutDownNow 是否立即关闭
     */
    public static void shutDown(boolean shutDownNow)
    {
        if (shutDownNow)
        {
            tpe.shutdownNow();
        }
        else
        {
            tpe.shutdown();
        }
    }
}
