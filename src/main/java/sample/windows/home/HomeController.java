package sample.windows.home;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.build.ApkBuilder;
import sample.exceptions.NotValidPasswordException;
import sample.model.Paths;
import sample.model.pojo.KeyProperty;
import sample.utils.Commands;
import sample.utils.FileController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static sample.ProConstants.*;

public class HomeController implements Initializable {

    public ListView<String> storeList;
    public ListView<String> aliasList;
    public ListView<String> aboutList;
    public PasswordField masterPass;
    public TextField newAliasNameTV;
    public Pane pathColorKeytool;
    public Pane pathColorGradlew;
    public Pane pathColorZipalign;
    public Pane pathColorApksigner;

    private String currentKeyStore;
    private KeyProperty currentAlias;
    private Paths paths;

    private ApkBuilder apkBuilder = new ApkBuilder();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadKeyStores();
        initListKeyStore();
        checkPaths();


    }

    public void addKeyStoreClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(new File("src/main/resources/add_key.fxml").toURI().toURL());
            VBox page = loader.load();


            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editor");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(((Node) actionEvent.getTarget()).getScene().getWindow());
            dialogStage.initStyle(StageStyle.UTILITY);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);


            KeyProperty keyProperty = new KeyProperty();
            FieldController controller = loader.getController();
            controller.setKeyProperty(keyProperty);
            dialogStage.showAndWait();

            if (keyProperty.getKeyStoreName() != null) {
                Commands.addKeyStore(keyProperty);
                loadKeyStores();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getSHAClick(ActionEvent actionEvent) {

        if (currentKeyStore == null) return;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("SHA - 1");
        VBox dialogPaneContent = new VBox();
        TextArea textArea = new TextArea();

        Commands.getSHAList(currentKeyStore, masterPass.getText())
                .doOnSuccess(strings -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String str : strings) {
                        stringBuilder.append(str).append("\n");
                    }
                    textArea.setText(stringBuilder.toString());
                })
                .doOnError(throwable -> {
                    if (throwable instanceof NotValidPasswordException) {
                        showAlertNotValidPassword();
                    }
                })
                .subscribe();

        dialogPaneContent.getChildren().addAll(textArea);
        alert.getDialogPane().setContent(dialogPaneContent);

        alert.showAndWait();
    }

    public void onPasswordEnter(KeyEvent keyEvent) {
        if (currentKeyStore != null && keyEvent.getCode().equals(KeyCode.ENTER)) {
            getAliasList(currentKeyStore);
        }
    }

    public void copyAliasClick(ActionEvent actionEvent) {

        if (currentAlias == null) return;

        String aliasName = newAliasNameTV.getText().trim();
        if (aliasName.isEmpty() || aliasName.length() < 3 || aliasName.contains(" ")) {
            showAlertEmptyAliasName();
            return;
        }

        Commands.addKeyStoreOrAlias(currentAlias, aliasName);

        getAliasList(currentKeyStore);
        aliasList.getSelectionModel().selectFirst();
    }


    private void initListKeyStore() {

        storeList.getSelectionModel().selectedItemProperty().addListener(((observableValue, s, t1) -> getAliasList(t1)));

        aliasList.getSelectionModel().selectedItemProperty().addListener(((observableValue, s, t1) -> getAliasProperties(t1)));

    }

    private void getAliasProperties(String aliasName) {

        if (currentKeyStore == null || masterPass.getText().isEmpty()) return;

        Commands.getKeyStoreProperties(currentKeyStore, aliasName, masterPass.getText())
                .doOnSuccess(keyProperty -> {

                    currentAlias = keyProperty;

                    aboutList.getItems().clear();

                    if (keyProperty.getKeyStoreName() != null) {
                        aboutList.getItems().add("Store:  " + keyProperty.getKeyStoreName());
                        aboutList.getItems().add("Alias:  " + keyProperty.getAliasName());
                        aboutList.getItems().add("Owner:  " + keyProperty.getFirstAndLastName());
                        aboutList.getItems().add("City:  " + keyProperty.getCity());
                        aboutList.getItems().add("Country code:  " + keyProperty.getCountryCode());
                    }
                })
                .subscribe();
    }

    private void getAliasList(String t1) {
        Commands.getAliasList(t1, masterPass.getText())
                .doOnSuccess(strings -> {
                    aliasList.getItems().clear();
                    aliasList.getItems().addAll(strings);
                })
                .doOnError(throwable -> {
                    if (throwable instanceof NotValidPasswordException) {
                        showAlertNotValidPassword();
                    }
                })
                .doFinally(() -> currentKeyStore = t1)
                .subscribe();
    }

    private void showAlertNotValidPassword() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error alert");
        alert.setHeaderText("Not valid password");
        alert.setContentText("Pls enter the password in master pas view and press ENTER button");

        alert.showAndWait();
    }

    private void showAlertEmptyAliasName() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error alert");
        alert.setHeaderText("Not valid alias name");
        alert.setContentText("Pls enter valid alias name");

        alert.showAndWait();
    }

    private void loadKeyStores() {

        File keyStoresFolder = new File(STORE_FOLDER);
        if (!keyStoresFolder.isDirectory()) return;

        File[] keyStores = keyStoresFolder.listFiles();

        storeList.getItems().clear();

        if (keyStores != null) {
            for (File keyStore : keyStores) {
                storeList.getItems().add(keyStore.getName());
            }
        }
    }

    public void onBuildClick(ActionEvent actionEvent) {

//        apkBuilder.buildKey();


        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(new File("src/main/resources/create_pattern_build.fxml").toURI().toURL());
            AnchorPane page = loader.load();

            Stage buildStage = new Stage();
            buildStage.setTitle("Edit Builder");
            buildStage.initModality(Modality.WINDOW_MODAL);
            buildStage.initOwner(((Node) actionEvent.getTarget()).getScene().getWindow());
            buildStage.initStyle(StageStyle.UTILITY);
            Scene scene = new Scene(page);
            buildStage.setScene(scene);


//            CreatePatternBuildController controller =  loader.getController();
//            controller.init();
//            KeyProperty keyProperty = new KeyProperty();
//            FieldController controller = loader.getController();
//            controller.setKeyProperty(keyProperty);

            buildStage.showAndWait();

//            if (keyProperty.getKeyStoreName() != null) {
//                Commands.addKeyStore(keyProperty);
//                loadKeyStores();
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    private void checkPaths() {

        File file = new File("paths.fox");

        paths = FileController.openSerializable(file);
        if (paths == null) {
            paths = new Paths();
            if (FileController.isExist("C:\\Program Files\\Android\\Android Studio\\jre\\bin\\keytool.exe")){
                paths.setKeytool("C:\\Program Files\\Android\\Android Studio\\jre\\bin\\keytool.exe");
            }else paths.setKeytool("");
            paths.setGradlew("");
            paths.setZipalign("");
            paths.setApksigner("");
        }

        if (paths.getKeytool().contains(KEY_KEYTOOL)) setPathColor(true, KEY_KEYTOOL);
        else setPathColor(false, KEY_KEYTOOL);

        if (paths.getZipalign().contains(KEY_ZIPALIGN)) setPathColor(true, KEY_ZIPALIGN);
        else setPathColor(false, KEY_ZIPALIGN);

        if (paths.getApksigner().contains(KEY_APKSIGNER)) setPathColor(true, KEY_APKSIGNER);
        else setPathColor(false, KEY_APKSIGNER);

        if (paths.getGradlew().contains(KEY_GRADLEW)) setPathColor(true, KEY_GRADLEW);
        else setPathColor(false, KEY_GRADLEW);
    }

    private void setPathColor(boolean isGreen, String id) {
        switch (id) {
            case KEY_KEYTOOL:
                if (isGreen) pathColorKeytool.setStyle(C_BACKGROUND_COLOR + COLOR_GREEN);
                else pathColorKeytool.setStyle(C_BACKGROUND_COLOR + COLOR_RED);
                break;
            case KEY_ZIPALIGN:
                if (isGreen) pathColorZipalign.setStyle(C_BACKGROUND_COLOR + COLOR_GREEN);
                else pathColorZipalign.setStyle(C_BACKGROUND_COLOR + COLOR_RED);
                break;
            case KEY_GRADLEW:
                if (isGreen) pathColorGradlew.setStyle(C_BACKGROUND_COLOR + COLOR_GREEN);
                else pathColorGradlew.setStyle(C_BACKGROUND_COLOR + COLOR_RED);
                break;
            case KEY_APKSIGNER:
                if (isGreen) pathColorApksigner.setStyle(C_BACKGROUND_COLOR + COLOR_GREEN);
                else pathColorApksigner.setStyle(C_BACKGROUND_COLOR + COLOR_RED);
                break;
        }
    }

    public void onKeytoolChooserClick(ActionEvent actionEvent) {
        String path = showFileChooser(actionEvent);
        if (!path.isEmpty()) {
//            paths.setKeytool(path);
            savePaths(path);
        }
    }

    private void savePaths(String path) {
        if (path.contains(KEY_KEYTOOL)) {
            paths.setKeytool(path);
            FileController.save(paths, "paths.fox");
            checkPaths();
        }
    }

    public void onGradlewChooserClick(ActionEvent actionEvent) {
        String path = showFileChooser(actionEvent);
        if (!path.isEmpty()) {
            savePaths(path);
        }
    }

    public void onApkSignerChooserClick(ActionEvent actionEvent) {
        String path = showFileChooser(actionEvent);
        if (!path.isEmpty()) {
            savePaths(path);
        }
    }

    public void onZipailignChooserClick(ActionEvent actionEvent) {
        String path = showFileChooser(actionEvent);
        if (!path.isEmpty()) {
            savePaths(path);
        }

    }

    private String showFileChooser(ActionEvent actionEvent) {

        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(((Node) actionEvent.getTarget()).getScene().getWindow());

        if (selectedFile == null) return "";
        else return selectedFile.getAbsolutePath();
    }
}