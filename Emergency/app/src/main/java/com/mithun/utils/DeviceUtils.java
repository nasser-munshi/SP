package com.mithun.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Created by Krishna Ray on 11/23/2015.
 */
public class DeviceUtils {
    Context appContext;
    public DeviceUtils(Context context){
        appContext = context;
    }
    public String getDeviceID(){
        String mDeviceId = "";
        try {
            TelephonyManager tm = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);
            mDeviceId = tm.getDeviceId();
        }catch (Exception e){
            mDeviceId = "ERROR";
            e.printStackTrace();
            Toast.makeText(appContext, "Device ID Error", Toast.LENGTH_LONG).show();
        }
        return mDeviceId;
    }
    public  boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }
}
