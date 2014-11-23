package com.xoozi.eat;


import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.xoozi.andromeda.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class ActivityPipes extends Activity implements TextWatcher, OnClickListener{

    private EditText            _edtWriter;
    
    private PipedReader         _reader;
    private PipedWriter         _writer;
    private Thread              _workerThread;
    private ConsumerProducer    _bq;
    private Thread              _bqThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pipes);

        _initWork();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        _workerThread.interrupt();
        _bqThread.interrupt();

        try {
            _reader.close();
            _writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void afterTextChanged(Editable arg0) {
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
            int arg3) {
    }

    @Override
    public void onTextChanged(CharSequence cs, int start, int before, int count) {

        if(count > before){
            try {
                _writer.write(cs.subSequence(start, start + count).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClick(View arg0) {
        String msg = _edtWriter.getText().toString();
        try {
            _bq.produce(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void _initWork(){
        Intent intent = getIntent();
        setTitle(intent.getStringExtra(EATActivity.KEY_NAME));

        findViewById(R.id.btn_bq_put).setOnClickListener(this);

        _edtWriter = (EditText) findViewById(R.id.edt_writer);
        _edtWriter.addTextChangedListener(this);

        _reader = new PipedReader();
        _writer = new PipedWriter();

        try{
            _writer.connect(_reader);        
        }catch(IOException e){
            e.printStackTrace();
        }

        _workerThread = new Thread(new TextHandlerTask(_reader));
        _workerThread.start();


        _bq = new ConsumerProducer();
        _bqThread = new Thread(new BlockingQueueTask(_bq));
        _bqThread.start();
    }




    private class TextHandlerTask implements Runnable {

        private final PipedReader   _r;

        public TextHandlerTask(PipedReader reader){
            _r = reader;
        }

        @Override
        public void run() {

            int i = 0;
            try {
                while(!Thread.currentThread().isInterrupted() && 
                        (i = _r.read()) != -1){
                    char c = (char) i;

                    Utils.amLog("char = " + c);
                }

                Utils.amLog("loop end i=" + i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private class ConsumerProducer{

        private final int LIMIT = 10;
        private BlockingQueue<String> _bq = 
            new LinkedBlockingQueue<String>(LIMIT);

        public void produce(String str) throws InterruptedException {

          _bq.put(str);
        }

        public String consume() throws InterruptedException {
         return _bq.take();
        }
    }
    
    private class BlockingQueueTask implements Runnable {

        private final  ConsumerProducer _cp;

        BlockingQueueTask(ConsumerProducer cp){
            _cp = cp;
        }

        @Override
        public void run() {

            String msg;
            while(!Thread.currentThread().isInterrupted()){
                try {
                    msg = _cp.consume();
                    Utils.amLog("blocking queue get:"+msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return ;
                }
            }
        }
    }
}
