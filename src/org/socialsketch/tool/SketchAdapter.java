package org.socialsketch.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.socialsketch.codeposter.ICodePostAdapter;
import processing.app.Sketch;
import processing.app.SketchCode;

/**
 * This is adapter to adapt sketch files to the format which SketchAdapter understands.
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class SketchAdapter  implements ICodePostAdapter
{

    /**
     * If this is not null, we will be "updating" existing one,
     * as opposed to saving it as new.
     * 
     */
    private String mPeviousGistId = null;
    
    private Map<String, String> mPrograms = new HashMap<String,String>();
    private List<String> mFileNames = new ArrayList<String>();
    
    /**
     * This is signature inside of the main code, which shows the reference to the
     * gist.
     * 
     */
    private static final String C_GIST_SIGNATURE = "//#GIST:";
    private String mDescription;
    
    /**
     * Initializes adapter with current state of the sketch.
     * 
     * @param sketch 
     */
    public SketchAdapter(Sketch sketch) {
        int cc = sketch.getCodeCount();
        
        mPeviousGistId =  parseMainProgramForId(    sketch.getMainProgram() );
        
        mDescription = "Processing sketch : " + sketch.getName();
        
        // add tabs
        for(int  i = 0 ; i < cc ; i++){
            SketchCode code = sketch.getCode(i);
            // what can we do with this code?
            String fileName = code.getFile().getName();
            String program = code.getProgram();
            mPrograms.put(fileName, program);
            mFileNames.add(fileName);
        }
        // add data folder.
        File dataFolder = sketch.getDataFolder();
        if ( dataFolder == null ){
            System.out.println("No DATA folder found.");
        }
        else{
            for(File f : dataFolder.listFiles()){
                System.out.println("File found: " + f);
            }
        }
        
        
    }

    
    
    /**
     * Defensive copy is returned. 
     * 
     * @return 
     */
    @Override
    public String[] getFileNames() {
        String[] fns = new String[mFileNames.size()];
        mFileNames.toArray(fns);
        return fns;
    }

    @Override
    public int getFileCount() {
        return mFileNames.size();
    }

    @Override
    public String getFileContentsByIndex(int idx) {
        String key = mFileNames.get(idx);
        return mPrograms.get(key);
    }

    @Override
    public String getFileContentsByName(String name) {
        return mPrograms.get(name);
    }

    @Override
    public String getPreviousPostId() {
        return mPeviousGistId;
    }

    /**
     * Parses main program for comment containing gist and then
     * returns it, when it is available! 
     * 
     * @param mainProgram
     * @return 
     */
    private static String parseMainProgramForId(String mainProgram) {
        
        int idx = mainProgram.indexOf(C_GIST_SIGNATURE);
        if ( idx == -1){
            return null; // there's no element found
        }
        
        // calcualte index following signature.
        int idxFollowingSignature = idx + C_GIST_SIGNATURE.length();
        
        StringBuilder sb = new StringBuilder();
        while (  Character.isDigit(mainProgram.charAt(idxFollowingSignature)) ){
            sb.append(mainProgram.charAt(idxFollowingSignature++));
        }
        //int endIdx = mainProgram.indexOf(' ', idxFollowingSignature);
        // or maybe we should only count numbers.
        //String extractedId = mainProgram.substring(idx + C_GIST_SIGNATURE.length(), endIdx );
        
        return sb.length() == 0 ? null : sb.toString();
    }

    @Override
    public String getDescription() {
        return mDescription;
    }

    @Override
    public String getFileNameByIndex(int i) {
        return mFileNames.get(i);
    }
    
    
}
