package firsttool;

import firsttool.codeposter.CodePostCompleteEvent;
import firsttool.codeposter.CodePoster;
import firsttool.codeposter.ICodePostComplete;
import firsttool.tweetposter.ITweetPostComplete;
import firsttool.tweetposter.TweetPostCompleteEvent;
import firsttool.tweetposter.TweetPoster;
import firsttool.tweetqueue.AbstractTweet;
import firsttool.tweetqueue.IQueueAccessPoint;
import firsttool.ui.IBasicPassiveUI;
import firsttool.ui.IBasicPassiveUI.BasicPassiveUIAction;
import firsttool.ui.IBasicPassiveUI.IBasicUIActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import processing.app.Formatter;

/**
 * When using this frame, all of the "building of frame" is hidden
 * in the super class
 * this should be just "controller" for logic of the elements.
 * 
 * This class should have NO dependencies on the PDE Tool or Editor or
 * whatever elements. This class should provide ability for those classes
 * to Observe it.
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class SketchModder
implements 
            ServiceLocator.ServiceRecord
        ,    ISketchModder
{


    private IQueueAccessPoint mQueueAccessPoint;

    
    private IBasicPassiveUI ui;
    
    /**
     * This is listener for modder actions.
     */
    private IOnModderActionListener mModderActionListener;
    
    
    private CodePoster codePoster = new CodePoster();
    
    private TweetPoster tweetPoster = new TweetPoster();
    
    /**
     * Runs on swing EDT thread.
     */
    public SketchModder() {
        ui = (IBasicPassiveUI) ServiceLocator.getSerivce(ServiceLocator.SVC_BASIC_PASSIVE_UI);
        ui.setOnBasicUIActionListener(new MyBasicUIListener());
        
        //TODO: decide which on close operation we use
        ui.getJFrame().setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        
        setupTweetFetching(); // tweets will be fetched into addTweetsToUI() method
        
        // still won't block but just in case.
        ui.setVisible(true); // as we're running on EDT thread, this shouldn't block 

    
    }

    private Formatter mFormatter;
    
    /**
     * Formats source code if can or null.
     * @param code
     * @return NULL if couldn't format code, either because there were no formatter or whatever.
     */
    private String formatSourceCode(String code){
        if ( mFormatter == null ){
            return null; // no formatter set
        }
        return mFormatter.format(code);
    }
    
    /**
     * When tweet fetching thread has tweet available,
     * BUT! THIS IS CALLED ON SWING THREAD
     * this one will be called.
     *
     * @param arlis 
     */
    private void onNewTweetsArrival(ArrayList<AbstractTweet> arlis) {
        for( AbstractTweet aTweet : arlis){
            ui.addTweet(aTweet);
        }
    }
    
    @Override
    public String getServiceDescription() {
        return "Simple Sketch Modder service for simple UI with tweets";
    }
    
    

    
    
    // ===========================================================
    // ===========================================================
    // ========================= static methods  =================
    // ===========================================================
    // ===========================================================
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGUI();
            }
        });
    }
    
    
    private static void createGUI(){
        SketchModder modder = new SketchModder();
            
            modder.setVisible(true);
            
            
    }

    
