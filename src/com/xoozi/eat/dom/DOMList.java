package com.xoozi.eat.dom;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

public class DOMList extends DOMItem {

    private List<DOMItem>   _list = new ArrayList<DOMItem>();

    public DOMList(Element element) {
        super(element);

        List<Element> children = XmlDOM.getChildElements(element, NODE_ITEM);
        
        for(Element e:children){
            DOMItem item = new DOMItem(e);
            _list.add(item);
        }
    }

    public List<DOMItem> getList(){
        return _list;
    }
}
