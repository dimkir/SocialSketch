package org.socialsketch.tool.tweetqueue;

// DK: I am not a big fan of putting here dependency on ImageIcon.. but 
// i guess that's the direction we're going.
// in future this should NOT be anything from Swing. but either more abstract
// "java.awt.Image" or maybe even just stream of bytes. Because I want (at least 
// as far as I can, keep AbstractTweet as "platform-independent" as possible.
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

    /**
     * This is message and also flag of the fact
     * that the whole AbstractTweet instance is DUMMY.
     * If it's not-NULL(valid string) then this instance is DUMMY
     */
    private String mDummyMessage;
    
    
    /**
     * Contains formatted text, which is preferred choice
     * of being returned by getText() in case it's not NULL.
     * Initial value is nULL, meaning that formatted text is not set.
     */
    private String mFormattedText;
    
    /**
     * This is icon string used by default, in case we're using dummy one.
     */
//    private String DEBUG_DEFAULT_ICON_STRING = "https://www.google.ie/images/srpr/logo11w.png"; // this one is too big, so it overheats my laptop
    private String DEBUG_DEFAULT_ICON_STRING = "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn1/c36.36.456.456/s160x160/522364_4101263010773_332752649_n.jpg"; 
    
    /**
     * This constructor is meant to create "dummy" abstract tweets.
     * @param dummyMessage 
     */
    public AbstractTweet(String dummyMessage) {
        mDummyMessage = dummyMessage;
    }

    
    
    AbstractTweet(Status tweet) {
        mOriginalTweet = tweet;
    }
    
    /**
     * Sets formatted text.
     * 
     * @param s NON-NULL value.
     * @throws NullPointerException when s is null.
     * @return instance of self for chaining.
     */
    public AbstractTweet setFormattedText(String s){
        if  ( s== null){
            throw new NullPointerException("null is not allowed as parameter for setFormattedText()");
        }
        mFormattedText = s;
        return this;
    }

    /**
     * Returns URL as string, pointing to the 
     * image representing this tweet. ? what does it return in case there's a 
     * WARNING: 
     *  What happens in case this is dummy Abstract tweet???
     * 
     * INCUIM: this should be poster's image.
     * @return 
     */
    public String getIconUrlString(){
        if ( isDummy() ){
            // TODO: what do we return ?
            return DEBUG_DEFAULT_ICON_STRING;
        }
        else{
            return mOriginalTweet.getUser().getBiggerProfileImageURL();
        }
    }
    
    /**
     * Returns some text? or actually code which will be inserted 
     * into PDE? 
     * TODO: maybe we need to rename it to "getCode()" because the way it
     * is used - is to retrieve code. However I don't want to tangle
     * different logics here. So I will leave it like it is.
     * @precond the mOriginalTweet is not null.
     * @return 
     */
    public String getText(){
         if ( isDummy() ){
             return mDummyMessage;
         }
         
         if ( mFormattedText != null ){
             return mFormattedText;
         }
         return mOriginalTweet.getText();
    }
    
    
    /**
     * Returns string representing status of the AbstractTweet
     * @return 
     */
    @Override
    public String toString() {
        if ( isDummy() ){
            return "This AbstrcatTweet intance is DUMMY: "  + mDummyMessage;
        }
        if ( mOriginalTweet == null ){
            return "[NULL]";
        }
        
        //return mOriginalTweet.toString();  // first let's try this.
                                            // just outputs too much shit: all the
                                            // json and stuff. messy on screen.
        Status t = mOriginalTweet;
        return String.format("Tweet by %s at %s: %s",  
                                t.getUser().getScreenName(),
                                t.getCreatedAt(),
                                t.getText()        
                            );
        //return mOriginalTweet.getText();
    }

    public long getTweetIdLong() {
        return mOriginalTweet.getId();
    }

    public String getUserWithoutAt() {
        // TODO: implement getUserWithoutAt
        return mOriginalTweet.getUser().getScreenName();
    }

    /**
     * Returns whether this is a dummy tweet or not. 
     * @return 
     */
    private boolean isDummy() {
        return (mDummyMessage != null);
        
    }
    
    
    
    
    
}
