package com.xoozi.eat;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.SystemClock;
import android.util.Log;
import android.util.LogPrinter;
import android.view.View;
import android.view.View.OnClickListener;

public class ActivityObservingMessage extends Activity implements OnClickListener {

    private static final String TAG = "EAT";
        
    private DumpThread      _dumpThread;
    private TraceThread     _traceThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observing_message);

        _initWork();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _dumpThread._handler.getLooper().quit();
        _traceThread._handler.getLooper().quit();
    }

    @Override
    public void onClick(View view){
        
        switch(view.getId()){
            case R.id.btn_dump:
                _testDump();
                break;

            case R.id.btn_trace:
                _traceThread.testTrace();
                break;
        }
    }


    private void _initWork(){
        Intent intent = getIntent();
        setTitle(intent.getStringExtra(EATActivity.KEY_NAME));

        findViewById(R.id.btn_dump).setOnClickListener(this);
        findViewById(R.id.btn_trace).setOnClickListener(this);


        _dumpThread = new DumpThread();
        _traceThread = new TraceThread();

        _dumpThread.start();
        _traceThread.start();
    }

    private void _testDump(){
        Handler h = _dumpThread._handler;

        h.sendEmptyMessageDelayed(1, 2000);
        h.sendEmptyMessage(2);
        h.obtainMessage(3, 0, 0, new Object()).sendToTarget();
        h.sendEmptyMessageDelayed(1, 300);
        h.postDelayed(new Runnable() {

            @Override
            public void run() {
                Log.d(TAG, "Execute");
            }
        }, 400);

        h.sendEmptyMessage(5);

        h.dump(new LogPrinter(Log.DEBUG, TAG), "");
    }


    private class DumpThread extends Thread{ 
        private Handler  _handler;

        @Override
        public void run() {
            Looper.prepare();
            _handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    Log.d(TAG, "handleMessage - what = " + msg.what);
                }
            };
            Looper.loop();
        }
    }


    private class TraceThread extends Thread {
        private Handler _handler;

        @Override
        public void run() {
            Looper.prepare();
            _handler = new Handler();
            Looper.myLooper().setMessageLogging(
                    new LogPrinter(Log.DEBUG, TAG));
            Looper.loop();
        }

        public void testTrace(){
            _handler.post(new Runnable() {

                @Override
                public void run() {
                    Log.d(TAG, "Executing Runnable");
                }
            });

            _handler.sendEmptyMessage(42);
        }
    }

}
