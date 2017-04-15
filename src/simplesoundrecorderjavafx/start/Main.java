package simplesoundrecorderjavafx.start;

/**
 * Created by Max on 03.11.2016.
 */
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import simplesoundrecorderjavafx.controllers.mainController;
import simplesoundrecorderjavafx.objects.JavaSoundRecorder;

import java.io.File;
import java.io.IOException;

public class Main extends Application {

    private JavaSoundRecorder record = JavaSoundRecorder.getInstance();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
        primaryStage.setTitle("SimpleSoundRecorderJavaFX");
       System.out.println( Runtime.getRuntime().availableProcessors());
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override
    public void stop(){// Action on close!!!
        System.out.println("Stage is closing");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
