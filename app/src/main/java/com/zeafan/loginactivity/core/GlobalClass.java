package com.zeafan.loginactivity.core;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GlobalClass  extends Application {

    public static class StaticCore{
        public static boolean IsNetworkAvailable(Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
            return false;
        }
        public static boolean iSAvailabe_Wifi(Context context) {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return mWifi!=null&&mWifi.isConnected();
        }
        public static boolean isAvailableAcessData(Context context) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class cmClass = null;
            try {
                cmClass = Class.forName(cm.getClass().getName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            return  (Boolean) method.invoke(cm);

        }
    }
}
