package firsttool.ui;

import firsttool.ServiceLocator;
import firsttool.ServiceLocator.ServiceRecord;
import firsttool.tweetqueue.AbstractTweet;
import firsttool.tweetqueue.IQueueAccessPoint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * Offer UI for displaying processing-related-tweets and convenient
 * API so that Processing Tool can conveniently communicate with the UI.
 * Also fires fires up the thread polling for new threads.
 * 
 * What's the purpose (conforming to SingleResponsibilityPrinciple)
 * of this class?
 * -construct frame capable of displaying Tweets? 
 *                                          ^^^^ ( how exactly do we define this "Tweet")?
 * display frame with updated tweets?
 * -implement events when user "clicks" on certain things??
 * 
 * At what abstraction level do we work?
 * -----------------------------------------
 * 
 * 
 * 
 * Which "abstract" concepts does this class control?
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class TweetBasicUI extends javax.swing.JFrame
implements IBasicPassiveUI, ServiceLocator.ServiceRecord
{
    private DefaultListModel listModel;
    
    private List<AbstractTweet> mTweets = new ArrayList<AbstractTweet>();
    
    
    private LocalIconStore mIconStore = new LocalIconStore();
    
    /**
     * Creates new form NewJFrame
     */
    public TweetBasicUI() {
        initComponents();
        //jList1.setModel(new DefaultListModel<AbstractTweet>());
        
        listModel = new DefaultListModel();
        listModel.addElement("Searching for processing sketches on twitter...");
        setListModel(listModel);        
        
        
        jList1.setCellRenderer(new TweetCellRender(mIconStore));
        
        
        jList1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //super.mouseClicked(e); //To change body of generated methods, choose Tools | Templates.
                JList list = (JList) e.getSource();
                System.out.println("JList got click event, at the moment: " + e.getClickCount() + " click(s) event");
                if (e.getClickCount() == 2) {
                    int index = list.locationToIndex(e.getPoint());
                    if ( index < 0 ){
                        return; // no supportive model.
                    }
                    ListModel dlm = list.getModel();
                    Object item = dlm.getElementAt(index);
                    list.ensureIndexIsVisible(index); // what is this? scrolling to visible?
                    System.out.println("Double clicked on " + item);
//                    fireBasicUIAction(new BasicPassiveUIAction("double clicked on item [" + item + "] in the list of tweets"));
                    String descr = "double clicked on item [" + item + "] in the list of tweets";
                    fireBasicUIAction(new BasicPassiveUIAction(BasicPassiveUIAction.ActionTypes.SELECT_TWEET_PRIMARY).setParam("tweet", item).setDescr(descr));
                }
            }
        }
    );
        

    }

    @Override
    public void addTweet(AbstractTweet aTweet) {
        // add tweets to the list.
        //System.out.println("onNewTweets() is called with : " + newTweets.size() + " tweets");
        if ( mTweets.isEmpty() ){
            listModel.clear(); // when list is empty, it will have "No elements avialble" element
                               // so we need to clean it
        }

        // add to internal list
            mTweets.add(aTweet);
        // add to model
            listModel.add(0, aTweet);
        
    }

    @Override
    public void setOnBasicUIActionListener(IBasicUIActionListener listener) {
        actionListener = listener;
    }
    
    private IBasicUIActionListener actionListener;
    
    private void fireBasicUIAction(IBasicPassiveUI.BasicPassiveUIAction action){
        if ( actionListener != null){
            actionListener.onBasicUIAction(this, action);
        }
        else{
            throw new RuntimeException("There is some event, but nooone is registererd to it.");
        }
    }
