package org.socialsketch.tool.tweetposter;

import twitter4j.MediaEntity;
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
    private final MediaEntity[] mediaEntities;
    
    
    
    public TweetPostCompleteEvent(Status updatedStatus) {
        mTweetUrl = makeDirectUrl(updatedStatus);
        mSuccess = true;
        
        mediaEntities = updatedStatus.getMediaEntities();
        TPUtils.printMediaEntities(mediaEntities);
        
    }

    public TweetPostCompleteEvent() {
        mSuccess = false;
        mediaEntities = null;
    }
    
    /**
     * May return NULL in case there were NO image with the tweet.
     * @return 
     */
    public String getImageUrl(){
        if ( mediaEntities == null ){
            return null;
        }
        return mediaEntities[0].getMediaURL();
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
