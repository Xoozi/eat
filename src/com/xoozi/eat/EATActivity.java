package com.xoozi.eat;

import java.io.IOException;
import java.util.List;

import org.jdom.JDOMException;

import com.xoozi.eat.dom.AssetConfig;
import com.xoozi.eat.dom.DOMItem;
import com.xoozi.eat.dom.DOMList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.BaseAdapter;

public class EATActivity extends Activity implements OnItemClickListener{
    public static final String KEY_CONTENT = "content";
    public static final String KEY_NAME = "name";
    
    private DOMList         _domList;
    private List<DOMItem>   _itemList;
    private ListView        _listView;
    private ListAdapter     _listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        _initWork();
    }

    @Override
    public void onItemClick(AdapterView<?> av, View view, int pos, long id) {

        DOMItem item = _itemList.get(pos);
        item.action(this);
    }

    private void _initWork(){
        Intent intent = getIntent();

        String content = intent.getStringExtra(KEY_CONTENT);

        try {
            _domList = AssetConfig.loadConfig(this, content);
            _itemList = _domList.getList();
            _listView = (ListView) findViewById(R.id.list_items);
            setTitle(_domList.getName());

            _listAdapter = new ListAdapter(this);
            _listView.setAdapter(_listAdapter);
            _listView.setOnItemClickListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }
    }


    private class ListAdapter extends BaseAdapter {

        private LayoutInflater      _layoutInflater;

        ListAdapter(Context context){
            
            _layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if(null == _itemList){
                return 0;
            }else{
                return _itemList.size();
            }
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            
            if(null==convertView){
                convertView = _layoutInflater.inflate(R.layout.list_item, null);
            }
            
            TextView txtName = (TextView) convertView.findViewById(R.id.txt_name);

            txtName.setText(_itemList.get(position).getName());
            
            return convertView;
        }
    }

}
