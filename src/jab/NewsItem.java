package jab;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: pulka
 * Date: 23.08.13
 * Time: 23:16
 * To change this template use File | Settings | File Templates.
 */
public class NewsItem implements IRss {
    public NewsItem(String xml, boolean unread) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        this.unread = unread;

        builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        link = xml.substring(xml.indexOf("<link>"),xml.indexOf("</link>"));
        title = xml.substring(xml.indexOf("<title>"),xml.indexOf("</title>")).replace("<title>"," ");
        description = xml.substring(xml.indexOf("<description>"),xml.indexOf("</description>"));


        HtmlCleaner cleaner = new HtmlCleaner();

        CleanerProperties props = cleaner.getProperties();

        props.setRecognizeUnicodeChars(true);

        props.setOmitUnknownTags(true);

        TagNode t = cleaner.clean(xml);
        String clean_xml = cleaner.getInnerHtml(t);

        t = cleaner.clean(description);
        description = cleaner.getInnerHtml(t);

        //String lnk = String.format("<a href=\"%s\" > %s </a>",link, link);
        t = cleaner.clean(link);
        link =  cleaner.getInnerHtml(t);

    }

    private String serElement(Node node) throws TransformerException {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        StringWriter buffer = new StringWriter();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(node),
                new StreamResult(buffer));
        String str = buffer.toString();
        return str;
    }

    protected String getElementValue(Element parent,String label) {
        return getCharacterDataFromElement((Element)parent.getElementsByTagName(label).item(0));
    }

    private String getCharacterDataFromElement(Element e) {
        try {
            Node child = e.getFirstChild();
            if(child instanceof CharacterData) {
                CharacterData cd = (CharacterData) child;
                return cd.getData();
            }
        }
        catch(Exception ex) {

        }
        return "";
    } //private String getCharacterDataFromElement


    private final DocumentBuilder builder;
    private String title;
    private String link;
    private String description;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getLink() {
        return link;
    }

    @Override
    public Date getPDate() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getAuthor() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean getUnread() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private boolean unread;
}
