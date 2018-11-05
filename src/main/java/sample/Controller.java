package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.model.FolderController;
import sample.model.pojo.KeyProperty;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {


  @FXML
  private ListView<KeyProperty> keyStoreList;


  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
  }

  public void onNewAction(ActionEvent actionEvent) throws IOException {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    File file = directoryChooser.showDialog(null);
    File fileSetting = new File(file.getPath() + "settings");
    Writer writer = new FileWriter(fileSetting);
    writer.write("12344");
    writer.close();
  }

  public void del(ActionEvent actionEvent) {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    File file = directoryChooser.showDialog(null);
    FolderController.delete(file);
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
