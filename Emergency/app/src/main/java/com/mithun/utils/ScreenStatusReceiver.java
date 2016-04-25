package com.mithun.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ScreenStatusReceiver extends BroadcastReceiver{
    public int press = 0;

    private static final long DOUBLE_CLICK_TIME_DELTA = 500;//milliseconds
    private boolean screenOff;
    private int mNumberOfPress = 6;
    private int mPressCount = 0;
    private long mLastClickTime = 0;
    private Context mServiceContex = null;
    private String actionMethod = null;

    public ScreenStatusReceiver(Context context){
        mServiceContex = context;
    }

    public ScreenStatusReceiver(Context context, int numberOfPress){
        mNumberOfPress = numberOfPress;
        mServiceContex = context;
    }
    public ScreenStatusReceiver(Context context, int numberOfPress, String actionMetodName){
        mNumberOfPress = numberOfPress;
        mServiceContex = context;
        actionMethod = actionMetodName;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) || intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            pressCounter();
        }
    }

    private void pressCounter() {
        long clickTime = System.currentTimeMillis();
        if(mPressCount == 0)
            mPressCount++;
        else{
            if ((clickTime - mLastClickTime) < DOUBLE_CLICK_TIME_DELTA ){
                mPressCount++;
                if(mPressCount >= mNumberOfPress){
                    Vibrator v = (Vibrator) mServiceContex.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(300);
                    mPressCount = 0;
                    if(actionMethod != null){
                        try {
                            Method m = mServiceContex.getClass().getMethod(actionMethod, null);
                            m.invoke(mServiceContex);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                            Toast.makeText(mServiceContex, e.getMessage(), Toast.LENGTH_LONG).show();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }else{
                mPressCount = 0;
            }
        }
        mLastClickTime = clickTime;
    }
}