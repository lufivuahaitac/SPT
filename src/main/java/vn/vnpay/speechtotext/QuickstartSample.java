/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.vnpay.speechtotext;

/**
 *
 * @author truongnq
 */
// Imports the Google Cloud client library
import org.apache.commons.codec.binary.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuickstartSample {
    public static void main(String[] args) {
        try {
            syncRecognizeFile("./S_000.flac");
        } catch (Exception ex) {
            Logger.getLogger(QuickstartSample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Performs speech recognition on raw PCM audio and prints the
     * transcription.
     *
     * @param fileName the path to a PCM audio file to transcribe.
     */
    public static void syncRecognizeFile(String fileName) throws Exception {
        try {
            System.out.println(Base64.encodeBase64String(Files.readAllBytes(Paths.get("./audio/a.flac"))));
        } catch (Exception ex){
            System.out.println(ex);
        }
    }
}
