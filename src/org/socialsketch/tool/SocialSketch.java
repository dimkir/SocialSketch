/* Distributed under dual license:
 * 
 * Creative Commons Attribution Share-Alike Non-Commercial
 * CC BY SA NC license.
 * 
 * and
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public L-icense for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 */
package org.socialsketch.tool;

import org.socialsketch.ui.dialogcallbacks.AdvancedCallback;
import org.socialsketch.ui.sharedialog.IconDemoApp;
import org.socialsketch.ui.dialogcallbacks.SSDialogParams;
import org.socialsketch.tool.ISketchModder.IOnModderActionListener;
import org.socialsketch.tool.tweetqueue.AbstractTweet;
import org.socialsketch.tool.ISketchModder.ModderAction;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.socialsketch.codeposter.CodePostCompleteEvent;
import org.socialsketch.codeposter.CodePoster;
import org.socialsketch.codeposter.ICodePostComplete;
import org.socialsketch.tool.ui.DashFrame;
import org.socialsketch.tool.ui.Dashboard;
import org.socialsketch.tool.ui.dialogs.MessageDialogClickable;
import org.socialsketch.tool.utils.ToolUtils;
import org.socialsketch.ui.imgpanel.example.SimpleGridView2;
import processing.app.Editor;
import processing.app.Sketch;
import processing.app.syntax.JEditTextArea;
import processing.app.tools.Tool;

/**
 * Tweet to sketch tool entry point class. 
 * 
 * This class has 3 roles:
 * a) this is entry point into the tool (as per Processing Tool specifications)
 * b) this is liason between rest of modules and the Processing IDE. No other
 * classes should be directly dependent/coupled to Processing IDE.
 * c) This is kinda class which is coupled to components performing "sharing operations"
 * but the components are NOT coupled to this class. This is made via implementing "observer" pattern
 * to receive events from the subject components. So this is OBSERVER class.
 * 
 * <p><b>Paradigm</b></p>
 * <p>
 * Paradigm of this class is as follows: it is extension of the PDE. When called, it initializes
 * components which can either service directly (eg. post a tweet), or registers for events
 * which User can feed into this component from the UI of the components.
 * </p>
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */

