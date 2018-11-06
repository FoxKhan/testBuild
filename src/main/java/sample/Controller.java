package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.exceptions.NotValidPasswordException;
import sample.model.pojo.KeyProperty;
import sample.utils.Commands;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static sample.ProConstants.STORE_FOLDER;

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

    private String currentKeyStore;
    private KeyProperty currentAlias;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadKeyStores();
        initListKeyStore();
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
}
