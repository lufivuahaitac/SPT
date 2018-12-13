/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.vnpay.speechtotext;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import vn.vnpay.config.Config;
import vn.vnpay.daos.ConnectionManager;
import vn.vnpay.daos.DataDao;

/**
 *
 * @author truongnq
 */
public class SpeechToText {

    private final ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(10);
    private static Logger logger;
    private static final Gson GSON = new Gson();
    private static FFmpeg ffmpeg;
    private static FFprobe ffprobe;
    private static FFmpegExecutor executor;
    private static File audioDir;

    public SpeechToText() {
        try {
            audioDir = new File(Config.getAppConfig().getString("AUDIO_DIR"));
            ffmpeg = new FFmpeg(Config.getAppConfig().getString("FFMPEG"));
            ffprobe = new FFprobe(Config.getAppConfig().getString("FFPROBE"));
            executor = new FFmpegExecutor(ffmpeg, ffprobe);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public static void main(String[] args) throws Exception {
        loadLogger();
        Config.init();
        ConnectionManager.getInstance();
        
//        System.out.println(DataDao.getInstance().getFullText(3391833422667776l));
        
        DataDao.getInstance().initTable();
        SpeechToText spt = new SpeechToText();
        FileAlterationObserver observer = new FileAlterationObserver(Config.getAppConfig().getString("AUDIO_DIR"));
        FileAlterationMonitor monitor = new FileAlterationMonitor(10000);
        FileAlterationListener listener = new FileAlterationListenerAdaptor() {
            @Override
            public void onFileCreate(File file) {
                // code for processing creation event
                if (file.getName().endsWith(".m4a")) {
                    System.out.println("Begin process ------" + file.getName() + "-----------------------");
                    spt.start(file);
                    System.out.println("End-----------------" + file.getName() + "-----------------------");
                }
            }

            @Override
            public void onFileDelete(File file) {
                // code for processing deletion event
            }

            @Override
            public void onFileChange(File file) {
                // code for processing change event
                System.out.println(file.getName());
            }
        };
        observer.addListener(listener);
        monitor.addObserver(observer);
        monitor.start();

        //System.out.println(spt.speechFileToText());
    }

    private void start(File inputFile) {
        threadPoolExecutor.execute(new HandleFile(inputFile, executor, audioDir, GSON));
    }
    
    private static void loadLogger() throws IOException {
        String configuration = new File(".").getCanonicalPath() + "/config/log4j2.xml";
        URI source = new File(configuration).toURI();
        Configurator.initialize("contextLog4J", null, source);
        logger = LogManager.getLogger(SpeechToText.class);
        System.out.println("Init Logger Success");
        logger.debug("Debugging log");
        logger.info("Info log");
        logger.warn("Hey, This is a warning!");
        logger.error("Oops! We have an Error. OK");
        logger.fatal("Damn! Fatal error. Please fix me.");
    }

}
