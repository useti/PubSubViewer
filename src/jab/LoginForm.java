package jab;

import org.jivesoftware.smack.XMPPException;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: ytihoglaz
 * Date: 06.08.13
 * Time: 16:43
 */
public class LoginForm {
    private JButton bLogin;
    public JPanel p;
    private JPasswordField fPassword;
    private JTextField fLogin;
    private JCheckBox chbSave;
    private JPanel pHeader;
    private JToolBar tbStatus;
    private JLabel lError;
    private JabberClient jabber;
    public JFrame frame;

    private final Properties config;

    public LoginForm() throws IOException {

        config = new Properties();

        config.load(new FileInputStream("config.properties"));

        String isSaved = config.getProperty("saved","false");

        if( isSaved.contains("true"))
        {
            chbSave.setSelected(true);
            fLogin.setText(config.getProperty("jid"));
            fPassword.setText(config.getProperty("pwd"));
        }

        bLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                login();
            }
        });

        chbSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!chbSave.isSelected()){
                    fPassword.setText("");
                    fLogin.setText("");
                }
                else
                {
                    fPassword.setText(config.getProperty("pwd"));
                    fLogin.setText(config.getProperty("jid"));
                }
            }
        });

        p.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        p.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void login() {
        String pwd =  new String(fPassword.getPassword()); //fPassword.getText();
        String log = fLogin.getText();
        lError.setVisible(false);
        jabber = JabberClient.newBuilder("xmpp.useti.ru")
        .setPassword(pwd)
        .setPort(5222)
        .setUser(log)
        //.setService(params.get("jservice").trim())
        .build();
        jabber.setSASLAuthenticationEnabled(true);
        jabber.setSASLPlain();
        jabber.setSelfSignedCertificateEnabled(true);

        try {
            jabber.connect();
        } catch (XMPPException e) {
            lError.setVisible(true);
            e.printStackTrace();
            return;
        }

        if (chbSave.isSelected())
        {
            config.setProperty("jid",log);
            config.setProperty("pwd",pwd);
        }

        config.setProperty("saved", String.format("%s", chbSave.isSelected()));

        try {
            config.store(new FileOutputStream("config.properties"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JFrame nf = new JFrame("NodesList");
        NodesList nl = new NodesList();
        nl.jabber = jabber;
        nf.setContentPane(nl.pNodes);
        nf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        nf.pack();
        nf.setVisible(true);

        frame.setVisible(false);

        try {
            nl.loadSubscriptions();
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("LoginForm");
        try {
            frame.setContentPane(new LoginForm().p);
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
