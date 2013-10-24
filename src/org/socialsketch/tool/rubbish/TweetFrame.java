package org.socialsketch.tool.rubbish;

import java.awt.Button;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * This frame shows the list of tweets and allows to click on them.
 * The way of usage is that this frame can be started and then
 * caller may subscribe to "clicks on tweet" and figure out what to do
 * with them.
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class TweetFrame extends JFrame
{
    
    TweetFrame(){
        super("TweetFrame");
        buildElements();
        setSize(600,600);
        
        pack();
    }

    /**
     * This one inserts elements inside of the frame.
     * Namely button and listview
     */
    private void buildElements() {
        // create list view
        // create button
        Button button1 = new Button("thi is button");
        button1.addActionListener(new ButtonActionListener());
        // insert into frame
        Container pane = getContentPane();
        pane.add(button1);
    }
    
    

    
    
    /**
     * This is just for testing.
     * @param args 
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                        // here we run the frame
                         createAndShowGUI();
                }
        });
    }
    

    /**
     * This will be called from main() on Swing Thread
     */
    private static void createAndShowGUI(){
            TweetFrame twFrame = new TweetFrame();
            twFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // what does happen here? System.exit() is called?
       //     twFrame.setSize(400,400);
            twFrame.setVisible(true);
    }
    
    
    
    
    /**
     * This one will be listening for the button clicks
     */
    class ButtonActionListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
    
}
