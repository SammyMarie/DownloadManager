package uk.co.sammy.classes;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Created by smlif on 01/01/2016.
 */
public class Download extends Observable implements Runnable {

    //MAX size of download buffer.
    private static final int MAX_BUFFER_SIZE = 1024;

    //These are the status names.
    public static final String STATUSES[] = {"Downloading", "Paused", "Complete", "Cancelled", "Error"};

    //These are the status codes.
    public static final int DOWNLOADING = 0;
    public static final int PAUSED = 1;
    public static final int COMPLETE = 2;
    public static final int CANCELLED = 3;
    public static final int ERROR = 4;

    private URL url; //download URL
    private int size; //size of download in bytes
    private int downloaded;//number of bytes downloaded
    private int status; //current status of download
    
    //Download Constructor
    public Download(URL url){
        this.url = url;
        size = -1;
        downloaded = 0;
        status = DOWNLOADING;
        
        //Begins the download.
        download();
    }

    //Gets this download's URL.
    public String getUrl(){
        return url.toString();
    }

    //Gets this download's size.
    public int getSize(){
        return size;
    }

    //Gets this download's progress.
    public float getProgress(){
        return ((float) downloaded / size) * 100;
    }

    //Gets this download's status.
    public int getStatus(){
        return status;
    }

    //Pauses the download.
    public void pause(){
        status = PAUSED;
        stateChanged();
    }

    //Resumes the download.
    public void resume(){
        status = DOWNLOADING;
        stateChanged();
        download();
    }

    //Cancels the download.
    public void cancel(){
        status = CANCELLED;
        stateChanged();
    }

    //Marks the download as having an error.
    private void error(){
        status = ERROR;
        stateChanged();
    }

    //Starts or resumes downloading.
    private void download() {
        Thread thread = new Thread(this);
        thread.start();
    }

    //Gets file name portion of URL.
    private String getFileName(URL url){
        String fileName = url.getFile();
        return fileName.substring(fileName.lastIndexOf('/') + 1);
    }

    //Downloads file
    @Override
    public void run() {
        RandomAccessFile file = null;
        InputStream stream = null;

        try{
            //Opens connection to URL.
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //Specifies what portion of file to download.
            connection.setRequestProperty("Range", "bytes = " + downloaded + "-");

            //Connects to server.
            connection.connect();

            //Makes sure response code is in the 200 range.
            if(connection.getResponseCode() / 100 != 2){
                error();
            }

            //Checks for valid content length.
            int contentLength = connection.getContentLength();
            if(contentLength < 1){
                error();
            }

            //Sets download size if not set.
            if(size == -1){
                size = contentLength;
                stateChanged();
            }

            //Opens file and seeks to the end of it.
            file = new RandomAccessFile(getFileName(url), "rw");
            file.seek(downloaded);

            stream = connection.getInputStream();
            while (status == DOWNLOADING){
                //Size of buffer equal to size left to download
                byte buffer[];
                if(size - downloaded > MAX_BUFFER_SIZE){
                    buffer = new byte[MAX_BUFFER_SIZE];
                }else{
                    buffer = new byte[size - downloaded];
                }

                //Reads from server int buffer.
                int read = stream.read(buffer);
                if(read == -1){
                    break;
                }

                //Writes buffer to file.
                file.write(buffer, 0, read);
                downloaded += read;
                stateChanged();
            }

            //Changes status to complete when download finished.
            if(status == DOWNLOADING){
                status = COMPLETE;
                stateChanged();
            }
        }catch (Exception e){
            error();
        }finally {
            //Closes file.
            if(file != null){
                try{
                    file.close();
                }catch (IOException ioe){
                    ioe.getStackTrace();
                }
            }
            //Closes connection to server.
            if(stream != null){
                try {
                    stream.close();
                }catch (IOException ioe){
                    ioe.getStackTrace();
                }
            }
        }
    }

    //Notifies observers of change in download status
    private void stateChanged() {
        setChanged();
        notifyObservers();
    }
}
