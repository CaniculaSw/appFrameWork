package app.studio.com.appframework.kxml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Beauties {
	private String count;
	private List<Beauty> beautyList;

	public List<Beauty> getBeautyList() {
		return beautyList;
	}

	public String getCount() {
		return count;
	}
	
	public void parseXml(XmlPullParser localKXmlParser,String parentName) throws XmlPullParserException, IOException {
        boolean flag =true;
		int eventType = localKXmlParser.getEventType();
		String elemName="";
		Beauty beauty;
        while(flag)
        {
        	switch (eventType) { 
        	case XmlPullParser.START_TAG:
        		elemName= localKXmlParser.getName();
        		if(parentName.equals(elemName))
        		{
        			count = localKXmlParser.getAttributeValue("","count");
        			beautyList = new ArrayList<Beauty>();
        		}
        		else if("beauty".equals(elemName))
        		{
        			beauty = new Beauty();
        			beauty.parseXml(localKXmlParser, elemName);
        			beautyList.add(beauty);
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
