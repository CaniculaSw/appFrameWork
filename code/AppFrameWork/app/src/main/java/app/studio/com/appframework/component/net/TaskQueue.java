/*
 * 文件名: TaskQueue.java
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

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 线程池使用的有序排序队列。<BR><BR>
 * @param <Runnable> 需要执行的任务
 */
public class TaskQueue<Runnable> extends PriorityBlockingQueue<Runnable>
{
    /**
     * 序列号
     */
    private static final long serialVersionUID = 5822298538545376529L;
    /**
     * 线程池等待队列的最大数量
     */
    private int initialCapacity;
    
    /**
     * 传入队列大小构造等待队列
     * @param initialCapacity 队列长度
     */
    public TaskQueue(int initialCapacity)
    {
        
        super(initialCapacity);
        this.initialCapacity = initialCapacity;
    }
    
    /**
     * 重载添加方法，使无界队列变为有界队列
     * {@inheritDoc}
     */
    @Override
    public boolean offer(Runnable o)
    {
        
        if (this.size() < initialCapacity)
        {
            return super.offer(o);
        }
        return false;
    }
    
    /**
     * 重载添加方法，使无界队列变为有界队列
     * {@inheritDoc}
     */
    @Override
    public boolean add(Runnable o)
    {
        if (this.size() < initialCapacity)
        {
            return super.add(o);
        }
        return false;
    }

    /**
     * 重载添加方法，使无界队列变为有界队列
     * {@inheritDoc}
     */
    @Override
    public boolean offer(Runnable o, long timeout, TimeUnit unit)
    {
        if (this.size() < initialCapacity)
        {
            return super.offer(o, timeout, unit);
        }
        return false;
    }
    
}