public class SocialSketch  
implements Tool, IDebugPrint
{
    /**
     * This is resource path for the text file, which contains
     * pde-source code which implements saving of the sketch "screenshots" upon keyrelease
     */
    private  static final String RESOURCE_NAME_FOR_FILE_WITH_SAVEFRAME_CODE = "/resources/my_save_frame.pde";
    
    private Editor mEditor;

    private boolean mSuccessfulInit = false;
    private ISketchModder mSketchModder;
    private Sketch sketch;
    
    private CodePoster mCodePoster;
    
    /**
     * If the the panel is externalized, this holds externalized frame reference.
     * Also works as a flag.
     */
    private DashFrame mExternalizedDashFrame; 
    
    @Override
    public void init(Editor editor) {
        mEditor = editor;
        try{
            mCodePoster = new CodePoster();
    //        writeLn("init method called from our first tool! Init was called already flag: " + mInitWasCalledAlredy);
            mSuccessfulInit = true;
        }
        catch(IOException ioex){
            mSuccessfulInit = false;
            MessageDialogClickable.showErrorMessage(editor, "Error initializing code poster", ioex);
        }
    }

    
    /**
     * This happens when tool is selected from the menu.
     * First and second and third times. All the time.
     */
    @Override
    public void run() {
        if ( ! mSuccessfulInit ){
            MessageDialogClickable.showErrorMessage(mEditor, "Cannot open the tool, because initialization wasn't successful!", null);
            return;
        }
        

        /**
         * First I need to add dashboard to the side.
         */
        mDashboard = new Dashboard();
        mEditor.add(mDashboard, BorderLayout.EAST);
        
        
        addButtonBar(mEditor);
        
       // addContextMenu(mEditor.getTextArea());
        
        
        sketch = mEditor.getSketch();
//        SketchCode code = sketch.getCode(0);
//        //sketch.handleNewCode();
        
        printSketchFolder();
        
//        writeLn("Now executing run() method. Has run() been called already?: " + mRunWasCalledAlredy);
        try {

            if ( mSketchModder == null) {
                    mSketchModder = initModder(); // and adds listeners
            }

            mSketchModder.setVisible(true); // hopefull this one is not blocking.
        
        }
        catch( ServiceLocator.ServiceLocatorException ex){
            JOptionPane.showMessageDialog(mEditor, "When loading sketch modder, we got the following error: " + ex.getMessage());
        }
    }
    private Dashboard mDashboard;
    


    @Override
    public String getMenuTitle() {
        return "Tweet-A-Sketch";
    }
    
    
    
    
    @Override
    public void writeLn(String s){
        System.out.println(s);
    }
    
    //======================================================================
    //======================================================================
    //=========================== static methods ===========================
    //======================================================================
    //======================================================================
    
    public static void main(String[] args) {
        // TODO code application logic here
        // some testing? or actually we can't run it standalone??
        throw new UnsupportedOperationException("Need to implement main, if that's something possible?");
    }

    /**
     * Just instantiates tweet UI (retrieving it from service)
     * and sets up listeners.
     * 
     * @return 
     */
    private ISketchModder initModder() throws ServiceLocator.ServiceLocatorException {
        
          ISketchModder  sketchModder = (ISketchModder) ServiceLocator.getSerivce(ServiceLocator.SVC_BASIC_SKETCH_MODDER);
           
          sketchModder.setOnModderActionListener(new MyOnModderActionListener());
          
          sketchModder.setSourceCodeFormatter(mEditor.createFormatter());
          return sketchModder;
    }

    /**
     * Helper method prints current sketch folder
     */
    private void printSketchFolder() {
        File sketchFolder = sketch.getFolder();
        System.out.println("++++++++++sketch folder: [" + sketchFolder.getAbsolutePath() + "]");
    }

    /**
     * Just test which adds button bar to the PDE window.
     * @param mEditor 
     */
    private void addButtonBar(Editor editor) {
        // TODO: implement addButtonBar
        JToolBar bar = new JToolBar();
        
        // -------------------------------------------------------------
        // ----------- detach pane button and logic --------------------
        // -------------------------------------------------------------
        final JButton detachPane = new JButton("Detach pane");
        ImageIcon icon = new ImageIcon("/resources/icons/binoculars.png");
        detachPane.setIcon(icon);
        detachPane.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 // detach pane.
                if ( mExternalizedDashFrame == null ){
                    // nothing externalized, need externalize
                    mExternalizedDashFrame = new DashFrame(mDashboard);
                    mExternalizedDashFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    mExternalizedDashFrame.addWindowListener(new MyWindowListener(new Runnable() {

                        @Override
                        public void run() {
                            internalizeDash();
                            refreshEditor();
                        }
                    }));
                    
                    mExternalizedDashFrame.setVisible(true);
                    // need to remove it from current dash.
                    //??
//                    BorderLayout layout = (BorderLayout) mEditor.getLayout();
//                    mEditor.remove(layout.getLayoutComponent(BorderLayout.EAST));                    
                    detachPane.setText("Reattach pane");
                }
                else{
                    // CURRENTLY EXTERNALIZED, need internalize.
                    internalizeDash();
                }
                // refresh the editor window
                refreshEditor();
            }
            
            void internalizeDash(){
                    mExternalizedDashFrame.setDashboard(null); // we reset links
                    mEditor.add(mDashboard, BorderLayout.EAST);
                    mExternalizedDashFrame.dispose();
                    mExternalizedDashFrame = null;
                    detachPane.setText("Detach pane");
            }
            
            void refreshEditor(){
                mEditor.repaint();
                mEditor.validate();
            }
        });
        
        bar.add(detachPane);
        
        // -------------------------------------------------------------
        // ----------- button to save sketch to gist --------------------
        // -------------------------------------------------------------
        final JButton btnSaveUpdate = new JButton("Save/Update sketch as gist");
        btnSaveUpdate.addActionListener(new ActionListener() {
            

            @Override
            public void actionPerformed(ActionEvent e) {
                btnSaveUpdate.setEnabled(false);
                final SketchAdapter skadapter = new SketchAdapter(mEditor.getSketch());
                
                mCodePoster.saveUpdateFromAdapter(skadapter, new ICodePostComplete() {
                    @Override
                    public void onCodePostComplete(CodePostCompleteEvent evt) {
                        if ( evt.isSuccessful() ){
                            MessageDialogClickable.show(mEditor, "Success saving/updating gist #" + evt.getGistId(), evt.getUrlString());
                            if ( skadapter.getPreviousPostId() == null ){
                                // I wonder if this works...
                                // this doesn't really work...
                                String program = mEditor.getSketch().getCode(0).getProgram();
                                program = "//#GIST:" + evt.getGistId() +"\n\n" + program;
                                mEditor.getSketch().getCode(0).setProgram(program);
                                
                                
                            }
                        }
                        else{
                            MessageDialogClickable.showErrorMessage(mEditor, "Error saving gist", evt.getCause() );
                        }
                        btnSaveUpdate.setEnabled(true);
                    }
                });
                // set some progress indicator.
                
            }
        });
        bar.add(btnSaveUpdate);
        
        
        
        bar.add(new JButton("Some more button?"));
        //bar.add(new MsgWithDetailsPanel("This is messgae", new Exception("this is exception")));
        mEditor.add(bar, BorderLayout.NORTH);
