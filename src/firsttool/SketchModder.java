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
import firsttool.ui.MessageDialogClickable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import processing.app.Formatter;

/**
 * What is the purpose of this class?
 * 
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
    
    
    private final CodePoster codePoster;
    
    private TweetPoster tweetPoster = new TweetPoster();
    
    /**
     * Runs on swing EDT thread.
     * 
     * Kinda should be failable.
     * 
     * @throws ModderEx when some initialization fails.
     */
    public SketchModder() throws ModderEx, ServiceLocator.ServiceLocatorException
    {
        try {
            ui = (IBasicPassiveUI) ServiceLocator.getSerivce(ServiceLocator.SVC_BASIC_PASSIVE_UI);
            ui.setOnBasicUIActionListener(new MyBasicUIListener());
            
            //TODO: decide which on close operation we use
            ui.getJFrame().setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            
            setupTweetFetching(); // tweets will be fetched into addTweetsToUI() method
            
            codePoster = new CodePoster(); // throws IOException
            
            // as we're running on EDT thread, this shouldn't block 
            ui.setVisible(true); // as we're running on EDT thread, this shouldn't block
            
            
            
        } catch (IOException ex) {
            Logger.getLogger(SketchModder.class.getName()).log(Level.SEVERE, null, ex);
            throw new ModderEx(ex);
        }
    
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
        try {
            SketchModder modder = new SketchModder();
            
            modder.setVisible(true);
            
            
            
        } catch (ModderEx ex) {
            Logger.getLogger(SketchModder.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } catch (ServiceLocator.ServiceLocatorException ex) {
            Logger.getLogger(SketchModder.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
            
            
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
    
    private IQueueAccessPoint startTweetFetchService() throws ServiceLocator.ServiceLocatorException {
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

    /**
     * This method is the "channel" through which SketchModder is receiving the sketch code 
     * to share.
     * 
     * @param sketchPDESource
     * @param mediaFile
     * @param aTweet 
     */
    @Override
    public void shareSketchCode(String sketchPDESource, File mediaFile, AbstractTweet aTweet) {
        JOptionPane.showMessageDialog(ui.getJFrame(), "Modder received code: " + sketchPDESource) ;
        shareCodeAsReplyImpl(sketchPDESource, mediaFile, aTweet);
    }

    private void setupTweetFetching() throws ServiceLocator.ServiceLocatorException {
        
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
     * We reply in simple way: 
     * a) post code to github as gist
     * b) if successful we tweet the link to the gist and screenshot of the image.
     * c) if tweet is successful, then we can comment on gist with the link to the image, so that gist as well has screenshot.
     * 
     * @param text
     * @param aTweet 
     */
    private void shareCodeAsReplyImpl(String text, final File mediaFile,  final AbstractTweet aTweet) {
        // post code to code server
        codePoster.postCode(text, new ICodePostComplete() {

            /** *****************************************************
             * This runs if post code is successful.
             * 
             * Which means we simply want to tweet it.
             ********************************************************/
            @Override
            public void onCodePostComplete(final CodePostCompleteEvent cevt) {

                if (cevt.isSuccessful()) {
                    final String postedUrl = cevt.getUrlString();
                   
                   // postedUrl removed from string
                   tweetPoster.postTweetReply("I remixed your sketch void setup size draw #p5 " + postedUrl , mediaFile, aTweet, new ITweetPostComplete() {

                        @Override
                        public void onTweetPostComplete(TweetPostCompleteEvent evt) {
                            if ( evt.isSuccessful() ){

                                 MessageDialogClickable.show(null, "Successfully replied to the tweet with", evt.getTweetUrl(), "The link to image is" , evt.getImageUrl());

                                 String commentMsg = String.format("This is comment with sketch screenshot url:  ![imge](%s)", evt.getImageUrl());
                                 codePoster.postComment(commentMsg, cevt.getGistId(), mCommentPostComplete);
                            }
                            else{
                                JOptionPane.showMessageDialog(null, "We managed to share gist, but cannot tweet it. Error: " + evt.getException().getMessage());
                                        
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
     * This callback is called when completed attempt to comment on gist with the image from tweet.
     */
    private ICodePostComplete mCommentPostComplete = new ICodePostComplete() {

        @Override
        public void onCodePostComplete(CodePostCompleteEvent evt) {
            if ( evt.isSuccessful()  ){
                // ??? hurray we completed commenting on gist.
                MessageDialogClickable.show(null, "Succesfully posted comment to gist", null);
            }
            else{
                // ??? there was some error, commenting on gist.
                MessageDialogClickable.show(null, "Failed posting comment to gist", null);
            }
        }
    };

//    /**
//     * Replies with code to given tweet.
//     * We reply in simple way: 
//     * a) post code to github as gist
//     * b) if successful we tweet the link to the gist and screenshot of the image.
//     * c) if tweet is successful, then we can comment on gist with the link to the image, so that gist as well has screenshot.
//     * 
//     * @param text
//     * @param aTweet 
//     */
//    private void sendCodeAsReplyFirstGistTweetCommentWithImage(String text, final File mediaFile,  final AbstractTweet aTweet) {
//        // post code to code server
//        codePoster.postCode(text, new ICodePostComplete() {
//
//            /**********************************************************
//             * This runs if post code is successful.
//             **********************************************************/
//            @Override
//            public void onCodePostComplete(CodePostCompleteEvent evt) {
//
//                if (evt.isSuccessful()) {
//                    String postedUrl = evt.getUrlString();
//                    
//                    // send tweet with image
//                    tweetWithImage(??);
//                    
//                } else {
//                    JOptionPane.showMessageDialog(null, "Error when posting code to internet");
//                }
//                //TODO: implement post
//            }
//        });
//
//        // on success: tweet the code url inside of the tweet.
//        
//    }    
//    
//    /**
//     * Helper. Tweets asynchronously and after that posts image as gist comment.
//     */
//    private void tweetWithImageAndGistComment(String gistUrl, File mediaFile, AbstractTweet aTweetInReplyTo) {
//        tweetPoster.postTweetReply("I remixed your sketch void setup draw #p5 " + gistUrl, mediaFile, aTweetInReplyTo, new ITweetPostComplete() {
//            @Override
//            public void onTweetPostComplete(TweetPostCompleteEvent evt) {
//                if (evt.isSuccessful()) {
//
//                    here we should post comment to gist.
//                    String imageUrl  = evt.getPostedImageUrl();
//                    if  ( imageUrl != null ){
//                        postGist(gistUrl, imageUrl, ? should here be as well listener?);
//                    }
//                    else{
//                        JOptionPane.showMessageDialog(null, "<html><B>Succesfully replied<b> to the tweet with: <a href='" + evt.getTweetUrl() + "'>"
//                                + evt.getTweetUrl() + "</a></html>");
//                    }
//                } else {
//                    JOptionPane.showMessageDialog(null, "Cannot post tweet to internet, error: " + evt.getException().getMessage());
//
//                }
//            }
//        });
//    }
    
    
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
                    AbstractTweet aTweet0 = (AbstractTweet) action.getParam("tweet");
                    fireModderAction(new ModderAction("?---??", ModderActionTypes.ACTION_MODDER_REQUESTS_CODE).setTweet(aTweet0));
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
