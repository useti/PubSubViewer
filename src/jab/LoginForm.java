package jab;

import org.jivesoftware.smack.XMPPException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
                String pwd =  new String(fPassword.getPassword()); //fPassword.getText();
                String log = fLogin.getText();
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
                    e.printStackTrace();
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

                nl.loadSubscriptions();
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