//        mEditor.setVisible(false);
        
        // none of these works.
//        mEditor.revalidate();
        mEditor.repaint();
        mEditor.validate();
        
        // none of these works.
//        mEditor.getContentPane().revalidate();
//        mEditor.getContentPane().repaint();
//        mEditor.getContentPane().validate();
        
        
        
    }

    /**
     * This class is listening to events of the "externalized" window, to know when it is
     * closed and make sure that panel is internalized. 
     */
    private static class MyWindowListener extends WindowAdapter {
        
        private final Runnable mOnWindowCloseCallback;

        @Override
        public void windowClosing(WindowEvent e) {
            mOnWindowCloseCallback.run();
            super.windowClosing(e); //To change body of generated methods, choose Tools | Templates.
        }

        
        public MyWindowListener(Runnable onWindowClose) {
            mOnWindowCloseCallback = onWindowClose;
        }
    }

    
class PopClickListener extends MouseAdapter {
    public void mousePressed(MouseEvent e){
        if (e.isPopupTrigger())
            doPop(e);
    }

    public void mouseReleased(MouseEvent e){
        if (e.isPopupTrigger())
            doPop(e);
    }

    private void doPop(MouseEvent e){
        PopUpDemo menu = new PopUpDemo();
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
}

class PopUpDemo extends JPopupMenu {
    JMenuItem anItem;
    public PopUpDemo(){
        anItem = new JMenuItem("Click Me!");
        add(anItem);
    }
}    
    private void addContextMenu(JEditTextArea textArea) {
        // TODO: implement addContextMenu
        
        //textArea.addMouseListener(new PopClickListener());
        textArea.setRightClickPopup(new PopUpDemo());
    }
    
    

    private  class MyOnModderActionListener implements IOnModderActionListener, ISketchModder.ModderActionTypes
    {