//
//    // -------------------------------------------------------------------------------
//    /**
//     * This is interface observer may subscribe to.
//     */
//    interface IOnNewTweetsListener
//    {
//        void onNewTweets(ArrayList<AbstractTweet> newTweets);
//    }
//    
//    
//    private IOnNewTweetsListener mOnNewTweetsListener;
//    
//    protected void fireNewTweetsEvent(ArrayList<AbstractTweet> newTweets){
//         if ( mOnNewTweetsListener != null ){
//                mOnNewTweetsListener.onNewTweets(newTweets);
//         }
//    }
//    
//    void setOnNewTweetsListener(IOnNewTweetsListener listener){
//         mOnNewTweetsListener = listener;
//    }
    // --------------------------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        btnInsertIntoPDE = new javax.swing.JButton();
        btnReplaceInPDE = new javax.swing.JButton();
        chkAlwaysOnTop = new javax.swing.JCheckBox();
        btnReplyToTweetWithCode = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jScrollPane1.setViewportView(jList1);

        jLabel1.setText("Tweets");

        btnInsertIntoPDE.setText("Insert into PDE");
        btnInsertIntoPDE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertIntoPDEActionPerformed(evt);
            }
        });

        btnReplaceInPDE.setText("Replace in PDE");
        btnReplaceInPDE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReplaceInPDEActionPerformed(evt);
            }
        });

        chkAlwaysOnTop.setText("Set Always On Top");
        chkAlwaysOnTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAlwaysOnTopActionPerformed(evt);
            }
        });

        btnReplyToTweetWithCode.setText("REPLY WITH PDE Code to current tweet");
        btnReplyToTweetWithCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReplyToTweetWithCodeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addGap(56, 56, 56)
                        .addComponent(btnInsertIntoPDE)
                        .addGap(33, 33, 33)
                        .addComponent(btnReplaceInPDE)
                        .addGap(18, 18, 18)
                        .addComponent(btnReplyToTweetWithCode, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(chkAlwaysOnTop)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1)
                    .addComponent(btnInsertIntoPDE, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                    .addComponent(btnReplaceInPDE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnReplyToTweetWithCode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(chkAlwaysOnTop)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnInsertIntoPDEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertIntoPDEActionPerformed
        // TODO add your handling code here:
        String descr = "pressed insert into pde button";
        AbstractTweet aTweet = getSelectedTweet();
        if ( aTweet == null){
           messageBox("First select tweet from the list box, to insert it into PDE");   
        }
        else{
            fireBasicUIAction(new BasicPassiveUIAction(BasicPassiveUIAction.ActionTypes.SELECT_TWEET_SECONDARY).setParam("tweet", aTweet));
        }
    }//GEN-LAST:event_btnInsertIntoPDEActionPerformed

    private void chkAlwaysOnTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAlwaysOnTopActionPerformed
        // TODO add your handling code here:
        JCheckBox chkbox =  (JCheckBox) evt.getSource();
        if ( chkbox.isSelected() ){
            setAlwaysOnTop(true);
        }
        else{
            setAlwaysOnTop(false);
        }
    }//GEN-LAST:event_chkAlwaysOnTopActionPerformed

    private void btnReplaceInPDEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReplaceInPDEActionPerformed
        //fireBasicUIAction(new BasicPassiveUIAction("pressed 'Replace in PDE' action"));
        
        String descr = "pressed replace in pde button";
        AbstractTweet aTweet = getSelectedTweet();
        if ( aTweet == null){
           messageBox("First select tweet from the list box, to send it into PDE");   
        }
        else{
            fireBasicUIAction(new BasicPassiveUIAction(BasicPassiveUIAction.ActionTypes.SELECT_TWEET_PRIMARY).setParam("tweet", aTweet));
        }        
    }//GEN-LAST:event_btnReplaceInPDEActionPerformed

    private void btnReplyToTweetWithCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReplyToTweetWithCodeActionPerformed
        // TODO add your handling code here:
        //fireBasicUIAction(new BasicPassiveUIAction("pressed 'Reply to code with PDE button event' a"));
        
        String descr = "pressed reply with code button";
        AbstractTweet aTweet = getSelectedTweet();
        if ( aTweet == null){
           messageBox("First select tweet from the list box to be able to reply to it");   
        }
        else{
            fireBasicUIAction(new BasicPassiveUIAction(BasicPassiveUIAction.ActionTypes.REPLY_TO_TWEET).setParam("tweet", aTweet));
        }        
    }//GEN-LAST:event_btnReplyToTweetWithCodeActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TweetBasicUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TweetBasicUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TweetBasicUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TweetBasicUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new TweetBasicUI();
                frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
                
                final IBasicPassiveUI ui =  (IBasicPassiveUI) frame;
                ui.setOnBasicUIActionListener(new IBasicUIActionListener() {

                    @Override
                    public void onBasicUIAction(IBasicPassiveUI basicUI, BasicPassiveUIAction action) {
                        JOptionPane.showMessageDialog(basicUI.getJFrame(), "Got action: " + action.toString());
                        ui.addTweet(new AbstractTweet("Dummy tweet"));
                    }
                });
                ui.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnInsertIntoPDE;
    private javax.swing.JButton btnReplaceInPDE;
    private javax.swing.JButton btnReplyToTweetWithCode;
    private javax.swing.JCheckBox chkAlwaysOnTop;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    protected void setListModel(ListModel lmodel){
        jList1.setModel(lmodel);
    }


    
//    /**
//     * Our super class knows that INCUIM we use
//     * button as a way to select tweet.
//     * TODO: need to make these abstraction layers more clear. 
//     * Maybe need to draw a diagram.
//     * @return 
//     */
//    protected JButton getSelectButton(){
//        return btnInsertIntoPDE;
//    }
    

    /**
     * Just returns selected in the list tweet. Or null
     * @return  NULL on nothing selected or error casting.
     */
    protected AbstractTweet getSelectedInListTweet() {
        Object selection = jList1.getSelectedValue();
        if ( selection == null ){ 
            return null;
        }
        if  ( selection instanceof AbstractTweet ){
            return (AbstractTweet) selection;
        }
        System.out.println("Error casting JList element to AbstractTweet. Probably just string was in the list");
        return null;
    }

    @Override
    public JFrame getJFrame() {
        System.out.println("Called getJFrame()");
        return this;
    }

    @Override
    public String getServiceDescription() {
        return "TweetBasicUI";
    }

    /**
     * Returns selected tweet or NULL in case there's nothing selected.
     * @return NULL in case nothing selected.
     *         valid AbstractTweet.
     */
    private AbstractTweet getSelectedTweet() {
       //? throw new UnsupportedOperationException("Not yet implemented");
       return (AbstractTweet) jList1.getSelectedValue();
    }

    /**
     * Just "verb" for showing message box.
     * @param msg
     */
    private void messageBox(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
    
    
}
