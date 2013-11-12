package org.socialsketch.codeposter;

import java.io.IOException;


/**
 * This is simple POJO class (evolvable) which holds credential parameters for authenticating with
 * CodePoster.
 * 
 * To get empty instance call {@link CodePoster#spawnBlankCredentials() CodePoster.spawnBlankCredentials}
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class Credentials {
    /**
     * This is declaration of the names of the environment variables, holding
     * github username and password accordingly.
     */
    public static final String ENV_GITHUB_USERNAME = "GITHUB_USERNAME";
    public static final String ENV_GITHUB_PASSWORD = "GITHUB_PASSWORD";
    
    private String mUserName;
    private String mPassword;

    
    /**
     * Can only be instantiated from the package.
     */
    Credentials(){
        
    }
    
    Credentials setUsername(String userName){
        mUserName = userName;
        return this;
    }
    
    Credentials setPassword(String password){
        mPassword = password;
        return this;
        
    }
    
    
    public String getPassword(){
        return mPassword;
    }
    
    public String getUserName(){
        return mUserName;
    }
    
    /**
     * Creates credentials from environment variables:
     * GITHUB_USERNAME
     * GITHUB_PASSWORD
     * 
     * This is surely failable, as these env variables may not be set.
     * 
     * @throws IOException in case there was no variables set.
     * @return 
     */
    public Credentials initFromEnvironment() throws IOException 
    {
        
        String userName = System.getenv(ENV_GITHUB_USERNAME);
        if ( userName == null ){
            String msg = String.format("Cannot find environment variable [%s] holding github username", ENV_GITHUB_USERNAME);
            throw new IOException(msg);
        }
        
        String password = System.getenv(ENV_GITHUB_PASSWORD);
        if ( password == null ){
            String msg = String.format("Cannot find environment variable [%s] holding github password", ENV_GITHUB_PASSWORD);
            throw new IOException(msg);            
        }
        
        setUsername(userName);
        setPassword(password);
        return this; // for chaining
    }        
}
