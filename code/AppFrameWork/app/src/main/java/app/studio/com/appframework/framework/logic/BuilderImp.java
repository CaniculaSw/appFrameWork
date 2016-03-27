/*
 * 文件名: BaseLogicBuilder.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
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
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class BuilderImp implements ILBuilder
{

    public static BuilderImp instance;

    private static final String TAG = "BaseLogicBuilder";

    /**
     * 对所有logic进行管理的缓存
     */
    private Map<String, ILogic> mLogicCache = new HashMap<String, ILogic>();

    /**
     * 构造方法，首先执行BaseLogicBuilder子类的init方法，然后对所有logic进行初始化。
     *
     * @param context 系统的context对象
     */
    protected BuilderImp(Context context)
    {
        init(context);
        initAllLogics(context);
    }

    /**
     * 初始化所有的logic对象<BR>
     * 在该方法遍历缓存中所有的logic对象，执行所有logic的init方法
     *
     * @param context 系统的context对象
     */
    private void initAllLogics(Context context)
    {
        Set<Entry<String, ILogic>> logics = mLogicCache.entrySet();
        for (Entry<String, ILogic> logicEntry : logics)
        {
            ILogic logic = logicEntry.getValue();
            logicEntry.getValue().init(context);
        }
    }

    /**
     * 根据接口类移除缓存中logic对象
     * 移除缓存中的logic对象<BR>
     * 根据接口类,可以将缓存中集中管理的logic对象移除
     *
     * @param interfaceClass logic的接口类
     */
    public void removeLogic(Class<?> interfaceClass)
    {
        mLogicCache.remove(interfaceClass.getName());
    }

    /**
     * 根据接口类注册对应的logic对象到管理缓存中<BR>
     * 一般在子类的init方法中被执行，否则logic对象的init方法就不被调用
     *
     * @param interfaceClass logic的接口类
     * @param logic          ILogic的实现类
     */
    protected void registerLogic(Class<?> interfaceClass, ILogic logic)
    {
        String interfaceName = interfaceClass.getName();
        Class<?> logicClass = logic.getClass();
        if (isInterface(logicClass, interfaceName)
                && isInterface(logicClass, ILogic.class.getName()))
        {
            mLogicCache.put(interfaceName, logic);
        }
        else
        {
            Log.w(TAG, "Register logic(" + logicClass.getName()
                    + ") failed.", new Throwable());
        }
    }

    /**
     * 对缓存中的所有logic对象增加hander<BR>
     * 对缓存中的所有logic对象增加hander，在该UI的onCreated时被框架执行，
     * 如果该logic对象里执行了sendMessage方法，则所有的活动的UI对象接收到通知。
     *
     * @param handler UI的handler对象
     */
    public void addHandlerToAllLogics(Handler handler)
    {
        Set<Entry<String, ILogic>> logics = mLogicCache.entrySet();
        for (Entry<String, ILogic> logicEntry : logics)
        {
            ILogic logic = logicEntry.getValue();
            logic.addHandler(handler);
        }
    }

    /**
     * 对缓存中的所有logic对象移除hander对象<BR>
     * 在该UI的onDestory时被框架执行，如果该logic对象
     * 执行了sendMessage方法，则所有的UI接收到通知
     *
     * @param handler UI的handler对象
     */
    public void removeHandlerToAllLogics(Handler handler)
    {
        Set<Entry<String, ILogic>> logics = mLogicCache.entrySet();
        for (Entry<String, ILogic> logicEntry : logics)
        {
            ILogic logic = logicEntry.getValue();
            logic.removeHandler(handler);
        }
    }

    /**
     * 根据logic接口类返回logic对象<BR>
     * 如果缓存没有则返回null。
     *
     * @param interfaceClass logic接口类
     * @return logic对象
     */
    public ILogic getLogicByInterfaceClass(Class<?> interfaceClass)
    {
        return mLogicCache.get(interfaceClass.getName());
    }

    /**
     * 判断一个子类是否实现了接口<BR>
     *
     * @param c           子类类型
     * @param szInterface 接口类名称
     * @return 是否实现了接口
     */
    private boolean isInterface(Class<?> c, String szInterface)
    {
        Class<?>[] face = c.getInterfaces();
        for (int i = 0, j = face.length; i < j; i++)
        {
            if (face[i].getName().equals(szInterface))
            {
                return true;
            }
            else
            {
                Class<?>[] face1 = face[i].getInterfaces();
                for (int x = 0; x < face1.length; x++)
                {
                    if (face1[x].getName().equals(szInterface))
                    {
                        return true;
                    }
                    else if (isInterface(face1[x], szInterface))
                    {
                        return true;
                    }
                }
            }
        }
        if (null != c.getSuperclass())
        {
            return isInterface(c.getSuperclass(), szInterface);
        }
        return false;
    }

    /**
     * 需要被BaseLogicBuilder子类的init实现<BR>
     * BaseLogicBuilder子类需要在该方法中通过registerLogic进行注册logic。
     *
     * @param context 系统的context对象
     */
    protected abstract void init(Context context);
}
