package jab;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.pubsub.*;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ytihoglaz
 * Date: 26.08.13
 * Time: 12:40
 * To change this template use File | Settings | File Templates.
 */
public class Feed {

    @Override
    public String toString() {
        return "Feed{" +
                "name='" + name + '\'' +
                ", jabber=" + jabber +
                ", items=" + items +
                ", item_names=" + item_names +
                ", newsHandlers=" + newsHandlers +
                ", me=" + me +
                ", unreadCounter=" + unreadCounter +
                '}';
    }

    public void Unsubscribe() throws XMPPException {
        List<Subscription> subscriptions = jabber.pmanager.getSubscriptions();
        for (Subscription s: subscriptions){
            if(s.getNode().equals(name))
            {
                LeafNode leafNode = jabber.pmanager.getNode(this.name);
                leafNode.unsubscribe(jabber.getJid(),s.getId());
            }
        }
    }



    public Feed(Subscription subscription, JabberClient jabber) throws NoSuchAlgorithmException {
        this.name = subscription.getNode();
        this.jabber = jabber;

        items = new Hashtable<String, IRss>();

        item_names = new ArrayList<String>();
        item_names_set = new Hashtable<String, String>();
        newsHandlers = new ArrayList<NewsArrivedEvent>();

        LeafNode leaf = null;
        try {
            leaf = this.jabber.pmanager.getNode(this.name);
            leaf.addItemEventListener(new ItemEventListener() {
                @Override
                public void handlePublishedItems(ItemPublishEvent itemPublishEvent) {
                    Collection<? extends Item> list = itemPublishEvent.getItems();
                    for(Item itm:list)
                    {
                        try {
                            NewsItem newsItem = new NewsItem(itm.toXML(), itm.getId(), true);
                            String title = newsItem.getTitle();
                            String title_hash = newsItem.getTitleHash();
                            String stored_title = item_names_set.get(title_hash);
                            if (stored_title == null)
                            {
                                item_names_set.put(title_hash, title);
                                item_names.add(0,title);
                                items.put(title, newsItem);
                                unreadCounter++;
                            }

                        } catch (ParserConfigurationException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (IOException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (SAXException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (TransformerException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }

                    for (NewsArrivedEvent event: newsHandlers){
                        event.HandleNews(((Feed)me).getName());
                    }
                }
            });
        } catch (XMPPException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }



        List<Message> offline_items = jabber.getOffline_messages();

        for (Message s:offline_items){
            EventElement event = (EventElement) s.getExtension("event","http://jabber.org/protocol/pubsub#event");

            List<PacketExtension> eventExtensions = event.getExtensions();

            for (PacketExtension item: eventExtensions){
                ItemsExtension itemsExtension = (ItemsExtension) item;
                List<? extends PacketExtension> list = itemsExtension.getItems();
                if(itemsExtension.getNode().equals(name))
                {
                    for (PacketExtension i: list){

                        try {
                            Item offline_item = (Item) i;

                                NewsItem newsItem = new NewsItem(offline_item.toXML(), offline_item.getId(), true);

                                String title = newsItem.getTitle();
                                String title_hash = newsItem.getTitleHash();
                                String stored_title = item_names_set.get(title_hash);
                                if (stored_title == null)
                                {
                                    item_names_set.put(title_hash, title);
                                    item_names.add(0, title);
                                    items.put(title, newsItem);
                                    unreadCounter++;
                                }

                        } catch (ParserConfigurationException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (IOException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (SAXException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (TransformerException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }

                    }
                }
            }
        }

        try {
            loadPersistent(leaf);
        } catch (XMPPException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TransformerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        me = this;
    }

    private void loadPersistent(LeafNode leaf) throws XMPPException, ParserConfigurationException, IOException, SAXException, TransformerException, NoSuchAlgorithmException, CloneNotSupportedException {
        DiscoverItems nodeItems = leaf.discoverItems();
        Iterator<DiscoverItems.Item> itr = nodeItems.getItems();

        while(itr.hasNext()) {

            List<String> ids = new ArrayList<String>();
            DiscoverItems.Item i = itr.next();
            String id = i.getName();
            ids.add(id);

            List<Item> its = leaf.getItems(ids);
            for (Object ii: its){
                if(ii.getClass().equals(PayloadItem.class)){
                    NewsItem newsItem = new NewsItem( ((Item) ii).toXML(), ((Item) ii).getId(), false);
                    String title = newsItem.getTitle();
                    String title_hash = newsItem.getTitleHash();
                    String stored_title = item_names_set.get(title_hash);
                    if (stored_title == null)
                    {
                        item_names_set.put(title_hash, title);
                        item_names.add(0, title);
                        items.put(title, newsItem);
                        unreadCounter++;
                    }
                }
//                else if (ii.getClass().equals(org.jivesoftware.smack.packet.DefaultPacketExtension.class)){
//                    DefaultPacketExtension extension = (DefaultPacketExtension)ii;
//                    NewsItem newsItem = new NewsItem(extension.toXML(), ((DefaultPacketExtension)ii)., false);
//                    String title = newsItem.getTitle();
//                    item_names.add(title);
//                    items.put(title,newsItem);
//                }
            }
        }
    }

    public String getName() {
        return name;
    }

    private final String name;
    private JabberClient jabber;

    public Dictionary<String, IRss> getItems() {
        return items;
    }

    private Dictionary<String,IRss> items;

    public List<String> getItem_names() {
        return item_names;
    }

    private List<String> item_names;
    private Dictionary<String, String> item_names_set;

    public void addNewsHandlers(NewsArrivedEvent newsHandler) {
        this.newsHandlers.add(newsHandler);
    }

    public void setReaded(String item_name) throws CloneNotSupportedException {
        NewsItem itm = (NewsItem) items.get(item_name);
        itm.setUnread(false);

        int item_num = item_names.indexOf(item_name);
        String title_hash = itm.getTitleHash();
        item_names_set.remove(title_hash);
        item_names.remove(item_num);
        items.remove(item_name);

        item_names_set.put(title_hash, itm.getTitle());
        item_names.add(item_num,itm.getTitle());
        items.put(itm.getTitle(),itm);
        unreadCounter--;
        if (unreadCounter < 0)
            unreadCounter = 0;
    }

    private List<NewsArrivedEvent> newsHandlers;

    private final Object me;

    public int getUnreadCounter() {
        return unreadCounter;
    }

    private int unreadCounter = 0;
}
