package jab;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.pubsub.*;
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

                    PayloadItem<SimplePayload> p = new PayloadItem<SimplePayload>(
                            tNodeName.getText() + "-" + System.currentTimeMillis(),
                            new SimplePayload(
                                    "post",
                                    "pubsub:" + tNodeName.getText() + ":post",
                                    "<post xmlns='pubsub:" + tNodeName.getText() + ":post'>" +
                                            "<title>" + tTitle.getText() + "</title>"+
                                            "<author>" //+ author.replaceAll( "&([^;]+(?!(?:\\w|;)))", "&amp;$1" )
                                            + "</author> " +
                                            "<link>" + tLink.getText() + "</link>" +
                                            "<pDate>"+ new Date(System.currentTimeMillis()).toString()  +"</pDate> " +
                                            "<description>" + tDescr.getText() + "</description> " +
                                            "</post>"));

                    myNode.send(p);

                    loadSubscriptions();
                    loadPosts(tNodeName.getText());

                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (TransformerException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
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
                form.setPersistentItems(false);
                form.setPublishModel(PublishModel.open);
                form.setSubscribe(true);

                try {
                    LeafNode leaf = (LeafNode) jabber.pmanager.createNode(tNodeName.getText(), form);
                    subscribe(leaf);

                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (TransformerException e) {
                    e.printStackTrace();
                }
            }
        });
        lNodes.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                DefaultListModel dlm = (DefaultListModel) lNodes.getModel();
                ListSelectionModel lsm = lNodes.getSelectionModel();

                if (!lsm.isSelectionEmpty())
                {
                    String node = dlm.get(lsm.getMinSelectionIndex()).toString();
                    tNodeName.setText(node);
                    loadPosts(node);
                }
            }
        });
        lPosts.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                DefaultListModel dlm = (DefaultListModel) lPosts.getModel();
                ListSelectionModel lsm = lPosts.getSelectionModel();

                if (!lsm.isSelectionEmpty()) {
                    String id = dlm.get(lsm.getMinSelectionIndex()).toString();
                    loadNewsItem(id);
                }
            }
        });
        bSubscribe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                try {
                    LeafNode leaf = jabber.pmanager.getNode(tNodeName.getText());
                    subscribe(leaf);
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (TransformerException e) {
                    e.printStackTrace();
                }

            }
        });
        bDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    jabber.pmanager.deleteNode(tNodeName.getText());
                } catch (XMPPException e) {
                    e.printStackTrace();
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
        tDescr.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (URISyntaxException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
        bUkeeper.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    jabber.sendMessage("drops@ukeeper.com","%p "+tLink.getText());
                } catch (XMPPException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
    }

    private void loadNewsItem(String id) {
        String node = tNodeName.getText() ;
        Feed f = feeds.get(node);
        IRss n = f.getItems().get(id);

        f.setReaded(id);

        loadPosts(node);

        tTitle.setText(n.getTitle());
        tLink.setText(n.getLink());
        tDescr.setText(n.getDescription());

    }

    private void subscribe(final LeafNode leaf) throws XMPPException, ParserConfigurationException, TransformerException, SAXException, IOException {
        leaf.subscribe(jabber.getJid());

        List<Subscription> subs = jabber.pmanager.getSubscriptions();

        for (Subscription s : subs){
            if (s.getNode().equals(leaf.getId())){
                Feed feed = new Feed(s,jabber);
                feed.addNewsHandlers(new NewsArrivedEvent() {
                    @Override
                    public void HandleNews(String feedID) {
                        loadPosts(feedID);
                    }
                });
                feeds.put( feed.getName(),feed);
            }
        }

        DefaultListModel model = new DefaultListModel();
        for (String str: feeds.keySet()){
            model.addElement(str);
        }
        lNodes.setModel(model);
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
            //Search available nodes
            //DiscoverItems discoverItems = jabber.pmanager.discoverNodes(null);

            List<Subscription> subscriptions = jabber.pmanager.getSubscriptions();

            DefaultListModel model = new DefaultListModel();
            for (Subscription s: subscriptions){
                Feed f= new Feed(s,jabber);
                f.addNewsHandlers(new NewsArrivedEvent() {
                    @Override
                    public void HandleNews(String from) {
                        loadPosts(from);
                    }
                });
                model.addElement(f.getName());
                feeds.put(f.getName(),f);
            }
            lNodes.setModel(model);
        } catch (XMPPException e1) {
            e1.printStackTrace();
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
    private JToolBar tbStatus;
    private JLabel lStatus;
    private JButton bUkeeper;
    public JabberClient jabber;
}
