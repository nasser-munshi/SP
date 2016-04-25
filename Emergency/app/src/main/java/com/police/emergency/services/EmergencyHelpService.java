package com.police.emergency.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.mithun.hardware.GPSTracker;
import com.mithun.tasks.HttpPostReqAsyncTask;
import com.mithun.utils.DeviceUtils;
import com.mithun.utils.ScreenStatusReceiver;
import com.police.emergency.R;

public class EmergencyHelpService extends Service {
    BroadcastReceiver mReceiver;
    GPSTracker gpsTracker = null;
    DeviceUtils deviceUtils = null;
    String mDeviceID ="";
    String httpAddress;

    public EmergencyHelpService(){}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG).show();
        if(android.os.Debug.isDebuggerConnected()){
            android.os.Debug.waitForDebugger();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)  {
        // For time consuming an long tasks you can launch a new thread here...
        httpAddress = getString(R.string.test_server_address_base)  + getString(R.string.server_address);
        Toast.makeText(this, " Service Started", Toast.LENGTH_LONG).show();
        gpsTracker = new GPSTracker(this);
        deviceUtils = new DeviceUtils(this);
        mDeviceID = deviceUtils.getDeviceID();
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mReceiver = new ScreenStatusReceiver(this, 3, "emergencyHelpSeekAtLocation");
        registerReceiver(mReceiver, filter);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    public void emergencyHelpSeekAtLocation(){
        String lat = "";
        String lng = "";
        Log.e("Status:", "Service Called1");

        if (deviceUtils.isNetworkConnected()) {
            if (gpsTracker.canGetLocation()) {
                Location location = gpsTracker.getLastKnownLocation();//getLocation();
                if(location != null){
                    lat = String.valueOf(location.getLatitude());
                    lng = String.valueOf(location.getLongitude());
                } else {
                    Log.e("Error:", "Location not found");
                    lat = "0.0";
                    lng = "0.0";
                }
                gpsTracker.stopUsingGPS();

                sendSms(getApplicationContext(), lat, lng);

                Log.e("Status:","Data Send Successfully");
                new HttpPostReqAsyncTask(this, httpAddress).execute(mDeviceID, lat, lng, "10");
            } else {
                gpsTracker.showSettingsAlert();
            }
        } else {
            Log.e("Status:","Please Check Your Internet Connection");
            Toast.makeText(getApplicationContext(), "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            if (gpsTracker.canGetLocation()) {
                Location location = gpsTracker.getLastKnownLocation();//getLocation();
                if(location != null){
                    lat = String.valueOf(location.getLatitude());
                    lng = String.valueOf(location.getLongitude());
                    Log.e("lat1", lat);
                    Log.e("lng1", lng);
                    Log.e("lat2", String.valueOf(location.getLatitude()));
                    Log.e("lng2", String.valueOf(location.getLongitude()));
                }else {
                    Log.e("Error:", "Location not found");
                    lat = "0.0";
                    lng = "0.0";
                }
                gpsTracker.stopUsingGPS();
                Log.e("Status:", "GPS Status Successful.");

                Log.e("lat", lat);
                Log.e("lng", lng);

                sendSms(getApplicationContext(), lat, lng);
            } else {
                gpsTracker.showSettingsAlert();
            }
        }
    }
    public class LocalBinder extends Binder {
        EmergencyHelpService getService() {
            return EmergencyHelpService.this;
        }
    }



    public void sendSms(Context context, String mStringLatitude, String mStringLongitude) {

        try {
            String msg = "";

            if((mStringLatitude.equals("0.0")) && (mStringLongitude.trim().equals("0.0"))){
                msg = "I am in Danger.";
                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                String deviceId = telephonyManager.getDeviceId();
                String msg3 = " IMEI:"+deviceId;

                msg = msg + msg3;

                Log.e("Error:", "Location Send Failed!");

            }else {
                try {
                    StringBuffer smsBody2 = new StringBuffer();
                    String msg1 = "I'm in Danger. Please Save Me.";

                    smsBody2.append(" Location: ");
                    smsBody2.append("http://maps.google.com/?q=");
                    smsBody2.append(mStringLatitude);
                    smsBody2.append(",");
                    smsBody2.append(mStringLongitude);

                    String msg2 = smsBody2.toString();



                    TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                    String deviceId = telephonyManager.getDeviceId();
                    String msg3 = " IMEI:"+deviceId;

                    String tempMsg = msg1 + msg2 + msg3 ;


                    if (tempMsg.length() > 150) {

//                        int def = tempMsg.length() - 150;
//                        msg2 = msg2.substring(0, msg2.length() - def);
                        msg = msg1 + msg2;
                    } else {
                        msg = tempMsg;
                    }
                } catch (Exception e) {

                    msg = "I am in Danger. Please Save Me.";
                    Toast.makeText(context,"Location send fail.",Toast.LENGTH_SHORT).show();
                    Log.e("Error:", "Location Send Failed!");

                }
            }

            Log.e("Message: ", msg);

            SmsManager smsManager = SmsManager.getDefault();
            SmsManager smsManager2 = SmsManager.getDefault();
            SmsManager smsManager3 = SmsManager.getDefault();
            smsManager.sendTextMessage("01757143494", null, msg, null, null);
            smsManager2.sendTextMessage("01757143494", null, msg, null, null);
            smsManager3.sendTextMessage("01757143494", null, msg, null, null);

            Toast.makeText(context, "Message Sent", Toast.LENGTH_LONG).show();
            Log.e("Message Status ", "Message Sent");
        }catch (Exception e) {

            Toast.makeText(context,
                    e.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();

            Toast.makeText(context,
                    "SMS failed, please try again later.", Toast.LENGTH_SHORT).show();
            Log.e("Message Status ", "Message Sent Failed! Please Try Again!");
        }
    }
}