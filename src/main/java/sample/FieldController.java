package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.model.pojo.KeyProperty;

public class FieldController {
    public TextField keyStoreName;
    public TextField keyName;
    public TextField password;
    public TextField firstAndLastName;
    public TextField city;
    public TextField countryCode;

    private KeyProperty keyProperty;

    public void setKeyProperty(KeyProperty keyProperty) {
        this.keyProperty = keyProperty;
    }

    public void okClick(ActionEvent actionEvent) {
        keyProperty.setKeyStoreName(keyStoreName.getText());
        keyProperty.setKeyName(keyName.getText());
        keyProperty.setPassword(password.getText());
        keyProperty.setFirstAndLastName(firstAndLastName.getText());
        keyProperty.setCountryCode(countryCode.getText());
        keyProperty.setCity(city.getText());

        Stage stage = (Stage)keyName.getScene().getWindow();
        stage.close();
    }
}
