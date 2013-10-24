package org.socialsketch.tool.tweetposter;

/**
 * Interface for callback, informing that some operation of TweetPoster is complete.
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public interface ITweetPostComplete {
    
    /**
     * Informs listener that some operation of TweetPoster is complete.
     * @param evt 
     */
    void onTweetPostComplete(TweetPostCompleteEvent evt);
}
