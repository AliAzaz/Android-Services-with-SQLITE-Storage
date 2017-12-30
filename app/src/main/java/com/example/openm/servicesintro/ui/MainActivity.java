package com.example.openm.servicesintro.ui;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.openm.servicesintro.R;
import com.example.openm.servicesintro.core.DataContract;
import com.example.openm.servicesintro.core.DatabaseHelper;
import com.example.openm.servicesintro.services.intentService;
import com.example.openm.servicesintro.services.normalService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnStart, btnStop;
    LinearLayout fldGrp01;
    ListView lstData;

    MyReceiver receiver;
    Boolean flag = true;

    DatabaseHelper db; /*database call object*/
    ListAdapter adapter;
    ArrayList<DataContract> dataLst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        fldGrp01 = (LinearLayout) findViewById(R.id.fldGrp01);
        lstData = (ListView) findViewById(R.id.lstData);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        /* Filtering Data that's coming from service */
        IntentFilter intent = new IntentFilter();
        intent.addAction("sendingData");
        receiver = new MyReceiver();
        registerReceiver(receiver, intent);

        /*Initialize db object*/
        db = new DatabaseHelper(this);
        dataLst = getDataDB();

        /*Setting up listview*/
        adapter = new ListAdapter(this, android.R.layout.simple_list_item_1, dataLst);
        lstData.setAdapter(adapter);

        /*Check service is running or not*/
        btnStart.setEnabled(isMyServiceRunning());

    }

    /*OnClick listener*/
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnStart:
                if (flag) {
                    startService(new Intent(getApplicationContext(), normalService.class).putExtra("flag", flag));
                } else {
                    startService(new Intent(getApplicationContext(), intentService.class).putExtra("flag", flag));
                }

                /*Checking Intent service currently running or not*/
                btnStart.setEnabled(isMyServiceRunning());

                break;
            case R.id.btnStop:
                if (stopService(new Intent(getApplicationContext(), normalService.class))) {
                    /*Checking Intent service currently running or not*/
                    btnStart.setEnabled(isMyServiceRunning());
                }
                break;
        }
    }

    /*Check is service still running or not*/
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.v("debug", service.service.getClassName());
            if (normalService.class.getName().equals(service.service.getClassName()) || intentService.class.getName().equals(service.service.getClassName())) {
                return false;
            }
        }
        return true;
    }

    /*Receive data from service*/
    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                JSONArray jsonArray = new JSONArray(intent.getExtras().getString("data"));

                db.insertData(jsonArray);
                dataLst = getDataDB();

            } catch (Exception e) {}
            finally {
                /*Setting up listview*/
                adapter = new ListAdapter(context, android.R.layout.simple_list_item_1, dataLst);
                adapter.notifyDataSetChanged();
                lstData.setAdapter(adapter);
            }

            /*Checking Intent service currently running or not*/
            btnStart.setEnabled(isMyServiceRunning());

        }
    }

    /*When application closed*/
    @Override
    protected void onDestroy() {
        this.unregisterReceiver(receiver);

        super.onDestroy();
    }

    /*Setting up menus*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        /*Check service is running or not*/
        btnStart.setEnabled(isMyServiceRunning());

        /*Clear listview*/
        lstData.setAdapter(null);

        if (id == R.id.action_nservice) {

            /*Setting flag true for Normal Service selection*/
            flag = true;
            fldGrp01.setVisibility(View.VISIBLE);

        } else if (id == R.id.action_iservice) {

            /*Setting flag false for Intent Service selection*/
            flag = false;

            /*Disabling Stop Service button*/
            fldGrp01.setVisibility(View.GONE);
        }

        return super.onOptionsItemSelected(item);
    }

    /*Get Data from database*/
    public ArrayList<DataContract> getDataDB() {
        Collection<DataContract> dt = db.getAllData();
        ArrayList<DataContract> data = new ArrayList<>();

        for (DataContract d : dt) {
            data.add(d);
        }

        return data;
    }

    /*Add custom listview*/
    public class ListAdapter extends ArrayAdapter {

        ArrayList<DataContract> list = new ArrayList<>();

        public ListAdapter(Context context, int textViewResourceId, ArrayList<DataContract> objects) {
            super(context, textViewResourceId, objects);
            list = objects;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            View v = view;
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.activity_lstview, null);
            TextView txtID = (TextView) v.findViewById(R.id.txtID);
            TextView txtTitle = (TextView) v.findViewById(R.id.txtTitle);
            TextView txtBody = (TextView) v.findViewById(R.id.txtBody);
            txtID.setText("ID:" + String.valueOf(list.get(position).getId()));
            txtTitle.setText("Title:" + list.get(position).getTitle());
            txtBody.setText("Body:" + list.get(position).getBody().replace("\\\n", "\n"));

            return v;
        }
    }


}