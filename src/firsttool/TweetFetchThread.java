package firsttool;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.twitshot.ConfigParsingException;
import org.twitshot.LibConfig;
import processing.core.PApplet;
import static processing.core.PApplet.println;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * This thread is responsible for fetching threads from Twitter.
 * Upon start of the thread it requests tweets and puts them into the queue.
 * Then it continues running, trying to fetch new threads, once new arrive, they're
 * added to the queue.
 * The queue can be accessed in a thread-safe manner, thus TweetFetchThread
 * is more like passive retriever. 
 * Tweets are taken off the thread by any client. Once the tweet it taken off the queue
 * it's gone. Subsequential requests to queue, will result in the next tweet.
 * Also when thread is empty it returns NULL.
 * Queue has certain size limit (let's say 10k items), when it's full, then oldest
 * tweets should pop off the thread.
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class TweetFetchThread extends Thread
{
    private static final String C_TWITTER_CONFIG_XML = "d:\\wamp\\www\\twitter\\src\\config\\logdaily.xml";
    private static final int C_TWEET_REFRESH_DELAY_MILLIS = 5 * 1000; // 5 seconds
    private static final int C_MAX_TWEETS_IN_THE_QUEUE = 10 * 1000; // 10k for starters. maybe need to reduce to 10 or 20 to test mechanisms
    
    //SomeQueueWhereWePutTweets
    private ConfigurationBuilder cb;
    private Twitter twitter;
    
    
    
    /**
     * This is the thread-safe queue throgh which exchange of data with
     * other threads will be happening.
     * I am not planning to block on this queue, i just chose it as it is 
     * thread-safe and provides a lot of convenient methods.
     */
    private ArrayBlockingQueue<AbstractTweet> mTheQueue = new ArrayBlockingQueue<AbstractTweet>(C_MAX_TWEETS_IN_THE_QUEUE);
    
    @Override
    public void run() {
            while( true ){
                // fetch tweets.
                initOnce();
                
                query();
                
                zzzSleep(C_TWEET_REFRESH_DELAY_MILLIS);
            }
    }
    
    
    Date freshestKnownDate = null;
    
    /*
     * NOT USED.
     * Returns freshest date. 
     * If ONE of the arguments is null - that's ok. Null is considered older than any date.
     * If BOTH arguments are null - this is IllegalArgumentException
     */
    private static Date getFreshestDate(Date a, Date b){
        if  ( a == null && b == null ){
             throw new IllegalArgumentException("Both arguments cannot be null");
        }
        if ( b == null ){
            return a; // non-null a is always fresher than NULL
        }
        if ( a == null){
            return b; // non-null b is always  fresher than NULL
        }
        
        if ( a.compareTo(b) > 0 ){
            return b;  // kinda should work
        }
        return a;
        
    }
    
    long mFreshestFoundTweetId = -1; // latest status id, so that we don't get same tweets several times
    
    private void query(){
         Query query = new Query("void OR size #p5");
         query.setResultType(Query.MIXED);
         query.setCount(100);
         
         
         if ( mFreshestFoundTweetId != -1 ){
             System.out.println("Setting sinceId to : " + mFreshestFoundTweetId);
          query.setSinceId(mFreshestFoundTweetId);    
         }
         
         
            try {
                QueryResult result = twitter.search(query);
                List<Status> tweets = result.getTweets();
                
                        
                // we loop backwards, to put older messages to queue first
                for (int i = tweets.size() -1; i >= 0; i--) {
                    
                    submitTweetToQueue(tweets.get(i));
                    Status t = tweets.get(i);
                    String user = t.getUser().getScreenName();
                    String msg = t.getText();
                    Date d = t.getCreatedAt();
                    mFreshestFoundTweetId = Math.max(t.getId(), mFreshestFoundTweetId);
//                    freshestKnownDate =  getFreshestDate(d, freshestKnownDate);
                    println("Tweet by " + user + " at " + d + ": " + msg);
                    
                };
                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");                
            } catch (TwitterException te) {
                println("Couldn't connect: " + te);
            }
         
    }

    /**
     * Instantiates ConfigurationBuilder and populates it with params from
     * xml file.
     * @param xmlPathfile
     * @return 
     */
    private ConfigurationBuilder createConfigurationFromXml(String xmlPathfile)
    {
        ConfigurationBuilder cb;
        try {
            //d:\wamp\www\twitter\src\config\logdaily.xml
            PApplet sketch = new PApplet();
            LibConfig libConfig = new LibConfig(sketch, xmlPathfile);
            cb = new ConfigurationBuilder();

            cb.setOAuthConsumerKey(libConfig.getUserToken());
            cb.setOAuthConsumerSecret(libConfig.getUserSecret());
            cb.setOAuthAccessToken(libConfig.getOAuthToken());
            cb.setOAuthAccessTokenSecret(libConfig.getOAuthSecret());
    //        cb.setOAuthConsumerKey("MpmV1xOOFociwNjp3FNfA");
    //        cb.setOAuthConsumerSecret("fjlsBtF98cq0VuCHXLQ78uyOz7fr8lm9WLhFbb4aU");
    //        cb.setOAuthAccessToken("6038892-lHWqBpkKhfAsJkCtyEeB3XORn9sGZP3PEy5L7eRKtk");
    //        cb.setOAuthAccessTokenSecret("REyXt8jvTdGUH3UkvGRJcXzB0nAGCjWvc39IlpM2NY");
        } catch (ConfigParsingException ex) {
            Logger.getLogger(TweetFetchThread.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Early crash is good. Can't access configuration");
        }
        return cb;
    }    
    
    /**
     * Inits twitter configuration once
     */
    private void initOnce() {
        cb = createConfigurationFromXml(C_TWITTER_CONFIG_XML);
        twitter = new TwitterFactory(cb.build()).getInstance();        
    }    
    

    private boolean hasNewTweets() {
        // TODO: impelment
        return false;
    }

    private AbstractTweet getNextNewTweet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }    
    
    
    /**
     * Here we just test this TweetFetchThread by starting it
     * and polling it every few seconds for the tweets it has.
     * @param args 
     */
    public static void main(String[] args) {
        TweetFetchThread tft = new TweetFetchThread();
        tft.start();
        while ( true ){
            zzzSleep(100);
            if ( tft.hasNewTweets() ){
                while ( tft.hasNewTweets() ){  // technically the request and fetch should be one atomic opertion TODO
                    AbstractTweet tweet = tft.getNextNewTweet();
                    System.out.println("Tweet fetched: "  + tweet.toString());
                }
                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            }
            else{
              //  System.out.println("No new tweets");
            }
        }
    }

    
    /**
     * Just utility to sleep (and avoid cluttering code with exception handling)
     * @param delay 
     */
    private static void zzzSleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ex) {
            Logger.getLogger(TweetFetchThread.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }    

    /**
     * This is helper method which puts tweet into The Queue
     * upon encountering the tweet. 
     * This is called from worker thread (from run())
     * @param get 
     */
    private void submitTweetToQueue(Status tweet) {
        
    }

}
