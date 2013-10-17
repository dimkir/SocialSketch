package components;

import java.io.File;
import javax.swing.JFrame;

/**
 * SketchShareDialog Parameters.
 * 
 * The SSDialog will need to receive some parameters (like directory names,
 * and other stuff) so passing parameters via separate "sprout" class allows for
 * what I call "sprouting" - along the development process, adding extra parameters
 * without affecting constructor signatures.
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class SSDialogParams {

    
    private Runnable onImagePickComplete;
    private File mImageDir;
    private JFrame mParentFrame;
    
    
    /**
     * Visibility is package, so that only from
     * inside of package it can be instantiated.
     * 
     * This class should be instantiated by some "factory" method
     * of the IconDemoApp
     */
    
    SSDialogParams() {
    }
    
    
    public void setListenerToRunafterClickOkOnEDT(Runnable r){
        onImagePickComplete = r;
    }

    Runnable getListenerToRunafterClickOkOnEDT() {
        return onImagePickComplete;
    }

    
    public void setDirectoryWithImages(File dir){
        mImageDir = dir;
    }
    
    File getDirectoryWithImages() {
        return mImageDir;
    }

    public void setParentFrame(JFrame frame){
         mParentFrame = frame;
    }
    
    /**
     * Returns parent frame. 
     * This parent frame will be used mostly for positioning the IconDemo window neatly relative
     * to the parent window.
     * 
     * @return  null or reference to parent frame.
     */
    JFrame getParentFrame() {
        return mParentFrame;
    }
    
    
}
