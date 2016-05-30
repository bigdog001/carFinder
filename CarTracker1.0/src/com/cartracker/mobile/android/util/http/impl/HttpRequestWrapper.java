package com.cartracker.mobile.android.util.http.impl;

import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.http.INetRequest;
import com.cartracker.mobile.android.util.http.INetResponse;
import com.cartracker.mobile.android.util.json.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Vector;

/**
 * Created by jw362j on 7/30/2014.
 */
public class HttpRequestWrapper implements INetRequest {

    private JsonObject data = null;

    private String url = null;

    private INetResponse response = null;

    private int id;//用来标识请求唯一性

    private int type = TYPE_HTTP_REST;

    private int mpriority=PRIORITY_HIGH_PRIORITY;

    private long currentSession;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(long currentSession) {
        this.currentSession = currentSession;
    }

    public int getPriority() {
        return mpriority;
    }

    public void setPriority(int priority){
        mpriority=priority;
    }

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public INetResponse getResponse() {
        return response;
    }


    public void setResponse(INetResponse resp) {
        this.response = resp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String getMethod() {
        return data.getString("method");
    }

    private final static int COMPUTE_SIGANATURE_CHAR_COUNT = 50;

    @Override
    public String getParamsString() {
        if (data == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if(type==INetRequest.TYPE_HTTP_CHAT||type == INetRequest.TYPE_HTTP_SEND_CHAT){
//			Log.v("a","=============requst:"+data.getString("message_body"));
            sb.append(data.getString("message_body"));
        }else{
            String[] keys = data.getKeys();
            if(keys == null || keys.length == 0){
                return "";
            }
            Vector<String> vecSig = new Vector<String>();
            for (String key : keys) {
                String val = data.getJsonValue(key).toString();
                sb.append(key).append('=').append(URLEncoder.encode(val)).append('&');

                // Modified by lin.zhu@opi-corp.com
                // 3G Server端计算Sig参数, 对value截长前50个字符.
                // Caution! 计算sig参数的入口(调用getSig方法)不止这一个,
                // 确保每个计算sig的入口均截长.
                if (val.length() > COMPUTE_SIGANATURE_CHAR_COUNT) {
                    val = val.substring(0, COMPUTE_SIGANATURE_CHAR_COUNT);
                }
                // End modify.

                vecSig.add(key+"="+val);
            }
            String[] ss = new String[vecSig.size()];
            vecSig.copyInto(ss);

        }
        String s = sb.toString();
        SystemUtil.log("request data is:" + s);
        SystemUtil.log("request url is:"+ url);

        return s;
    }

    /**
     * 将请求序列化操作
     * @param
     * @return 序列化数据
     */
    public byte[] serialize() {
        if (data == null) {
            return null;
        }
        if (INetRequest.TYPE_HTTP_POST_IMG == getType()) {
            return data.getBytes("data");
        }
        byte[] ret = null;
        try {
            ret = getParamsString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public boolean useGzip() {
        if (data != null) {
            String data_type = data.getString(gzip_key);
            if (data_type != null && data_type.equals(gzip_value)) {
                return true;
            }
        }
        return false;
    }



}
