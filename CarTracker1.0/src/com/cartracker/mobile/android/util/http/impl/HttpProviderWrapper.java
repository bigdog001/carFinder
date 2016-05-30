package com.cartracker.mobile.android.util.http.impl;

import android.content.Context;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.http.INetProvider;
import com.cartracker.mobile.android.util.http.INetRequest;
import com.cartracker.mobile.android.util.http.INetResponse;
import com.cartracker.mobile.android.util.json.JsonObject;
import com.cartracker.mobile.android.util.json.JsonParser;
import com.cartracker.mobile.android.util.json.JsonUtil;
import com.cartracker.mobile.android.util.json.JsonValue;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: jw362j
 * Date: 12/5/13
 * Time: 10:16 AM
 */
public class HttpProviderWrapper implements INetProvider {
    private static INetProvider instance = new HttpProviderWrapper();
    //    private static Object lock = new Object();
    private static Context mContext;

    private HttpProviderWrapper() {
        System.setProperty("http.keepAlive", "true");
    }

    public static INetProvider getInstance(Context c) {
        if (mContext == null) {
            mContext = c;
        }
        return instance;
    }

    private Vector<INetRequest> reqTxtVec = new Vector<INetRequest>();
    private Vector<INetRequest> reqImgVec = new Vector<INetRequest>();
    private Vector<INetRequest> reqPostImgVec = new Vector<INetRequest>();
    private Vector<INetRequest> reqEnquireVec = new Vector<INetRequest>();
    private HttpThread[] txtThreads = null;
    private HttpThread[] imgThreads = null;
    private HttpThread postImgThreads = null;
    private HttpThread enquireThreads = null;


    private void checkThreads() {

        if (txtThreads == null) {
            txtThreads = new HttpThread[2];
            for (int i = 0; i < txtThreads.length; i++) {
                txtThreads[i] = new HttpThread(reqTxtVec);
                txtThreads[i].start();
            }
        }
        if (null == imgThreads) {
            imgThreads = new HttpThread[2];
            for (int i = 0; i < imgThreads.length; i++) {
                imgThreads[i] = new HttpThread(reqImgVec);
                imgThreads[i].setPriority(Thread.NORM_PRIORITY);
                imgThreads[i].start();
            }
        }
        if (null == postImgThreads) {
            postImgThreads = new HttpThread(reqPostImgVec);
            postImgThreads.start();
        }

        if (null == enquireThreads) {
            enquireThreads = new HttpThread(reqEnquireVec);
            enquireThreads.start();
        }
    }

    public void addRequest(INetRequest req, int priority) {
        synchronized (HttpProviderWrapper.this) {
            checkThreads();
            if (INetRequest.TYPE_HTTP_GET_IMG == req.getType() || INetRequest.TYPE_HTTP_GET_EMONTICONS == req.getType()) {
                synchronized (reqImgVec) {
                    switch (priority) {
                        case INetRequest.PRIORITY_HIGH_PRIORITY:
                            reqImgVec.insertElementAt(req, 0);
                            reqImgVec.notify();
                            break;
                        case INetRequest.PRIORITY_LOW_PRIORITY:
                            reqImgVec.addElement(req);
                            reqImgVec.notify();
                            break;
                    }
                }
            } else if (INetRequest.TYPE_HTTP_POST_IMG == req.getType() || INetRequest.TYPE_HTTP_SYNC_CONTACT == req.getType()) {
                synchronized (reqPostImgVec) {
                    reqPostImgVec.addElement(req);
                    reqPostImgVec.notify();
                }

            } else if (INetRequest.TYPE_HTTP_CHAT == req.getType()) {
                synchronized (reqEnquireVec) {
                    reqEnquireVec.addElement(req);
                    reqEnquireVec.notify();
                }
            } else {
                synchronized (reqTxtVec) {
                    switch (priority) {
                        case INetRequest.PRIORITY_HIGH_PRIORITY:
                            reqTxtVec.insertElementAt(req, 0);
                            reqTxtVec.notify();
                            break;
                        case INetRequest.PRIORITY_LOW_PRIORITY:
                            reqTxtVec.addElement(req);
                            reqTxtVec.notify();
                            break;
                    }
                }
            }
        }
    }


    /**
     * add one request
     * add a request ,the txt request has hight level priroty ,it should be in the top of queue
     */
    public void addRequest(INetRequest req) {
        synchronized (HttpProviderWrapper.this) {
            checkThreads();
        }
        if (INetRequest.TYPE_HTTP_GET_IMG == req.getType() || INetRequest.TYPE_HTTP_GET_EMONTICONS == req.getType()) {
            synchronized (reqImgVec) {
                reqImgVec.addElement(req);
                reqImgVec.notify();
            }
        } else if (INetRequest.TYPE_HTTP_POST_IMG == req.getType() || INetRequest.TYPE_HTTP_SYNC_CONTACT == req.getType()) {
            synchronized (reqPostImgVec) {
                reqPostImgVec.addElement(req);
                reqPostImgVec.notify();
            }
        } else if (INetRequest.TYPE_HTTP_CHAT == req.getType()) {
            synchronized (reqEnquireVec) {
                reqEnquireVec.addElement(req);
                reqEnquireVec.notify();
            }
        } else {
            synchronized (reqTxtVec) {
                reqTxtVec.insertElementAt(req, 0);
                reqTxtVec.notify();
            }
        }
    }

