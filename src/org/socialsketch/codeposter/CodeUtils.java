package org.socialsketch.codeposter;

import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;

/**
 *
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class CodeUtils {

    private final static Logger logger = org.apache.log4j.Logger.getLogger(CodeUtils.class);
    
    /**
     * Debug method just outputs to console the files.
     *
     * @param gist
     */
    public static void fileListToConsole(Gist gist) {
        Map<String, GistFile> map = gist.getFiles();
        Set<String> keySet = map.keySet();
        for (String kkk : keySet) {
            logger.info("Got file record: [" + kkk + "] with value of length: " + map.get(kkk).getContent().length());
        }
    }
    
}
