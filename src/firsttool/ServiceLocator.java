package firsttool;

/**
 * This is service locator class. As decribed in 
 * {@see http://gameprogrammingpatterns.com/service-locator.html}
 * In order to decouple classes(components) of this package
 * as much as possible. It seems currently beneficial
 * to have single access point to different classes.
 * 
 * 
 * We'll start with providing access point to TweetFetchThread queue.
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class ServiceLocator {
    
                                // this is service provides 
                                // tweet queue
    public static final String SVC_LIVE_TWEET_QUEUE = "live_tweet_queue"; 
    private static TweetFetchThread smFetchThread;
    
    
    
    
    public static final String SVC_EASY_TWEETED_SKETCHES_UI = "easy_tweeted_sketches_ui";
    private static EasyFrame smEasyFrame;

    /**
     * WARNING: Be sure to include here all the available service names.
     */
    private static final String[] C_AVAILABLE_SERVICE_NAMES = {
                SVC_LIVE_TWEET_QUEUE,
                SVC_EASY_TWEETED_SKETCHES_UI
    };
    
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
        
        if ( serviceName.equals( SVC_EASY_TWEETED_SKETCHES_UI )){
            if ( smEasyFrame == null ){
                initializeEasyFrame();
            }
            return smEasyFrame;
        }
        
        throw new IllegalArgumentException("Service named [" + serviceName + "] cannot be found");
    }

    /**
     * Encapsulates initialization of the EasyFrame
     */
    private static void initializeEasyFrame() {
        smEasyFrame = new EasyFrame();
    }

    
    /**
     * Returns list of service names available through this ServiceLocator
     * @return 
     */
    public static String[] getAvailableServiceNames() {
        return C_AVAILABLE_SERVICE_NAMES;
    }
            
            
     
    /**
     * Just base class for the services provided via ServiceProvider.
     */
    public interface ServiceRecord {
            String getServiceDescription();
    }
    
    
    
    /**
     * Just prints out the list of services and 
     * tries to instantiate them.
     * 
     * @param args 
     */
    public static void main(String[] args) {
        String[] serviceNames = ServiceLocator.getAvailableServiceNames();
        
        // just list service names.
        for(String svc : serviceNames){
            System.out.println("Found service: [" +  svc + "]");
        }

        // now let's try to isntantiate them and get description
        for(String svc : serviceNames){
            System.out.println("Instantiating service: " + svc + " ...");
            
            ServiceRecord sinstance = ServiceLocator.getSerivce(svc);
            
            
            System.out.println("Found service: [" +  svc + "] with description: " + sinstance.getServiceDescription());
            
            if ( sinstance instanceof EasyFrame ){
                EasyFrame frm = (EasyFrame) sinstance;
                frm.setVisible(true);
            }
            
        }
        
        
         
    }
    
}
