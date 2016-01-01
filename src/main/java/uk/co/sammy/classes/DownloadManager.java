package uk.co.sammy.classes;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

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

    //Flags whether table selection is being cleared or not.
    private boolean clearing;

    //DownloadManager Constructor
    public DownloadManager(){
        //Sets application title.
        setTitle("Download Manager");

        //Sets window size
        setSize(700, 500);

        //Handles window closing events.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                actionExit();
            }
        });

        //Sets up file menu.
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem fileExitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        fileExitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionExit();
            }
        });
        fileMenu.add(fileExitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        //Sets up add panel.
        JPanel addPanel = new JPanel();
        addTextField = new JTextField(30);
        addPanel.add(addTextField);
        JButton addButton = new JButton("Add Download");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionAdd();
            }
        });
        addPanel.add(addButton);

        //Sets up Downloads Table
        tableModel = new DownloadsTableModel();
        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                tableSelectionChanged();
            }
        });

        //Allows single row selection.
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //Sets up ProgressBar as renderer
        ProgressRenderer renderer = new ProgressRenderer(0, 100);
        renderer.setStringPainted(true);//shows progress text
        table.setDefaultRenderer(JProgressBar.class, renderer);

        //Sets table's row height to fit JProgressBar.
        table.setRowHeight((int) renderer.getPreferredSize().getHeight());

        //Sets up download panel.
        JPanel downloadsPanel = new JPanel();
        downloadsPanel.setBorder(BorderFactory.createTitledBorder("Downloads"));
        downloadsPanel.setLayout(new BorderLayout());
        downloadsPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        //Sets up buttons panel.
        JPanel buttonsPanel = new JPanel();
        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionPause();
            }
        });
        pauseButton.setEnabled(false);
        buttonsPanel.add(pauseButton);

        resumeButton = new JButton("Resume");
        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionResume();
            }
        });
        resumeButton.setEnabled(false);
        buttonsPanel.add(resumeButton);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionCancel();
            }
        });
        cancelButton.setEnabled(false);
        buttonsPanel.add(cancelButton);

        clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionClear();
            }
        });
        clearButton.setEnabled(false);
        buttonsPanel.add(clearButton);

        //Adds panel to display
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(addPanel, BorderLayout.NORTH);
        getContentPane().add(downloadsPanel, BorderLayout.CENTER);
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
    }

    //Called when table row changes.
    private void tableSelectionChanged() {
        //Unregisters from receiving notification of last selected download.
        if (selectedDownload != null){
            selectedDownload.deleteObserver(DownloadManager.this);
        }

        //Registers selected download to receive notification.
        if(!clearing){
            selectedDownload = tableModel.getDownload(table.getSelectedRow());
            selectedDownload.addObserver(DownloadManager.this);
            updateButtons();
        }
    }

    //Adds a new download.
    private void actionAdd() {
        URL verifiedUrl = verifyUrl(addTextField.getText());
        if(verifiedUrl != null){
            tableModel.addDownload(new Download(verifiedUrl));
            addTextField.setText("");//resets add text field
        }else{
            JOptionPane.showMessageDialog(this, "Invalid Download URL", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Verifies download URL
    private URL verifyUrl(String url) {
        //Allows only HTTP URLs.
        if(!url.toLowerCase().startsWith("http://")){
            return null;
        }

        //Verifies URL format.
        URL verifiedUrl = null;
        try{
            verifiedUrl = new URL(url);

        }catch (MalformedURLException mue){
            mue.getStackTrace();
            return null;
        }

        //Makes sure URL specifies a file.
        if(verifiedUrl.getFile().length() < 2){
            return null;
        }
        return verifiedUrl;
    }

    //Exits the program.
    private void actionExit() {
        System.exit(0);
    }

    //Pauses selected download
    private void actionPause(){
        selectedDownload.pause();
        updateButtons();
    }

    //Resumes selected download.
    private void actionResume(){
        selectedDownload.resume();
        updateButtons();
    }

    //Cancels selected download.
    private void actionCancel(){
        selectedDownload.cancel();
        updateButtons();
    }

    //Clears selected download.
    private void actionClear(){
        clearing = true;
        tableModel.clearDownload(table.getSelectedRow());
        clearing = false;
        selectedDownload = null;
        updateButtons();
    }

    //Updates each buttons state
    private void updateButtons(){
        if(selectedDownload != null){
            int status = selectedDownload.getStatus();
            switch (status){
                case Download.DOWNLOADING:
                    pauseButton.setEnabled(true);
                    resumeButton.setEnabled(false);
                    cancelButton.setEnabled(true);
                    clearButton.setEnabled(false);
                    break;

                case Download.PAUSED:
                    pauseButton.setEnabled(false);
                    resumeButton.setEnabled(true);
                    cancelButton.setEnabled(true);
                    clearButton.setEnabled(false);
                    break;

                case Download.ERROR:
                    pauseButton.setEnabled(false);
                    resumeButton.setEnabled(true);
                    cancelButton.setEnabled(false);
                    clearButton.setEnabled(true);
                    break;

                default: //Handles complete or cancelled states
                    pauseButton.setEnabled(false);
                    resumeButton.setEnabled(false);
                    cancelButton.setEnabled(false);
                    clearButton.setEnabled(false);
            }
        }else{
            //No download selected in table.
            pauseButton.setEnabled(false);
            resumeButton.setEnabled(false);
            cancelButton.setEnabled(false);
            clearButton.setEnabled(false);
        }
    }

    //Called when Download notifies observers of changes.
    @Override
    public void update(Observable o, Object arg) {
        //Updates buttons on changes in selected download
        if(selectedDownload != null && selectedDownload.equals(o)){
            updateButtons();
        }
    }
}
