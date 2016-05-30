package com.cartracker.mobile.android.util.http.impl;

/**
 * Created by jw362j on 7/30/2014.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.apache.http.HttpHost;

import java.net.InetSocketAddress;

public final class HttpProxy {

    static public HttpHost getProxyHttpHost(Context context) {
        ConnectivityManager ConnMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = ConnMgr.getActiveNetworkInfo();
        String proxyHost = null;
        int proxyPort = 0;
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                // WIFI: global http proxy
//                proxyHost = android.net.Proxy.getHost(context);
//                proxyPort = android.net.Proxy.getPort(context);
            } else {
                // GPRS: APN http proxy
                proxyHost = android.net.Proxy.getDefaultHost();
                proxyPort = android.net.Proxy.getDefaultPort();
            }
        }
        if (proxyHost != null) {
            return new HttpHost(proxyHost, proxyPort);
        } else {
            return null;
        }
    }

    static public java.net.Proxy getProxyNetProxy(Context context) {
        ConnectivityManager ConnMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = ConnMgr.getActiveNetworkInfo();
        String proxyHost = null;
        int proxyPort = 0;

        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                // WIFI: global http proxy
//                proxyHost = android.net.Proxy.getHost(context);
//                proxyPort = android.net.Proxy.getPort(context);
            } else {
                // GPRS: APN http proxy
                proxyHost = android.net.Proxy.getDefaultHost();
                proxyPort = android.net.Proxy.getDefaultPort();
            }
        }
        if (proxyHost != null) {
            return new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        } else {
            return null;
        }
    }
}