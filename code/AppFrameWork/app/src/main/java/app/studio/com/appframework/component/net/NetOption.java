package app.studio.com.appframework.component.net;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import library.sswwm.com.component.log.Logger;


/**
 * Http通道接入的基础类，需要继承并实现相关接口
 * 子类至少需要实现getUrl()和getBody()两个方法
 */
public abstract class NetOption
{
    /**
     * 打印标志
     */
    private static final String TAG = "BaseHttpAccess";

    /**
     * 发送Http请求
     *
     * @param httpCallback 回调对象
     */
    protected void send(final INetCallBack httpCallback)
    {
        ThreadTask runnable = new ThreadTask()
        {
            @Override
            public void run()
            {
                //0.网络是否连接，如果网络不是连接之间返回


                NetRequest request = buildRequest();

                NetResponse response = NetConnector.connect(request);

                if (response.getResponseCode() == null)
                {
                    response.setResponseCode(NetResponse.ResponseCode.Failed);
                }

                // 4.在联网正常的情况下，解析数据
                switch (response.getResponseCode())
                {
                    case Succeed:
                    case BadRequest:
                    case UnAuthorized:
                    case Forbidden:
                    case NotFound:
                    case Conflict:
                    case InternalError:

                        parserResultCode(response);
                        if (response.getResponseCode() == NetResponse.ResponseCode.Succeed)
                        {
                            Logger.e(TAG, "handleResponse()");
                            response.setObj(handleResponse(response));
                        }
                        break;
                    default:
                        // 不可解析的情况，设置结果值是-1
                        response.setResultCode(-1);
                        break;
                }
                // 5.回调
                httpCallback.onResult(response);
            }
        };
        //加入线程池运行
        ThreadPoolUtil.execute(runnable);
    }

    /**
     * 获取请求的URL
     *
     * @return 返回请求URL
     */
    protected abstract String getUrl();

    /**
     * 获取请求要携带的消息体
     *
     * @return 返回消息体
     */
    protected abstract String getBody();

    /**
     * 获取请求要携带的HTTP头域
     *
     * @return 返回HTTP头域列表
     */
    protected abstract List<NameValuePair> getRequestProperties();

    /**
     * 处理响应
     *
     * @param response 收到的响应
     * @return 返回解析对象
     */
    protected abstract Object handleResponse(final NetResponse response);

    /**
     * 请求method类型<BR>
     *
     * @return 默认为POST请求
     */
    protected NetRequest.RequestMethod getRequestMethod()
    {
        return NetRequest.RequestMethod.GET;
    }

    /**
     * 请求消息体数据类型<BR>
     *
     * @return 默认为XML格式
     */
    protected NetRequest.ContentType getContentType()
    {
        return NetRequest.ContentType.JSON;
    }

    /**
     * 请求是否需要进行GZIP压缩
     *
     * @return 是否需要GZIP压缩
     */
    protected boolean isGZip()
    {
        return false;
    }

    /**
     * 请求信任任意主机
     *
     * @return 是否信任任意主机？ true信任，false证书验证（默认值true）
     */
    protected boolean isTrustAll()
    {
        return true;
    }

    /**
     * 请求的响应是否需要返回字节流的数据
     *
     * @return 默认不需要byte数组
     */
    protected boolean isNeedByte()
    {
        return false;
    }

    /**
     * 获取请求连接的超时时间，单位毫秒
     *
     * @return 默认为15000毫秒
     */
    protected int getConnectionTimeOut()
    {
        return NetRequest.CONNECT_TIMEOUT;
    }

    /**
     * 获取请求读取数据的超时时间，单位毫秒
     *
     * @return 默认为15000毫秒
     */
    protected int getReadTimeOut()
    {
        return NetRequest.READ_TIMEOUT;
    }

    /**
     * 解析服务器返回的错误码及描述<BR>
     *
     * @param response 响应对象
     */
    private void parserResultCode(NetResponse response)
    {
        String data = response.getData();
        if (data != null)
        {
            // JSON
            if (getContentType() == NetRequest.ContentType.JSON && !isNeedByte())
            {
                try
                {
                    JSONObject rootJsonObj = new JSONObject(data);
                    // 仅解析Result
                    if (rootJsonObj.has("Result"))
                    {
                        JSONObject resultObj = rootJsonObj.getJSONObject("Result");
                        int resultCode = resultObj.getInt("resultCode");
                        String resultDesc = resultObj.getString("resultDesc");

                        response.setResultCode(resultCode);
                        response.setResultDesc(resultDesc);
                    }
                }
                catch (JSONException e)
                {
                    Logger.e(TAG, e.toString());
                }
            }
        }
    }

    /**
     * 构建Request对象 <BR>
     *
     * @return Request 返回请求对象
     */
    protected NetRequest buildRequest()
    {
        NetRequest request = new NetRequest();
        request.setUrl(getUrl());
        request.setBody(getBody());
        request.setRequestMethod(getRequestMethod());
        request.setContentType(getContentType());
        request.setRequestProperties(getRequestProperties());
        request.setNeedByte(isNeedByte());
        request.setGZip(isGZip());
        request.setConnectionTimeOut(getConnectionTimeOut());
        request.setReadTOut(getReadTimeOut());
        request.setTrustAll(isTrustAll());
        return request;
    }
}
