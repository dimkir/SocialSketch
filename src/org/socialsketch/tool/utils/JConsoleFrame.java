package org.socialsketch.tool.utils;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * This is frame which displays console. Can be used to display text messages.
 *
 * HOW TO USE: probably need to extend it and somehow start populating area...
 * via i println() or something like that.
 *
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class JConsoleFrame extends JFrame {

    private JTextArea mTextArea;
    private TextAreaOutputStream mTAOutputStream;
    private PrintStream mTAPrintStream;

    public OutputStream getOutputStream() {
        return mTAOutputStream;
    }

    public PrintStream getPrintStream() {
        return mTAPrintStream;
    }

    /**
     * This is kinda default constructor, but should there be more?
     *
     * @throws HeadlessException
     */
    public JConsoleFrame() {
        mTextArea = new JTextArea(15, 30); // ? 
        mTextArea.setEditable(false);
        mTextArea.setWrapStyleWord(true);
        mTAOutputStream = new TextAreaOutputStream(mTextArea, "CONSOLE");
        mTAPrintStream = new PrintStream(mTAOutputStream);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(mTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPane);
        getContentPane().add(panel);
        pack();

    }

    public void println(String s) {
        print(s + "\n");
    }

    public void print(String s) {
        mTAPrintStream.append(s);
    }

    public void println(String[] elements) {
        for (String s : elements) {
            println(s);
        }
    }

    /**
     * Run this if you want to see console frame.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGUI();
            }
        });
    }

    /**
     * Runs on EDT (Swing UI) thread and creates and displays the frame/window.
     */
    private static void createGUI() {
        JConsoleFrame frame = new JConsoleFrame();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * This class is taken from stackoverflow
     * http://stackoverflow.com/questions/9776465/how-to-visualize-console-java-in-jframe-jpanel
     * and original author is Hovercraft Full Of Eels
     * http://stackoverflow.com/users/522444/hovercraft-full-of-eels
     *
     */
    static class TextAreaOutputStream extends OutputStream {

        private final JTextArea textArea;
        private final StringBuilder sb = new StringBuilder();
        private String title;

        public TextAreaOutputStream(final JTextArea textArea, String title) {
            this.textArea = textArea;
            this.title = title;
            sb.append(title + "> ");
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }

        @Override
        public void write(int b) throws IOException {

            if (b == '\r') {
                return;
            }

            if (b == '\n') {
                final String text = sb.toString() + "\n";
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        textArea.append(text);
                    }
                });
                sb.setLength(0);
                sb.append(title + "> ");
                return;
            }

            sb.append((char) b);
        }
    }
}
