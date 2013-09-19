package firsttool;

import components.IconDemoApp;
import firsttool.ISketchModder.IOnModderActionListener;
import firsttool.tweetqueue.AbstractTweet;
import firsttool.ISketchModder.ModderAction;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import processing.app.Base;
import processing.app.Editor;
import processing.app.Sketch;
import processing.app.tools.Tool;

/**
 * Tweet to sketch tool.
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 * 
 * Distributed under dual license:
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
public class Tweet2Sketch  
implements Tool
{


    
    
    Editor mEditor;

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
        sketch = mEditor.getSketch();
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
                    
                    mEditor.setText(action.getSourceCode());      // this will return formatted source code.
                                                                 // as this seems to be paramter for this action.
                    File saveFrameFile = new File(mEditor.getSketch().getFolder(), "my_save_frame.pde");
                    try {
                        Base.saveFile(action.getSourceCode(), saveFrameFile);
                        mEditor.getSketch().addFile(saveFrameFile);
                    } catch (IOException ex) {
                        Logger.getLogger(Tweet2Sketch.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(mEditor, ex.getMessage());
                    }
                    break;
                case ACTION_MODDER_REQUESTS_CODE:
                    // show modal dialog for choosing the image.
                            // this should be run on EDT thread.
                                Runnable onImageSelect = new Runnable() {
                                    @Override
                                    public void run() {
                                        if ( ! SwingUtilities.isEventDispatchThread() ){
                                            throw new RuntimeException("This runnble must be called on EDT");
                                        }
                                        mSketchModder.sendSketchCode(mEditor.getText(), tweet);
                                        JOptionPane.showMessageDialog(mEditor, "Just sent code from PDE current window into tweetUI.");                    
                                    }
                                };
                    IconDemoApp iconFrame = new IconDemoApp(mEditor.getSketch().getFolder(),
                                                                        onImageSelect);
                    
                    iconFrame.setVisible(true); // ? i wonder if this one is blocking or not? // probably not.
                    break;
                
            }
        }
    }

        
    
}
