package jab;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.pubsub.*;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ytihoglaz
 * Date: 26.08.13
 * Time: 12:40
 * To change this template use File | Settings | File Templates.
 */
public class Feed {



    public Feed(Subscription subscription, JabberClient jabber) throws XMPPException, ParserConfigurationException, TransformerException, SAXException, IOException {
        this.name = subscription.getNode();
        this.jabber = jabber;

        items = new HashMap<String, IRss>();

        item_names = new ArrayList<String>();

        LeafNode leaf = this.jabber.pmanager.getNode(this.name);

//        leaf.subscribe(this.jabber.getJid());
        leaf.addItemEventListener(new ItemEventListener() {
            @Override
            public void handlePublishedItems(ItemPublishEvent itemPublishEvent) {
                Collection<? extends Item> list = itemPublishEvent.getItems();
                unreadCounter+= list.size();

                for(Item itm:list)
                {
                    try {
                        NewsItem newsItem = new NewsItem(itm.toXML());
                        String title = newsItem.getTitle();

                        item_names.add(title);
                        items.put(title,newsItem);

                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (SAXException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (TransformerException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }

                //TODO: Add handler;
            }
        });


        //LeafNode n = jabber.pmanager.getNode(name);

        DiscoverItems nodeItems = leaf.discoverItems();
        Iterator<DiscoverItems.Item> itr = nodeItems.getItems();


        while(itr.hasNext()) {
            List<String> ids = new ArrayList<String>(1);
            DiscoverItems.Item i = itr.next();
            String id = i.getName();
            ids.add(id);
            List<Item> its = leaf.getItems(ids);
            for (Item ii: its){
                NewsItem newsItem = new NewsItem(ii.toXML());
                String title = newsItem.getTitle();
                item_names.add(title);
                items.put(title,newsItem);
            }
        }



    }

    public String getName() {
        return name;
    }

    private final String name;
    private JabberClient jabber;

    public Map<String, IRss> getItems() {
        return items;
    }

    private Map<String,IRss> items;

    public List<String> getItem_names() {
        return item_names;
    }

    private List<String> item_names;

    public int getUnreadCounter() {
        return unreadCounter;
    }

    private int unreadCounter = 0;
}
