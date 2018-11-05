package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static sample.ProConstanse.STORE_FOLDER;

public class Controller implements Initializable {

    public ListView<String> keyStoreList;
    public ListView<String> keyList;
    public ListView aboutList;
    public PasswordField masterPass;
    public Button btn1;
    public Button btn2;
    public Button btn3;
    public Button btn4;
    public Button btn5;
    public Button btn6;

    private final static String ZIPA = "zipalign -v -p 4 c:/users/artk5/documents/testapplication/app/build/outputs/apk/release/app-release-unsigned.apk c:/app/app-release-unsigned-aligned.apk";
    private final static String PATH_TO_ZIPA = "C:\\Users\\nika-\\AppData\\Local\\Android\\Sdk\\build-tools\\27.0.3\\";


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadKeyStores();
        initListKeyStore();
    }

    private void initListKeyStore() {

        keyStoreList.getSelectionModel().selectedItemProperty().addListener(((observableValue, s, t1) -> {

            Commands.getAliasList(t1, masterPass.getText())
                    .doOnSuccess(strings -> keyList.getItems().addAll(strings))
                    .subscribe();

            Commands.getSHAList(t1, masterPass.getText())
                    .doOnSuccess(strings -> {
                       strings.toString();//todo
                    })
                    .subscribe();
        }));
    }

    private void loadKeyStores() {

        File keyStoresFolder = new File(STORE_FOLDER);
        if (!keyStoresFolder.isDirectory()) return;

        File[] keyStores = keyStoresFolder.listFiles();

        keyStoreList.getItems().clear();

        if (keyStores != null) {
            for(File keyStore: keyStores){
                keyStoreList.getItems().add(keyStore.getName());
            }
        }
    }


    public void genKey(ActionEvent actionEvent) {
        Commands.addKeyStore("123456", "simpleName","key0", "Max Boyar", "Moscow", "RU");
//        System.out.println("genKey");
//        testCmd();
    }


    public void genAlias(ActionEvent actionEvent) {
        Commands.addKeyStore("123456", "simpleName","key1", "Max Boyar", "Moscow", "RU");
    }

  public void addKey() {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(new File("src/main/resources/add_key.fxml").toURI().toURL());
      HBox page = loader.load();

      Stage dialogStage = new Stage();
      dialogStage.setTitle("Editor");
      dialogStage.initModality(Modality.WINDOW_MODAL);
      dialogStage.initOwner(null);
      Scene scene = new Scene(page);
      dialogStage.setScene(scene);


      KeyProperty keyProperty = new KeyProperty();
      FieldController controller = loader.getController();
      controller.setKeyProperty(keyProperty);
      dialogStage.showAndWait();
      keyStoreList.getItems().add(keyProperty);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void getSHA(ActionEvent actionEvent) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("SHA - 1");
    VBox dialogPaneContent = new VBox();
    TextArea textArea = new TextArea();
    textArea.setText("ТУТ ТВОЙ КОД");

    dialogPaneContent.getChildren().addAll(textArea);
    alert.getDialogPane().setContent(dialogPaneContent);

    alert.showAndWait();
  }
}
