package components;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;

/**
 * Action class that shows the image specified in it's constructor.
 */
class ThumbnailAction extends AbstractAction {
    /**
     *The icon if the full image we want to display.
     */
    private Icon displayPhoto;
    private final IconDemoApp mAppFrame;

    /**
     * @param Icon - The full size photo to show in the button.
     * @param Icon - The thumbnail to show in the button.
     * @param String - The description of the icon.
     */
    public ThumbnailAction(Icon photo, Icon thumb, String desc, final IconDemoApp mAppFrame) {
        this.mAppFrame = mAppFrame;
        displayPhoto = photo;
        // The short description becomes the tooltip of a button.
        putValue(SHORT_DESCRIPTION, desc);
        // The LARGE_ICON_KEY is the key for setting the
        // icon when an Action is applied to a button.
        putValue(LARGE_ICON_KEY, thumb);
    }

    /**
     * Shows the full image in the main area and sets the application title.
     */
    public void actionPerformed(ActionEvent e) {
        mAppFrame.setCenterImage(displayPhoto);
        
        mAppFrame.setTitle("Icon Demo: " + getValue(SHORT_DESCRIPTION).toString());
        //setTitle("Icon Demo: " + getValue(SHORT_DESCRIPTION).toString());
        // ^^^ ?? should this be referencing mAppFrame or not?
    }
    
}
