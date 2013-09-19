package firsttool.codeposter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.egit.github.core.Authorization;
import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.service.GistService;
import org.eclipse.egit.github.core.service.OAuthService;

/**
 * Provides API to post code online. 
 * 
 * Now it probably will be dummy, but in future I was thinking to 
 * post code to pastebin.
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class CodePoster {
    private final GistService mGistService;
    private boolean C_IS_GIST_PUBLIC  = true; // makes all gists be visible on my gist-profile.
                                            // otherwise they're only visible for people with link
    
    
    /**
     * Initializes Code Poster.
     * 
     * Incuim: connects to GITHUB and attempts to authorize user.
     * @throws IOException 
     */
    public CodePoster() throws IOException{
		OAuthService oauthService = new OAuthService();

		// Replace with actual login and password
		oauthService.getClient().setCredentials("dimkir", "lifebook777");

		// Create authorization with 'gist' scope only
		Authorization auth = new Authorization();
		auth.setScopes(Arrays.asList("gist"));
		auth = oauthService.createAuthorization(auth);

		// Create Gist service configured with OAuth2 token
		mGistService = new GistService();
		mGistService.getClient().setOAuth2Token(auth.getToken());

     
    }
    
    
    /**
     * This method attempts to post gist and returns gist URI if successful.
     * 
     * @param contents
     * @param name
     * @return
     * @throws IOException 
     */
    private String postGist(String contents, String name) throws IOException{
		// Create Gist
		Gist gist = new Gist();
		gist.setPublic(C_IS_GIST_PUBLIC);
                
		gist.setDescription("Created using Sketch2Tweet tool");
		GistFile file = new GistFile();
		file.setContent(contents);
		file.setFilename(name);
                
		gist.setFiles(Collections.singletonMap(file.getFilename(), file));
		gist = mGistService.createGist(gist);
                mGistService.createComment(gist.getId(), 
                   "This is comment which can show image of the sketch ![imge](http://s13.postimg.org/6snibyalj/screenshot_155.png)");
                
		System.out.println("Created Gist at " + gist.getHtmlUrl());              
                return gist.getHtmlUrl();
    }
           

    /**
     * Posts code to some "pasting service" and returns url.
     * 
     * Non blocking call.
     * 
     * @param text
     * @param code to be executed on the same thread as it was called... but how to achieve it???
     */
    public void postCode(final String text, final ICodePostComplete iCodePostComplete) {
        //throw new UnsupportedOperationException("Not yet implemented");
        // ?? can I use timer?
        // TODO: this shouldn't be called immediately, but after some time.
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    String gistUrl = postGist(text, "Sketch code");
                    CodePostCompleteEvent completeEvent = new CodePostCompleteEvent(gistUrl, true /* success flag */);
                    iCodePostComplete.onCodePostComplete(completeEvent);
                } catch (IOException ex) {
                    Logger.getLogger(CodePoster.class.getName()).log(Level.SEVERE, null, ex);
                    iCodePostComplete.onCodePostComplete(new CodePostCompleteEvent("Error message", false /* success flag */));
                }
                //throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        thread.start();
        
    }
    
}
