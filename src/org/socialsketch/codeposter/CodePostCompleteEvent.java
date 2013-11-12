package org.socialsketch.codeposter;

/**
 * This is event which informs callback of the status of the operations of code post.
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class CodePostCompleteEvent {
    
    
    private final boolean mOperationSuccess;
    private final String mHtmlUrl;
    private final String mErrorMessage;
    
    /**
     * Gist id, NULL if not available.
     */
    private String mGistId;
    private Exception mCause;

    /**
     * Creates event. If event successful the param0 is the success url.
     * If event is Unsuccessful param0 is the error message.
     * @param param0
     * @param successFlag 
     */
    CodePostCompleteEvent(String param0, boolean successFlag) {
        // TODO: implement CodePostCompleteEvent
        mOperationSuccess = successFlag;
        if (  mOperationSuccess ){
            mHtmlUrl = param0;
            mErrorMessage = null;
        }
        else{
            mErrorMessage = param0;
            mHtmlUrl = null;
        }
    }

    public String getUrlString() {
        return mHtmlUrl;
    }
    
    
    /**
     * Returns if the last operation was successful or not.
     * @return 
     */
    public boolean isSuccessful(){
        return mOperationSuccess;
    }

    /**
     *  Sets gist ID.
     */
    void setGistId(String id) {
        mGistId = id;
    }
    
    /**
     * Returns gist Id. (If set. )
     * @return 
     */
    public String getGistId(){
        return mGistId;
    }

    public void setCause(Exception ex){
        mCause = ex;
    }
    
    public Exception getCause() {
        return mCause;
    }
    
    
}
