package com.cartracker.mobile.android.util.http;

import com.cartracker.mobile.android.util.json.JsonObject;

/**
 * Created by jw362j on 7/30/2014.
 */
public interface INetRequest {
    public static final int TYPE_HTTP_REST = 0;
    public static final int TYPE_HTTP_GET_IMG = 1;
    public static final int TYPE_HTTP_POST_IMG = 2;
    public static final int TYPE_HTTP_GET_EMONTICONS = 3;
    public static final int TYPE_HTTP_GET_HTML = 4;
    public static final int TYPE_HTTP_SYNC_CONTACT = 5;
    public static final int TYPE_HTTP_CHAT=6;
    public static final int TYPE_HTTP_SEND_CHAT=7;

    public static final int PRIORITY_LOW_PRIORITY = 0;
    public static final int PRIORITY_HIGH_PRIORITY = 1;


    public static final String gzip_key = "gz";
    public static final String gzip_value = "compression";

    boolean useGzip();

    /**
     * 获取图片请求的优先级
     * @return
     */
    int getPriority();

    /**
     * 设置图片请求的优先级
     * @return
     */
    void setPriority(int priority);

    /**
     * 获取请求类型
     * @return
     */
    int getType();

    /**
     * 设置请求类型
     * @param type
     */
    void setType(int type);

    /**
     * 设置响应回调.
     * @param resp
     */
    void setResponse(INetResponse resp);

    /**
     * 获取响应回调.
     * @return
     */
    INetResponse getResponse();

    /**
     * 设置url.
     * @param url
     */
    void setUrl(String url);

    /**
     * 获取url.
     * @return
     */
    String getUrl();

    int getId();

    void setId(int id);

    long getCurrentSession();

    void setCurrentSession(long currentSession);

    /**
     * 设置请求参数，获取照片时不需要设置.
     * @param data
     */
    void setData(JsonObject data);

    /**
     * 获取请求参数.
     * @return
     */
    JsonObject getData();



    String getParamsString();

    String getMethod();



    byte[] serialize();
}
