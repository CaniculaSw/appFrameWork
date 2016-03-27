package app.studio.com.appframework.kxml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class Film {
	private String name;

	public String getName() {
		return name;
	}

	public void parseXml(XmlPullParser localKXmlParser,String parentName) throws XmlPullParserException, IOException {
        boolean flag =true;
		int eventType = localKXmlParser.getEventType();
		String elemName="";
        while(flag)
        {
        	switch (eventType) { 
        	case XmlPullParser.START_TAG:
        		elemName= localKXmlParser.getName();
        		if("name".equals(elemName))
        		{
        			name = localKXmlParser.nextText();
        		}
        		break;
        	case XmlPullParser.END_TAG:
        		elemName= localKXmlParser.getName();
        		if(parentName.equals(elemName))
        		{
        			flag =false;
        			continue;
        		}
        		break;
        	}
        	eventType=localKXmlParser.next();
        }
	}
}
