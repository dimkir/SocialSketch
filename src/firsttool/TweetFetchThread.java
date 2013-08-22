package firsttool;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
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
 * it's gone. Subsequent requests to queue, will result in the next tweet.
 * Also when thread is empty it returns NULL.
 * Queue has certain size limit (let's say 10k items), when it's full, then oldest
 * tweets should pop off the thread.
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class TweetFetchThread extends Thread
implements IQueueAccessPoint
{
    /**
     * These are constants.
     * I always prepend them with C_ and write in CAPITAL letters.
     */
    //TODO: this refernce to file is BAD
    private static final String C_TWITTER_CONFIG_XML = "d:\\wamp\\www\\twitter\\src\\config\\logdaily.xml";
    private static final int C_TWEET_REFRESH_DELAY_MILLIS = 5 * 1000; // 5 seconds
    private static final int C_MAX_TWEETS_IN_THE_QUEUE = 10 * 1000; // 10k for starters. maybe need to reduce to 10 or 20 to test mechanisms
    private static final int C_TWEET_SEARCH_RESULT_COUNT = 100; // 10k for starters. maybe need to reduce to 10 or 20 to test mechanisms
    private static final String C_TWITTER_DEFAULT_SEARCH_QUERY = "void OR size #p5";    
    
    /**
     * This are twitter4j objects.
     */
    private ConfigurationBuilder cb;
    private Twitter twitter;

    
    /**
     * When querying for tweets,
     * we need every next query only to include newly appeared
     * (since last query) tweets. This variable holds
     * freshest found tweet id. 
     * -1 means it is uninitialized. Maybe need to use 0 for that purpose.
     */
    private static final long C_UNINITIALIZED_TWEET_ID = -1; // also this one should be always smaller than current tweet id.
                                                             // do we have "overflow" problem?
    private long mFreshestFoundTweetId = C_UNINITIALIZED_TWEET_ID; // latest status id, so that we don't get same tweets several times    
    
    /**
     * This is the thread-safe queue through which exchange of data with
     * other threads will be happening.
     * I am not planning to block on this queue, i just chose it as it is 
     * thread-safe and provides a lot of convenient methods.
     */
    private ArrayBlockingQueue<AbstractTweet> mTheQueue = new ArrayBlockingQueue<AbstractTweet>(C_MAX_TWEETS_IN_THE_QUEUE);
    
    /**
     * This is the query which is used to fetch tweets.
     * It has default value.
     * It can be initialized via constructor or set via setter.
     * As it is String, it shouldn't require any special thread safety.
     * run() will be using this variable every iteration to query.
     * TODO: the question is - if we change query, should we also reset the mFreshestFoundTweetId??
     */
    private String mCurrentQuery;
    

    public TweetFetchThread() {
        setQuery(C_TWITTER_DEFAULT_SEARCH_QUERY);
    }
    
    /**
     * Initializes thread with non-null non empty query
     * @param query non-null, non-empty string representing query to twitter.
     */
    public TweetFetchThread(String query){
        setQuery(query);
    }
    
    /**
     * Sets query to be queried during NEXT request to twitter search.
     * @param q non-null, non-empty string representing query to twitter.
     * @throws IllegalArgumentException may the query string be invalid
     * Added final modifier just because lint was complaining this was used in constructor.
     */
    public final void setQuery(String query){
        if ( query == null || query.length() < 1){
            throw new IllegalArgumentException("Query  cannot be null or empty string");
        }        
        mCurrentQuery = query;
    }
    
    
    @Override
    public void run() {
        try{
            while( true ){
                initTwitter4jIfNotInitialized();
                // fetch tweets.
                queryAndPopulate();
                
                Thread.sleep(C_TWEET_REFRESH_DELAY_MILLIS);
            }
        }
        catch(InterruptedException intex){
            printException(intex);
        }
    }
    
    
    /*
     * NOT USED.
     * NOT SURE IF .compareTo() if statement is correct.
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
    

    
    
    /**
     * Queries for new tweets and populates TheQueue with them.
     * TODO: At the moment if twitter request result in exception, we just
     * catch it and log error message. Need to determine better business logic for that.
     * @throws InterruptedException 
     */
    private void queryAndPopulate() throws InterruptedException
    {
         Query query = new Query(mCurrentQuery);
         query.setResultType(Query.MIXED);
         query.setCount(C_TWEET_SEARCH_RESULT_COUNT);
         
         
         if ( mFreshestFoundTweetId != C_UNINITIALIZED_TWEET_ID ){
             logln("Setting sinceId to : " + mFreshestFoundTweetId);
             query.setSinceId(mFreshestFoundTweetId);    
         }
         
         
            try {
                logln("Performing query[" + query + "]");
                QueryResult result = twitter.search(query);
                List<Status> tweets = result.getTweets();
                
                        
                // we loop backwards, to put older messages to queue first
                for (int i = tweets.size() -1; i >= 0; i--) {
                    submitTweetToQueue(tweets.get(i));  // INCUIM this can block if The Queue is full
                                                        // which is fine with us
                    Status t = tweets.get(i);
//                    String user = t.getUser().getScreenName();
//                    String msg = t.getText();
//                    Date d = t.getCreatedAt();
                    mFreshestFoundTweetId = Math.max(t.getId(), mFreshestFoundTweetId);
                    logln(  String.format("Fetched tweet by %s at %s: %s",  
                                t.getUser().getScreenName(),
                                t.getCreatedAt(),
                                t.getText()         )
                          );
                }// for
                logln("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");                
                
            } catch (TwitterException te) {
                // TODO: see current method title for more details. Need better handling logic for this.
                logln("Couldn't perform twitter query: " + te);
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
            PApplet sketch = new PApplet(); // this is kinda hack. I dunno if it's good to instantiate 
                                            // PApplet object. I just do this because LibConfig needs access to loadXML()
            LibConfig libConfig = new LibConfig(sketch, xmlPathfile);
            cb = new ConfigurationBuilder();

            cb.setOAuthConsumerKey(libConfig.getUserToken());
            cb.setOAuthConsumerSecret(libConfig.getUserSecret());
            cb.setOAuthAccessToken(libConfig.getOAuthToken());
            cb.setOAuthAccessTokenSecret(libConfig.getOAuthSecret());
    // these are NOT valid keys from some forum post. just here for illustratoin
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
     * Initializes twitter4j objects to be ready to query. Will be called every loop in
     * run(), so this one needs to check itself not to run many times.
     * Reads twitter credentials from file and creates as
     * a final result instance of Twitter object.
     */
    private void initTwitter4jIfNotInitialized() {
        if ( twitter  == null ){
                cb = createConfigurationFromXml(C_TWITTER_CONFIG_XML);
                twitter = new TwitterFactory(cb.build()).getInstance();        
        }
    }    
    
    /**
     * Just returns next tweet from the queue or null.
     * @return 
     */
    @Override
    public AbstractTweet getNextOrNull() {
        return mTheQueue.poll(); // poll() doesn't wait, but returns null if nothing available
    }    
    
    
    /**
     * Here we just test this TweetFetchThread by starting it
     * and polling it every few seconds for the tweets it has.
     * @param args 
     */
    public static void main(String[] args) {
        
        TweetFetchThread tft = new TweetFetchThread();
//        TweetFetchThread tft = new TweetFetchThread("#processing");
        tft.start();
        
        IQueueAccessPoint queueAccessPoint = tft;
        
        while ( true ){
            zzzSleep(500);
            AbstractTweet aTweet;
            while ( (aTweet = queueAccessPoint.getNextOrNull() ) != null){  // technically the request and fetch should be one atomic opertion TODO
                System.out.println("Tweet fetched: "  + aTweet.toString());
            }
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
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
    private void submitTweetToQueue(Status tweet) throws InterruptedException {
        AbstractTweet aTweet = new AbstractTweet(tweet);
        mTheQueue.put(aTweet); // this will block the thread if the queue is full
                               // that's what I probably want. If noone is looking at the queu
                               // what's the point of repeatedly query tweets?
        
//        mTheQueue.add(aTweet); // this will throw IllegalStateException if queue is full
//        mTheQueue.offer(aTweet); // retrurns true/false if the queue is full
//        mTheQueue.offer(aTweet, 100, TimeUnit.MILLISECONDS); // retrurns true/false if the queue is full
//                                                             // but waits for the queue to free up
        
        
    }

    /**
     * We encapsulate printing of stacktrace into method,
     * because I read that this is how you should do this.
     * Just in case you may want to override it in the future. Or whatever.
     * @param ex 
     * TODO: this uses standard System.err stream or smth need to make it more abstract.
     * 
     */
    private void printException(Throwable ex) {
            ex.printStackTrace();
    }

    /**
     * This is "delegate" which allows abstract logging from within 
     * the thed.
     * @param format 
     */
    private void logln(String msg) {
        println(msg);
    }
}
