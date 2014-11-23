package com.xoozi.eat;

import com.xoozi.andromeda.utils.Utils;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class ServiceMessenger extends Service{

    private WorkerThread    _workThread;
    private Messenger       _messenger;

    

    @Override
    public void onCreate() {
        super.onCreate();
        _workThread = new WorkerThread();
        _workThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _workThread.quit();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        synchronized(this){
            if(null == _messenger){
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return _messenger.getBinder();
    }

    private void _onWorkerPrepared(){
        _messenger = new Messenger(_workThread._workHandler);

        synchronized(this){
            notifyAll();
        }
    }

    private class WorkerThread extends Thread{
        Handler     _workHandler;

        @Override
        public void run() {
            Looper.prepare();

            _workHandler = new Handler(){

                @Override
                public void handleMessage(Message msg) {
                    Utils.amLog("Got a msg what:" + msg.what);

                    Bundle data = msg.getData();
                    if(null != data){
                        String name = data.getString("name");
                        if(null != name){
                            Utils.amLog("msg'data name:" + name);
                        }
                    }

                    switch(msg.what){
                        case 1:
                        try {
                            msg.replyTo.send(Message.obtain(null, msg.what, 1,
                                    3));
                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            };
            _onWorkerPrepared();            
            Looper.loop();
        }

        public void quit(){
            _workHandler.getLooper().quit();
        }
    }
}
