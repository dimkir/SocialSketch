package firsttool;

import firsttool.tweetqueue.TweetFetchThread;
import firsttool.ui.IBasicPassiveUI;
import firsttool.ui.TweetBasicUI;

/**
 * This is service locator class. As described in 
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
    
    
    
    
    public static final String SVC_BASIC_SKETCH_MODDER = "basic_sketch_modder";
    private static SketchModder smSketchModder;

    
    public static final String SVC_BASIC_PASSIVE_UI = "svc_basic_passive_ui";
    private static IBasicPassiveUI smBasicPassiveUI;

    /**
     * WARNING: Be sure to include here all the available service names.
     */
    private static final String[] C_AVAILABLE_SERVICE_NAMES = {
                SVC_LIVE_TWEET_QUEUE,
                SVC_BASIC_SKETCH_MODDER,
                SVC_BASIC_PASSIVE_UI
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
        
        if ( serviceName.equals( SVC_BASIC_SKETCH_MODDER )){
            if ( smSketchModder == null ){
                smSketchModder = initializeEasyFrame();
            }
            return smSketchModder;
        }
        
        
        if ( serviceName.equals( SVC_BASIC_PASSIVE_UI )){
            if ( smBasicPassiveUI == null ){
                smBasicPassiveUI = initializeBasicPassiveUI();
            }
            return (ServiceRecord) smBasicPassiveUI;
        }        
        
        throw new IllegalArgumentException("Service named [" + serviceName + "] cannot be found");
    }

    /**
     * Encapsulates initialization of the EasyFrame
     */
    private static SketchModder initializeEasyFrame() {
        return new SketchModder();
    }

    
    /**
     * Returns list of service names available through this ServiceLocator
     * @return 
     */
    public static String[] getAvailableServiceNames() {
        return C_AVAILABLE_SERVICE_NAMES;
    }

    private static IBasicPassiveUI initializeBasicPassiveUI() {
        return new TweetBasicUI();
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
            
            if ( sinstance instanceof ISketchModder ){
                SketchModder frm = (SketchModder) sinstance;
                frm.setVisible(true);
            }
            
        }
        
        
         
    }
    
}
