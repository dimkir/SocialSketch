package firsttool;

import twitter4j.Status;

/**
 * This is the abstract tweet object which is used to transfer information
 * to the subscriber (observer).
 * I like introducing extra wrappers and layers, because they allow to build 
 * less coupled systems. Maybe in future this isn't even going to be a tweet,
 * but some abstract entity which is returned by some "polling service".
 *
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class AbstractTweet {
    /**
     * Simplest implementation we just save here reference to Status.
     * We assume Status is POJO.
     */
    private Status mOriginalTweet;

    
    AbstractTweet(Status tweet) {
        mOriginalTweet = tweet;
    }

    @Override
    public String toString() {
        if ( mOriginalTweet == null ){
            return "[NULL]";
        }
        
        
        return mOriginalTweet.toString();  // first let's try this.
        //return mOriginalTweet.getText();
    }
    
    
    
    
    
}