//    // there's no need to keep this reference here.
//    // as NewJFrame only thinks of this as IQueueAccessPoint
//    // and doesn't care of it's implementation. NewjFrame simply knows
//    // that it should poll at regular intervals the queue. but how it's 
//    // implemented it's not his business.
//    /**
//     * This is the thread which is doing the polling job.
//     */
//    protected TweetFetchThread thread;
    
    private IQueueAccessPoint startTweetFetchService() {
//        TweetFetchThread thread = new TweetFetchThread();
//        thread.start();
//        return thread;
        ServiceLocator.ServiceRecord service = ServiceLocator.getSerivce(ServiceLocator.SVC_LIVE_TWEET_QUEUE);
        return (IQueueAccessPoint) service;  // yeah we have cast here, but hurray! we're decoupled
                                             // now from instantiation of the service
    }

    @Override
    public void setVisible(boolean b) {
        ui.setVisible(b);
    }

    private void fireModderAction(ModderAction action){
        if ( mModderActionListener != null){
            mModderActionListener.onModderAction(this, action);
        }
        else{
            throw new RuntimeException("We want to crash early, if there's an action fired by modder, but noone is registered to listen to it");
        }
    }
    
    @Override
    public void setOnModderActionListener(IOnModderActionListener listener) {
        mModderActionListener = listener;
    }

    @Override
    public void sendSketchCode(String text, AbstractTweet aTweet) {
        JOptionPane.showMessageDialog(ui.getJFrame(), "Modder received code: " + text) ;
        sendCodeAsReply(text, aTweet);
    }

    private void setupTweetFetching() {
        
        Timer timer;        
        mQueueAccessPoint = startTweetFetchService(); // we start thread, but we don't
                                                     // care much about the fact that it's thread
                                                     // fetching. All we care is that 
                                                     // we get reference to IQueueAccessPoint
                                                     // which we can poll at any time (thead safe)
                                                     // and we can get latest items.
 
        // now we need to start timer. this timer will call every now and then and will be firing ON SWING THREAD
        // when there's availble tweet.
        timer = new Timer(1000, new ActionListener() {  // technically we don't need update more frequently than thred is updating, but that doesn't matter.
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<AbstractTweet> arlis = null;
                AbstractTweet aTweet = null;
                while ( ( aTweet = mQueueAccessPoint.getNextOrNull() ) != null ){
                    if ( arlis == null ){ arlis = new ArrayList<AbstractTweet>(); }
                    arlis.add(aTweet);
                }
                
                if ( arlis != null){
                    onNewTweetsArrival(arlis);
                }
            }

        });
        
        
        timer.setInitialDelay(500); // wait half a second, util we set up the thing
        timer.start();            
    }

    /**
     * Replies with code to given tweet.
     * We reply in simple way: we just post code to some server and
     * and we just post tweet with this code.
     * 
     * @param text
     * @param aTweet 
     */
    private void sendCodeAsReply(String text, final AbstractTweet aTweet) {
        // post code to code server
        codePoster.postCode(text, new ICodePostComplete() {

            /**
             * This runs if post code is successful.
             */
            @Override
            public void onCodePostComplete(CodePostCompleteEvent evt) {

                if (evt.isSuccessful()) {
                    String postedUrl = evt.getUrlString();
                   tweetPoster.postTweetReply("Replied to tweet with code: " + postedUrl, aTweet, new ITweetPostComplete() {

                        @Override
                        public void onTweetPostComplete(TweetPostCompleteEvent evt) {
                            if ( evt.isSuccessful() ){
                                
                                // technically we should maybe force-refresh the screen?
                                // ??
                                 throw new UnsupportedOperationException("Not supported yet.");
                            }
                            else{
                                JOptionPane.showMessageDialog(null, "Cannot post tweet to internet");
                                        
                            }
                        }
                    });
                    
                } else {
                    JOptionPane.showMessageDialog(null, "Error when posting code to internet");
                }
                //TODO: implement post
            }
        });

        // on success: tweet the code url inside of the tweet.
        
    }

    /**
     * Sets source code formatter.
     * @param formatter NOT NULL
     */
    @Override
    public void setSourceCodeFormatter(Formatter formatter) {
        if ( formatter == null ){
            throw new NullPointerException("parameter for setSourceCodeFormatter() cannot be null");
        }
        mFormatter = formatter;
    }

    

    /**
     * This class implements listening for the events from the UI 
     */
    private  class MyBasicUIListener implements IBasicUIActionListener {

        /**
         * Here I need to catch events from the UI and "translate" them to
         * into ModderActions
         * @param basicUI
         * @param action 
         */
        @Override
        public void onBasicUIAction(IBasicPassiveUI basicUI, BasicPassiveUIAction action) {
            BasicPassiveUIAction.ActionTypes buiAction = action.getType();
            switch ( buiAction ){
                case REPLY_TO_TWEET:
                    // we need to save somewhere the tweet.
                    fireModderAction(new ModderAction("?---??", ModderActionTypes.ACTION_MODDER_REQUESTS_CODE));
                    break;
                case SELECT_TWEET_PRIMARY:
                    AbstractTweet aTweet = (AbstractTweet) action.getParam("tweet");
                        // try to get code formatted. (provided we
                        String txt = aTweet.getText();
                        txt = formatSourceCode(txt);
                        ModderAction act = new ModderAction("?--??",ModderActionTypes.ACTION_CODE_EDITOR_REPLACE_WITH).setTweet(aTweet);
                        if ( txt != null ){
                            act.setSourceCode(txt);
                        }
                        else{
                            act.setSourceCode(aTweet.getText());
                        }
                    
                        fireModderAction(act);
                    break;
                case SELECT_TWEET_SECONDARY:
                    AbstractTweet aTweet2 = (AbstractTweet) action.getParam("tweet");
                    fireModderAction(new ModderAction("??--?", ModderActionTypes.ACTION_CODE_EDITOR_INSERT_INTO).setTweet(aTweet2));
                    break;
                default:
                    throw new RuntimeException("We do not support other actions,yet");
            }
        }
    }
    
    
}
