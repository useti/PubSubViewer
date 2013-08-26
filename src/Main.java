import jab.LoginForm;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import javax.swing.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws XMPPException {
        XMPPConnection.DEBUG_ENABLED = true;
        new Main();
//        System.out.println("Hello World!");
    }

    private Main() {
        JFrame frame = new JFrame("LoginForm");
        LoginForm lf = null;
        try {
            lf = new LoginForm();
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame.setContentPane(lf.p);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        lf.frame = frame;
    }

}
