package app.studio.com.appframework.kxml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Films {

	private List<Film> filmList;

	public List<Film> getFilmList() {
		return filmList;
	}
	
	public void parseXml(XmlPullParser localKXmlParser,String parentName) throws XmlPullParserException, IOException {
        boolean flag =true;
		int eventType = localKXmlParser.getEventType();
		String elemName="";
		Film film;
        while(flag)
        {
        	eventType = localKXmlParser.getEventType();
        	switch (eventType) { 
        	case XmlPullParser.START_TAG:
        		elemName= localKXmlParser.getName();
        		if(parentName.equals(elemName))
        		{
        			filmList = new ArrayList<Film>();
        		}
        		else if("film".equals(elemName))
        		{
        			film = new Film();
        			film.parseXml(localKXmlParser, elemName);
        			filmList.add(film);
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
