import jab.LoginForm;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import javax.swing.*;

public class Main {

    public static void main(String[] args) throws XMPPException {
//        XMPPConnection.DEBUG_ENABLED = true;
        new Main(args);
//        System.out.println("Hello World!");
    }

    private Main(String[] argv) {
        JFrame frame = new JFrame("LoginForm");
        LoginForm lf = new LoginForm();
        frame.setContentPane(lf.p);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        lf.frame = frame;
    }

}
