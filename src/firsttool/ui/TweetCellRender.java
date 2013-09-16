package firsttool.ui;

import firsttool.tweetqueue.AbstractTweet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * This component extends things. 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class TweetCellRender extends JLabel
implements ListCellRenderer
{
    private static final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);
    private final LocalIconStore mIconStore;
    private final JList mParentList;
    
    public TweetCellRender(LocalIconStore iconStore, JList list) {
        mParentList = list;
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
         if ( value instanceof AbstractTweet ){
             
            // CELL CONTAINS ABSTRACT TWEET OBJECT
            final AbstractTweet entry = (AbstractTweet) value;
            
            // set text of the label
            setText("<HTML>" + index + "::" + entry.getText() + "</HTML>");

            // try to fetch an icon
            ImageIcon iicon =  mIconStore.getIconForUrl(entry.getIconUrlString());
            
            if ( iicon != null) { // we have icon available for this tweet already.
                setIcon(iicon);
            }
            else{
                // trigger download of the icon.
                startIconDownload(entry.getIconUrlString(), mIconStore, this, mParentList);
                
                // set default icon from iconStore
                setIcon(mIconStore.getDefaultIcon());
            }
         }
         else{
             // probably no THIS LIST CELL DOESN'T CONTAIN "AbtractTweet" object,
             // so we probably just need to render text, without icons.
             setText(value.toString());
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
    private static void startIconDownload(final String iconUrlString, final LocalIconStore iconStore, final JLabel cell, final JList parentList) {

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
                    
                    iconStore.addImageIcon(iconUrlString, imageIcon);
                    // TODO: need to check if this cell still holds the tweet which I was
                    //         downloading the picture for.
    //                    if (THIS_TWEET_IS_STILL_IN_THE_LIST) {
                            cell.setIcon(imageIcon);
                            //cell.invalidate();
                            parentList.invalidate();
                            parentList.setFixedCellHeight(0);
                            parentList.setFixedCellWidth(0);
                            parentList.setFixedCellHeight(-1);
                            parentList.setFixedCellWidth(-1);                            
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
    
    

        
    
}
