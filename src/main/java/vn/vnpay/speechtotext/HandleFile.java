/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.vnpay.speechtotext;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.CountDownLatch;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import vn.vnpay.config.Config;
import vn.vnpay.utils.SequenceGenerator;

/**
 *
 * @author truongnq
 */
public class HandleFile implements Runnable{
    private final File inputFile;
    private final FFmpegExecutor executor;
    private final File audioDir;
    private final Gson GSON;
    public HandleFile(final File inputFile, FFmpegExecutor executor, File audioDir, Gson GSON){
        this.inputFile = inputFile;
        this.executor = executor;
        this.audioDir = audioDir;
        this.GSON = GSON;
    }

    @Override
    public void run() {
        try {
            long uid = SequenceGenerator.getInstance().nextId();

            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(inputFile.getAbsolutePath()) // Filename, or a FFmpegProbeResult
                    .addOutput(Config.getAppConfig().getString("AUDIO_DIR") + uid + "_%03d.flac")
                    .setAudioChannels(1)
                    .addExtraArgs("-f", "segment")
                    .addExtraArgs("-segment_time", "20")
                    //                .setStartOffset(40, TimeUnit.SECONDS)
                    //                .setDuration(20, TimeUnit.SECONDS)
                    .setAudioSampleRate(44100)
                    .setFormat("flac")
                    .done();
            executor.createJob(builder).run();

            FileFilter fileFilter = new WildcardFileFilter(uid + "_*.flac");
            File[] files = audioDir.listFiles(fileFilter);
            CountDownLatch countDown = new CountDownLatch(files.length);
            for (int i = 0; i < files.length; i++) {
                RecognitionAudio audio = RecognitionAudio.builder().content(Base64.encodeBase64String(FileUtils.readFileToByteArray(files[i]))).build();
                RecognitionConfig config = RecognitionConfig.builder()
                        .enableAutomaticPunctuation(true)
                        .encoding("FLAC")
                        .languageCode("en-US")
                        .model("video")
                        .build();
                RequestObject req = RequestObject.builder()
                        .audio(audio)
                        .config(config)
                        .build();
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), GSON.toJson(req));
                Request request = new Request.Builder()
                        .url(Config.getAppConfig().getString("GOOGLE_SPEECH_URL"))
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();
                HttpClient.getInstance().executeHttpRequest(i , uid, request, countDown);
                files[i].delete();
            }
            inputFile.delete();
            countDown.await();
            System.out.println("done: " + uid);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
}
