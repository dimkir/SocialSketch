package org.socialsketch.codeposter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.Authorization;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.service.GistService;
import org.eclipse.egit.github.core.service.OAuthService;

/**
 * Provides API to post code online. 
 * 
 * This class implements posting of the code as gists on github.
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class CodePoster {

    private static final Logger logger = org.apache.log4j.Logger.getLogger(CodePoster.class);
    
    
    /**
     * This is reference to gist service objects which does all the heavy lifting.
     * It is initialized by constructor and valid throughout the lifespan of the CodePoster object.
     * 
     * TODO: what if authentication session expires? how do we fix that?
     */
    private final GistService mGistService;
    private boolean C_IS_GIST_PUBLIC  = true; // makes all gists be visible on my gist-profile.
                                            // otherwise they're only visible for people with link
    
    
    /**
     * Initializes Code Poster. 
     * Connects to github and creates authorized session. 
     * Throws IOException if there was error connecting or authorizing.
     * 
     * Incuim: connects to GITHUB and attempts to authorize user.
     * @throws IOException in case authorization fails or smth.
     */
    public CodePoster(String userName, String password) throws IOException{
		OAuthService oauthService = new OAuthService();

		// Replace with actual login and password
		oauthService.getClient().setCredentials(userName, password);

		// Create authorization with 'gist' scope only
		Authorization auth = new Authorization();
		auth.setScopes(Arrays.asList("gist"));
		auth = oauthService.createAuthorization(auth);

		// Create Gist service configured with OAuth2 token
		mGistService = new GistService();
		mGistService.getClient().setOAuth2Token(auth.getToken());

     
    }
    
    /**
     * Initializes code poster with credentials taken from the Environment.
     * See {@link Credentials#initFromEnvironment() } for details.
     * @throws IOException 
     */
    public CodePoster() throws IOException{
        this(CodeUtils.spawnBlankCredentials().initFromEnvironment());
    }
    
    
    public CodePoster(Credentials credentials) throws IOException{
        this(credentials.getUserName(), credentials.getPassword());
    }
    
    
    /**
     * This method attempts to post gist and returns gist object if successful.
     * 
     * @param contents
     * @param fileName filename of the main gist file
     * @param gistId NULL or gist id. If NULL, then new gist will be created.
     * @return
     * @throws IOException ?probably if there was error creating gist? like authentication expiration
     *                      or network error? or "gist too big" or smth like that. But it's a guess at the mo.
     */
    private Gist postGistBlocking(String contents, String fileName, String gistId) throws IOException{
		// Create Gist
		Gist gist = new Gist();
                if ( gistId != null ){
                    gist.setId(gistId);
                }
		gist.setPublic(C_IS_GIST_PUBLIC);
                
		gist.setDescription("Created using Sketch2Tweet tool");
		GistFile file = new GistFile();
		file.setContent(contents);
		file.setFilename(fileName);
                
		gist.setFiles(Collections.singletonMap(file.getFilename(), file));
                
                if ( gistId != null){
                    gist = mGistService.updateGist(gist);
                    
                }
                else{
                    gist = mGistService.createGist(gist);
                }
               

//               // we won't really be commenting it here anymore, as at the moment of posting code,
//               // we don't have reference to the screenshot image URL yet.                  
//                mGistService.createComment(gist.getId(), 
//                   "This is comment which can show image of the sketch ![imge](http://s13.postimg.org/6snibyalj/screenshot_155.png)");
                
		System.out.println("Created Gist at " + gist.getHtmlUrl());              
                //return gist.getHtmlUrl();
                return gist;
    }
           

    /**
     * Posts code to some "pasting service" and returns url.
     * 
     * Non blocking call.
     * 
     * @param text
     * @param iCodePostComplete
     */
    public void postCode(final String text, final ICodePostComplete iCodePostComplete) {
        String gistIdNull = null;
        postCode(text, iCodePostComplete, gistIdNull);
        
    }
    
    /**
     * This is "updating" gist with the given code.
     * 
     * @param text new text of the gist.
     * @param iCodePostComplete callback 
     * @param gistId NULL or valid id. When NULL supplied, new gist will be created. Otherwise existing gist will be updated.
     */
    public void postCode(final String text, final ICodePostComplete iCodePostComplete, final String gistId){
        // TODO: this shouldn't be called immediately, but after some time.
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    Gist gist = postGistBlocking(text, "sketch_code.pde", gistId);
                    CodePostCompleteEvent completeEvent = new CodePostCompleteEvent(gist.getHtmlUrl(), true /* success flag */);
                                          completeEvent.setGistId(gist.getId());
                    
                    iCodePostComplete.onCodePostComplete(completeEvent);
                } catch (IOException ex) {
                    logger.error("Error posting code.", ex);
//                    Logger.getLogger(CodePoster.class.getName()).log(Level.SEVERE, null, ex);
                    iCodePostComplete.onCodePostComplete(new CodePostCompleteEvent("Error message", false /* success flag */));
                }
                //throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        thread.start();        
    }
    
    /**
     * Posts gist comment for the code in a separate thread.
     * And upon completion (both successful and unsuccessful) triggers callback (from that worker thread).
     */
    public void postComment(final String commentText, final String gistToCommentId, final ICodePostComplete iCodePostComplete) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                CodePostCompleteEvent codePostCompleteEvent;
                try {
                    Comment comment = postGistCommentBlocking(commentText, gistToCommentId);
                    boolean successFlag = true;
                    String msg = String.format("Successfully posted comment for gist %s. Comment available at: %s",  gistToCommentId, comment.getUrl());
                    codePostCompleteEvent = new CodePostCompleteEvent(msg, successFlag);
                    iCodePostComplete.onCodePostComplete(codePostCompleteEvent);

                } // run
                catch (IOException ex) {
                    // FAILURE POSTING THE COMMENT
                    boolean successFlag = false;
                    logger.error("Error posting comment", ex);
//                    Logger.getLogger(CodePoster.class.getName()).log(Level.SEVERE, null, ex);
                    String msg = String.format("There was error posting comment for the gist %s. Exception message: %s", 
                                                            gistToCommentId,
                                                            ex.getMessage());
                    codePostCompleteEvent = new CodePostCompleteEvent( msg, successFlag);
                    iCodePostComplete.onCodePostComplete(codePostCompleteEvent);
                }
            }// run
        };

        thread.start();
    }
    
    /**
     * Utility method: just posts comment for given gist. Returns it on success or throws IOException error.
     * @param commentText
     * @param gistToCommentId 
     * @throws IOException probably if there was some error with the thing.
     * @return on success: comment object.
     */
    private Comment postGistCommentBlocking(String commentText, String gistToCommentId) throws IOException {
        Comment rez = mGistService.createComment(
                gistToCommentId, 
                commentText
//                "This is comment which can show image of the sketch ![imge](http://s13.postimg.org/6snibyalj/screenshot_155.png)"
                );
        return rez;
    
    }
    

    /**
     * This is entry point for quick testing and displaying how CodePoster class works.
     * 
     * Basically this method shows how to post code and post comments for code.
     * 
     * @param args 
     */
    public static void main(String[] args) {
        try {
            // TODO: need to implement testing code.
            final CodePoster copo = new CodePoster(CodeUtils.spawnBlankCredentials().initFromEnvironment()); // throws IOException 
            copo.postCode("This is my code to post lalalala", new ICodePostComplete() {

                @Override
                public void onCodePostComplete(CodePostCompleteEvent evt) {
                    copo.postCode("this is new code to\n post", new ICodePostComplete() {

                        @Override
                        public void onCodePostComplete(CodePostCompleteEvent evt) {
                                        if ( evt.isSuccessful() ){
                                            copo.postComment("This is comment for the gist", evt.getGistId(), new ICodePostComplete() {

                                                @Override
                                                public void onCodePostComplete(CodePostCompleteEvent evt) {
                                                    if ( evt.isSuccessful() ){
                                                        // succesful
                                                        System.out.println("Succesfully posted comment for the gist: " + evt.toString());
                                                    }
                                                    else{
                                                        // unsuccessful
                                                        System.out.println("Failure posting comment for the gist: " + evt.toString());
                                                    }
                                                }
                                            });
                                        }
                                        else{
                                            System.out.println("Code posting wasn't successful: " + evt.toString());
                                        }                            
                        }
                    }, evt.getGistId());
                }
            });
            
            
        } catch (IOException ex) {
            System.out.println("There was error probably creating CodePost object(?authrorization)");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        
    }

    /**
     * Initiates saving from adapter, this is asynchronous.
     * 
     * 
     * 
     * @param adapter  NOT null.
     * @param callback (May Be null, in case we don't care to return success status).
     *                  is called from worker thread, so ensure proper wrapping if receiving it
     *                  in Swing.
     * 
     */
    public void saveUpdateFromAdapter(final ICodePostAdapter adapter, final ICodePostComplete callback) {
        if (adapter == null ){
            throw new IllegalArgumentException("Adapter parameter cannot be null");
        }
        
        new Thread(){

            @Override
            public void run() {
                
                try {
                    Gist gist = saveUpdateFromAdapterBlocking(adapter, callback);
                    CodePostCompleteEvent evt = new CodePostCompleteEvent(gist.getHtmlUrl(), true );
                    evt.setGistId(gist.getId());
                    
                    doCallback(evt);
                } catch (IOException ex) {
                    logger.error("Error posting or updating gist from the adapter", ex);
//                    Logger.getLogger(CodePoster.class.getName()).log(Level.SEVERE, null, ex);
                    
                    CodePostCompleteEvent evt = new CodePostCompleteEvent("Error message", false);
                    evt.setCause(ex);
                    doCallback(evt);
                }
            }
            
            void doCallback(CodePostCompleteEvent codePostCompleteEvent){
                if ( callback != null ){
                    callback.onCodePostComplete(codePostCompleteEvent);
                }
                else{
                    logger.debug("Callback isn't registered.", new Exception("This is just dummy exception to show the stack trace."));
                }
            }

            
        }.start();
    }
    

    /**
     * This is method
     * 
     * @param adapter
     * @param callback 
     */
    private Gist saveUpdateFromAdapterBlocking(ICodePostAdapter adapter, ICodePostComplete callback) throws IOException 
    {
		Gist gist = new Gist();
                if ( adapter.getPreviousPostId() != null ){
                    gist.setId(adapter.getPreviousPostId());
                }
		gist.setPublic(C_IS_GIST_PUBLIC);
                
                if ( adapter.getDescription() != null ){
                    gist.setDescription(adapter.getDescription());
                }
                else{
                    gist.setDescription("Created using code poster component");
                }
                
                
                Map<String, GistFile> fileMap = prepareFilesMap(adapter);
                
//		gist.setFiles(Collections.singletonMap(file.getFilename(), file));
		gist.setFiles(fileMap);
                
                CodeUtils.fileListToConsole(gist);
                
                
                
                if ( adapter.getPreviousPostId() != null){
                    gist = mGistService.updateGist(gist);
                }
                else{
                    gist = mGistService.createGist(gist);
                }
               

//               // we won't really be commenting it here anymore, as at the moment of posting code,
//               // we don't have reference to the screenshot image URL yet.                  
//                mGistService.createComment(gist.getId(), 
//                   "This is comment which can show image of the sketch ![imge](http://s13.postimg.org/6snibyalj/screenshot_155.png)");
                
		System.out.println("Created Gist at " + gist.getHtmlUrl());              
                //return gist.getHtmlUrl();
                return gist;        
    }

    //TODO: add valid values.
    
    /**
     * Prepares map of filename to gist file.
     * 
     * @param adapter ?? what are valid values ?
     * @return 
     */
    private Map<String, GistFile> prepareFilesMap(ICodePostAdapter adapter) {
        System.out.println("Called perpareFilesMap()");
        Map<String, GistFile> map = new HashMap<String, GistFile>();
        for(int i = 0 ; i < adapter.getFileCount() ; i++){
            String name = adapter.getFileNameByIndex(i);
            GistFile gf = new GistFile();
            gf.setFilename(name);
            gf.setContent(adapter.getFileContentsByIndex(i));
            map.put(name, gf);
            System.out.println("Adding element: " + name );
        }
        
        return map;
    }
//    
//    		GistFile file = new GistFile();
//		file.setContent(contents);
//		file.setFilename(fileName);

    
}
