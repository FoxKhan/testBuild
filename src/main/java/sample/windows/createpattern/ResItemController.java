package sample.windows.createpattern;

import javafx.scene.control.TextField;

public class ResItemController {
    public TextField txtPropName;
    public TextField txtBefore;

    public void setFields(String propName, String before){

        txtPropName.setText(propName);
        txtPropName.editableProperty().set(false);
        txtBefore.setText(before);
        txtBefore.editableProperty().set(false);
    }
}
