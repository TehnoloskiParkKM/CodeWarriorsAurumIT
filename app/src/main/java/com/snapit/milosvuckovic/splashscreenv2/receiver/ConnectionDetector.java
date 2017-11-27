package com.snapit.milosvuckovic.splashscreenv2.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;


public class ConnectionDetector {

    Context context;

    public ConnectionDetector(Context context){
        this.context=context;
    }


    public boolean isConnected(){
        ConnectivityManager connectivity=(ConnectivityManager)
                context.getSystemService(Service.CONNECTIVITY_SERVICE);

        if (connectivity != null){
            NetworkInfo info = connectivity.getActiveNetworkInfo();

            if(info != null){
                if(info.getState() == NetworkInfo.State.CONNECTED){
                    return true;
                }
            }
        }
        return false;
    }
}