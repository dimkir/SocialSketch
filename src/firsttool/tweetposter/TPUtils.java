package firsttool.tweetposter;

import twitter4j.MediaEntity;
import twitter4j.URLEntity;

/**
 * These are utils to be used within the package.
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
class TPUtils {

    /**
     * Prints media entities to stdout.
     * 
     * @param mediaEntities the value of mediaEntities
     */
    static void printMediaEntities(MediaEntity[] mediaEntities) {
        if (mediaEntities == null) {
            System.out.println("$$$$ MEDIA Entities are NULL empty");
            return;
        }
        for (MediaEntity medent : mediaEntities) {
            System.out.println("$$$$ Media Entity: " + medent.getMediaURL());
        }
    }

    /**
     * Just helper method to print url entities.
     *
     * @param urlEntities can be NULL if no entities available.
     */
    static void printUrlEntities(URLEntity[] urlEntities) {
        if (urlEntities == null) {
            System.out.println("$$$$ URL Entities are NULL empty");
            return;
        }
        for (URLEntity ue : urlEntities) {
            System.out.println("$$$$ URL Entity: " + ue.getURL());
        }
    }
    
}
