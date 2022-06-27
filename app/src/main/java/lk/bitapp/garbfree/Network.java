package lk.bitapp.garbfree;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class Network {

    static Context context;
    //We use this class to determine if the application has been connected to either WIFI Or MobileNetwork

    private static Network instance = new Network();

    ConnectivityManager connectivityManager;
    boolean connected = false;

    public static Network getInstance(Context ctx) {
        context = ctx.getApplicationContext();
        return instance;
    }

    public boolean isOnline() {
        try {
            connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected =(networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected());
            return connected;

        } catch (Exception e) {
            System.out.println("CheckConnectivity Error: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return connected;
    }
}

