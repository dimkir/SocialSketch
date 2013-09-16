package firsttool;

import firsttool.tweetqueue.AbstractTweet;
import processing.app.Formatter;

/**
 * This is interface for the UI containing tweets. 
 * This is interface through which UI is exposed to the Processing Tool.
 * 
 * This Interface should provide way for the ProcessingTool to work
 * with different abstract UIs.
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public interface ISketchModder {

    public void setVisible(boolean b);
    
    public void setOnModderActionListener(IOnModderActionListener listener);

//    public void setOnTweetInsertSelectedListener(IOnTweetPickListener aThis);
//
//    public void setOnTweetReplaceSelectedListener(IOnTweetPickListener iOnTweetPickListener);
//
//    public void setOnFetchTweetFromPDESelectedListener(IOnTweetPickListener myFetchFromPDEListener);

    public void sendSketchCode(String text, AbstractTweet aTweet);

    /**
     * Sets formatter so that Modder can format source code.
     * 
     * @param createFormatter 
     */
    public void setSourceCodeFormatter(Formatter createFormatter);
    
    
    /**
     * This is happening when tweet was "selected". (Here I more mean abstract "selected" by user
     * for some purpose. Not just "selected in menu". Maybe user picked it and clicked "insert" button or smth.
     * 
     * This interface is provided, to create a convenient 
     * abstraction layer. The client of EasyFrame (can be any external class or superclass)
     * just has to:
     *  1) create instance of the EasyFrame 
     *  2) subscribe to this event and he knows that: list of updated tweets will be
     *      presented to user. And user can pick a tweet. (eg. can pick tweet to be inserted into PDE)
     */
    public interface IOnTweetPickListener{
        void onTweetPickListener(AbstractTweet tweet, SketchModder easyFrame); 
                                                  // TODO ^^^^^ this maybe later can be extracted
                                                  //            to interface
    }        
    
    public interface IOnModderActionListener
    {
        void onModderAction(ISketchModder modder, ModderAction action);
    }
    
    public class ModderAction implements ModderActionTypes
    {
        private AbstractTweet mTweet;
        
        private String mMsg;
        private int mActionType;
        
        /**
         * Source code used as parameter for some actions.
         */
        private String mCode;
        
        ModderAction(String msg, int actionType){
            if ( msg == null ){
                throw new NullPointerException("msg parameter cannot be null");
            }
            mActionType = actionType;
            mMsg = msg;
        }
        
        /**
         * Sets tweet and returns instance for chaining.
         * 
         * @throws NullPointerException if there's a null supplied insted of tweet.
         * @param tw NON-NULL value of tweet.
         * @return 
         */
        ModderAction setTweet(AbstractTweet tw){
            if ( tw == null ){
                throw new NullPointerException("parameter tw cannot be NULL");
            }
            mTweet = tw;
            return this; // for chaining
        }

        @Override
        public String toString() {
            return mMsg;
        }
        
        public int getActionType(){
            return mActionType;
        }

        /**
         * Returns tweet if it is part of the action or NULL if there's no tweet
         * associated with the action.
         * @return 
         */
        AbstractTweet getTweet() {
            return mTweet;
        }

        /**
         * Sets source code for the action. (if action was to replace in editor for example).
         * @param code NON-null source code.
         */
        void setSourceCode(String code) {
            if ( code == null ){
                throw new NullPointerException("parameter for setCode() cannot be null");
            }
            mCode = code;
        }
        
        public String getSourceCode(){
            return mCode;
        }
        
    }
    
    public interface ModderActionTypes
    {
        public static final int ACTION_CODE_EDITOR_REPLACE_WITH = 1;
        public static final int ACTION_CODE_EDITOR_INSERT_INTO = 2;
        public static final int ACTION_MODDER_REQUESTS_CODE = 3;
        
    }
}
