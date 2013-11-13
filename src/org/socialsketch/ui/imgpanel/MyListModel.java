package org.socialsketch.ui.imgpanel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.Icon;

/**
 * This will be a funny model, which knows how to load images.
 * 
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class MyListModel extends AbstractListModel<ImgRecord>  
                        implements MySwingWorker.IOnIconReady
                           
{
    private final List<ImgRecord> mImageRecords = new ArrayList<ImgRecord>();

    

    /**
     * Adds image files to the model.s
     * @param imageFiles 
     */
    void addImageFiles(File[] imageFiles) {
        // TODO: implement addImageFiles
        for (File img : imageFiles) {
            System.out.println("Starting worker thread for file: " + img.getAbsolutePath());
            (new MySwingWorker(img, this)).execute();
        }
    }


    void addIcon(Icon icon, File originalFilepath){
        System.out.println("Receiving icon: "+  icon.getIconHeight() );
        mImageRecords.add(new ImgRecord(icon, originalFilepath));
        int from = getSize() -2;
        int to =  getSize() -1;
        if ( from < 0 ){
            from = 0;
        }
        String msg = String.format("addIcon() from %d to %d", from , to);
        System.out.println(msg);
        fireIntervalAdded(this, from, to);
    }


    @Override
    public void onIconReady(Icon icon, File originalFilepath) {
        addIcon(icon, originalFilepath);
    }

    @Override
    public int getSize() {
        return mImageRecords.size();
    }

    @Override
    public ImgRecord getElementAt(int index) {
        return mImageRecords.get(index);
    }
    
}
