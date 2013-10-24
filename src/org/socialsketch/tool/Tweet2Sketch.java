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
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 */
package org.socialsketch.tool;

import org.socialsketch.ui.sharedialog.AdvancedCallback;
import org.socialsketch.ui.sharedialog.IconDemoApp;
import org.socialsketch.ui.sharedialog.SSDialogParams;
import org.socialsketch.tool.ISketchModder.IOnModderActionListener;
import org.socialsketch.tool.tweetqueue.AbstractTweet;
import org.socialsketch.tool.ISketchModder.ModderAction;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
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

public class Tweet2Sketch  
implements Tool
{
    /**
     * This is resource path for the text file, which contains
     * pde-source code which implements saving of the sketch "screenshots" upon keyrelease
     */
    private  static final String RESOURCE_NAME_FOR_FILE_WITH_SAVEFRAME_CODE = "/resources/my_save_frame.pde";
    
    private Editor mEditor;

    private boolean mInitWasCalledAlredy = false;
    private ISketchModder mSketchModder;
    private Sketch sketch;
    
    @Override
    public void init(Editor editor) {
        mEditor = editor;
        
//        writeLn("init method called from our first tool! Init was called already flag: " + mInitWasCalledAlredy);
        mInitWasCalledAlredy = true;
    }

    
    /**
     * This happens when tool is selected from the menu.
     * First and second and third times. All the time.
     */
    @Override
    public void run() {
        
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
    


    @Override
    public String getMenuTitle() {
        return "Tweet-A-Sketch";
    }
    
    
    
    
    private void writeLn(String s){
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
    private void addButtonBar(Editor mEditor) {
        // TODO: implement addButtonBar
        JToolBar bar = new JToolBar();
        
        bar.add(new JButton("Hello button"));
        bar.add(new JButton("Hello button2"));
        bar.add(new JButton("Hello button3"));
        mEditor.add(bar, BorderLayout.NORTH);
        
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
//                    tweetText = tweet.getText();
//                    mEditor.setText(tweetText);                    
                    
                    File saveFrameFile = new File(mEditor.getSketch().getFolder(), "my_save_frame.pde");
                    System.out.println("Saving screenshot making code file in: " + saveFrameFile.getAbsolutePath());
                    try {
                        PrintWriter pWriter = new PrintWriter(saveFrameFile);
                        pWriter.append(getResourceAsString(RESOURCE_NAME_FOR_FILE_WITH_SAVEFRAME_CODE));
                        pWriter.close();
                        
                        mEditor.getSketch().reload();
                    } catch (IOException ex) {
                        Logger.getLogger(Tweet2Sketch.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(mEditor, ex.getMessage());
                    }
                    // this code will help figure out what's the problem when sometimes I click on the 
                    // tweet to insert it into pde and it doesn't appear there.
                    System.out.println("Setting code: \n==========================8<====================\n");
                    System.out.println(action.getSourceCode());
                    System.out.println("\n===================>8===========================\n\n");
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
                
            }
        }//onModderAction

 
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
        IconDemoApp iconFrame = new IconDemoApp(ssparams);
        iconFrame.setVisible(true);
    }


       /**
         * Returns string with code of "my_save_frame.pde" file (text of file which)
         * Fetches contents of file resource at location "/resources/my_save_frame.pde"
         * as string.
         * 
         * @return resource in case everyting is ok. Or crash if resource is not available?
         */
        private String getResourceAsString(String resourceName) throws IOException {
            // TODO: implement getMySaveFrameSource
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();
            try {
                InputStream is = this.getClass().getResourceAsStream(resourceName);
                if ( is != null) { 
                    System.out.println("Received NON null input stream.");
                }
                else{
                    System.out.println("Received null input stream of resource");
                }
                InputStreamReader isr = new InputStreamReader(is);
                br = new BufferedReader(isr);
                
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
//            } catch (IOException ex) {
//                Logger.getLogger(Tweet2Sketch.class.getName()).log(Level.SEVERE, null, ex);
//                throw ex;
            } finally {
                try {
                    if ( br != null) { 
                        br.close();
                    }
                    else{
                        System.out.println("Br is null, nothing to close");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Tweet2Sketch.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            return sb.toString();

        }        
    
}
