package firsttool;

import firsttool.EasyFrame.IOnTweetPickListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
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
public class FirstTool  
implements Tool, ActionListener, IOnTweetPickListener
{
    private final static int C_CHECK_DELAY = 2000; // 2 seconds
    Editor mEditor;
    private Timer timer;

    private boolean mInitWasCalledAlredy = false;
    private boolean mRunWasCalledAlredy = false;
    private Sketch sketch;
    private EasyFrame mEasyFrame;
    
    @Override
    public void init(Editor editor) {
        mEditor = editor;
        
        writeLn("init method called from our first tool! Init was called already flag: " + mInitWasCalledAlredy);
        mInitWasCalledAlredy = true;
        
        
    }

    
    /**
     * This happens when tool is selected from the menu.
     * First and second and third times. All the time.
     */
    @Override
    public void run() {
        writeLn("Now executing run() method. Has run() been called already?: " + mRunWasCalledAlredy);
        printCharacterCount();
        sketch = mEditor.getSketch();

        mRunWasCalledAlredy = true;
        
        if ( mEasyFrame == null) {
            mEasyFrame = new EasyFrame();
            mEasyFrame.setOnTweetPickListener(this);
        }
//        Base base = mEditor.getBase();
        mEasyFrame.setVisible(true); // hopefull this one is not blocking.
    }
    

    /**
     * This happens when someone selects a tweet.
     * @param tweet
     * @param easyFrame 
     */
    @Override
    public void onTweetPickListener(AbstractTweet tweet, EasyFrame easyFrame) {
        String tweetText = tweet.getText();
        mEditor.insertText(tweetText);
    }    

    @Override
    public String getMenuTitle() {
        return "Tweet to sketch tool";
    }
    
    
    
    
    private void writeLn(String s){
        System.out.println(s);
    }

    private void printCharacterCount() {
        writeLn("Lenght of text is: " + mEditor.getText().length());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        printCharacterCount();
    }
    
    //======================================================================
    //======================================================================
    //=========================== static methods ===========================
    //======================================================================
    //======================================================================
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        // some testing? or actually we can't run it standalone??
    }    
}
