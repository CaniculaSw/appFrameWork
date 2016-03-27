/*
 * 文件名: BaseActivity.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 刘鲁宁
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package app.studio.com.appframework.framework.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import library.sswwm.com.framework.logic.BuilderImp;
import library.sswwm.com.framework.logic.ILBuilder;
import library.sswwm.com.framework.logic.ILogic;


public abstract class BaseFragmentActivity extends FragmentActivity
{

    protected String TAG = this.getClass().getSimpleName();

    /**
     * 该activity持有的handler类
     */
    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if (mHandler != null)
            {
                BaseFragmentActivity.this.handleStateMessage(msg);
            }
        }
    };

    /**
     * 缓存持有的logic对象的集合
     */
    private final Set<ILogic> mLogicSet = new HashSet<ILogic>();

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (!isInit())
        {
            setLogicBuilder(createLogicBuilder(this.getApplicationContext()));
            initSystem(getApplicationContext());
        }
        BuilderImp.instance.addHandlerToAllLogics(getHandler());
        try
        {
            initLogics();
        }
        catch (Exception e)
        {
            Log.e(TAG, "Init logics failed :" + e.getMessage(), e);
        }
    }

    /**
     * 获取hander对象<BR>
     *
     * @return 返回handler对象
     */
    protected final Handler getHandler()
    {
        return mHandler;
    }

    /**
     * 发送消息
     *
     * @param what 消息标识
     */
    protected final void sendEmptyMessage(int what)
    {
        if (mHandler != null)
        {
            mHandler.sendEmptyMessage(what);
        }
    }

    /**
     * 延迟发送空消息
     *
     * @param what        消息标识
     * @param delayMillis 延迟时间
     */
    protected final void sendEmptyMessageDelayed(int what, long delayMillis)
    {
        if (mHandler != null)
        {
            mHandler.sendEmptyMessageDelayed(what, delayMillis);
        }
    }

    /**
     * post一段操作到UI线程
     *
     * @param runnable Runnable
     */
    protected final void postRunnable(Runnable runnable)
    {
        if (mHandler != null)
        {
            mHandler.post(runnable);
        }
    }

    /**
     * 发送消息
     *
     * @param msg 消息对象
     */
    protected final void sendMessage(Message msg)
    {
        if (mHandler != null)
        {
            mHandler.sendMessage(msg);
        }
    }

    /**
     * 延迟发送消息
     *
     * @param msg         消息对象
     * @param delayMillis 延迟时间
     */
    protected final void sendMessageDelayed(Message msg, long delayMillis)
    {
        if (mHandler != null)
        {
            mHandler.sendMessageDelayed(msg, delayMillis);
        }
    }

    /**
     * activity是否已经初始化，加载了mLogicBuilder对象<BR>
     * 判断activiy中是否创建了mLogicBuilder对象
     *
     * @return 是否加载了mLogicBuilder
     */
    protected final boolean isInit()
    {
        return BuilderImp.instance != null;
    }

    /**
     * 系统的初始化方法<BR>
     *
     * @param context 系统的context对象
     */
    protected abstract void initSystem(Context context);

    /**
     * Logic建造管理类需要创建的接口<BR>
     * 需要子类继承后，指定Logic建造管理类具体实例
     *
     * @param context 系统的context对象
     * @return Logic建造管理类具体实例
     */
    protected abstract BuilderImp createLogicBuilder(Context context);

    /**
     * 获取全局的LogicBuilder对象<BR>
     *
     * @return 返回LogicBuilder对象
     */
    public static ILBuilder getLogicBuilder()
    {
        return BuilderImp.instance;
    }

    /**
     * 初始化logic的方法，由子类实现<BR>
     * 在该方法里通过getLogicByInterfaceClass获取logic对象
     */
    protected abstract void initLogics();

    /**
     * 通过接口类获取logic对象<BR>
     *
     * @param interfaceClass 接口类型
     * @return logic对象
     */
    protected final ILogic getLogicByInterfaceClass(Class<?> interfaceClass)
    {
        ILogic logic =
                BuilderImp.instance.getLogicByInterfaceClass(interfaceClass);
        if (null != logic && !mLogicSet.contains(logic))
        {
            logic.addHandler(getHandler());
            mLogicSet.add(logic);
        }
        if (logic == null)
        {
            Log.e(TAG, "Not found logic by interface class (" + interfaceClass
                    + ")", new Throwable());
            return null;
        }
        return logic;
    }

    /**
     * 设置全局的logic建造管理类<BR>
     *
     * @param logicBuilder logic建造管理类
     */
    protected static final void setLogicBuilder(BuilderImp logicBuilder)
    {
        BuilderImp.instance = logicBuilder;
    }

    /**
     * logic通过handler回调的方法<BR>
     * 通过子类重载可以实现各个logic的sendMessage到handler里的回调方法
     *
     * @param msg Message对象
     */
    protected void handleStateMessage(Message msg)
    {

    }

    public void finish()
    {
        removeHandler();
        super.finish();
    }

    protected void onDestroy()
    {
        removeHandler();
        super.onDestroy();
    }

    private void removeHandler()
    {
        if (this.mHandler != null)
        {
            if (mLogicSet.size() > 0)
            {
                for (ILogic logic : mLogicSet)
                {
                    logic.removeHandler(this.mHandler);
                }
            }
            else if (BuilderImp.instance != null)
            {
                BuilderImp.instance.removeHandlerToAllLogics(this.mHandler);
            }
            this.mHandler = null;
        }
    }
}
