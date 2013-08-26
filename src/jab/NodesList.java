package jab;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.pubsub.*;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ytihoglaz
 * Date: 09.08.13
 * Time: 10:36
 * To change this template use File | Settings | File Templates.
 */
public class NodesList {

    public NodesList() {
        feeds = new HashMap<String,Feed>();
        bPublish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ConfigureForm form = new ConfigureForm(FormType.submit);
                form.setPersistentItems(false);
                form.setDeliverPayloads(true);
                form.setAccessModel(AccessModel.open);

                try {
                    LeafNode myNode = jabber.pmanager.getNode(tNodeName.getText());

                    PayloadItem p = new PayloadItem(
                            tNodeName.getText() + "-" + System.currentTimeMillis(),
                            new SimplePayload(
                                    "post",
                                    "pubsub:" + tNodeName.getText() + ":post",
                                    "<post xmlns='pubsub:" + tNodeName.getText() + ":post'><title>Lord of the Rings</title>"
                                            + tLink.getText()
                                            + "</post>"));

                    myNode.send(p);

                    loadSubscriptions();
                    loadPosts(tNodeName.getText());

                } catch (XMPPException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (TransformerException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (SAXException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
        bCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // Create the node
                ConfigureForm form = new ConfigureForm(FormType.submit);
                form.setAccessModel(AccessModel.open);
                form.setDeliverPayloads(true);
                form.setNotifyRetract(true);
                form.setPersistentItems(true);
                form.setPublishModel(PublishModel.open);
                form.setSubscribe(true);

                try {
                    LeafNode leaf = (LeafNode) jabber.pmanager.createNode(tNodeName.getText(), form);
                    subscribe(leaf);

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
                }
            }
        });
        lNodes.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                DefaultListModel dlm = (DefaultListModel) lNodes.getModel();
                String node = dlm.get(e.getFirstIndex()).toString();
                tNodeName.setText(node);
                loadPosts(node);
            }
        });
        lPosts.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                DefaultListModel dlm = (DefaultListModel) lPosts.getModel();
                String id = dlm.get(e.getFirstIndex()).toString();
                loadNewsItem(id);
            }
        });
        bSubscribe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                try {
                    LeafNode leaf = jabber.pmanager.getNode(tNodeName.getText());
                    subscribe(leaf);
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
                }

            }
        });
        bDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    jabber.pmanager.deleteNode(tNodeName.getText());
                } catch (XMPPException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
        tLink.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (Desktop.isDesktopSupported())
                    {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (URISyntaxException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void loadNewsItem(String id) {
        String node = tNodeName.getText() ;
        //            LeafNode n = jabber.pmanager.getNode(node);
//            Collection<String> ids = new ArrayList<String>(1);
//            ids.add(id);
//            List<? extends Item> items = n.getItems(ids);
//            for (Item i: items){
//                NewsItem newsItem = new NewsItem(i.toXML());
//
//                tTitle.setText(newsItem.getTitle());
//                tLink.setText(newsItem.getLink());
//                tDescr.setText(newsItem.getDescription());
//            }
        Feed f = feeds.get(node);
        IRss n = f.getItems().get(id);

        tTitle.setText(n.getTitle());
        tLink.setText(n.getLink());
        tDescr.setText(n.getDescription());

    }

    private void subscribe(final LeafNode leaf) throws XMPPException, ParserConfigurationException, TransformerException, SAXException, IOException {
        leaf.subscribe(jabber.getJid());

        leaf.addItemEventListener(new ItemEventListener() {
            @Override
            public void handlePublishedItems(ItemPublishEvent itemPublishEvent) {
                loadPosts(leaf.getId());
            }
        });

        loadSubscriptions();
    }

    private void loadPosts(String node) {
        DefaultListModel model = new DefaultListModel();
        Feed f = feeds.get(node);
        for (String s: f.getItem_names())
        {
            model.addElement(s);
        }
        lPosts.setModel(model);
    }

    public void loadSubscriptions() throws ParserConfigurationException, IOException, SAXException, TransformerException {
        try {
            //List<Affiliation> list =  jabber.pmanager.getAffiliations();

            List<Subscription> subscriptions = jabber.pmanager.getSubscriptions();

            DefaultListModel model = new DefaultListModel();
            for (Subscription s: subscriptions){
                Feed f= new Feed(s,jabber);
                model.addElement(f.getName());
                feeds.put(f.getName(),f);
            }
            lNodes.setModel(model);
        } catch (XMPPException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("NodesList");
        frame.setContentPane(new NodesList().pNodes);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private Map<String,Feed> feeds;

    public JPanel pNodes;
    private JList lNodes;
    private JButton bCreate;
    private JButton button1;
    private JButton button2;
    private JTextField tNodeName;
    private JEditorPane tLink;
    private JButton bPublish;
    private JList lPosts;
    private JButton bSubscribe;
    private JButton bDelete;
    private JEditorPane tTitle;
    private JEditorPane tDescr;
    public JabberClient jabber;
}
