package com.xoozi.eat;


import java.util.Random;

import com.xoozi.andromeda.utils.Utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ActivityOnewayMessenger extends Activity 
    implements OnClickListener{

    private Messenger   _remoteService = null;
    private boolean     _bound = false; 
    private ServiceConnection _remoteConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder service) {

           _remoteService = new Messenger(service);
           _bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

           _remoteService = null;
           _bound = false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oneway_messenger);

        _initWork();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(_bound){
            unbindService(_remoteConnection);
        }
    }

    @Override
    public void onClick(View view){

        if(_bound){
            try {
                _remoteService.send(Message.obtain(null, 2, 0, 0));
            } catch (RemoteException e) {
                Utils.amLog("remote send:" + e.toString());
            }
        }
    }


    private void _initWork(){
        Intent intent = getIntent();
        setTitle(intent.getStringExtra(EATActivity.KEY_NAME));
        
        findViewById(R.id.btn_remote_send).setOnClickListener(this);

        Intent bindIntent = new Intent("com.xoozi.eat.chapter5.ACTION_BIND");
        bindService(bindIntent, _remoteConnection, Context.BIND_AUTO_CREATE);
    }
}
