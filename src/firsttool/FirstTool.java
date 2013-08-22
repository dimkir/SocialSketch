package firsttool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import processing.app.Base;
import processing.app.Editor;
import processing.app.Sketch;
import processing.app.tools.Tool;

/**
 *
 * @author Dimitry Alhambra <dimitry@languagekings.com>
 */
public class FirstTool  implements Tool, ActionListener
{

     
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
    private final static int C_CHECK_DELAY = 2000; // 2 seconds
    Editor mEditor;
    private Timer timer;

    private boolean mInitWasCalledAlredy = false;
    private boolean mRunWasCalledAlredy = false;
    private Sketch sketch;
    
    @Override
    public void init(Editor editor) {
        mEditor = editor;
        
        writeLn("init method called from our first tool! Init was called already flag: " + mInitWasCalledAlredy);
        mInitWasCalledAlredy = true;
    }

    @Override
    public void run() {
        writeLn("Now executing run() method. Has run() been called already?: " + mRunWasCalledAlredy);
        printCharacterCount();
        sketch = mEditor.getSketch();
        timer = new Timer(C_CHECK_DELAY, this);
        timer.setDelay(C_CHECK_DELAY);
        timer.start();
        
        mRunWasCalledAlredy = true;
        
//        Base base = mEditor.getBase();
    }

    @Override
    public String getMenuTitle() {
        return "This is my first tool";
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
}
