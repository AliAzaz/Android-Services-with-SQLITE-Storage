package com.example.openm.servicesintro.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.openm.servicesintro.get.GetData;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

/**
 * Created by aliazaz on 28-Dec-17.
 */

public class intentService extends IntentService {

    public intentService() {
        super(intentService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {

            /*We use this handler cause Intent Service run on its worker thread that why it can't show toast*/
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // run this code in the main thread
                    Toast.makeText(getApplicationContext(), "Start Service", Toast.LENGTH_SHORT).show();
                }
            });

            sendingData(getApplicationContext(), new GetData(getApplicationContext(),intent.getBooleanExtra("flag",true)).execute().get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void sendingData(Context mContext, String result) {
        if (result != null) {
            String json = result;
            if (json.length() > 0) {
                try {
                    Toast.makeText(mContext, "Result " + new JSONArray(json).length(), Toast.LENGTH_SHORT).show();
                    Intent broadcast = new Intent();
                    broadcast.setAction("sendingData");
                    broadcast.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    broadcast.putExtra("data", json);
                    sendBroadcast(broadcast);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(mContext, "Result 0", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "Connection Error", Toast.LENGTH_SHORT).show();
        }
    }


}
