package com.xoozi.eat;


import java.util.Random;

import com.xoozi.andromeda.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ActivityTwowayMessage extends Activity implements OnClickListener {

    private final static int    SHOW_PROGRESS_BAR = 1;
    private final static int    HIDE_PROGRESS_BAR = 0;

    private TextView            _text;
    private ProgressBar         _progress;

    private BackgroundThread    _thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twoway_message);

        _initWork();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _thread.exit();
    }

    @Override
    public void onClick(View view){
        _thread.doWork();
    }



    private void _initWork(){
        Intent intent = getIntent();
        setTitle(intent.getStringExtra(EATActivity.KEY_NAME));

        _text = (TextView)findViewById(R.id.txt_value);
        _progress = (ProgressBar)findViewById(R.id.progress_test);

        findViewById(R.id.btn_send).setOnClickListener(this);

        _thread = new BackgroundThread();
        _thread.start();
    }

    private final Handler _uiHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch(msg.what){
                case SHOW_PROGRESS_BAR:
                    _progress.setVisibility(View.VISIBLE);
                    break;

                case HIDE_PROGRESS_BAR:
                    _text.setText(String.valueOf(msg.arg1));
                    _progress.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    };

    private class BackgroundThread extends Thread{

        private Handler _backgroundHandler;

        @Override
        public void run() {
            Looper.prepare();
            _backgroundHandler = new Handler();
            Looper.loop();
        }

        public void doWork(){
            
            _backgroundHandler.post(new Runnable() {
                @Override
                public void run() {
                    Message uiMsg = _uiHandler.obtainMessage(
                                    SHOW_PROGRESS_BAR, 0, 0, null);
                    _uiHandler.sendMessage(uiMsg);

                    Random r = new Random();
                    int randomInt = r.nextInt(5000);
                    SystemClock.sleep(randomInt);

                    uiMsg = _uiHandler.obtainMessage(
                                    HIDE_PROGRESS_BAR, randomInt, 0, null);
                    _uiHandler.sendMessage(uiMsg);
                }
            });
        }
        
        public void exit(){
            _backgroundHandler.getLooper().quit();
        }
    }
}
