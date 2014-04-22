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
import java.security.NoSuchAlgorithmException;
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
                } catch (NoSuchAlgorithmException e) {
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
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
                    try {
                        loadNewsItem(id);
                    } catch (CloneNotSupportedException e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        });
        bSubscribe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                try {
                    String nodeName = tNodeName.getText();
                    subscribe(nodeName);
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
                } catch (NoSuchAlgorithmException e) {
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
        bShowNodeList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                NodeSelector dialog = new NodeSelector(jabber);
                dialog.pack();
                dialog.setVisible(true);
                if(dialog.getAnswer()) {
                    for(String s: dialog.getSelectedNodes())
                    {
                        if(feeds.get(s) == null)
                        {
                            try {
                                subscribe(s);
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
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                        }
                    }
                }
            }
        });
        bJuick.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                MsgForm dialog = new MsgForm();
                dialog.pack();
                dialog.setMessageText(String.format("%s - %s",tTitle.getText(),tLink.getText()));
                dialog.setVisible(true);
                if (dialog.getAnswer()){
                    try {
                        jabber.sendMessage("juick@juick.com",dialog.getMessageText());
                    } catch (XMPPException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        });
        bUnsub.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String feed_id = tNodeName.getText();
                if(feeds.get(feed_id)!=null){
                    try {
                        feeds.get(feed_id).Unsubscribe();
                    } catch (XMPPException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    feeds.remove(feed_id);
                    DefaultListModel model = new DefaultListModel();
                    for (String s : feeds.keySet()){
                        model.addElement(s);
                    }
                    lNodes.setModel(model);
                }
            }
        });
        bUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    feeds.clear();
//                    DefaultListModel listModel = (DefaultListModel) lNodes.getModel();
//                    listModel.removeAllElements();
                    loadSubscriptions();
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
                }
            }
        });
    }

    private void subscribe(String nodeName) throws XMPPException, ParserConfigurationException, TransformerException, SAXException, IOException, NoSuchAlgorithmException {
        LeafNode leaf = jabber.pmanager.getNode(nodeName);
        subscribe(leaf);
    }

    private void loadNewsItem(String id) throws CloneNotSupportedException {
        String node = tNodeName.getText() ;
        Feed f = feeds.get(node);
        IRss n = f.getItems().get(id);

        f.setReaded(id);

        loadPosts(node);

        tTitle.setText(n.getTitle());
        tLink.setText(n.getLink());
        tDescr.setText(n.getDescription());
        tDescr.scrollRectToVisible(new Rectangle(1,1,1,1));

    }

    private void subscribe(final LeafNode leaf) throws XMPPException, ParserConfigurationException, TransformerException, SAXException, IOException, NoSuchAlgorithmException {
        leaf.subscribe(jabber.getJid());

        List<Subscription> subs = jabber.pmanager.getSubscriptions();

        for (Subscription s : subs){
            if (s.getNode().equals(leaf.getId())){
                final Feed feed = new Feed(s,jabber);
                feed.addNewsHandlers(new NewsArrivedEvent() {
                    @Override
                    public void HandleNews(String feedID) {
                        String msg = String.format("Added %s new items to feed %s",feed.getUnreadCounter(),feed.getName());
                        lStatus.setText(msg);
                        if(tNodeName.getText().equals(feed.getName())){
                            loadPosts(feed.getName());}
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

    public void loadSubscriptions() throws ParserConfigurationException, IOException, SAXException, TransformerException, NoSuchAlgorithmException {
        try {
            //Search available nodes
            //DiscoverItems discoverItems = jabber.pmanager.discoverNodes(null);

            List<Subscription> subscriptions = jabber.pmanager.getSubscriptions();

            DefaultListModel model = new DefaultListModel();
            for (Subscription s: subscriptions){
                final Feed f= new Feed(s,jabber);
                f.addNewsHandlers(new NewsArrivedEvent() {
                    @Override
                    public void HandleNews(String from) {
                        String msg = String.format("Added %s new items to feed %s",f.getUnreadCounter(),f.getName());
                        lStatus.setText(msg);
                        if(tNodeName.getText().equals(f.getName())){
                            loadPosts(f.getName());}
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
    private JButton bShowNodeList;
    private JButton bUnsub;
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
    private JButton bJuick;
    private JButton bUpdate;
    private JTabbedPane tabbedPane1;
    private JPanel Default;
    public JabberClient jabber;

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
