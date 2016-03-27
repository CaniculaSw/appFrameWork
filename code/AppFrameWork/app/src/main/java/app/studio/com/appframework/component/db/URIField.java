/*
 * 文件名: URIField.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 定义系统要用到的URI
 * 创建人: qinyangwang
 * 创建时间:2012-2-14
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package app.studio.com.appframework.component.db;

import android.net.Uri;

public class URIField
{
    /**
     * 系统数据库操作权限/provider权限，与AndroidManifest.xml中的provider中的配置一致
     */
    public static final String AUTHORITY = "com.sswwm.common";

    /**
     * 系统数据库操作权限URI: 系统 provider URI
     */
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

}
