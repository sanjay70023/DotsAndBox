package com.tiptop.dotsandboxes.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.lang.reflect.Method;
import java.util.Locale;

import timber.log.Timber;

import static android.content.Context.WIFI_SERVICE;

public class CheckInternet {

    /* class for checking Internet connectivity */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null
                && networkInfo.isAvailable()
                && networkInfo.isConnected();
    }

    public static boolean isWifiEnabled(Context context) {
        boolean isWifiEnabled = false;
        WifiManager wifi = (WifiManager) context.getSystemService(WIFI_SERVICE);
        if (wifi.isWifiEnabled()) {
            //wifi is enabled
            isWifiEnabled = true;
        }

        return isWifiEnabled;
    }

    public static void turnOnWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    public static WifiInfo getCurrentConnectedWifi(Context context) {
        WifiInfo connectedWifiInfo = null;

        if (isWifiEnabled(context)) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
            connectedWifiInfo = wifiManager.getConnectionInfo();
        }
        return connectedWifiInfo;
    }

    public static boolean isMobileDataOn(Context context) {
        boolean isMobileDataOn = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            isMobileDataOn = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isMobileDataOn;
    }

    public static String getCurrentConnectedWIFISSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        Timber.e("SSID --->" + wInfo.getSSID().replace("\"", ""));
        return wInfo.getSSID().replace("\"", "");
    }

    public static String getCurrentMacAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        return wInfo.getBSSID();
    }

    public static String getCurrentIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        int ipAddress = wInfo.getIpAddress();

        return String.format(Locale.ENGLISH, "%d.%d.%d.%d",
                (ipAddress & 0xff),
                (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff),
                (ipAddress >> 24 & 0xff));
    }

    public static String getCurrentWifiGateWay(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        int gateWay = dhcpInfo.gateway;

        return String.format(Locale.ENGLISH, "%d.%d.%d.%d",
                (gateWay & 0xff),
                (gateWay >> 8 & 0xff),
                (gateWay >> 16 & 0xff),
                (gateWay >> 24 & 0xff));
    }
}