        @Override
        public void onModderAction(ISketchModder modder, ModderAction action) {
            int actType = action.getActionType();
            final AbstractTweet tweet = action.getTweet();
            String tweetText;
            switch ( actType ){
                case ACTION_CODE_EDITOR_INSERT_INTO:
                    tweetText = tweet.getText();
                    mEditor.insertText(tweetText);                    
                    break;
                case ACTION_CODE_EDITOR_REPLACE_WITH:
                    try{
                            String resStr = ToolUtils.getResourceAsString(RESOURCE_NAME_FOR_FILE_WITH_SAVEFRAME_CODE);
                            insertTab("my_save_frame.pde", resStr);
                    }
                    catch(FileNotFoundException  fnex){
                            // tell that there was error.
                            showError("There was error creating tab for the 'screenshot making' code.", fnex);
                    }
                    catch(IOException ioex){
                            // ??
                            showError("There was error creating tab for the 'screenshot making' code.", ioex);
                    }
                    // this code will help figure out what's the problem when sometimes I click on the 
                    // tweet to insert it into pde and it doesn't appear there.
                    writeLn("Setting code: \n==========================8<====================\n");
                    writeLn(action.getSourceCode());
                    writeLn("\n===================>8===========================\n\n");
                    mEditor.setText(action.getSourceCode());      // this will return formatted source code.
                                                                // as this seems to be paramter for this action.
                    break;
                case ACTION_MODDER_REQUESTS_CODE:
//                    // show modal dialog for choosing the image.
//                            // this should be run on EDT thread.
//                                Runnable onImageSelect = new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if ( ! SwingUtilities.isEventDispatchThread() ){
//                                            throw new RuntimeException("This runnble must be called on EDT");
//                                        }
//                                        mSketchModder.shareSketchCode(mEditor.getText(), tweet);
//                                        JOptionPane.showMessageDialog(mEditor, "Just sent code from PDE current window into tweetUI.");                    
//                                    }
//                                };
//                    IconDemoApp iconFrame = new IconDemoApp(mEditor.getSketch().getFolder(),
//                                                                        onImageSelect);
//                    
//                    iconFrame.setVisible(true); // ? i wonder if this one is blocking or not? // probably not.
                    showInputTweetDetailsDialog(tweet);
                    break;
                    
//                case ACTION_TERMINATE_ME:
//                    modder.shutdown(); // shuts down the services.
//                    modder.setVisible(false);
//                    mSketchModder = null; // dereference
//                    break;
                
            }
        }//onModderAction

        
        private void showError(String userErrorMsg, Exception ex) {
            MessageDialogClickable.showErrorMessage(mEditor, userErrorMsg, ex);
        }

 
    }// class MyOnModderActionListener

    /**
     * Adds new tab with given name and contents to the current project.
     * How do we handle situation when it is not possible to create file?
     * 
     * @failable in rare situations
     * 
     * @param tabFilename filename to be used for the tab in format "myfile.pde" 
     * 
     * @throws rethrows FileNotFoundException in case there was an error (basically
     *          Exception will already be logged).
     */
    private void insertTab(String tabFilename, String contents) throws FileNotFoundException
    {
        //                    tweetText = tweet.getText();
//                    mEditor.setText(tweetText);
        PrintWriter pWriter = null;
        File saveFrameFile = new File(mEditor.getSketch().getFolder(), tabFilename);
        writeLn("Saving screenshot making code file in: " + saveFrameFile.getAbsolutePath());
        try {
            pWriter = new PrintWriter(saveFrameFile);
            pWriter.append(contents); // TODO:  I wonder how come that append doesn't throw any exceptions?
            pWriter.close();
            pWriter = null;
            
            mEditor.getSketch().reload();
        } catch (FileNotFoundException ex) {
            // surprisingly this is also thrown when we can't write to the file.
            Logger.getLogger(SocialSketch.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
            // how do we handle error?
        }
        finally{
           if ( pWriter != null ){
               pWriter.close();
           }
        }
    }
    
    /**
     * Shows input dialog
     */
    private void showInputTweetDetailsDialog(final AbstractTweet atweet)
    {
        SSDialogParams ssparams = IconDemoApp.spawnEmptyParameters();
        ssparams
                .setDirectoryWithImages(mEditor.getSketch().getFolder())
                .setParentFrame(mEditor)
                .setAdvancedCallback(new AdvancedCallback() {

                    @Override
                    public void onFinishedSelection(AdvancedCallback.FinishedSelectionEvent evt) {
                            if ( evt.hasCancelled() ){
                                JOptionPane.showMessageDialog(mEditor, "You have cancelled posting of a sketch code");
                            }
                            else{
                                mSketchModder.shareSketchCode(mEditor.getText(), evt.getSelectedImageFile() , atweet);
                                JOptionPane.showMessageDialog(mEditor, "Just sent code from PDE current window into ISketchModder.");                            
                            }
                    }
                });
        JFrame iconFrame = new SimpleGridView2(ssparams);
//        JFrame iconFrame = new IconDemoApp(ssparams);
        iconFrame.setVisible(true);
    }


    
}
