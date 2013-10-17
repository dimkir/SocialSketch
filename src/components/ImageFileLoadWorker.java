package components;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingWorker;

/**
 * This class is a implementation of separate SwingWorker thread which is tasked
 * with scanning the given directory for image files and plugging them into 
 * given IconDemoApp (JFrame) as JButtons (? with appropriate actions? which actions?)
 * With ThumbnailActions.
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class ImageFileLoadWorker extends SwingWorker<Void, ThumbnailAction> {

    private File mDir;
    private final IconDemoApp mParentApp;
    
    private static final int C_ICON_WIDTH = 128;
    private static final int C_ICON_HEIGHT = 128;
    
    private MissingIcon placeholderIcon = new MissingIcon();

    /**
     * Just initializes the IFLWorker with paraemters, BUT DOES NOT start it.
     * 
     * @param dirWithImages
     * @param parentApp 
     */
    //TODO: what if directory is non-existent? or parentApp is NULL?
    ImageFileLoadWorker(File dirWithImages, IconDemoApp parentApp) 
    {
        mDir = dirWithImages;
        mParentApp = parentApp;
    }

    
    /**
     * Creates full size and thumbnail versions of the target image files
     * on the worker thread and also plugs those images as "buttons" into the frame.
     */
    @Override
    protected Void doInBackground() throws Exception {
        // get dir listing
        File[] filesInDirectory = mDir.listFiles(new FileFilter() {

            /**
             * Only accept PNG & JPG files
             */
            public boolean accept(File pathname) {
                if ( pathname.isFile() && pathname.getPath().toLowerCase().endsWith(".jpg")){    return  true; }
                if ( pathname.isFile() && pathname.getPath().toLowerCase().endsWith(".jpeg")){    return  true; }
                if ( pathname.isFile() && pathname.getPath().toLowerCase().endsWith(".png")){    return  true; }
                return false;
            }
        });
        
        for(File f : filesInDirectory){
            ImageIcon icon = createImageIconFromFile(f);
            
            if ( icon == null ){
                System.out.println("Couldn't create icon from file: "+ f.getAbsolutePath());
                continue;
            }

            ThumbnailAction thumbAction = creatActionFromIcon(icon);

            publish(thumbAction);
            
        }
        // unfortunately we must return something, and only null is valid to
        // return when the return type is void.
        return null;
    }    
//    /**
//     * Creates full size and thumbnail versions of the target image files.
//     */
//    @Override
//    protected Void doInBackground() throws Exception {
//        for (int i = 0; i < imageCaptions.length; i++) {
//            ImageIcon icon;
//            icon = createImageIcon(imagedir + imageFileNames[i], imageCaptions[i]);
//
//            ThumbnailAction thumbAction = creatActionFromIcon(icon);
//
//
//            publish(thumbAction);
//        }
//        // unfortunately we must return something, and only null is valid to
//        // return when the return type is void.
//        return null;
//    }

    /**
     * Process all loaded images.
     */
    @Override
    protected void process(List<ThumbnailAction> chunks) {
        for (ThumbnailAction thumbAction : chunks) {
            JButton thumbButton = new JButton(thumbAction);
            // add the new button BEFORE the last glue
            // this centers the buttons in the toolbar
            mParentApp.addThumbButton(thumbButton);
            
        }
    }

    /**
     * Creates new instance of ThumbnailAction and fills it with the 
     * given image and image thumbnail.
     * 
     * @param icon
     * @return 
     */
    private ThumbnailAction creatActionFromIcon(ImageIcon icon) {
        ThumbnailAction thumbAction;
        if (icon != null) {

            ImageIcon thumbnailIcon = new ImageIcon(GraphicsUtils.getScaledImage(icon.getImage(), C_ICON_WIDTH, C_ICON_HEIGHT));

            thumbAction = new ThumbnailAction(icon, thumbnailIcon, "some caption blah blahb blah", mParentApp);

        } else {
            // the image failed to load for some reason
            // so load a placeholder instead
            thumbAction = new ThumbnailAction(placeholderIcon, placeholderIcon, "some caption... bla blah", mParentApp);
        }
        return thumbAction;
    }

    /**
     * Attempts to create ImageIcon from file. Failable. Returns null on error.
     * @param f
     * @return
     * @throws MalformedURLException 
     */
    private ImageIcon createImageIconFromFile(File f) {
        try {
//            java.net.URL imgURL =  new URL(f.getAbsolutePath());
            java.net.URL imgURL =  f.toURL();
            
            if (imgURL != null) {
                String descrForIcon = f.getAbsolutePath();
                return new ImageIcon(imgURL, descrForIcon);
                // TODO: ^^ what happens if there's an error?
            } else {
                System.err.println("Couldn't find file: " + f.getAbsolutePath());
                return null;        
            }
            
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(ImageFileLoadWorker.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
