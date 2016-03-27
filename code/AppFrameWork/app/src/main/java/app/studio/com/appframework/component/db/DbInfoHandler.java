package app.studio.com.appframework.component.db;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import library.sswwm.com.component.log.Logger;

/**
 * Created by Administrator on 2016/3/3.
 */
public class DbInfoHandler extends DefaultHandler
{
    private String strXmlTag = null;

    private DatabaseInfo databaseInfo;

    private List<DatabaseInfo.Table> tableList;

    private List<DatabaseInfo.Field> fileList;

    private DatabaseInfo.Table table;

    private DatabaseInfo.Field field;

    public void doParse(DatabaseInfo inDatabaseInfo, String inStrToParse)
            throws ParserConfigurationException, SAXException, IOException
    {

        Logger.i("DbInfoHandler", "DbInfoHandler : \n" + inStrToParse);
        this.databaseInfo = inDatabaseInfo;
        StringReader read = new StringReader(inStrToParse);
        InputSource source = new InputSource(read);
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();
        XMLReader xr = sp.getXMLReader();
        xr.setContentHandler(this);
        xr.parse(source);
    }

    /**
     * [XML开始]<BR>
     * [功能详细描述]
     *
     * @throws SAXException XML异常
     * @see DefaultHandler#startDocument()
     */
    @Override
    public void startDocument() throws SAXException
    {
        tableList = new ArrayList<>();
        databaseInfo.setListTable(tableList);
        super.startDocument();
    }

    /**
     * [XML结束]<BR>
     * [功能详细描述]
     *
     * @throws SAXException XML异常
     * @see DefaultHandler#endDocument()
     */
    public void endDocument() throws SAXException
    {
    }

    /**
     * [XML节点开始]<BR>
     * [功能详细描述]
     *
     * @param namespaceURI 名空间
     * @param localName    名
     * @param qName        名
     * @param atts         属性
     * @throws SAXException XML异常
     */
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException
    {
        strXmlTag = localName;
        if (localName.equals("table"))
        {
            table = new DatabaseInfo.Table();
            tableList.add(table);
            fileList = new ArrayList<>();
            table.setListFiled(fileList);
        }
        if (localName.equals("field"))
        {
            field = new DatabaseInfo.Field();
            fileList.add(field);
        }
    }

    /**
     * [XML节点结束]<BR>
     * [功能详细描述]
     *
     * @param namespaceURI 名空间
     * @param localName    名
     * @param qName        名
     * @throws SAXException XML异常
     */
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException
    {
        strXmlTag = null;
    }

    /**
     * [XML值]<BR>
     * [功能详细描述]
     *
     * @param ch     字符
     * @param start  起始
     * @param length 长度
     * @see DefaultHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length)
    {
        if (strXmlTag != null)
        {
            String data = new String(ch, start, length);

            if (strXmlTag.equals("dversion"))
            {
                this.databaseInfo.setVersion(data);
            }
            else if (strXmlTag.equals("dname"))
            {
                this.databaseInfo.setName(data);
            }
            else if (strXmlTag.equals("tname"))
            {
                this.table.setName(data);
            }
            else if (strXmlTag.equals("fname"))
            {
                this.field.setName(data);
            }
            else if (strXmlTag.equals("ftype"))
            {
                this.field.setType(data);
            }
            else if (strXmlTag.equals("fobligatory"))
            {
                this.field.setObligatory(data);
            }
        }
    }
}
