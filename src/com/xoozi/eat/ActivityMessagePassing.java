package com.xoozi.eat;



import com.xoozi.andromeda.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;

public class ActivityMessagePassing extends Activity implements OnClickListener {

    private LooperThread        _looperThread;
    private int                 _index = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_passing);

        _initWork();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _looperThread._handler.getLooper().quit();
    }

    @Override
    public void onClick(View view){
        Message msg = _looperThread._handler.obtainMessage(_index++);
        _looperThread._handler.sendMessage(msg);
    }


    private void _initWork(){
        Intent intent = getIntent();
        setTitle(intent.getStringExtra(EATActivity.KEY_NAME));

        findViewById(R.id.btn_switch).setOnClickListener(this);
        
        _looperThread = new LooperThread();
        _looperThread.start();        
    }

    private void _doLongRunningOperation(int what){

        Utils.amLog("Index:" + what + " LongRunningOperation start");
        SystemClock.sleep(2000);
        Utils.amLog("Index:" + what + " LongRunningOperation end");
    }

    private class LooperThread extends Thread 
            implements MessageQueue.IdleHandler{
        private Handler  _handler;

        @Override
        public void run() {
            Looper.prepare();

            _handler = new Handler(){

                @Override
                public void handleMessage(Message msg) {
                    _doLongRunningOperation(msg.what);
                }

            };

            Looper.myQueue().addIdleHandler(this);
            Looper.myQueue().addIdleHandler(
                    new MessageQueue.IdleHandler(){
                            @Override
                            public boolean queueIdle(){
                                Utils.amLog("LooperThread Idle b");
                                return true;
                            }
                        });
            Looper.loop();
            Utils.amLog("LooperThread quit...");
        }

        @Override
        public boolean queueIdle() {
            Utils.amLog("LooperThread Idle a");
            return true;
        }

    }

}
