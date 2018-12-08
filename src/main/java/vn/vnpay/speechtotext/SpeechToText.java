/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.vnpay.speechtotext;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import vn.vnpay.utils.SequenceGenerator;

/**
 *
 * @author truongnq
 */
public class SpeechToText {

    private static final Gson GSON = new Gson();
    private static FFmpeg ffmpeg;
    private static FFprobe ffprobe;
    private static FFmpegExecutor executor;
    private static File audioDir;

    public SpeechToText() {
        try {
            audioDir = new File("./audio/");
            ffmpeg = new FFmpeg("D:\\app\\ffmpeg\\bin\\ffmpeg.exe");
            ffprobe = new FFprobe("D:\\app\\ffmpeg\\bin\\ffprobe.exe");
            executor = new FFmpegExecutor(ffmpeg, ffprobe);
        } catch (IOException ex) {
            Logger.getLogger(SpeechToText.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws Exception {
        SpeechToText spt = new SpeechToText();
        spt.test("./audio/song.mp3");
        //System.out.println(spt.speechFileToText());
    }

    private void test(String filePath) throws IOException {
        long uid = SequenceGenerator.getInstance().nextId();

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(filePath) // Filename, or a FFmpegProbeResult
                .addOutput("./audio/" + uid + "_%03d.flac")
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
                    .url("https://cxl-services.appspot.com/proxy?url=https%3A%2F%2Fspeech.googleapis.com%2Fv1p1beta1%2Fspeech%3Arecognize")
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();
            HttpClient.getInstance().executeHttpRequest(i + "_" + uid , request);
            files[i].delete();
        }
    }

}
