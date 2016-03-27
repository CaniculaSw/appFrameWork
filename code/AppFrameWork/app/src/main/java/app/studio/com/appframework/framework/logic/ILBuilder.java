/*
 * 文件名: ILogicBuilder.java
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

import android.os.Handler;

public interface ILBuilder
{
    /**
     * 根据logic接口类返回logic对象<BR>
     * 如果缓存没有则返回null。
     *
     * @param interfaceClass logic接口类
     * @return logic对象
     */
    public ILogic getLogicByInterfaceClass(Class<?> interfaceClass);

    /**
     * 对缓存中的所有logic对象增加hander<BR>
     * 对缓存中的所有logic对象增加hander，在该UI的onCreated时被框架执行，
     * 如果该logic对象里执行了sendMessage方法，则所有的活动的UI对象接收到通知。
     *
     * @param handler UI的handler对象
     */
    public void addHandlerToAllLogics(Handler handler);

    /**
     * 对缓存中的所有logic对象移除hander对象<BR>
     * 在该UI的onDestory时被框架执行，如果该logic对象
     * 执行了sendMessage方法，则所有的UI接收到通知
     *
     * @param handler UI的handler对象
     */
    public void removeHandlerToAllLogics(Handler handler);
}
