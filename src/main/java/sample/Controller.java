package sample;

import io.reactivex.functions.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import sample.exceptions.NotValidPasswordException;
import sample.model.Paths;
import sample.model.pojo.KeyProperty;
import sample.utils.Commands;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import static sample.ProConstants.*;

public class Controller implements Initializable {

    public ListView<String> storeList;
    public ListView<String> aliasList;
    public ListView<String> aboutList;
    public PasswordField masterPass;
    public TextField newAliasNameTV;
    public Button btn1;
    public Button btn2;
    public Button btn3;
    public Button btn4;
    public Button btn5;
    public Button btn6;
    public Pane pathColorKeytool;
    public Pane pathColorGradlew;
    public Pane pathColorZipalign;
    public Pane pathColorApksigner;

    private String currentKeyStore;
    private KeyProperty currentAlias;
    private Paths paths;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadKeyStores();
        initListKeyStore();
        checkPaths();
    }

    public void genKeyTest(ActionEvent actionEvent) {
        Commands.addKeyStoreOrAlias("123456", "simpleName", "key0", "Max Boyar", "Moscow", "RU");
    }

    public void genAliasTest(ActionEvent actionEvent) {
        Commands.addKeyStoreOrAlias("123456", "simpleName", "key1", "Max Boyar", "Moscow", "RU");
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
                Commands.addKeyStoreOrAlias(
                        keyProperty.getPassword(),
                        keyProperty.getKeyStoreName(),
                        keyProperty.getAliasName(),
                        keyProperty.getFirstAndLastName(),
                        keyProperty.getCity(),
                        keyProperty.getCountryCode()
                );
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

        Commands.addKeyStoreOrAlias(
                currentAlias.getPassword(),
                currentAlias.getKeyStoreName().substring(0, currentAlias.getKeyStoreName().length() - 4),
                aliasName,
                currentAlias.getFirstAndLastName(),
                currentAlias.getCity(),
                currentAlias.getCountryCode()
        );

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

    private void showAlertEmptyAliasName() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error alert");
        alert.setHeaderText("Not valid alias name");
        alert.setContentText("Pls enter valid alias name");

        alert.showAndWait();
    }

    public void onBuildClick(ActionEvent actionEvent) {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(((Node) actionEvent.getTarget()).getScene().getWindow());

        if (selectedDirectory != null) {
            System.out.println(selectedDirectory.getAbsolutePath());

            HashMap<String, String> pathPreferences = new HashMap<>();
            pathPreferences.put(KEY_KEYTOOL, selectedDirectory.getAbsolutePath());

            String path = pathPreferences.get(KEY_KEYTOOL);


        }
    }

    private void checkPaths() {

        File file = new File("paths.fox");
        if (file.exists()) {

            FileInputStream fin = null;
            ObjectInputStream oin = null;

            try {
                fin = new FileInputStream(file);
                oin = new ObjectInputStream(fin);
                paths = (Paths) oin.readObject();

                Commands.checkPath(paths.getKeytool())
                        .doOnSuccess(aBoolean -> setPathColor(aBoolean, KEY_KEYTOOL))
                        .subscribe();

                Commands.checkPath(paths.getZipalign())
                        .doOnSuccess(aBoolean -> setPathColor(aBoolean, KEY_ZIPALIGN))
                        .subscribe();

                Commands.checkPath(paths.getGradlew())
                        .doOnSuccess(aBoolean -> setPathColor(aBoolean, KEY_GRADLEW))
                        .subscribe();

                Commands.checkPath(paths.getApksigner())
                        .doOnSuccess(aBoolean -> setPathColor(aBoolean, KEY_APKSIGNER))
                        .subscribe();

            } catch (Exception e) {
                e.printStackTrace();
                if (paths == null) paths = new Paths();
            } finally {
                if (fin != null) {
                    try {
                        fin.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (oin != null) {
                    try {
                        oin.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void setPathColor(boolean isGreen, String id) {
        switch (id){
            case KEY_KEYTOOL:
                if (isGreen) pathColorKeytool.setStyle("-fx-background-color: " + COLOR_GREEN);
                else pathColorKeytool.setStyle("-fx-background-color: " + COLOR_RED);
                break;
            case KEY_ZIPALIGN:
                if (isGreen) pathColorZipalign.setStyle("-fx-background-color: " + COLOR_GREEN);
                else pathColorZipalign.setStyle("-fx-background-color: " + COLOR_RED);
                break;
            case KEY_GRADLEW:
                if (isGreen) pathColorGradlew.setStyle("-fx-background-color: " + COLOR_GREEN);
                else pathColorGradlew.setStyle("-fx-background-color: " + COLOR_RED);
                break;
            case KEY_APKSIGNER:
                if (isGreen) pathColorApksigner.setStyle("-fx-background-color: " + COLOR_GREEN);
                else pathColorApksigner.setStyle("-fx-background-color: " + COLOR_RED);
                break;
        }
    }

    public void onKeytoolChooserClick(ActionEvent actionEvent) {
        String path = showFileChooser(actionEvent);
        if (!path.isEmpty()){
            paths.setKeytool(path);
        }
    }

    public void onGradlewChooserClick(ActionEvent actionEvent) {
        String path = showFileChooser(actionEvent);
        if (!path.isEmpty()){
            paths.setGradlew(path);
        }
    }

    public void onApkSignerChooserClick(ActionEvent actionEvent) {
        String path = showFileChooser(actionEvent);
        if (!path.isEmpty()){
            paths.setApksigner(path);
        }
    }

    public void onZipailignChooserClick(ActionEvent actionEvent) {
        String path = showFileChooser(actionEvent);
        if (!path.isEmpty()){

            Commands.checkPath(path);
            paths.setZipalign(path);
        }

    }

    private String showFileChooser(ActionEvent actionEvent) {

        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(((Node) actionEvent.getTarget()).getScene().getWindow());

        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        }

        return "";
    }
}
