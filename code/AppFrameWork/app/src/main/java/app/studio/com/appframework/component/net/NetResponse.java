package app.studio.com.appframework.component.net;

/**
 * Http的响应封装基础类
 */
public class NetResponse
{
    /**
     * http响应的错误类型
     */
    public enum ResponseCode
    {
        /**
         * 操作成功
         */
        Succeed,
        
        /**
         * 超时
         */
        Timeout,
        
        /**
         * 网络错误
         */
        NetworkError,
        
        /**
         * 鉴权失败
         */
        AuthError,
        
        /**
         * 请求参数错误
         */
        ParamError,
        
        /**
         * 未知错误
         */
        Failed,
        /**
         * 错误请求
         */
        BadRequest,
        /**
         * 401需要鉴权
         */
        UnAuthorized,
        /**
         * 403鉴权未通过
         */
        Forbidden,
        /**
         * 404 请求路径未找到
         */
        NotFound,
        /**
         * 409 服务器在完成请求时发生冲突
         */
        Conflict,
        ResponseCode, /**
         * 500 服务器错误
         */
        InternalError
    }
    
    /**
     * 与此Response对应的请求对象
     */
    private NetRequest request;
    
    /**
     * HTTP响应返回码
     */
    private ResponseCode responseCode;
    
    /**
     * 服务器连接正常时返回的数据
     */
    private String data;
    
    /**
     * the data type that the server responded.
     */
    private byte[] byteData;
    
    /**
     * 对服务器返回的data进行封装处理。返回给最终的调用者使用。
     */
    private Object obj;
    
    /**
     * 服务器正常返回时的业务结果码
     */
    private int resultCode;
    
    /**
     * 服务器正常返回时的业务结果描述
     */
    private String resultDesc;

    /**
     * 获取与响应匹配的请求
     * @return 请求对象
     */
    public NetRequest getRequest()
    {
        return request;
    }

    /**
     * 设置与响应匹配的请求
     * @param request 请求对象
     */
    public void setRequest(NetRequest request)
    {
        this.request = request;
    }

    /**
     * 获取响应的HTTP响应码
     * @return Http响应码
     */
    public ResponseCode getResponseCode()
    {
        return responseCode;
    }

    /**
     * 设置响应的HTTP响应码
     * @param responseCode Http响应码
     */
    public void setResponseCode(ResponseCode responseCode)
    {
        this.responseCode = responseCode;
    }

    /**
     * 获取响应的数据
     * @return Http响应的数据
     */
    public String getData()
    {
        return data;
    }

    /**
     * 设置响应的数据
     * @param data Http响应的数据
     */
    public void setData(String data)
    {
        this.data = data;
    }

    /**
     * 获取响应的数据
     * @return Http响应的byte类型数据
     */
    public byte[] getByteData()
    {
        return byteData;
    }

    /**
     * 设置响应的数据
     * @param byteData Http响应的byte类型数据
     */
    public void setByteData(byte[] byteData)
    {
        this.byteData = byteData;
    }

    /**
     * 获取响应的数据的解析后对象封装
     * @return Http响应的数据的对象封装
     */
    public Object getObj()
    {
        return obj;
    }

    /**
     * 设置响应的数据的解析后对象封装
     * @param obj Http响应的数据的对象封装
     */
    public void setObj(Object obj)
    {
        this.obj = obj;
    }

    /**
     * 获取响应的业务响应码
     * @return Http业务响应码
     */
    public int getResultCode()
    {
        return resultCode;
    }

    /**
     * 设置响应的业务响应码
     * @param resultCode Http业务响应码
     */
    public void setResultCode(int resultCode)
    {
        this.resultCode = resultCode;
    }

    /**
     * 获取响应的业务描述信息
     * @return Http业务描述信息
     */
    public String getResultDesc()
    {
        return resultDesc;
    }

    /**
     * 设置响应的业务描述信息
     * @param resultDesc Http业务描述信息
     */
    public void setResultDesc(String resultDesc)
    {
        this.resultDesc = resultDesc;
    }
    
}
