package firsttool.tweetposter;

import firsttool.tweetqueue.AbstractTweet;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;

    
    
/**
 * Provides API to post to twitter. 
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class TweetPoster {

    /**
     * This are twitter4j objects.
     */
    private Twitter twitter;    
    
    /**
     * Flag defines whether we want to post tweets just like standalone 
     * tweets or as replies to someone.
     */
    private boolean C_POST_TWEETS_AS_REPLIES = true;
    
    /**
     * Initializes object. 
     * This one may be initialized with object member variables, 
     * thus this should not do any heavy-lifting.
     */
    public  TweetPoster(){
        twitter = TwitterFactory.getSingleton();
    }
    
    
    /**
     * Posts reply to the tweet.
     * This one is NOT BLOCKING call (asynchronous)
     * 
     * 
     * 
     * @param tweetText
     * @param mediaFile CAN BE NULL, or link to the file which should be posted as tweet media.
     * @param aTweet object representing tweet to be replied to. CAN BE NULL, in case there's no reply to be made.
     * @param listener callback method to be called upon completion of posting.
     *          This should be called on the same thread as it was called from(in our case EDT?)
     */
    public void postTweetReply(String tweetText, File mediaFile, AbstractTweet aTweet, final ITweetPostComplete listener) {
        //TODO: implement this one
       // throw new UnsupportedOperationException("Not yet implemented");
        // this should be called on worker thread.

        //        if ( notInitialized() ){
//            initialize();
//        }
        final StatusUpdate update;
        if ( C_POST_TWEETS_AS_REPLIES && aTweet != null){
            String user = aTweet.getUserWithoutAt();
            update = new StatusUpdate("@" + user + " " + tweetText);
           
            update.setInReplyToStatusId(aTweet.getTweetIdLong());
        }
        else{
            update = new StatusUpdate( tweetText);
            // the update below needs to add some timestamp or something not to triger duplicate error.
            //update = new StatusUpdate("This is posted via API and it doesn't have links setup size void #p5 will it appear in search and hashtag?");
            
        }
        
        // attach media file
        if ( mediaFile != null ){
             update.setMedia(mediaFile);
        }
        
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Status updatedStatus = twitter.updateStatus(update);
                    
                    // debug print entities in the posted tweet.
                            URLEntity[] urlEntities = updatedStatus.getURLEntities();
                            TPUtils.printUrlEntities(urlEntities);

                            MediaEntity[] mediaEntities = updatedStatus.getMediaEntities();
                            TPUtils.printMediaEntities(mediaEntities);
                    
                    listener.onTweetPostComplete(new TweetPostCompleteEvent(updatedStatus));
                    
                } catch (TwitterException ex) {
                    Logger.getLogger(TweetPoster.class.getName()).log(Level.SEVERE, null, ex);
                    listener.onTweetPostComplete(new TweetPostCompleteEvent().setException(ex));
                }

            }


        });
        
        t.start();
        
    }
            
   
}
