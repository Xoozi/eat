package com.xoozi.eat.dom;

import org.jdom.Element;

import com.xoozi.andromeda.utils.Utils;
import com.xoozi.eat.EATActivity;
import com.xoozi.eat.R;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DOMItem{

    public static final String NODE_ITEM            = "item";
    public static final String ATTR_NAME            = "name";
    public static final String ATTR_CONTENT         = "content";
    public static final String ATTR_TYPE            = "type";

    public static final String VALUE_TYPE_LIST      = "list";
    public static final String VALUE_TYPE_ACTIVITY  = "activity";


    private String  _name;
    private String  _content;
    private String  _type;

    public DOMItem(Element element){
        _name       = element.getAttributeValue(ATTR_NAME);
        _content    = element.getAttributeValue(ATTR_CONTENT);
        _type       = element.getAttributeValue(ATTR_TYPE);
    }

    public String getName(){
        return _name;
    }

    public String getContent(){
        return _content;
    }
    
    public String getType(){
        return _type;
    }

    public void action(Context context){
        if(null != _type){
            if(_type.equals(VALUE_TYPE_LIST)){
                _actionList(context);
            }else if(_type.equals(VALUE_TYPE_ACTIVITY)){
                _actionActivity(context);
            }else{
                Utils.amLog("action wtf");
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private void _actionActivity(Context context){
        Utils.amLog("action activity:"+_content);
        
        try {
            Class cl = Class.forName(_content);
            Intent  intent   = new Intent(context, cl);
            intent.putExtra(EATActivity.KEY_NAME, _name);
            context.startActivity(intent);
        } catch (ClassNotFoundException e) {
            Toast.makeText(context, R.string.toast_no_activity, 
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void _actionList(Context context){
        Utils.amLog("action list:"+_content);

        Intent  intent   = new Intent(context, EATActivity.class);
        intent.putExtra(EATActivity.KEY_CONTENT, _content);
        context.startActivity(intent);
    }
}
