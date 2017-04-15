package simplesoundrecorderjavafx.objects; /**
 * Created by Koval on 03.11.2016.
 */

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


public class JavaSoundRecorder {

    private TargetDataLine line;

    private boolean flag;

    private static  JavaSoundRecorder recorder;

    private AudioFormat format;

    private static final int BUFFER_SIZE = 4096;

    private ByteArrayOutputStream recordBytes;


    private JavaSoundRecorder() {}

    public static synchronized JavaSoundRecorder getInstance() {
        if (recorder == null)
            synchronized (JavaSoundRecorder.class) {
                if (recorder == null)
                    recorder = new JavaSoundRecorder();
            }
        return recorder;
    }



    /**
     * Defines an audio format
     */
    AudioFormat getAudioFormat() {
        float sampleRate = 44100;
        int sampleSizeInBits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        format = new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
        return format;
    }

    /**
     * Captures the sound and record into a WAV file
     */
    void start() {


        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = AudioSystem.getTargetDataLine(format);
            line.open(format);
            line.start();// start capturing


            System.out.println("Start capturing...");
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = 0;

            recordBytes = new ByteArrayOutputStream();
            flag = true;
            System.out.println("Start recording...");

            while (flag) {
                System.out.println("!!!");
                bytesRead = line.read(buffer, 0, buffer.length);
                recordBytes.write(buffer, 0, bytesRead);
            }

        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Closes the target data line to finish capturing and recording
     */
    void finish() {
        flag = false;
     //   System.out.println("Finished not");
      //  if (line != null) {
            line.stop();
            line.drain();
            line.close();
     //   }
        System.out.println("Finished");
    }



    public void recordSound(){
    recorder.start();

    }


    public void stopRecordSound(){

        recorder.finish();
    }

    public void save(File wavFile) throws IOException {
        byte[] audioData = recordBytes.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
        AudioInputStream audioInputStream = new AudioInputStream(bais, format,
                audioData.length / format.getFrameSize());
       // System.out.println("Done!!");
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, wavFile);

        audioInputStream.close();
        recordBytes.close();
    }


}