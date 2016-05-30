package com.cartracker.mobile.android.util.http;


import android.content.Context;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.util.http.impl.HttpRequestWrapper;
import com.cartracker.mobile.android.util.json.JsonObject;

/**
 * Created by jw362j on 8/7/2014.
 */
public class HttpRequestProvider {

    public static void userLogin(final INetResponse response, String userName, String passwd) {
        JsonObject bundle = m_buildRequestBundle();
//        bundle.put(TokenGenInputParameters.opParameterName, "TokenGen");
        String userAuthNTokenGenURL = null;

        INetRequest request = m_buildRequest(userAuthNTokenGenURL, bundle, response, 0);
//        HttpProviderWrapper.getInstance(VariableKeeper.mActivity, VariableKeeper.mProperties).addRequest(request);
    }

    private static INetRequest m_buildRequest(String url, JsonObject sm, INetResponse response, int requestType) {
        INetRequest request = new HttpRequestWrapper();
        request.setType(requestType);
        request.setUrl(url);
        request.setData(sm);
        request.setResponse(response);
        return request;
    }


    private static JsonObject m_buildRequestBundle() {
        JsonObject bundle = new JsonObject();
//        bundle.put(TokenGenInputParameters.responseTypeParameterName, "json");
//        bundle.put(PropertyNames.userLoginServerAppId_parameterName, VariableKeeper.mProperties.getProperty(PropertyNames.userLoginServerAppId_propertyName));
//        VariableKeeper.appID = VariableKeeper.mProperties.getProperty(PropertyNames.userLoginServerAppId_propertyName);
        bundle.put("client_info", getClientInfo());
        return bundle;
    }

    /**
     * 统计信息
     *
     * @return
     */
    public static String getClientInfo() {

        int cellid = 0;
        if (VariableKeeper.context != null) {
            TelephonyManager tm = (TelephonyManager) VariableKeeper.context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                // GsmCellLocation location = (GsmCellLocation)
                // tm.getCellLocation();
                CellLocation location = tm.getCellLocation();
                if (location != null) {
                    if (location instanceof GsmCellLocation) {
                        cellid = ((GsmCellLocation) location).getCid();
                    }
                }
            }
        }

        JsonObject clientInfo = new JsonObject();
        clientInfo.put("model", VariableKeeper.Build_MODEL);
        clientInfo.put("os", VariableKeeper.VERSION_SDK + "_" + VariableKeeper.Version_RELEASE);
        clientInfo.put("screen", VariableKeeper.screen);
        clientInfo.put("font", "");
//        clientInfo.put("ua", Variables.ua);
        clientInfo.put("from", VariableKeeper.fromid);
//		Log.v("kxy", "------from: " + Variables.from);
        clientInfo.put("cellId", cellid);
        clientInfo.put("version", VariableKeeper.appVersionName);
        return clientInfo.toJsonString();
    }

}
