package firsttool.ui;

import firsttool.tweetqueue.AbstractTweet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * This is implementation of "list element" or as in Android they say "list item"
 * which can download icons by itself and set them.
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */

public class TweetCellRender extends JLabel
implements ListCellRenderer
{
    private static final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);
    private final LocalIconStore mIconStore;
    
    // TODO: this doesn't really have to be here, as into getListCellRendererComponent() 
    // source JList will be passed.
   // private final JList mParentList;

    /**
     * After downloading icon, it places it into icon store, to not download it repeatedly.
     * 
     * @param iconStore just container, where icons will be stored after download. 
     *        So that they don't download.
     *                  
     * @param list ?? what is this parameter?. I guess it is not used, as we will be able to receive
     *          the source JList anyways as parameter.
     */
    public TweetCellRender(LocalIconStore iconStore) {
     //   mParentList = list;
        mIconStore = iconStore;
        setOpaque(true);
        setIconTextGap(12);        
    }

    
    /**
     * This should return the right rendering of the component.
     */
    @Override
    public Component getListCellRendererComponent(JList list, 
                                                                  Object value, 
                                                                            int index, 
                                                                   boolean isSelected, 
                                                                 boolean cellHasFocus)
    {
         System.out.println(System.currentTimeMillis()+ ":: getListCellRendererComponent() is called with index: " + index);
         if ( value instanceof AbstractTweet ){
             
            // CELL CONTAINS ABSTRACT TWEET OBJECT
            final AbstractTweet entry = (AbstractTweet) value;
            
            // set text of the label
            setText("<HTML>" + index + "::" + entry.getText() + "</HTML>");

            // try to fetch an icon
            ImageIcon iicon =  getIconFromIconStoreIfStoreIsAvailable(entry.getIconUrlString());
            
            if ( iicon != null) { // we have icon available for this tweet already.
                setIcon(iicon);
            }
            else{
                // trigger download of the icon.
//                startIconDownload(entry.getIconUrlString(), mIconStore, this, mParentList);
                startIconDownload(entry.getIconUrlString(), mIconStore, this, list);
                
                // set default icon from iconStore
                setIcon(getDefaultIconFromIconStoreIfStoreIsAvailable());
            }
         }
         else{
             // probably no THIS LIST CELL DOESN'T CONTAIN "AbtractTweet" object,
             // so we probably just need to render text, without icons.
             setText("List element is not instance of AbstractTweet, thus rendring it as text:" + value.toString());
         }
         

         if (isSelected) {
           setBackground(HIGHLIGHT_COLOR);
           setForeground(Color.white);
         } else {
           setBackground(Color.white);
           setForeground(Color.black);
         }        
        return this;
    }

    /**
     * Starts icon download on the background and then
     * will return value to the swing EDT thread.
     * 
     * @param iconUrlString 
     */
    private void startIconDownload(final String iconUrlString, final LocalIconStore iconStore, final JLabel cell, final JList parentList) {

        final SwingWorker worker = new SwingWorker<ImageIcon, Void>() {

            @Override
            protected ImageIcon doInBackground() throws InterruptedException, MalformedURLException, IOException  {
                
                System.out.println("Creating url...");
                URL url = new URL(iconUrlString);
                System.out.println("Reading image into image...");
                Image img = ImageIO.read(url);
                
                System.out.println("Reading image done.");
                
                
                ImageIcon icon = new ImageIcon(img); 
                System.out.println("Creating icon done.");
                
                
//                ImageIcon icon = new ImageIcon(iconUrlString);
                
//                while ( icon.getImageLoadStatus() == MediaTracker.LOADING ){
//                    Thread.sleep(100);
//                }
                return icon;
                
//                if ( icon.getImageLoadStatus() == MediaTracker.COMPLETE ){
//                    return icon;
//                }
//                else{
//                    // error loading
//                    System.out.println("Error loading icon from: [" + iconUrlString + "]");
//                    return null;
//                }
//                        
            }

            @Override
            protected void done() {
                try {
                    ImageIcon imageIcon = get();
                    // I add the 
                    String msg = String.format("TweetCellRender: after background download of the Icon at url [%s], the icon status is : %s",
                                iconUrlString, ( imageIcon == null ? "invalid(null)" : "valid(successdowloading)"));
                    System.out.println(msg);
                    // TODO: looksl like this can be NULL, in case download of the icon 
                    //       crashed with an exception
                    
                    addToIconStore(iconUrlString, imageIcon);
                    
                    // TODO: need to check if this cell still holds the tweet which I was
                    //         downloading the picture for.
    //                    if (THIS_TWEET_IS_STILL_IN_THE_LIST) {
                    
                    
                    // **********************************************
                    // **********************************************
                    // **********************************************
                    // this was a bug, as this statement doesn't seem to produce any effect on the
                    // cell. In case there would be no LocalIconStore specified, it will
                    // just enter infinite loop "download icon, trigger revalidate, trigger getListCellRendererComponent() -> downloadicon
                    // looks like this one doesn't have any effect.
//                            cell.setIcon(imageIcon);
                    
                    
                            //cell.invalidate();
                            forceJListToRevalidate(parentList);
    //                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(TweetCellRender.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(TweetCellRender.class.getName()).log(Level.SEVERE, null, ex);
                }
            }



        };
        worker.execute();
    }
    
    /**
     * Just a dirty hack to "revalidate" the JList.
     *
     * Look like simply calling to JList.invalidate() doesn't really do the job.
     */
    private static void forceJListToRevalidate(JList parentList) {
        parentList.invalidate();
        parentList.setFixedCellHeight(0);
        parentList.setFixedCellWidth(0);
        parentList.setFixedCellHeight(-1);
        parentList.setFixedCellWidth(-1);
    }
    
    /**
     * Helper which adds icon to icon store if the icon store is available. 
     * If icon store is null, then prints warning.
     * @param iconUrlString
     * @param imageIcon 
     */
    private void addToIconStore(String iconUrlString, ImageIcon imageIcon) {
        if ( mIconStore == null ){
            String msg = String.format("TweetCellRender: Warning: Cannot add icon [%s] to iconstore, as iconstore is NULL", iconUrlString);
        }
        else{
            mIconStore.addImageIcon(iconUrlString, imageIcon);
        }
    }

    
    
    /**
     * Example of how this renderer may be used.
     * @param args 
     */
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                 createAndShowGui();
            }

        });
    }
    
    private static void createAndShowGui() {
        // cell. In case there would be no LocalIconStore specified, it will
        // just enter infinite loop "download icon, trigger revalidate, trigger getListCellRendererComponent() -> downloadicon
                                                                           // this should have been the thing.
        TweetCellRenderFrameTest frame = new TweetCellRenderFrameTest(new TweetCellRender(new LocalIconStore()));
        frame.setVisible(true);
    }

    /**
     * Helper/gate: returns icon from icon store, if icon store instance is available.
     * If icon store is not available, then return null.
     * @param iconUrlString
     * @return NULL in case icon store is not available, or this icon is not available inside of the icon store.
     *          Icon reference in case this icon was available in the icon store.
     */
    private ImageIcon getIconFromIconStoreIfStoreIsAvailable(String iconUrlString) {
        if ( mIconStore != null ){
            return mIconStore.getIconForUrl(iconUrlString);        
        }
        else{
            return null;
        }
           
    }

    /**
     * Returns default icon from the store, if the store is available.
     * @return NULL if no icon store is available.
     */
    private Icon getDefaultIconFromIconStoreIfStoreIsAvailable() {
        if ( mIconStore == null ){
            return null;
        }
        else{
             return mIconStore.getDefaultIcon();
        }
    }

    
}
