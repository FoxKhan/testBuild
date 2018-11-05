package sample;

import io.reactivex.Completable;
import io.reactivex.functions.Action;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(new File("src/main/resources/ttt.fxml").toURI().toURL());
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        Label label = (Label) root.lookup("#label");

        ((Button) root.lookup("#btn1")).setOnAction(actionEvent -> label.setText("hello...."));
        ((Button) root.lookup("#btn2")).setOnAction(actionEvent -> label.setText(".... world"));
        ((Button) root.lookup("#btn3")).setOnAction(actionEvent -> cmd().doOnComplete(() -> System.out.println("end command"))
                .subscribe());
    }


    private Completable cmd() {
        return Completable.fromCallable((Callable<Void>) () -> {

            System.out.println("start command");

            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", PATH_TO_ZIPA + ZIPA);
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                System.out.println(line);
            }
            return null;
        });
    }

    private final static String ZIPA = "zipalign -v -p 4 c:/users/artk5/documents/testapplication/app/build/outputs/apk/release/app-release-unsigned.apk c:/app/app-release-unsigned-aligned.apk";
    private final static String PATH_TO_ZIPA = "C:\\Users\\nika-\\AppData\\Local\\Android\\Sdk\\build-tools\\27.0.3\\";

    public static void main(String[] args) {
        launch(args);
    }
}
