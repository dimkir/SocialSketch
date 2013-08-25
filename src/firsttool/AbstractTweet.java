package firsttool;

// DK: I am not a big fan of putting here dependency on ImageIcon.. but 
// i guess that's the direction we're going.
// in future this should NOT be anything from Swing. but either more abstract
// "java.awt.Image" or maybe even just stream of bytes. Because I want (at least 
// as far as I can, keep AbstractTweet as "platform-independent" as possible.
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
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

    /**
     * Returns ImageIcon (in swing format) of the
     * tweet author.
     * @return 
     */
    public ImageIcon getImageIcon(){
        // load image from resource
        URL url = getClass().getClassLoader().getResource("resources/monotone_smiley_happy.png");
        
        ImageIcon icon  = new ImageIcon(url);
        return icon;
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
         return mOriginalTweet.getText();
    }
    
    @Override
    public String toString() {
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
    
    
    
    
    
}
