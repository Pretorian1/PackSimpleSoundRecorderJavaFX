package simplesoundrecorderjavafx.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import simplesoundrecorderjavafx.objects.Audio;
import simplesoundrecorderjavafx.objects.JavaSoundRecorder;
import simplesoundrecorderjavafx.objects.SilenceDetector;

import java.io.File;
import java.io.IOException;

/**
 * Created by Max on 03.11.2016.
 */
public class mainController {

    @FXML
    private Button mainButton;


    private Audio audio = Audio.getInstance();

    private SilenceDetector silenceDetector = SilenceDetector.getInstance();


    public void startRecord(ActionEvent actionEvent) {

        mainButton.setDisable(true);
            audio.playSound(audio.playSoundW);
            while (audio.playSoundW.isPlaying()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        Platform.runLater(() -> { silenceDetector.startListenSilence();
            b: while(true){
                if(silenceDetector.isInterrupt())
                {
                    audio.playSound(audio.playSoundE);
                    while (audio.playSoundE.isPlaying()) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    silenceDetector.setInterrupt(false);
                    mainButton.setDisable(false);
                    break b;
                }
                else {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
