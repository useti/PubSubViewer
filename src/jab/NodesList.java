package jab;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.pubsub.*;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
                                            + tPost.getText()
                                            + "</post>"));

                    myNode.send(p);

                    loadSubscriptions();
                    loadPosts(tNodeName.getText());

                } catch (XMPPException e) {
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
                String node = tNodeName.getText() ;
                try {

                    LeafNode n = jabber.pmanager.getNode(node);
                    Collection<String> ids = new ArrayList<String>(1);
                    ids.add(id);
                    List<? extends Item> items = n.getItems(ids);
                    for (Item i: items){
                        tPost.setText(i.toXML());
                    }
                } catch (XMPPException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            }
        });
    }

    private void subscribe(final LeafNode leaf) throws XMPPException {
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
        try {
            LeafNode n =  jabber.pmanager.getNode(node);
            DiscoverItems nodeItems = n.discoverItems();
            DefaultListModel model = new DefaultListModel();
            Iterator<DiscoverItems.Item> itr = nodeItems.getItems();
            while(itr.hasNext()) {
                DiscoverItems.Item i = itr.next();
                model.addElement(i.getName());
            }
            lPosts.setModel(model);
        } catch (XMPPException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void loadSubscriptions()
    {
        try {
            List<Affiliation> list =  jabber.pmanager.getAffiliations();
            DefaultListModel model = new DefaultListModel();
            for (Affiliation s: list){
                model.addElement(s.getNodeId());
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

    public JPanel pNodes;
    private JList lNodes;
    private JButton bCreate;
    private JButton button1;
    private JButton button2;
    private JTextField tNodeName;
    private JEditorPane tPost;
    private JButton bPublish;
    private JList lPosts;
    private JButton bSubscribe;
    public JabberClient jabber;
}
