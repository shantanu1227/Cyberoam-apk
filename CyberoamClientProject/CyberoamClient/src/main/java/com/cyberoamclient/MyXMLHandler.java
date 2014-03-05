package com.cyberoamclient;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by Shantanu on 7/1/13.
 */
public class MyXMLHandler extends DefaultHandler {

    public static ItemList itemList;
    public boolean current = false;
    public String currentValue = null;

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        // TODO Auto-generated method stub

        current = true;

        if (localName.equals("requestresponse"))
        {
            /** Start */
            itemList = new ItemList();

        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // TODO Auto-generated method stub
        current = false;

        if(localName.equals("status"))
        {
            itemList.setItem(currentValue);
        }
        else if(localName.equals("message"))
        {
            itemList.setManufacturer(currentValue);
        }
        else if(localName.equals("logoutmessage"))
        {
            itemList.setModel(currentValue);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        // TODO Auto-generated method stub

        if(current)
        {
            currentValue = new String(ch, start, length);
            current=false;
        }
    }


}
