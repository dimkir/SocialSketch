package firsttool.tweetposter;

import twitter4j.Status;
import twitter4j.TwitterException;

/**
 * This is event communicating to the "caller" the status of posting operation.
 *
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class TweetPostCompleteEvent {
    private String mTweetUrl;
    private boolean mSuccess;
    private TwitterException mException;
    
    
    
    public TweetPostCompleteEvent(Status status) {
         mTweetUrl = makeDirectUrl(status);
         mSuccess= true;
    }

    public TweetPostCompleteEvent() {
        mSuccess = false;
    }
    
    
    
    
    
    
    public boolean isSuccessful(){
        return mSuccess;
    }

    public String getTweetUrl() {
        
        return mTweetUrl;
    }

    TweetPostCompleteEvent setException(TwitterException ex) {
        // TODO: implement setException
            mException = ex;
            return this;  // for chaining
    }

    /**
     * Creates direct URL in form of :
     * {@link https://twitter.com/redFrik/status/371780282010959872}
     * 
     * to a given status.
     * @param status
     * @return 
     */
    private String makeDirectUrl(Status status) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://twitter.com/");
        sb.append(status.getUser().getScreenName());
        sb.append("/status/");
        sb.append(status.getId());
        return sb.toString();
                
    }

    public Exception getException() {
        // TODO: implement getException
        return mException;
    }
    
}