    public void cancel() {
        synchronized (reqImgVec) {
            reqImgVec.clear();
        }
    }

    public void stop() {
        synchronized (HttpProviderWrapper.this) {
            if (null != txtThreads) {
                synchronized (reqTxtVec) {
                    for (int i = 0; i < txtThreads.length; i++) {
                        if (null != txtThreads[i]) {
                            txtThreads[i].running = false;
                        }
                    }
                    reqTxtVec.clear();
                    reqTxtVec.notifyAll();
                    txtThreads = null;
                }
            }

            // 为了保证退出后也能同步通讯录头像
            if (null != imgThreads) {
                synchronized (reqImgVec) {
                    for (int i = 0; i < imgThreads.length; i++) {
                        if (null != imgThreads[i]) {
//							synchronized (imgThreads[i].reqVec) {
                            imgThreads[i].running = false;
                            imgThreads[i].burnning = false;
//								txtThreads[i].reqVec.clear();
//								txtThreads[i].reqVec.notify();
//							}
                        }
                    }
                    reqImgVec.clear();
                    reqImgVec.notifyAll();
                    imgThreads = null;
                }
            }

            if (null != postImgThreads) {
                synchronized (postImgThreads.reqVec) {
                    postImgThreads.running = false;
                    postImgThreads.burnning = true;
                    postImgThreads.reqVec.notify();
                }
                postImgThreads = null;
            }

            if (null != enquireThreads) {
                synchronized (enquireThreads.reqVec) {
                    enquireThreads.running = false;
                    enquireThreads.burnning = false;
                    enquireThreads.reqVec.notify();
                }
                enquireThreads = null;
            }
        }
    }

    static class HttpThread extends Thread {
        private Vector<INetRequest> reqVec = null;
        private JsonObject error = new JsonObject();

        public HttpThread(Vector<INetRequest> reqVec) {
            this.reqVec = reqVec;
            setPriority(Thread.NORM_PRIORITY);
            error.put(VariableKeeper.APP_CONSTANT.error_code_name, "-99");
            error.put(VariableKeeper.APP_CONSTANT.error_msg_name, "connecting error,try later！");
        }

        protected boolean running = true;

        /**
         * close it after all of the request finish
         */
        protected boolean burnning = false;

