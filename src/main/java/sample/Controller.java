package sample;

import javafx.event.ActionEvent;
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
}
