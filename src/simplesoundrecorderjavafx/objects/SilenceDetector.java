package simplesoundrecorderjavafx.objects;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by Max on 09.11.2016.
 */
public class SilenceDetector {


    private static SilenceDetector silenceDetector;
    private static boolean detected;
    private boolean wait_For;
    private static short count;
    private static Thread thread;
    private static int counter;
    private TargetDataLine line;
    private static ByteArrayOutputStream recordBytes;
    private boolean flag;
    private AudioFormat format;
    private Thread recordThread;
    private AudioDispatcher dispatcher;
    private boolean interrupt;

    public boolean isInterrupt() {
        return interrupt;
    }

    public void setInterrupt(boolean interrupt) {
        this.interrupt = interrupt;
    }

    private final static String NAME_SILENCE ="Unnamed Record Silence";
    private static final int BUFFER_SIZE = 4096;


    private SilenceDetector(){}

    public static synchronized SilenceDetector getInstance() {
        if (silenceDetector == null)
            synchronized (SilenceDetector.class) {
                if (silenceDetector == null)
                    silenceDetector = new SilenceDetector();
            }
        return silenceDetector;
    }

    public void startListenSilence (){


        try {
            dispatcher = createAudioDispatcher(44100,1024,0);

        } catch (LineUnavailableException e) {
            dispatcher = null;
            e.printStackTrace();
        }
        detected = false;
        dispatcher.addAudioProcessor(new AudioProcessor() {
            float threshold = -75; //dB
            @Override
            public boolean process(AudioEvent audioEvent) {
                float[] buffer = audioEvent.getFloatBuffer();
                double level = soundPressureLevel(buffer);
                if(detected && !flag){
                    System.out.println("Just go inside");
                    if(recordThread==null)
                    recordSound();
                }
                if (count == 60){
                    System.out.println("Interrupt!!!");
                    detected = false;
                    finish();
                    interrupt = true;
                    dispatcher = null;
                    count=0;
                    thread.stop();
                    thread = null;//very dangerous they say but it works!!!
                    return false;
                }
                    else if (level > threshold) {
                        System.out.println("Sound detected. = " + level);
                        wait_For = true;
                        if (count == 0)
                        detected = true;
                        count = 0;
                        return true;
                    } else if (count!=60 && wait_For){
                        try {
                            System.out.println("Sleep!!!");
                            Thread.sleep(25);
                            count++;
                            detected = false;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                else if (!wait_For){
                    System.out.println("Wait for");
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
                return false;
            }
            @Override
            public void processingFinished() {}
            private double soundPressureLevel(final float[] buffer) {
                double power = 0.0D;
                for (float element : buffer) {
                    power += element*element;
                }
                double value = Math.pow(power, 0.5)/ buffer.length;;
                return 20.0 * Math.log10(value);
            }
        });
        thread = new Thread(dispatcher, "Audio Dispatcher");
        thread.start();
    }

   private AudioDispatcher createAudioDispatcher(final int sampleRate,final int audioBufferSize, final int bufferOverlap) throws LineUnavailableException {
       format = new AudioFormat(sampleRate, 16, 2, true,true);
       line =  AudioSystem.getTargetDataLine(format);
       line.open(format);
       line.start();
       AudioInputStream stream = new AudioInputStream(line);
       TarsosDSPAudioInputStream audioStream = new JVMAudioInputStream(stream);
       return new AudioDispatcher(audioStream,audioBufferSize,bufferOverlap);
   }

    public void recordSound(){
        recordThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Start capturing...");
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = 0;

                recordBytes = new ByteArrayOutputStream();
                synchronized (this){
                flag = true;}
                System.out.println("Start recording...");

                while (true) {
                    bytesRead = line.read(buffer, 0, buffer.length);
                    recordBytes.write(buffer, 0, bytesRead);
                }
            }
        });
        recordThread.setPriority(Thread.MAX_PRIORITY);
        recordThread.start();

    }

    void finish() {
        flag = false;
        line.stop();
        line.drain();
        line.close();
        File file = new File(NAME_SILENCE+counter+".wav");
        try {
            save(file);
            counter++;
            recordThread.stop();
            recordThread = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finished");
    }

    public void save(File wavFile) throws IOException {
        byte[] audioData = recordBytes.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
        AudioInputStream audioInputStream = new AudioInputStream(bais, format,
                audioData.length / format.getFrameSize());
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, wavFile);

        audioInputStream.close();
        recordBytes.close();
    }


    public static void main(String[] args) throws LineUnavailableException {

        getInstance().startListenSilence();
    }

}
