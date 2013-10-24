package org.socialsketch.tool.rubbish.examples;

import org.socialsketch.tool.ServiceLocator;
import org.socialsketch.tool.tweetqueue.AbstractTweet;
import org.socialsketch.tool.tweetqueue.SwingQueueAccessPointEventWrapper;
import org.socialsketch.tool.utils.JConsoleFrame;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * This is example which simply creates frame with console,
 * And just starts receiving updates for the tweets and printing 
 * them to "console".
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class SwingQueueUpdateEventObserver extends 
                    JConsoleFrame  // this is simple JFrame with big textArea, and also implments methods
                                    // print() println() for easy typing to console
                                     // should be useful for testing purposes
{

    public SwingQueueUpdateEventObserver() throws ServiceLocator.ServiceLocatorException {
         super();
         
         SwingQueueAccessPointEventWrapper swingUpdater = new SwingQueueAccessPointEventWrapper(new SwingQueueAccessPointEventWrapper.IOnNewTweet() {

             @Override
             public void onNewTweet(ArrayList<AbstractTweet> tweetArlis) {
                    println("Event has happened");
                    for(AbstractTweet aTweet: tweetArlis){
                        println(aTweet.toString());
                    }
             }
         });
                 
        
         for(int i = 0 ; i < 5 ; i++){
            println("*** Hello this test line should be in console " + i);
         }
    }
    
    
    
    
    /**
     * This just runs the frame and 
     * starts displaying updated tweets.
     * You can run main() to test how it works.
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
     *  Just prepares and launches GUI,
     * is run on EDT (swing UI) thread.
     */
    private static void createGUI(){
        try {
            SwingQueueUpdateEventObserver frame = new SwingQueueUpdateEventObserver();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true); // kinda blocking call :)
        } catch (ServiceLocator.ServiceLocatorException ex) {
            
            Logger.getLogger(SwingQueueUpdateEventObserver.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error loading application: " + ex.getMessage());
        }
    }
    
}
