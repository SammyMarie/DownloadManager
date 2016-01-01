package uk.co.sammy.classes;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by smlif on 01/01/2016.
 */
//This manages the download table's data
public class DownloadsTableModel extends AbstractTableModel implements Observer {

    //Names for the table's columns.
    private static final String columnNames[] = {"URL", "Size", "Progress", "Status"};

    //Classes for each column's values.
    private static final Class[] columnClasses = {String.class, String.class, JProgressBar.class, String.class};

    //Table's list of downloads.
    private ArrayList<Download> downloadList = new ArrayList<>();

    //Adds a new download to table.
    public void addDownload(Download download){
        //Registers notification when download changes.
        download.addObserver(this);
        downloadList.add(download);

        //Fire table row insertion notification to table
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    //Gets a download for a specified row.
    public Download getDownload(int row){
        return downloadList.get(row);
    }

    //Removes a download from the list.
    public void clearDownload(int row){
        downloadList.remove(row);

        //Fire table row deletion notification to table.
        fireTableRowsDeleted(row, row);
    }

    //Gets table's row count.
    @Override
    public int getRowCount() {
        return downloadList.size();
    }

    //Gets table's column count
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    //Gets column's name
    public String getColumnName(int column){
        return columnNames[column];
    }

    //Gets column's class
    public Class getColumnClass(int column){
        return columnClasses[column];
    }

    //Gets value for specific row and column combination.
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Download download = downloadList.get(rowIndex);
        switch (columnIndex){
            case 0: //URL
                return download.getUrl();

            case 1: //Size
                int size = download.getSize();
                return (size == -1) ? "" : Integer.toString(size);

            case 2: //Progress
                return new Float(download.getProgress());

            case 3: //Status
                return Download.STATUSES[download.getStatus()];
        }
        return "";
    }

    //Called when a Download notifies its observer of changes
    @Override
    public void update(Observable observe, Object arg) {
        int index = downloadList.indexOf(observe);

        //Fire table row update notification to table.
        fireTableRowsUpdated(index, index);
    }
}
