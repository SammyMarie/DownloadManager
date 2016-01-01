package uk.co.sammy.classes;

import javax.swing.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by smlif on 01/01/2016.
 */
public class DownloadManager extends JFrame implements Observer {

    //Adds download text field.
    private JTextField addTextField;

    //Download Table's data model.
    private DownloadsTableModel tableModel;

    //Table listing downloads.
    private JTable table;

    //Buttons for managing selected downloads.
    private JButton pauseButton, resumeButton;
    private JButton cancelButton, clearButton;

    //Currently selected download.
    private Download selectedDownload;

    

    @Override
    public void update(Observable o, Object arg) {

    }
}
