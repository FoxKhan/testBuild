package sample;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import sample.build.model.ResModel;
import sample.common.FileUtilsKt;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;

public class CreateBuildController {
    private final String XML_RES = "xml res";
    private final String XML_VECTOR = "xml vector";
    private final String PNG = "png";
    private final String MANIFEST = "manifest";
    private final String XML_VECTOR_FOLDER = "xml vector folder";
    private final String[] choiceArray = {XML_RES, XML_VECTOR, PNG, MANIFEST, XML_VECTOR_FOLDER};
    public Label labelProName;
    public ListView<ResModel> listProRes;
    public ChoiceBox<String> vChoiceRes;
    public AnchorPane root;
    public TreeView<String> treeView;
    private String pathToPro;
    private TreeItem<String> currentTreeItem;
    private ArrayList<ResModel> resources = new ArrayList<>();

    void init() {
        vChoiceRes.getItems().addAll(choiceArray);
    }

    public void onChooseProBtnClick(ActionEvent actionEvent) {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(root.getScene().getWindow());

        if (null != selectedDirectory) {
            pathToPro = selectedDirectory.getAbsolutePath();
            labelProName.setText(selectedDirectory.getName());

            TreeItem<String> treeItem = new TreeItem<>(selectedDirectory.getName());
            treeItem.setExpanded(true);

            try {
                createTree(treeItem, pathToPro);
                treeItem.getChildren().sort(Comparator.comparing(t -> t.getValue().toLowerCase()));
                treeView.setRoot(treeItem);

                treeView.getSelectionModel().selectedItemProperty().addListener((ChangeListener<? super TreeItem<String>>) (observable, oldValue, newValue) -> {
                    currentTreeItem = newValue;
                });


            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(selectedDirectory.getAbsolutePath());
        }
    }

    private String getRelativePath(TreeItem<String> treeItem) {

        String fullPath;
        StringBuilder sb = new StringBuilder();
        ArrayList<String> sa = new ArrayList<>();
        TreeItem<String> tempValue;

        tempValue = treeItem;

        sa.add(tempValue.getValue());

        while (true) {
            try {
                tempValue = tempValue.getParent();
                sa.add("\\");
                sa.add(tempValue.getValue());
            } catch (Exception e) {
                for (int i = sa.size() - 3; i >= 0; i--) {
                    sb.append(sa.get(i));
                }
                fullPath = sb.toString();
                break;
            }
        }
        return fullPath;
    }

    private void createTree(TreeItem<String> rootItem, String rootPath) throws IOException {

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(rootPath))) {

            for (Path path : directoryStream) {

                String name = path.getFileName().toString();
                TreeItem<String> newItem = new TreeItem<>(name);
                newItem.setExpanded(false);

                rootItem.getChildren().add(newItem);

                if (Files.isDirectory(path)) {
                    createTree(newItem, path.toString());
                }
            }
        }
    }


    public void onAddResClick(ActionEvent actionEvent) {

        if (currentTreeItem == null) return;
        String relativePath = getRelativePath(currentTreeItem);

        String resType = vChoiceRes.getValue();
        if (resType.isEmpty()) return;

        String fullPath = pathToPro + relativePath;

        File file = new File(fullPath);

        ResModel res = new ResModel(currentTreeItem.getValue(), resType, relativePath, null);


        switch (resType) {
            case XML_RES:
                if (file.isDirectory()) break;
                FileUtilsKt.checkFile(fullPath, "xml");
                addResInResList(res);

                break;
            case XML_VECTOR:
                if (file.isDirectory()) break;
                FileUtilsKt.checkFile(fullPath, "xml");
                addResInResList(res);

                break;
            case PNG:
                if (file.isDirectory()) break;
                FileUtilsKt.checkFile(fullPath, "png");
                addResInResList(res);

                break;
            case MANIFEST:
                if (file.isDirectory()) break;
                FileUtilsKt.checkFile(fullPath, "xml");
                addResInResList(res);

                break;
            case XML_VECTOR_FOLDER:
                if (!file.isDirectory()) break;
                addResInResList(res);

                break;
        }


    }

    private void addResInResList(ResModel resModel) {
        resources.add(resModel);
        listProRes.getItems().add(resModel);
    }

    public void onDeleteResClick(ActionEvent actionEvent) {

    }
}
