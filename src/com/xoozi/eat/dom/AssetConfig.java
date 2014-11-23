package com.xoozi.eat.dom;

import java.io.IOException;
import java.io.InputStream;

import org.jdom.JDOMException;

import android.content.Context;
import android.content.res.AssetManager;

public class AssetConfig{

    public static final String CONFIG_FOLDER    = "config";
    public static final String CONFIG_ROOT      = "root.xml";

    public static DOMList loadConfig(Context context, String config)
        throws IOException, JDOMException {

        AssetManager am =  context.getAssets();

        if(null == config)
            config = CONFIG_ROOT;
        
        InputStream isConfig = am.open(CONFIG_FOLDER + "/" + config);
        XmlDOM dom = new XmlDOM(isConfig);                                        
        
        DOMList ret = new DOMList(dom.getRoot());

        isConfig.close();

        return ret;
    }
}
