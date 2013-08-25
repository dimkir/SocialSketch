package firsttool;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * This component extends things. 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class TweetCellRender extends JLabel
implements ListCellRenderer
{
    private static final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);
    public TweetCellRender() {
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
            AbstractTweet entry = (AbstractTweet) value;
            setText(index + "::" + entry.getText());
            setIcon(entry.getImageIcon());
         }
         else{
             // probably no icon and just test
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
    
}