        @Override
        public void run() {
            INetRequest currentRequest = null;
            boolean reconnect = false;
            while (running || burnning) {
//				Thread.yield();
                if (!reconnect) {
                    synchronized (reqVec) {
                        if (reqVec.size() > 0) {
                            currentRequest = reqVec.firstElement();
                            reqVec.removeElementAt(0);
                        } else {
                            if (burnning) {
                                burnning = false;
                                return;
                            }
                            try {
                                reqVec.wait();
                                continue;
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                }

                HttpClient httpClient = null;
                if (currentRequest != null) {
                    String url_str = currentRequest.getUrl();
                    // data is null that means get image by get request
                    INetResponse currentResponse = currentRequest.getResponse();
                    try {

                        HttpRequestBase httpRequest = null;
                        if (INetRequest.TYPE_HTTP_GET_IMG == currentRequest.getType() || INetRequest.TYPE_HTTP_GET_EMONTICONS == currentRequest.getType()) {// 获取图片
                            httpRequest = new HttpGet(url_str);

                            httpRequest.addHeader("Referer", VariableKeeper.APP_CONSTANT.ApiHost);
                            httpRequest.addHeader("Accept", "*/*");
                            JsonObject temp = new JsonObject();
                            temp.put(VariableKeeper.APP_CONSTANT.error_code_name, "-90");
                            temp.put(VariableKeeper.APP_CONSTANT.error_msg_name, "failed to get image");
                            error = temp;
                        } else if (INetRequest.TYPE_HTTP_GET_HTML == currentRequest.getType()) {
                            httpRequest = new HttpGet(url_str);
                            httpRequest.addHeader("Accept", "*/*");
                            JsonObject temp = new JsonObject();
                            temp.put(VariableKeeper.APP_CONSTANT.error_code_name, "-91");
                            temp.put(VariableKeeper.APP_CONSTANT.error_msg_name, "network access failed");
                            error = temp;
                        } else {
                            httpRequest = new HttpPost(url_str);
                            JsonObject temp = new JsonObject();
                            temp.put(VariableKeeper.APP_CONSTANT.error_code_name, "-99");
                            temp.put(VariableKeeper.APP_CONSTANT.error_msg_name, "no network,please check...");
                            error = temp;
                            if (INetRequest.TYPE_HTTP_POST_IMG == currentRequest.getType()) {
                                httpRequest.addHeader("Content-Type", "multipart/form-data; charset=UTF-8; boundary=FlPm4LpSXsE");// sending the image data
                            } else {
                                httpRequest.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");//
                            }
                            // httpRequest.addHeader("Connection","Keep-Alive");
                            httpRequest.addHeader("Connection", "keep-alive");
                            httpRequest.addHeader("Accept", "*/*");
                            byte[] requestBytes = currentRequest.serialize();
                            HttpEntity entity = new ByteArrayEntity(requestBytes);
                            ((HttpPost) httpRequest).setEntity(entity);
                        }
                        BasicHttpParams httpParameters = new BasicHttpParams();

                        if (INetRequest.TYPE_HTTP_POST_IMG == currentRequest.getType()) {
                            httpParameters.setIntParameter(HttpConnectionParams.SO_TIMEOUT, 90000); // setting for timeout
                            httpParameters.setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 90000);
                            httpParameters.setIntParameter(HttpConnectionParams.SOCKET_BUFFER_SIZE, 8192 * 4);
                        } else {
                            httpParameters.setIntParameter(HttpConnectionParams.SO_TIMEOUT, 45000); // setting for timeout
                            httpParameters.setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 45000);
                            httpParameters.setIntParameter(HttpConnectionParams.SOCKET_BUFFER_SIZE, 8192);
                        }
                        httpClient = new DefaultHttpClient(httpParameters);
                        SystemUtil.log("http connect to : " + currentRequest.getUrl());

                        HttpHost host = HttpProxy.getProxyHttpHost(mContext);
                        if (host != null) {
                            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, host);
                        }
                        httpRequest.removeHeaders(HTTP.EXPECT_DIRECTIVE);


                        HttpResponse httpResponse = httpClient.execute(httpRequest);

                        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                            InputStream is = httpResponse.getEntity().getContent();
                            if (currentRequest.useGzip()) {
                                is = new GZIPInputStream(is);
                            }
                            byte[] buf = JsonUtil.toByteArray(is);
                            JsonValue resp = null;
                            if (INetRequest.TYPE_HTTP_GET_HTML == currentRequest.getType()) {
                                String s = new String(buf, "UTF-8");
                                s = s.replace("\\r", "");
                                JsonObject respObj = new JsonObject();
                                respObj.put(INetResponse.HTML_DATA, s);
                                resp = respObj;
                            } else if (INetRequest.TYPE_HTTP_GET_IMG == currentRequest.getType() || INetRequest.TYPE_HTTP_GET_EMONTICONS == currentRequest.getType()) {// 获取图片
                                JsonObject respObj = new JsonObject();
                                respObj.put(INetResponse.IMG_DATA, buf);
                                resp = respObj;

                            } else {// to get the json format data structure we want
                                resp = deserialize(buf, currentRequest);
                            }

                            if (resp == null) {
                                JsonObject temp = new JsonObject();
                                temp.put(VariableKeeper.APP_CONSTANT.error_code_name, "1+1");
                                temp.put(VariableKeeper.APP_CONSTANT.error_msg_name, "server response error,unknown response !");
                                SystemUtil.log("server response error,not a correct data format:" + new String(buf, "UTF-8"));
                                error = temp;
                            }
                            if (null != currentResponse) {
                                if (running) {
                                    if (resp == null) currentResponse.response(currentRequest, error);
                                    currentResponse.response(currentRequest, resp);
                                }
                            }
//                            reconnect = false;
                        }
                        // 下面的处理过程不完整，没有给response返回，所以在response中关闭等待框的逻辑得不到处理
                    } catch (UnknownHostException unKnownHost) {
                        JsonObject ret = new JsonObject();
                        ret.put(VariableKeeper.APP_CONSTANT.error_code_name, "-97");
                        ret.put(VariableKeeper.APP_CONSTANT.error_msg_name, "no network ,please check...");
                        if (null != currentResponse) {
                            currentResponse.response(currentRequest, ret);
                        }

                    } catch (Exception e) {// exception
                        e.printStackTrace();
                        if (reconnect) {
                            if (null != currentResponse) {
                                currentResponse.response(currentRequest, error);
                            }
                            reconnect = false;
                        }
                    } finally {
                        try {
                            if (httpClient != null) {
                                httpClient.getConnectionManager().shutdown();
                            }
                        } catch (Exception e) {
                            SystemUtil.log(e.getMessage());
                        }
                    }
                }
            }
        }
    }

    private static   JsonValue deserialize(byte[] data, INetRequest currentRequest) {
        try {
            if (data == null) {
                return null;
            }
//            String ss="jeEyBU4gIkc\/gPWs1AxMI7pLmMc=";
            String s = new String(data, "UTF-8");
            s = s.replace("\\r", "");
            SystemUtil.log("server response come back ---->" + s);
            return JsonParser.parse(s);
        } catch (UnsupportedEncodingException e) {
            SystemUtil.log(e.getMessage());
        }
        return null;
    }

}
