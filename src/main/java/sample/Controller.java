package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    @FXML
    private ListView keyStoreList;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        keyStoreList.getItems().add("hoohoh");
    }
}
