/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.socialsketch.ui.imgpanel;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 * @author Dimitry Kireyenkov <dimitry@languagekings.com>
 */
public class MyListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component cmp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); //To change body of generated methods, choose Tools | Templates.
        
        if ( value instanceof ImgRecord ){
            ImgRecord imgRecord = (ImgRecord) value;
            JLabel label = (JLabel) cmp;
            
            label.setIcon(imgRecord.getIcon());
            label.setText("");
            return label;
        }
        
        return cmp;
        
    }
}
