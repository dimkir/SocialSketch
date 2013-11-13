package org.socialsketch.ui.sharedialog;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 * Bunch of "utility" static methods to help with graphics-related operations.
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class GraphicsUtils {
    /**
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     * @param srcImg - source image to scale
     * @param w - desired width
     * @param h - desired height
     * @return - the new resized image
     */
    public static Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
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
            Logger.getLogger(ImageScanAndWrapSWorker.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
