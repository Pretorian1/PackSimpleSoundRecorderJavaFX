package simplesoundrecorderjavafx.objects;

import javafx.scene.media.AudioClip;

/**
 * Created by MAKSYM on 18.04.2016.
 */
public final class Audio {

    private static Audio audio;

    public static final String WORK = "/sounds/Speak_please.mp3";
    public static final String END_WORK ="/sounds/Thank_you!.mp3";


    public AudioClip playSoundW = new AudioClip(getClass().getResource(WORK).toExternalForm());
    public   AudioClip playSoundE = new AudioClip(getClass().getResource(END_WORK).toExternalForm());

    private Audio() {}

    public static synchronized Audio getInstance() {
        if (audio == null)
            synchronized (Audio.class) {
                if (audio == null)
                    audio = new Audio();
            }
        return audio;
    }

    public void playSound(AudioClip audioClip){
        audioClip.play();
    }
    public void setAudioToDefault(){
        playSoundW = new AudioClip(getClass().getResource(WORK).toExternalForm());
        playSoundE = new AudioClip(getClass().getResource(END_WORK).toExternalForm());
    }
}
