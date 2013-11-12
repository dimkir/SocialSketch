package org.socialsketch.tool.utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.log4j.Logger;

/**
 *
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class ToolUtils {

         private static final Logger logger = org.apache.log4j.Logger.getLogger(ToolUtils.class);
                
       /**
         * Returns string with code of "my_save_frame.pde" file (text of file which)
         * Fetches contents of file resource at location "/resources/my_save_frame.pde"
         * as string.
         * 
         * @param resourceName name of the resource to try to load.
         * @throws java.io.IOException in case there was no resource found or some error.
         * @return resource in case everything is ok. Or crash if resource is not available?
         */
        public static String getResourceAsString(String resourceName) throws IOException {
            InputStream is = null;
            InputStreamReader isr = null;
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();
            try {
                is = ToolUtils.class.getResourceAsStream(resourceName);
                if ( is != null) { 
                    logger.info("Received NON null (VALID) input stream for resource [" + resourceName + "]");
                }
                else{
                    logger.info("Received NULL input stream for resource [" + resourceName + "]");
                }
                
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);
                
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
//            } catch (IOException ex) {
//                Logger.getLogger(SocialSketch.class.getName()).log(Level.SEVERE, null, ex);
//                throw ex;
                return sb.toString();
                
            } finally {
                tryclose(br, BufferedReader.class.getSimpleName());
                tryclose(isr, InputStreamReader.class.getSimpleName());
                tryclose(is, InputStream.class.getSimpleName());
            }
        }            

        /**
         * Just helper to try to close resource.
         * 
         * @param closable
         * @param alias 
         */
        private static void tryclose(Closeable closable, String alias) {
            if ( closable == null ){
                String msg = String.format("There was no %s to  close.", alias);
                logger.info(msg);
                return;
            }

            try{
                closable.close();
            }
            catch(IOException ioex){
                String msg = String.format("There was no error closing resource %s. Resource probably stayed open.", alias);
                logger.warn(msg);
            }
        }
        
        
        
}
