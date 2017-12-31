package com.example.openm.servicesintro.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.openm.servicesintro.get.GetData;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

/**
 * Created by aliazaz on 28-Dec-17.
 */

public class intentService extends IntentService {

    private static final String TAG = intentService.class.getName();
    Handler handler;

    public intentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {

            /*We use this handler cause Intent Service run on its own worker thread that why it can't show toast*/
            handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // run this code in the main thread
                    Toast.makeText(getApplicationContext(), "Start Service", Toast.LENGTH_SHORT).show();
                }
            });

            /*Pass the fetched data in function*/
            sendingData(getApplicationContext(), new GetData(getApplicationContext(), intent.getBooleanExtra("flag", true)).execute().get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void sendingData(final Context mContext, String result) {
        if (result != null) {
            String json = result;
            if (json.length() > 0) {
                try {
                    Log.i(TAG, "Result: " + new JSONArray(json).length());

                    /*Broadcast the data. Now you can receive this broadcast anywhere in the
                    * whole app with the "extends of BroadcastReceiver" class*/
                    Intent broadcast = new Intent();
                    broadcast.setAction("sendingData"); //with this name you can get data
                    broadcast.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    broadcast.putExtra("data", json);
                    sendBroadcast(broadcast);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // run this code in the main thread
                        Toast.makeText(mContext, "Result 0", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // run this code in the main thread
                    Toast.makeText(mContext, "Connection Error", Toast.LENGTH_SHORT).show();
                }
            });

            mContext.stopService(new Intent(mContext, intentService.class));
        }
    }


}
