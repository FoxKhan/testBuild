package sample;

import javafx.scene.control.TextField;

public class ResItemController {

    public TextField txtBefore;
    public TextField txtAfter;
    public TextField txtPropName;

    public void setFields(String propName,String before, String after){

        txtPropName.setText(propName);
        txtPropName.editableProperty().set(false);
        txtBefore.setText(before);
        txtBefore.editableProperty().set(false);
        txtAfter.setText(after);
    }
}
