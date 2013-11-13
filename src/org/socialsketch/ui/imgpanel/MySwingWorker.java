package org.socialsketch.ui.imgpanel;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import org.socialsketch.ui.dialogcallbacks.GraphicsUtils;
import org.socialsketch.ui.dialogcallbacks.MissingIcon;

/**
 * This worker opens the image and converts it into icon. Remember that this
 * is ONCE OFF object, which is only good for single download.
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class MySwingWorker extends SwingWorker<MySwingWorker.IconBundle, Void>
{
    
    private final File mOriginalFile;
    private final IOnIconReady mOnIconReady;
    
    /**
     * Default thumbnail width and heights.
     */
    private int mThumbnailWidth = 128;
    private int mThumbnailHeight = mThumbnailWidth;
            

    public MySwingWorker(File imageFileToLoadAsIcon, IOnIconReady callback) {
        mOriginalFile = imageFileToLoadAsIcon;
        if ( callback == null ){
            throw new NullPointerException();
        }
        mOnIconReady = callback;
    }


    
    
    /**
     * This one should actually create icon.
     * @return
     * @throws Exception 
     */
    @Override
    protected MySwingWorker.IconBundle doInBackground() throws Exception {
        System.out.println("doInBackground() for file: " + mOriginalFile.getAbsolutePath());
        ImageIcon icon = createImageIconFromFile(mOriginalFile);
        
        
        if ( icon == null ){
            System.out.println("doInBackground(): couldn't create icon. NUll resulted.");
            // TODO: change so that in future only one instance is used.
            return new IconBundle(new MissingIcon(), mOriginalFile);
        }
        else{
            System.out.println("Created icon: height: " + icon.getIconHeight());
            ImageIcon thumbnailIcon = new ImageIcon(GraphicsUtils.getScaledImage(icon.getImage(), mThumbnailWidth, mThumbnailHeight));
            return new IconBundle(thumbnailIcon, mOriginalFile);
        }
        
    }

    @Override
    protected void done() {
        try {
            IconBundle iconBundle = get();
            mOnIconReady.onIconReady(iconBundle.getIcon(), iconBundle.getOriginalFilePath());
            
        } catch (InterruptedException ex) {
            Logger.getLogger(MySwingWorker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(MySwingWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    
    /**
     * Attempts to create ImageIcon from file. Failable. Returns null on error.
     * @param f
     * @return
     * @throws MalformedURLException
     */
    public static ImageIcon createImageIconFromFile(File f) {
        try {
            URL imgURL = f.toURL();
            if (imgURL != null) {
                String descrForIcon = f.getAbsolutePath();
                return new ImageIcon(imgURL, descrForIcon);
            } else {
                System.err.println("Couldn't find file: " + f.getAbsolutePath());
                return null;
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(MySwingWorker.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    
    public interface IOnIconReady{
        void onIconReady(Icon icon, File originalFilepath);
    }
 
    
    
    public static class IconBundle 
    {
        private final Icon mIcon;
        private final File mOriginalFilepath;
        
        
        IconBundle(Icon icon, File originalFilepath){
            mIcon = icon;
            mOriginalFilepath = originalFilepath;
        }
        
        File getOriginalFilePath(){
            return mOriginalFilepath;
        }
        
        Icon getIcon(){
            return mIcon;
        }
    }
}
