package com.xoozi.eat.dom;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;

import com.xoozi.andromeda.utils.Utils;

/**
 * xml DOM
 * @author xoozi
 *
 */
public class XmlDOM
{
	private InputSource	_xmlSource;
	private InputStream	_xmlStream;
	private File 		_xmlFile;
	private Document 	_xmlDom;
	
	public XmlDOM(File xmlFile)
	{
		_xmlFile = xmlFile;
	}
	public XmlDOM(InputStream	xmlStream){
		_xmlStream = xmlStream;
	}
	public XmlDOM(InputSource xmlSource){
		_xmlSource = xmlSource;
	}
	
	
	public Element getRoot() throws JDOMException, IOException{
		
		Document xmlDom = _getXmlDom();

		Element returnNode =  xmlDom.getRootElement();

		return returnNode;
		
	}
	
	public String	getXmlDocContent(){
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		String result=null;
		try {
			
		    XMLOutputter outputter = new XMLOutputter();
		    outputter.output(_getXmlDom(), baos);
		    result = baos.toString();
            baos.close();
		} catch (Exception e) {
		    e.printStackTrace();
		} 
		
		return result;
	}
	
	
	/**
	 * Get child element by name
	 * @param parentNode
	 * @param childNodeName
	 * @return ����element
	 */
	public static Element getChildElement(Element parentNode, String childNodeName)
	{
		List<?> childList = parentNode.getChildren();
		Iterator<?> listIt = childList.iterator();
		
		Element returnNode = null;
		
		while (listIt.hasNext())
		{
			Element element = (Element) listIt.next();
			if(element.getName().equalsIgnoreCase(childNodeName))
			{
				returnNode = element;
				break;
			}
			
		}
		return returnNode;
	}
	
	/**
	 * Get all children
	 * @param parentNode
	 * @param childNodeName
	 * @return ����element
	 */
	public static List<Element> getChildElements(Element parentNode, String childNodeName)
	{
		List<?> childList = parentNode.getChildren();
		Iterator<?> listIt = childList.iterator();
		
		List<Element> returnNodes = new ArrayList<Element>();
		
		while (listIt.hasNext())
		{
			Element element = (Element) listIt.next();
			if(element.getName().equalsIgnoreCase(childNodeName))
			{
				returnNodes.add(element);
			}
			
		}
		return returnNodes;
	}


	private Document _getXmlDom() throws JDOMException, IOException{
		if(_xmlDom == null)
		{
            SAXBuilder sax = new SAXBuilder();
			
            if(null!=_xmlFile){
                _xmlDom = sax.build(_xmlFile);
            }else if(null!=_xmlStream){
                _xmlDom = sax.build(_xmlStream);
            }else if(null!=_xmlSource){
                _xmlDom = sax.build(_xmlSource);
            }else{
                Utils.amLog("WTF no file no stream");
            }
		}
		return _xmlDom;
	}
}
