package jab;

import org.jivesoftware.smack.XMPPException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: ytihoglaz
 * Date: 06.08.13
 * Time: 16:43
 * To change this template use File | Settings | File Templates.
 */
public class MainForm {
    private JButton bLogin;
    public JPanel p;
    private JPasswordField fPassword;
    private JTextPane fLogin;
    private JabberClient jabber;

    public MainForm() {
        bLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String pwd = fPassword.getText();
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
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainForm");
        frame.setContentPane(new MainForm().p);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
