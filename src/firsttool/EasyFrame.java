package firsttool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * When using this frame, all of the "building of frame" is hidden
 * in the super class
 * this should be just "controller" for logic of the elements.
 * This class should have NO dependencies on the PDE Tool or Editor or
 * whatever elements. This class should provide ability for those classes
 * to Observe it.
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class EasyFrame extends TweetBasicUI
implements TweetBasicUI.IOnNewTweetsListener,
            ServiceLocator.ServiceRecord
{

    private DefaultListModel listModel;
    private boolean listModelEmpty = true;
    public EasyFrame() {
        super();
        listModel = new DefaultListModel();
        listModel.addElement("Searching for processing sketches on twitter...");
        setListModel(listModel);
        setOnNewTweetsListener(this);
        
        getSelectButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractTweet aTweet = getSelectedInListTweet();
                if ( aTweet == null ){
                    messageBox("First select tweet in the list.");
                    return;
                }
                fireOnTweetPickListenerEvent(aTweet, EasyFrame.this);
            }

        });
    }

    /**
     * Just displays message box.
     * @param msg 
     */
    private void messageBox(String msg) {
        JOptionPane.showMessageDialog(this,  msg);
    }
    
    
    @Override
    public void onNewTweets(ArrayList<AbstractTweet> newTweets) {
        // add tweets to the list.
        System.out.println("onNewTweets() is called with : " + newTweets.size() + " tweets");
        if ( listModelEmpty ){
            listModel.clear(); // when list is empty, it will have "No elements avialble" element
                               // so we need to clean it
            listModelEmpty = false;
        }
        
        for(AbstractTweet aTweet : newTweets){
            listModel.add(0, aTweet);
        }
        
    }    
    
   // ====================================================================  
   // ====================================================================  
   // ======================   this is crap resp for IOnTweetListener ====  
   // ==================================interfce =========================  
   // ====================================================================  
    /**
     * This fires the event (and forwards it to observers)
     * @param aTweet
     * @param easyFrame 
     */
    protected void fireOnTweetPickListenerEvent(AbstractTweet aTweet, EasyFrame easyFrame){
        if ( mOnTweetPickListener  != null ){
            mOnTweetPickListener.onTweetPickListener(aTweet, easyFrame);
        }
    }
    
    private IOnTweetPickListener mOnTweetPickListener = null;
    void setOnTweetPickListener(IOnTweetPickListener listener){
        mOnTweetPickListener = listener;
    }

    @Override
    public String getServiceDescription() {
        return "EasyFrame service for simple UI with tweets";
    }
    
    
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
    interface IOnTweetPickListener{
        void onTweetPickListener(AbstractTweet tweet, EasyFrame easyFrame); 
                                                  // TODO ^^^^^ this maybe later can be extracted
                                                  //            to interface
    }    
    
    
    // ===========================================================
    // ===========================================================
    // ========================= static methods  =================
    // ===========================================================
    // ===========================================================
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGUI();
            }
        });
    }
    
    
    private static void createGUI(){
            new EasyFrame().setVisible(true);
    }


    
}
