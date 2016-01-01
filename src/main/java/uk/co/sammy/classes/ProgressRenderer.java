package uk.co.sammy.classes;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by smlif on 01/01/2016.
 */
//This renders the progress bar in the table.
public class ProgressRenderer extends JProgressBar implements TableCellRenderer{

    //ProgressRenderer Constructor
    public ProgressRenderer(int min, int max){
        super(min, max);
    }

    //Returns this JProgressBar as renderer for downloads
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setValue((int) ((Float) value).floatValue());
        return this;
    }
}
