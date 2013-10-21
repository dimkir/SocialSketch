package components;

import java.io.File;

/**
 * This is advanced callback which is called by IconDempApp when user has finished
 * selecting image or closed window.
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public interface AdvancedCallback {
    void onFinishedSelection(FinishedSelectionEvent evt);

    /**
     * This is data structure which informs observer of what has happened
     * after user has finished selection.
     */
    public static class FinishedSelectionEvent {
        
        
        private File mSelectedImageFile;

        /**
         * 
         * @param selectedImageFile NULL in case no file selected. or valid reference to File pointing at selected image file.
         */
        public FinishedSelectionEvent(File selectedImageFile) {
            mSelectedImageFile = selectedImageFile;
        }
        
        public boolean hasCancelled(){
            return mSelectedImageFile == null;
        }
        
        public boolean hasSelected(){
            return mSelectedImageFile != null ;
        }
        
        public File getSelectedImageFile(){
            return mSelectedImageFile;
        }
        
        public String getSelectedImagePath(){
            return getSelectedImageFile().getAbsolutePath();
        }
    }
}
