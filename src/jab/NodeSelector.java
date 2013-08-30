package jab;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.pubsub.Item;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NodeSelector extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JabberClient jabber;

    public List<String> getSelectedNodes() {
        return selectedNodes;
    }

    private List<String> selectedNodes;
    private boolean answer = false;
    public boolean getAnswer() { return answer; }
    private JList lNodes;

    public NodeSelector(JabberClient jabber) {
        this.jabber = jabber;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        selectedNodes = new ArrayList<String>();

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        discoverNodes();


        lNodes.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                DefaultListModel dlm = (DefaultListModel) lNodes.getModel();
                ListSelectionModel lsm = lNodes.getSelectionModel();

                if (!lsm.isSelectionEmpty()) {
                    int start = lsm.getMinSelectionIndex();
                    int stop  = lsm.getMaxSelectionIndex();
                    if (start!=stop){
                        for (int i = start; i<stop;i++)
                        {
                            selectedNodes.add(dlm.get(i).toString());
                        }
                    }
                    else {
                        selectedNodes.add(dlm.get(start).toString());
                    }
                }
            }
        });
    }

    private void discoverNodes() {
        //Search available nodes
        try {
            DiscoverItems discoverItems = this.jabber.pmanager.discoverNodes(null);
            Iterator<DiscoverItems.Item> itr = discoverItems.getItems();

            List<String> ids = new ArrayList<String>();
            while(itr.hasNext()) {
                DiscoverItems.Item i = itr.next();
                String id = i.getNode();
                ids.add(id);
            }

            DefaultListModel model = new DefaultListModel();

            for (String s : ids) model.addElement(s);
            lNodes.setModel(model);

        } catch (XMPPException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void onOK() {
// add your code here
        answer = true;
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        answer = false;
        dispose();
    }

//    public static void main(String[] args) {
////        NodeSelector dialog = new NodeSelector(jabber);
//        dialog.pack();
//        dialog.setVisible(true);
//        System.exit(0);
//    }
}
