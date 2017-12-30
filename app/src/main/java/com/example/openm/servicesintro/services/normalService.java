package com.example.openm.servicesintro.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.openm.servicesintro.get.GetData;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

/**
 * Created by aliazaz on 28-Dec-17.
 */

public class normalService extends Service {

    Handler mHandler;
    boolean flag = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mHandler = new Handler();
        flag = intent.getBooleanExtra("flag", true);
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Start Service", Toast.LENGTH_SHORT).show();
        try {
            sendingData(normalService.this, new GetData(normalService.this, intent.getBooleanExtra("flag", true)).execute().get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Stop Service", Toast.LENGTH_SHORT).show();
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
