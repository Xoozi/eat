package com.xoozi.eat;



import com.xoozi.andromeda.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ActivitySharedMemory extends Activity implements OnClickListener {

    private Watchword   _watchword = new Watchword();
    private Thread      _waitingThread; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_memory);

        _initWork();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _waitingThread.interrupt();
    }

    @Override
    public void onClick(View view){
        synchronized(_watchword){
            _watchword.flag = 1;
            _watchword.msg = "wtf";
            _watchword.notifyAll();  
        }
    }


    private void _initWork(){
        Intent intent = getIntent();
        setTitle(intent.getStringExtra(EATActivity.KEY_NAME));

        findViewById(R.id.btn_switch).setOnClickListener(this);
        
        _waitingThread = new Thread(new WaitingTask(_watchword));
        _waitingThread.start();
    }


    private class Watchword{
        String msg = "no";
        int flag = 0;
    }

    private class WaitingTask implements Runnable {

        private final Watchword   _w;

        WaitingTask(Watchword w){
            _w = w;
        }

        @Override
        public void run() {
            synchronized(_w){
                while(0 == _w.flag){
                    try {
                        _w.wait();
                    } catch (InterruptedException e) {
                        Utils.amLog("WaitingTask interrupted"); 
                        return ;
                    }
                }                

                Utils.amLog("Got the watchword:"+_w.msg);
            }
        }

    }
}

