package jab;

import javax.swing.*;
import java.awt.event.*;

public class MsgForm extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JEditorPane fMessage;

    public boolean getAnswer() {
        return answer;
    }

    private boolean answer = false;

    public void setMessageText(String messageText) {
        this.messageText = messageText;
        fMessage.setText(this.messageText);
    }

    private String messageText="";

    public String getMessageText() {
        return messageText;
    }

    public MsgForm() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

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
    }

    private void onOK() {
        messageText = fMessage.getText();
        answer = true;
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        MsgForm dialog = new MsgForm();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
