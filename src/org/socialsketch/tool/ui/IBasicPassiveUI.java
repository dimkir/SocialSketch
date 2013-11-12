package org.socialsketch.tool.ui;

import org.socialsketch.tool.tweetqueue.AbstractTweet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;

/**
 * These are the methods which "Basic passive UI" should implement.
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public interface IBasicPassiveUI {
    
    void addTweet(AbstractTweet aTweet);
    void setOnBasicUIActionListener(IBasicUIActionListener listener);
    
    void setVisible(boolean visibleFlag);
    
    /**
     * In case there's explicit necessity to show parent JFrame to display the panel,
     * then this instance will be used.
     * 
     * This one is meant for using in case we need let's say display 
     * a DialogMessage or smth.
     * 
     * @return NULL in case there's no parent dash frame (eg. we're embedded as panel)
     */
    JFrame getJFrame();

    public interface IBasicUIActionListener
    {
        void onBasicUIAction(IBasicPassiveUI basicUI, BasicPassiveUIAction action);
    }
    
    public class BasicPassiveUIAction
    {
        public enum ActionTypes { SELECT_TWEET_PRIMARY , SELECT_TWEET_SECONDARY, REPLY_TO_TWEET };
        private ActionTypes mActionType;
        private String mActionDescr;
        
        private Map<String, Object> mParams = new HashMap<String, Object>();

        
        BasicPassiveUIAction setDescr(String dscr){
            mActionDescr = dscr;
            return this; // for chaining
        }
        
        public BasicPassiveUIAction(ActionTypes actionType) {
            mActionType = actionType;
        }
        
        /**
         * 
         * 
         * @param key
         * @param val
         * @return  self for chaining.
         */
        BasicPassiveUIAction setParam(String key, Object val){
            mParams.put(key, val);
            return this;
        }
        
        /**
         * Fetches parameter with given name or throws if that parameter doesn't exist.
         * We go for early crash.
         * 
         * @param key - name of EXISTING parameter.
         * @throws IllegalArgumentException if there's no such element.
         * @return 
         */
        public Object getParam(String key){
            if ( ! mParams.containsKey(key) ){
                throw new IllegalArgumentException("key [" + key + "] is not availble as one of the parametes of BasicPassivUIAction");
            }
            return mParams.get(key);
        }

        
        
        
        public BasicPassiveUIAction(String actionDescr)
        {
             if ( actionDescr == null ){
                 throw new NullPointerException("parameter actionDescr cannot be null");
             }
             mActionDescr = actionDescr;
        }

        @Override
        public String toString() {
            return mActionDescr;
        }
        
        
        
        public ActionTypes getType(){
            return mActionType;
        }
    }
}

