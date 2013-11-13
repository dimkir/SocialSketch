package org.socialsketch.ui.dialogcallbacks;

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
    
    private AdvancedCallback mAdvancedCallback;
    
    
    /**
     * As I moved this class to separate package, I had to change visibility of 
     * the constructor to public.
     * 
     * Visibility is package, so that only from
     * inside of package it can be instantiated.
     * 
     * This class should be instantiated by some "factory" method
     * of the IconDemoApp
     */
    
    public SSDialogParams() {
    }
    
    
    public SSDialogParams setListenerToRunafterClickOkOnEDT(Runnable r){
        onImagePickComplete = r;
        return this;
    }

    Runnable getListenerToRunafterClickOkOnEDT() {
        return onImagePickComplete;
    }

    
    
    public SSDialogParams setAdvancedCallback(AdvancedCallback advCallback){
        mAdvancedCallback = advCallback;
        return this;
    }
    
    public AdvancedCallback getAdvancedCallback(){
        return mAdvancedCallback;
    }
    
    public SSDialogParams setDirectoryWithImages(File dir){
        mImageDir = dir;
        return this;
    }
    
    public File getDirectoryWithImages() {
        return mImageDir;
    }

    public SSDialogParams setParentFrame(JFrame frame){
        mParentFrame = frame;
        return this;
    }
    
    /**
     * Returns parent frame. 
     * This parent frame will be used mostly for positioning the IconDemo window neatly relative
     * to the parent window.
     * 
     * @return  null or reference to parent frame.
     */
    public JFrame getParentFrame() {
        return mParentFrame;
    }
    
    
}
