package firsttool.tweetposter;

import firsttool.tweetqueue.AbstractTweet;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

    
    
    /**
 * Provides API to post to twitter. 
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class TweetPoster {

    /**
     * This are twitter4j objects.
     */
    private ConfigurationBuilder cb;
    private Twitter twitter;    
    
    /**
     * Flag defines whether we want to post tweets just like standalone 
     * tweets or as replies to someone.
     */
    private boolean C_POST_TWEETS_AS_REPLIES = false;
    
    /**
     * Initializes object. 
     * This one may be initialized with object member variables, 
     * thus this should not do any heavy-lifting.
     */
    public  TweetPoster(){
        cb = createConfigurationFromXml(null);
        twitter = new TwitterFactory(cb.build()).getInstance();
    }
    
    
    /**
     * Posts reply to the tweet.
     * This one is NOT BLOCKING call (asynchronous)
     * 
     * 
     * 
     * @param tweetText
     * @param aTweet object representing tweet to be replied to.
     * @param listener callback method to be called upon completion of posting.
     *          This should be called on the same thread as it was called from(in our case EDT?)
     */
    public void postTweetReply(String tweetText, AbstractTweet aTweet, final ITweetPostComplete listener) {
        //TODO: implement this one
       // throw new UnsupportedOperationException("Not yet implemented");
        // this should be called on worker thread.

        //        if ( notInitialized() ){
//            initialize();
//        }
        String user = aTweet.getUserWithoutAt();
        final StatusUpdate update;
        if ( C_POST_TWEETS_AS_REPLIES ){
            update = new StatusUpdate("@" + user + " " + tweetText);
            update.setInReplyToStatusId(aTweet.getTweetIdLong());
        }
        else{
            update = new StatusUpdate( tweetText);
            // the update below needs to add some timestamp or something not to triger duplicate error.
            //update = new StatusUpdate("This is posted via API and it doesn't have links setup size void #p5 will it appear in search and hashtag?");
            
        }
        
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Status updatedStatus = twitter.updateStatus(update);
                    listener.onTweetPostComplete(new TweetPostCompleteEvent(updatedStatus));
                    
                } catch (TwitterException ex) {
                    Logger.getLogger(TweetPoster.class.getName()).log(Level.SEVERE, null, ex);
                    listener.onTweetPostComplete(new TweetPostCompleteEvent().setException(ex));
                }

            }
        });
        
        t.start();
        
    }
/**
     * 
     * @param xmlPathfile
     * @return 
     */
    private ConfigurationBuilder createConfigurationFromXml(String xmlPathfile)
    {
        ConfigurationBuilder cb;
       cb = new ConfigurationBuilder();
//        try {
//            PApplet sketch = new PApplet(); // this is kinda hack. I dunno if it's good to instantiate 
//                                            // PApplet object. I just do this because LibConfig needs access to loadXML()
//            LibConfig libConfig = new LibConfig(sketch, xmlPathfile);
//
//            cb.setOAuthConsumerKey(libConfig.getUserToken());
//            cb.setOAuthConsumerSecret(libConfig.getUserSecret());
//            cb.setOAuthAccessToken(libConfig.getOAuthToken());
//            cb.setOAuthAccessTokenSecret(libConfig.getOAuthSecret());
            
    // these are keys for my test twitter account with READ WRITE permissions
       
            cb.setOAuthConsumerKey("R40rPo3ZXuGKWcrPDDsY2w");
            cb.setOAuthConsumerSecret("czn2Ntngx7yPrSRHCjJ4dYNWmuGpWCeIU6rnbHWAvsI");
            cb.setOAuthAccessToken("1413163736-m71AJm5YqswVjbqcqSi0qSmjbbjSTblgU4yLqFH");
            cb.setOAuthAccessTokenSecret("aRxUqe7KNUkju1i6EzQHuwpDxnNo8hIlUbJEYFRtNag");
//        } catch (ConfigParsingException ex) {
//            Logger.getLogger(TweetFetchThread.class.getName()).log(Level.SEVERE, null, ex);
//            throw new RuntimeException("Early crash is good. Can't access configuration");
//        }
        return cb;
    }        
    
}
