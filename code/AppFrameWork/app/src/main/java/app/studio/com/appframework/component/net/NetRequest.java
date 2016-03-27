package app.studio.com.appframework.component.net;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import library.sswwm.com.utils.StringUtil;

public class NetRequest
{
    /**
     * 请求方法的定义
     */
    public enum RequestMethod
    {
        /**
         * get请求
         */
        GET,

        /**
         * post请求
         */
        POST,

        /**
         * PUT
         */
        PUT,

        /**
         * DELETE
         */
        DELETE
    }

    /**
     * 请求体类型的定义
     */
    public enum ContentType
    {
        XML,
        JSON
    }

    /**
     * 连接超时时间
     */
    public static final int CONNECT_TIMEOUT = 15000;

    /**
     * 获取数据超时时间
     */
    public static final int READ_TIMEOUT = 15000;

    /**
     * 唯一标示请求的字符串
     */
    private String requestID;

    /**
     * 请求的url
     */
    private String url;

    /**
     * 发送时携带的请求体
     */
    private String body;

    /**
     * 请求类型，默认为get请求
     */
    private RequestMethod requestMethod = RequestMethod.GET;

    /**
     * 请求数据的格式，默认为xml格式封装
     */
    private ContentType contentType = ContentType.XML;

    /**
     * 请求附带的request property.
     */
    private List<NameValuePair> requestProperties;

    /**
     * need to set the response data's type to 'byte' instead of 'String'
     */
    private boolean needByte;

    /**
     * 是否需要进行Gzip压缩
     */
    private boolean gZip;

    /**
     * http请求连接的超时时间
     */
    private int connectionTimeOut;

    /**
     * http数据读取的超时时间
     */
    private int readTimeOut;

    /**
     * 是否信任任意主机
     */
    private boolean isTrustAll = true;

    /**
     * 获取请求url
     *
     * @return 返回请求的URL
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * 设置请求url
     *
     * @param url 请求的URL
     */
    public void setUrl(String url)
    {
        this.url = StringUtil.fixUrlSprit(url);
    }

    /**
     * 获取请求消息体
     *
     * @return 请求体
     */
    public String getBody()
    {
        return body;
    }

    /**
     * 设置请求消息体
     *
     * @param body 请求体
     */
    public void setBody(String body)
    {
        this.body = body;
    }

    /**
     * 获取请求方法
     *
     * @return 请求方法
     */
    public RequestMethod getRequestMethod()
    {
        return requestMethod;
    }

    /**
     * 设置请求方法
     *
     * @param requestMethod 请求方法
     */
    public void setRequestMethod(RequestMethod requestMethod)
    {
        this.requestMethod = requestMethod;
    }

    /**
     * 获取请求消息体的格式类型
     *
     * @return 请求的消息类型
     */
    public ContentType getContentType()
    {
        return contentType;
    }

    /**
     * 设置请求消息体的格式类型
     *
     * @param contentType 请求的消息类型
     */
    public void setContentType(ContentType contentType)
    {
        this.contentType = contentType;
    }

    /**
     * 获取请求的property
     *
     * @return 请求的property
     */
    public List<NameValuePair> getRequestProperties()
    {
        return requestProperties;
    }

    /**
     * 设置请求的property
     *
     * @param requestProperties 请求的property
     */
    public void setRequestProperties(List<NameValuePair> requestProperties)
    {
        this.requestProperties = requestProperties;
    }

    /**
     * 添加一个请求的property
     *
     * @param key   请求的property的key值
     * @param value 请求的property的value值
     */
    public void addRequestProperty(String key, String value)
    {
        if (requestProperties == null)
        {
            requestProperties = new ArrayList<NameValuePair>();
        }
        if (key != null && value != null)
        {
            requestProperties.add(new BasicNameValuePair(key, value));
        }
    }

    /**
     * 该请求是否需要返回字节数组
     *
     * @return boolean值
     */
    public boolean isNeedByte()
    {
        return needByte;
    }

    /**
     * 设置该请求获取的数据类型是否为字节数组
     *
     * @param needByte 是否的boolean值
     */
    public void setNeedByte(boolean needByte)
    {
        this.needByte = needByte;
    }

    /**
     * 该请求是否需要GZip
     *
     * @return 是否需要GZip
     */
    public boolean isGZip()
    {
        return gZip;
    }

    /**
     * 设置该请求获取的数据类型是否需要GZip
     *
     * @param gzip 是否需要GZip值
     */
    public void setGZip(boolean gzip)
    {
        this.gZip = gzip;
    }

    /**
     * 获取请求ID号，唯一标示此请求
     *
     * @return 请求ID
     */
    public String getRequestID()
    {
        return requestID;
    }

    /**
     * 设置请求ID号，唯一标示此请求
     *
     * @param requestID 请求ID
     */
    public void setRequestID(String requestID)
    {
        this.requestID = requestID;
    }

    /**
     * 获取请求连接的超时时间，单位毫秒
     *
     * @return 请求超时时间
     */
    public int getConnectionTimeOut()
    {
        return connectionTimeOut;
    }

    /**
     * 设置请求连接的超时时间，单位毫秒
     *
     * @param connectionTimeOut 请求超时时间
     */
    public void setConnectionTimeOut(int connectionTimeOut)
    {
        this.connectionTimeOut = connectionTimeOut;
    }

    /**
     * 获取请求读取数据的超时时间，单位毫秒
     *
     * @return 请求读取数据超时时间
     */
    public int getReadTOut()
    {
        return readTimeOut;
    }

    /**
     * 获取请求读取数据的超时时间，单位毫秒
     *
     * @param timeOut 请求读取数据超时时间
     */
    public void setReadTOut(int timeOut)
    {
        this.readTimeOut = timeOut;
    }

    /**
     * 是否信任主机
     *
     * @return 是否信任
     */
    public boolean isTrustAll()
    {
        return isTrustAll;
    }

    /**
     * 设置信任所有
     *
     * @param istrustAll 是否信任主机
     */
    public void setTrustAll(boolean istrustAll)
    {
        this.isTrustAll = istrustAll;
    }

}
