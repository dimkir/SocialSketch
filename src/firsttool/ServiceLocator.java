package firsttool;

/**
 * This is service locator class. As decribed in 
 * @see http://gameprogrammingpatterns.com/service-locator.html
 * In order to decouple classes(components) of this package
 * as much as possible. It seems currently beneficial
 * to have single access point to different classes.
 * 
 * 
 * We'll start with providing access point to TweetFetchThread queue.
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class ServiceLocator {
    
    public static final String SVC_LIVE_TWEET_QUEUE = "live_tweet_queue"; 
                                // this is service provides 
                                // tweet queue
    
    private static TweetFetchThread smFetchThread;

    
    /**
     * Returns requested service.
     * @param serviceName allowed service names are public constants of ServiceLocator,
     *                  starting with SVC_
     * @return 
     */
    static public ServiceRecord getSerivce(String serviceName){
        if ( serviceName.equals(SVC_LIVE_TWEET_QUEUE)){
            if ( smFetchThread == null ){
                smFetchThread = new TweetFetchThread();    
                smFetchThread.start();
            }
            
            return smFetchThread;
            
        }
        throw new IllegalArgumentException("Service named [" + serviceName + "] cannot be found");
    }
            
            
     
    /**
     * Just base class for the services provided via ServiceProvider.
     */
    public interface ServiceRecord {
            String getServiceDescription();
    }
}
