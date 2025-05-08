 package org.magelan.editor.util;
 
  import java.awt.*;
    import java.awt.datatransfer.*;
    import java.awt.event.*;
    import javax.swing.*;
// apr99
    public class CopyPaste {
        public static void main(String args[]) {
            // set up frames, panels, text areas
            JFrame frame = new JFrame("Clipboard demo");
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            final JTextArea textarea = new JTextArea(10, 40);

            // create buttons and set up listeners for them
            JPanel buttonpanel = new JPanel();
            JButton copybutton = new JButton("Copy");
            JButton pastebutton = new JButton("Paste");
            JButton exitbutton = new JButton("Exit");
            copybutton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Clipboard cb =
                        Toolkit.getDefaultToolkit().
                        getSystemClipboard();
                    String s = textarea.getText();
                    StringSelection contents =
                        new StringSelection(s);
                    cb.setContents(contents, null);
                    textarea.setText("");
                }
            });
            pastebutton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Clipboard cb =
                        Toolkit.getDefaultToolkit().
                        getSystemClipboard();
                    Transferable content =
                        cb.getContents(this);
                    try {
                        String s =
                            (String)content.
                            getTransferData(DataFlavor.
                            stringFlavor);
                        textarea.setText(s);
                    }
                    catch (Throwable exc) {
                        System.err.println(e);
                    }
                }
            });
            exitbutton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            buttonpanel.add(copybutton);
            buttonpanel.add(pastebutton);
            buttonpanel.add(exitbutton);

            panel.add("North", textarea);
            panel.add("South", buttonpanel);

            // make frame visible
            frame.getContentPane().add("Center", panel);
            frame.pack();
            frame.setVisible(true);
        }
    }

