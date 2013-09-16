package firsttool.codeposter;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Provides API to post code online. 
 * 
 * Now it probably will be dummy, but in future I was thinking to 
 * post code to pastebin.
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class CodePoster {

    /**
     * Posts code to some "pasting service" and returns url.
     * 
     * Non blocking call.
     * 
     * @param text
     * @param code to be executed on the same thread as it was called... but how to achieve it???
     */
    public void postCode(String text, final ICodePostComplete iCodePostComplete) {
        //throw new UnsupportedOperationException("Not yet implemented");
        // ?? can I use timer?
        // TODO: this shouldn't be called immediately, but after some time.
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                iCodePostComplete.onCodePostComplete(new CodePostCompleteEvent());
                //throw new UnsupportedOperationException("Not supported yet.");
            }
        }, 
                1000);
        
    }
    
}
