/*
 * 文件名: ThreadTask.java
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

public abstract class ThreadTask implements Comparable<ThreadTask>, Runnable
{
    
    /**
     * 高优先级
     */
    public static final int MAX_PRIORITY = 3;
    
    /**
     * 一般优先级
     */
    public static final int NORMAL_PRIORITY = 2;
    
    /**
     * 低优先级
     */
    public static final int LOW_PRIORITY = 1;
    
    /**
     * 序列
     */
    private static final long serialVersionUID = 3307064726710802147L;
    
    /**
     * 该任务优先级   
     */
    private int priority;
    
    /**
     * 
     * 通过优先级，构造任务
     * @param priority 优先级
     */
    public ThreadTask(int priority)
    {
        this.priority = priority;
    }
    
    /**
     *  构造任务，默认优先级为一般
     */
    public ThreadTask()
    {
        this.priority = NORMAL_PRIORITY;
    }
    
    /**
     * 重写比较方法
     * {@inheritDoc}
     */
    public int compareTo(ThreadTask t)
    {
        if (priority > t.priority)
        {
            return -1;
        }
        else if (priority < t.priority)
        {
            return 1;
        }
        
        if (this.equals(t))
        {
            return 0;
        }
        return 1;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null)
        {
            return false;
        }
        
        if (getClass() != o.getClass())
        {
            return false;
        }
        ThreadTask t = (ThreadTask) o;
        if (priority == t.priority)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
