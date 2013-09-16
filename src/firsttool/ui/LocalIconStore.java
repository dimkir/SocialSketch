package firsttool.ui;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

/**
 * Collection storing Icons with convenient populating and retrieving API.
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class LocalIconStore {

    private ImageIcon mDefaultIcon;
    
    private Map<String, ImageIcon> mIconMap = new HashMap<String,ImageIcon>();
    
    /**
     * Returns default ImageIcon (in swing format) for the cell.
     * 
     * @return 
     */
    public ImageIcon getDefaultIcon(){
        if ( mDefaultIcon == null){
            mDefaultIcon = loadDefaultIconFromResource();
        }
        return mDefaultIcon;
    }

    private ImageIcon loadDefaultIconFromResource() {
        // load image from resource
        URL url = getClass().getClassLoader().getResource("resources/monotone_smiley_happy.png");
        ImageIcon icon  = new ImageIcon(url);
        return icon;
    }

    
    /**
     * Returns image icon with given Url or NULL in case there's no such url.
     * @param iconUrlString
     * @return 
     */
    public ImageIcon getIconForUrl(String iconUrlString) {
        return mIconMap.get(iconUrlString); 
        
    }

    /**
     * Adds image icon with given url to the store.
     * 
     * @param iconUrlString 
     * @param imageIcon non-null icon
     * @throws NullPointerException when imageIcon is null
     */
    public void addImageIcon(String iconUrlString, ImageIcon imageIcon) {
        if ( imageIcon == null ){
            throw new NullPointerException("image icon parameter cannot be null");
        }
        mIconMap.put(iconUrlString, imageIcon);
    }
    
}
