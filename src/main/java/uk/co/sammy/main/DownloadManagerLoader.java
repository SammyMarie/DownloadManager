package uk.co.sammy.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.co.sammy.classes.DownloadManager;


/**
 * Created by smlif on 01/01/2016.
 */
public class DownloadManagerLoader {
    private static ApplicationContext context;

    public static void main(String... args){
        context = new ClassPathXmlApplicationContext("spring.xml");
        DownloadManager downloader = (DownloadManager) context.getBean("downloader");
        downloader.setVisible(true);
    }
}
