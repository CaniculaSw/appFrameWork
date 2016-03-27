package app.studio.com.appframework.kxml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class Beauty {
	private String name;
	private String age;
	private Films films;

	public String getName() {
		return name;
	}

	public String getAge() {
		return age;
	}

	public Films getFilms() {
		return films;
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
        		else if("age".equals(elemName))
        		{
        			age = localKXmlParser.nextText();
        		}
        		else if("films".equals(elemName))
        		{
        			films = new Films();
        			films.parseXml(localKXmlParser, elemName);     			
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
