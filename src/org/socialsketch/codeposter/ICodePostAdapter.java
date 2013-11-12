package org.socialsketch.codeposter;

/**
 * This is adapter interface which someone who wants to post/update via code poster
 * some code container which has multiple elements (Ie. to save directory of source code or smth).
 * 
 * <p>
 * Remember that all filenames of the elements should be unique. 
 * They are rather "aliases" for the filenames, so probably they can contain relative
 * path elements... haven't tested it though.
 * </p>
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public interface ICodePostAdapter {
   
     /**
      * Must return array of filenames.
      * 
      * @return 
      */
     String[] getFileNames(); // all names should be unique
     
     int getFileCount();
     
     String getFileContentsByIndex(int idx);
     
     String getFileContentsByName(String name);
     
     /**
      * Returns previous post id (if exists), in case
      * we want to update already existing code, as opposed to
      * posting new one.
      * 
      * @return NULL or post id string. 
      */
     String getPreviousPostId();

     String getDescription();

    public String getFileNameByIndex(int i);
     
}
