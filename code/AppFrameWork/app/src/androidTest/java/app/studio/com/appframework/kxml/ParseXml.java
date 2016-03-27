package app.studio.com.appframework.kxml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ParseXml {

	public Beauties beauties;
	public static void main(String[] args) {
		ParseXml parse =new ParseXml();
		try {
			System.out.println("start");
			parse.parseXml(new FileInputStream(new File("./xml/test.xml")));
			System.out.println("end");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void parseXml(InputStream paramInputStream) {
	    try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			//    通过XmlPullParserFactory工厂类实例化一个XmlPullParser解析类
			XmlPullParser localKXmlParser = factory.newPullParser();
			localKXmlParser.setInput(paramInputStream, "UTF-8");
			int eventType = localKXmlParser.getEventType();
			String elemName="";
			while (eventType != localKXmlParser.END_DOCUMENT){  
				switch (eventType) { 
				case XmlPullParser.START_TAG:
					elemName = localKXmlParser.getName();
					if("beauties".equals(elemName))
					{
						beauties = new Beauties();
						beauties.parseXml(localKXmlParser, elemName);
					}
			     default:
			    	 break;
				}
				 eventType =localKXmlParser.next();
				}
	    
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
}
