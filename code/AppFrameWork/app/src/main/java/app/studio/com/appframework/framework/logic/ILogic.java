/*
 * 文件名: ILogic.java
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

/**
 * 业务逻辑基础接口
 *
 * @author 刘鲁宁
 * @version [RCS Client V100R001C03, Feb 11, 2012]
 */
public interface ILogic
{
    /**
     * 初始化方法<BR>
     * 在被系统管理的logic在注册到LogicBuilder中后立即被调用的初始化方法。
     *
     * @param context 系统的context对象
     */
    public void init(Context context);

    /**
     * 对logic增加handler<BR>
     * 在logic对象里加入UI的handler
     *
     * @param handler UI传入的handler对象
     */
    public void addHandler(Handler handler);

    /**
     * 对logic移除handler<BR>
     * 在logic对象里移除UI的handler
     *
     * @param handler UI传入的handler对象
     */
    public void removeHandler(Handler handler);
}
