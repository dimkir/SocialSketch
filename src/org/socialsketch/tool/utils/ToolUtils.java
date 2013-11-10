package org.socialsketch.tool.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.socialsketch.tool.Tweet2Sketch;

/**
 *
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class ToolUtils {

       /**
         * Returns string with code of "my_save_frame.pde" file (text of file which)
         * Fetches contents of file resource at location "/resources/my_save_frame.pde"
         * as string.
         * 
         * @return resource in case everything is ok. Or crash if resource is not available?
         */
        public static String getResourceAsString(String resourceName) throws IOException {
            // TODO: implement getMySaveFrameSource
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();
            try {
                InputStream is = ToolUtils.class.getResourceAsStream(resourceName);
                if ( is != null) { 
                    System.out.println("Received NON null input stream.");
                }
                else{
                    System.out.println("Received null input stream of resource");
                }
                InputStreamReader isr = new InputStreamReader(is);
                br = new BufferedReader(isr);
                
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
//            } catch (IOException ex) {
//                Logger.getLogger(Tweet2Sketch.class.getName()).log(Level.SEVERE, null, ex);
//                throw ex;
            } finally {
                try {
                    if ( br != null) { 
                        br.close();
                    }
                    else{
                        System.out.println("Br is null, nothing to close");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Tweet2Sketch.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            return sb.toString();

        }            
}
