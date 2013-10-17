package firsttool.ui;

import java.awt.Desktop;
import java.awt.Font;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * This implements static method to show dialog with clickable link in it.
 * 
 * And skeleton for the class is taken from here: (later parametrization and error checking is added)
 * http://stackoverflow.com/questions/8348063/clickable-links-in-joptionpane
 * 
 * Some code (launch via Desktop object) taken from here:
 * http://johnbokma.com/mexit/2008/08/19/java-open-url-default-browser.html
 * 
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class MessageDialogClickable {

    public static void main(String[] args) {
        show(null, "This is link you can click:", "http://www.google.com"); 
        show(null, "This is link you can click:", "http://www.google.com", "This is another link:" , "http://twitter.com"); 
    }
    
    /**
     * Shows message dialog with one message and one link.
     * 
     * @param parent
     * @param message
     * @param url 
     */
    public static void show(JFrame parent, String message, String url){
        show(parent, message, url, null, null); // only to show 1 message and 1 link.
    }
    
    /**
     * Shows the dialog with provided messages and links. (Up to two).

     * @param parent    CAN be NULL.
     * @param message   NOT NULL
     * @param url       CAN be NULL ( then will display no link) or URL WITH PROTOCOL! (eg. "http://www.google.com")
     * @param secondMessage  CAN BE NULL
     * @param secondUrl      CAN BE NULL. Only is used if the secondMessage is not null.
     */
    public static void show(JFrame parent, String message, String url, String secondMessage, String secondUrl)
    {
        // for copying style
        JLabel label = new JLabel();
        Font font = label.getFont();

        // create some css from the label's font
        StringBuilder style = new StringBuilder("font-family:" + font.getFamily() + ";");
        style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
        style.append("font-size:" + font.getSize() + "pt;");

        // html content
        StringBuilder html = new StringBuilder();
        html.append("<html><body style=\"" + style.toString() + "\">" );
        html.append(message + " ");
        if ( url != null ){
            html.append("<a href=\""+ url +"\">" + url + "</a>");
        }
        
        if ( secondMessage != null ){
            html.append("<p>");
            html.append(secondMessage  + " "); // space to separate link from main message
            if ( secondUrl != null ){
                html.append("<a href=\""+ secondUrl +"\">" + secondUrl + "</a>");
            }
            html.append("</p>");
        }
        
        html.append("</body></html>");

        JEditorPane ep = new JEditorPane("text/html", html.toString());

        // handle link events
        ep.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    if ( e.getURL() == null ){
                        System.out.println("e.getURL: is NULL");
                        JOptionPane.showMessageDialog(null, "The text was clicked but hyperlink seems to contain invalid url and thus is NULL");
                    }
                    else{
                        System.out.println("e.getURL: " + e.getURL());
                        launchUrl(e.getURL().toString()); // roll your own link launcher or use Desktop if J6+
                    }
                }
            }
        });
        ep.setEditable(false);
        ep.setBackground(label.getBackground());

        // show
        JOptionPane.showMessageDialog(parent, ep);
    }


    /**
     * Launches url on standard browser.
     * @param toString 
     */
    private static void launchUrl(String urlToLaunch)
    {
        try {
            System.out.println("Launching url [" + urlToLaunch + "]");
            if ( !Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported( java.awt.Desktop.Action.BROWSE) ){
                JOptionPane.showMessageDialog(null, "On this computer java cannot open automatically url in browser, you have to copy/paste it manually.");
                return;
            }
            
            Desktop desktop = Desktop.getDesktop();
            URI uri = new URI(urlToLaunch);
            
            desktop.browse(uri);
        } catch (URISyntaxException ex) {
            Logger.getLogger(MessageDialogClickable.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Url ["  + urlToLaunch + "] seems to be invalid ");
        } catch (IOException ex) {
            Logger.getLogger(MessageDialogClickable.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "There was some error opening the url. \n Details:\n" + ex.getMessage());
        }
        
        
    }
}
