package org.socialsketch.ui.sharedialog;

import org.socialsketch.ui.sharedialog.ImageFileLoadWorker.ImageEnvelope;
import java.io.File;
import java.io.FileFilter;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

/**
 * This class is a implementation of separate SwingWorker thread which is tasked
 * with scanning the given directory for image files, loading them to memory,
 * creating thumbnails, wrapping them into "envelopes" and supplying those envelopes
 * to the IImageEnvelopeReceiver callback on EDT thread.
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
//TODO: single responsibility of this class should be just loading and converting images.
// no Thumbnail Action creation should be. This Thumbnail action should be the responsibility of 
// the IconDemoApp as it is intrinsic to him.
public class ImageFileLoadWorker extends SwingWorker<Void, ImageEnvelope> {

    private final File mDir;
//    private final IconDemoApp mParentApp;
    
    private final IImageEnvelopeReceiver mImageEnvelopeReceiver;
    
    private static final int C_ICON_WIDTH = 128;
    private static final int C_ICON_HEIGHT = 128;
    
    private MissingIcon placeholderIcon = new MissingIcon();

    /**
     * Just initializes the IFLWorker with parameters, BUT DOES NOT start it.
     * 
     * @param dirWithImages ?? this parameter may be failable. NOT NULL.
     *                      As directory may be on network drive or some other "slow response" storage,
     *                      we don't check it's validity/availability in constructor. 
     *                      This will be done in the thread. 
     *                      However we DO want to be sure that this parameter is not null, as this signifies
     *                      bug in code, as rather unavailable storage.
     * @param imageEnvelopeReceiver NOT NULL. Reference to the receiver of the envelopes. Will be run on EDT thread.
     * 
     * @throws IllegalArgumentException if dirWithImages is NULL or if imageEnvelopReceiver is null.
     */
    ImageFileLoadWorker(File dirWithImages, IImageEnvelopeReceiver imageEnvelopeReceiver) 
    {
        if ( imageEnvelopeReceiver == null ){
            throw new IllegalArgumentException("imageEnvelopeReceiver parameter cannot be NULL. " + 
                     "Because if null, then there's no point for the worker to work. "+ 
                    "As processed images won't be passed to anywhere");            
        }
        mImageEnvelopeReceiver = imageEnvelopeReceiver;
        
        if ( dirWithImages == null ){
            throw new IllegalArgumentException("dirWithImages parameter cannot be NULL");
        }
        
        mDir = dirWithImages;
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
            ImageIcon icon = GraphicsUtils.createImageIconFromFile(f);
            
            if ( icon == null ){
                System.out.println("Couldn't create icon from file: "+ f.getAbsolutePath());
                continue;
            }

//            ThumbnailAction thumbAction = creatActionFromIcon(icon);
//            publish(thumbAction);
            
            ImageEnvelope envelope = makeImageEnvelopeFrom(icon, f);
            publish(envelope);
        }
        // unfortunately we must return something, and only null is valid to
        // return when the return type is Void.
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

//    /**
//     * Process all loaded images.
//     * 
//     * Looks like after "publishing" all of other things, this method will be called
//     * on Swing Thread to process all the actions.
//     */
//    @Override
//    protected void process(List<ThumbnailAction> chunks) {
//        for (ThumbnailAction thumbAction : chunks) {
//            JButton thumbButton = new JButton(thumbAction);
//            // add the new button BEFORE the last glue
//            // this centers the buttons in the toolbar
//            mParentApp.addThumbButton(thumbButton);
//            
//        }
//    }
    
    /**
     * Process all loaded images.
     * 
     * Looks like after "publishing" all of other things, this method will be called
     * on Swing Thread to process all the actions.
     */
    @Override
    protected void process(List<ImageEnvelope> chunks) {
        for (ImageEnvelope imageEnvelope : chunks) {
                mImageEnvelopeReceiver.submitImageEnvelope(imageEnvelope);
        }
    }    
   
    /**
     * Helper: Creates "envelope" containing results of the threads, work
     * eg: loaded and resized images.
     * 
     * @param icon NOT NULL
     * @return 
     */
    private ImageEnvelope makeImageEnvelopeFrom(ImageIcon icon, File originImage) {
        ImageEnvelope envelope;
//        if (icon != null) {

            ImageIcon thumbnailIcon = new ImageIcon(GraphicsUtils.getScaledImage(icon.getImage(), C_ICON_WIDTH, C_ICON_HEIGHT));

            envelope = new ImageEnvelope(icon, thumbnailIcon, "come caption blah blahb blah", originImage);

//        } else {
//            // the image failed to load for some reason
//            // so load a placeholder instead
//            envelope = new ImageEnvelope(placeholderIcon, placeholderIcon, "some caption... bla blah");
//        }
        return envelope;
    }    

    /**
     * Container for the processed set of single image file. 
     * (Container for image and generated thumbnail and short string of description).
     */
    public static class ImageEnvelope {

        private final Icon mMainImage;
        private final Icon mThumbnail;
        private final String mDescription;
        private File mOriginPath;
        
        private ImageEnvelope(Icon mainImageIcon, Icon thumbnailIcon, String descr, File originPath) {
            mMainImage = mainImageIcon;
            mOriginPath = originPath;
            mThumbnail= thumbnailIcon;
            mDescription = descr;
        }
        Icon getMainImage(){
            return mMainImage;
        }

        Icon getThumbnailImage() {
            return mThumbnail;
        }

        String getDescription() {
            return mDescription;
        }

        File getOriginPath() {
            return mOriginPath;
        }
    }
}
