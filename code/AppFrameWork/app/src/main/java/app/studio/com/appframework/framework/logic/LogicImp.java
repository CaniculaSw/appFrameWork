/*
 * 文件名: BaseLogic.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: logic抽象类，所有的业务实现logic必须继承
 * 创建人: 刘鲁宁
 * 创建时间:Feb 11, 2012
 *
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package app.studio.com.appframework.framework.logic;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.util.List;
import java.util.Vector;

public abstract class LogicImp implements ILogic
{
    /**
     * 日志标记
     */
    protected String TAG = this.getClass().getSimpleName();

    /**
     * logic对象中UI监听的handler缓存集合
     */
    private final List<Handler> mHandlerList = new Vector<Handler>();

    /**
     * 系统的context对象
     */
    private Context mContext;

    private Handler mHandler = new MyHander();

    /**
     * 初始化方法
     * 在被系统管理的logic在注册到LogicBuilder中后立即被调用的初始化方法。
     *
     * @param context 系统的context对象
     */
    public void init(Context context)
    {
        this.mContext = context;
    }

    /**
     * 对logic增加handler
     * 在logic对象里加入UI的handler
     *
     * @param handler UI传入的handler对象
     */
    public final void addHandler(Handler handler)
    {
        mHandlerList.add(handler);
    }

    /**
     * 对logic移除handler<BR>
     * 在logic对象里移除UI的handler
     *
     * @param handler UI传入的handler对象
     */
    public final void removeHandler(Handler handler)
    {
        mHandlerList.remove(handler);
    }


    /**
     * 发送消息给UI
     * 通过监听回调，通知在该logic对象中所有注册了handler的UI消息message对象
     *
     * @param what 返回的消息标识
     * @param obj  返回的消息数据对象
     */
    public void sendMessage(int what, Object obj)
    {
        synchronized (mHandlerList)
        {
            for (Handler handler : mHandlerList)
            {
                if (obj == null)
                {
                    handler.sendEmptyMessage(what);
                }
                else
                {
                    Message message = new Message();
                    message.what = what;
                    message.obj = obj;
                    handler.sendMessage(message);
                }
            }
        }
    }

    /**
     * 发送无数据对象消息给UI
     * 通过监听回调，通知在该logic对象中所有注册了handler的UI消息message对象
     *
     * @param what 返回的消息标识
     */
    public void sendEmptyMessage(int what)
    {
        synchronized (mHandlerList)
        {
            for (Handler handler : mHandlerList)
            {
                handler.sendEmptyMessage(what);
            }
        }
    }

    /**
     * 延迟发送空消息给UI
     * 通过监听回调，延迟通知在该logic对象中所有注册了handler的UI消息message对象
     *
     * @param what        返回的消息标识
     * @param delayMillis 延迟时间，单位秒
     */
    public void sendEmptyMessageDelayed(int what, long delayMillis)
    {
        if (!mHandler.hasMessages(what))
        {
            mHandler.sendEmptyMessageDelayed(what, delayMillis);
        }

    }

    /**
     * 延迟发送消息给UI
     * 通过监听回调，延迟通知在该logic对象中所有注册了handler的UI消息message对象
     *
     * @param what        返回的消息标识
     * @param obj         返回的消息数据对象
     * @param delayMillis 延迟时间，单位秒
     */
    public void sendMessageDelayed(int what, Object obj, long delayMillis)
    {
        if (!mHandler.hasMessages(what))
        {
            Message msg = new Message();
            msg.what = what;
            msg.obj = obj;
            mHandler.sendMessageDelayed(msg, delayMillis);
        }
    }

    private class MyHander extends Handler
    {
        public void handleMessage(Message msg)
        {
            synchronized (mHandlerList)
            {
                for (Handler handler : mHandlerList)
                {
                    if (!handler.hasMessages(msg.what))
                    {
                        if (msg.obj == null)
                        {
                            handler.sendEmptyMessage(msg.what);
                        }
                        else
                        {
                            Message message = new Message();
                            message.what = msg.what;
                            message.obj = msg.obj;
                            handler.sendMessage(message);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取上下文
     *
     * @return Context
     */
    public Context getContext()
    {
        return this.mContext;
    }
}
