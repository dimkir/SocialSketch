package firsttool.tweetposter;

import firsttool.tweetqueue.AbstractTweet;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
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
     * Initializes object. 
     * This one may be initialized with object member variables, 
     * thus this should not do any heavy-lifting.
     */
    public  TweetPoster(){
        cb = createConfigurationFromXml(null);
    }
    
    
    /**
     * Posts reply to the tweet.
     * This one is NOT BLOCKING call (asynchronous)
     * 
     * 
     * 
     * @param tweet
     * @param aTweet object representing tweet to be replied to.
     * @param listener callback method to be called upon completion of posting.
     *          This should be called on the same thread as it was called from(in our case EDT?)
     */
    public void postTweetReply(String tweet, AbstractTweet aTweet, ITweetPostComplete listener) {
        //TODO: implement this one
       // throw new UnsupportedOperationException("Not yet implemented");
        // this should be called on worker thread.

        //        if ( notInitialized() ){
//            initialize();
//        }
        
        StatusUpdate update = new StatusUpdate(tweet);
        update.setInReplyToStatusId(aTweet.getTweetIdLong());
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
