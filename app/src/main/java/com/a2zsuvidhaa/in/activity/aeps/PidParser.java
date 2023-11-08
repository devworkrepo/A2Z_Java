package com.a2zsuvidhaa.in.activity.aeps;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

public class PidParser {


  public   static String[] parse(String xml) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xmlPullParser = factory.newPullParser();

        xmlPullParser.setInput(new StringReader(xml));
        String[] respStrings = {"na","na"};
        int eventType = xmlPullParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
                System.out.println("Start document");
            } else if (eventType == XmlPullParser.START_TAG) {
                if (xmlPullParser.getName().equalsIgnoreCase("Resp")) {

                    int count = xmlPullParser.getAttributeCount();
                    for (int i = 0; i < count; i++) {
                        String attributeName = xmlPullParser.getAttributeName(i);
                        System.out.println(attributeName);

                        if(attributeName.equalsIgnoreCase("errCode")){
                            respStrings[0] = xmlPullParser.getAttributeValue(i);
                            System.out.println("errCode : " + xmlPullParser.getAttributeValue(i));
                        }
                        if(attributeName.equalsIgnoreCase("errInfo")){
                            respStrings[1] = xmlPullParser.getAttributeValue(i);
                            System.out.println("errInfo : " + xmlPullParser.getAttributeValue(i));
                        }
                    }

                }
            }
            eventType = xmlPullParser.next();
        }
        return respStrings;
    }
}
