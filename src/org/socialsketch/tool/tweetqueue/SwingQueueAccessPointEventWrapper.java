package org.socialsketch.tool.tweetqueue;

import org.socialsketch.tool.ServiceLocator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * This class encapsulates checking for new
 * tweets via Swing.
 * Long story short: as our UI is made in swing, but we 
 * always have to perform network operations on separate
 * worker thread, we will always face problems of communication between
 * the threads.
 * Ideally from client side (swing UI), the client should simply register
 * as observer for the event of new thread appearing, and it should receive
 * and the listener method should be called within the UI thread when new 
 * tweets are ready. 
 * This is what we try to accomplish with this class. This is basically wrapper,
 * for thread exchange operations. Maybe not the most amazing implementation, but
 * kinda works.
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class SwingQueueAccessPointEventWrapper {
   
    private static int C_DEFAULT_REFRESH_DELAY_MILLIS = 1000; // 1 sec
    private IOnNewTweet mOnNewTweet;
    
    private Timer mRefreshTimer;
    
    /**
     * Creates new wrapper which will trigger update events on EDT thread
     * with default delay.
     * @param listener non-null
     */
    public SwingQueueAccessPointEventWrapper(IOnNewTweet listener) throws ServiceLocator.ServiceLocatorException{
        this(C_DEFAULT_REFRESH_DELAY_MILLIS, listener );
    }
    
    /**
     * Creates new wrapper which will trigger update events on
     * EDT thread, with given refresh delay.
     * @param listener
     * @param refreshDelayMillis 
     */
    public SwingQueueAccessPointEventWrapper(int refreshDelayMillis, IOnNewTweet listener) throws ServiceLocator.ServiceLocatorException
    {
        if ( listener == null ){
            throw new IllegalArgumentException("Listener parameter cannot be null");
        }
        // !!! This is important!! never forget it!
        mOnNewTweet = listener;

        if ( refreshDelayMillis < 0 ){
            throw new IllegalArgumentException("refresh delay cannot be negative number, now(" + refreshDelayMillis +")");
        }
        
        
        // setup service
        // TODO: should this be accessed through Service locator? It's actually not even in the same package?
        final IQueueAccessPoint queueAccessPoint = (IQueueAccessPoint) ServiceLocator.getSerivce(ServiceLocator.SVC_LIVE_TWEET_QUEUE);
        
        
        // setup timer
        mRefreshTimer = new javax.swing.Timer(refreshDelayMillis, new ActionListener() {
            /**
             * This will be called every few seconds.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                // here I need to collect things and put them there!
                // here i need to fetch all available tweets into array list
                // and if there're any available to fire the event.
                logln("SwingQueueAccessPointWrapper: Timer event has occured");
                ArrayList<AbstractTweet> arlis = null;
                AbstractTweet aTweet = null;
                while ( ( aTweet = queueAccessPoint.getNextOrNull() ) != null ){
                        // here we just put thing into arlis
                        if ( arlis == null ){ arlis = new ArrayList<AbstractTweet>(); }
                        arlis.add(aTweet);
                }
                // just see of there're any
                if ( arlis != null ){
                    fireOnTweetsReceived(arlis);
                }
            }


        });

        
        
        //??? timer needs to start
        // actually i don't want initial delay. as we set up listener BEFORE
        // starting timer, we can start straight away.
        //mRefreshTimer.setInitialDelay(refreshDelayMillis); 
        mRefreshTimer.start();
    }

    private void logln(String msg) {
        System.out.println(this.getClass().getSimpleName() + " " + msg);
    }

    
    /**
     * This just fires event if there's observer registered.
     * @param arlis 
     */
    private void fireOnTweetsReceived(ArrayList<AbstractTweet> arlis) {
        if ( mOnNewTweet != null ){
            mOnNewTweet.onNewTweet(arlis);
        }
        else{
            throw new NullPointerException("fireOnTweetsReceived(): WARNING:: we don't have listener registered. Suspicious logic");  
        }
    }
    
    /**
     * This is interface which observer (From Swing EDT) who wants
     * to listen for the new tweets can subscribe to.
     */
    public interface IOnNewTweet {
        void onNewTweet(ArrayList<AbstractTweet> tweetArlis);
    }
    
    
    
    /**
     * Here we try to implement test case of this class in swing.
     * @param args 
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                 createAndShowGui();
            }

        });
        
    }
    
    private static void createAndShowGui() {
        try {
            // TODO: implement createAndShowGui
            final QueueWrapperTestFrame frame = new QueueWrapperTestFrame();
            
            SwingQueueAccessPointEventWrapper queueWrapper = new SwingQueueAccessPointEventWrapper(500, new IOnNewTweet() {

                                                  @Override
                                                  public void onNewTweet(ArrayList<AbstractTweet> tweetArlis) {
                                                      for( AbstractTweet aTweet : tweetArlis){
                                                        frame.addMessageToConsole(aTweet.getText());
                                                      }
                                                  }
                                              });
            
            
            frame.setVisible(true);
        } catch (ServiceLocator.ServiceLocatorException ex) {
            System.out.println("Test run crashed");
            Logger.getLogger(SwingQueueAccessPointEventWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
}
