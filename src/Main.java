import jab.LoginForm;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import javax.swing.*;

public class Main {

    public static void main(String[] args) throws XMPPException {
        XMPPConnection.DEBUG_ENABLED = true;
        new Main(args);
        System.out.println("Hello World!");
    }

    private Main(String[] argv) {

//        JabberClient jabber = JabberClient.newBuilder("xmpp.useti.ru")
//                .setPassword("cjhjrnsczx40K")
//                .setPort(5222)
//                .setUser("admin@useti.ru")
//                //.setService(params.get("jservice").trim())
//                .build();
//        jabber.setSASLAuthenticationEnabled(true);
//        jabber.setSASLPlain();
//        jabber.setSelfSignedCertificateEnabled(true);
//        List<String> tj = new ArrayList<String>();
//        tj.add("ytihoglaz@useti.ru");

        JFrame frame = new JFrame("LoginForm");
        LoginForm lf = new LoginForm();
        frame.setContentPane(lf.p);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        lf.frame = frame;

//        Processor.Builder bld = Processor.newBuilder(jabber)
//                .to_jids( tj )
//                .interval(300)
//                .jabber_delay(60)
//                .rt_enable(false)
//                .expand_links(false)
//                .jprefix("")
//                .jsufix("");
//
//        Processor proc = bld.build();
//        proc.activate();

    }

}
