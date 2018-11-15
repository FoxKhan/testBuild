package sample.windows.createpattern;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import sample.build.gradle.GradleManager;
import sample.build.model.FGradleModel;
import sample.build.model.ResModel;
import sample.build.xml.XmlManager;
import sample.common.FileUtilsKt;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ResourceBundle;

public class CreatePatternBuildController implements Initializable {
    private final String XML_RES = "xml res";
    private final String XML_VECTOR = "xml vector";
    private final String PNG = "png";
    private final String XML_VECTOR_FOLDER = "xml vector folder";
    private final String GRADLE = "gradle file";


    private final String[] choiceArray = {XML_RES, XML_VECTOR, PNG, XML_VECTOR_FOLDER, GRADLE};


    public Label labelProName;
    public ListView<ResModel> listProRes;
    public ChoiceBox<String> vChoiceRes;
    public AnchorPane root;
    public TreeView<String> treeView;
    public ListView<String> resItemsList;
    private String pathToPro;
    private TreeItem<String> currentTreeItem;
    private ArrayList<ResModel> resources = new ArrayList<>();
    private ResModel currentProRes;

    private ObservableList<String> resInners = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initListProRes();
        vChoiceRes.getItems().addAll(choiceArray);
        resItemsList.setItems(resInners);

    }

    private void initListProRes() {
        listProRes.getSelectionModel().selectedItemProperty().addListener(((observableValue, s, t1) -> {

            currentProRes = t1;
            String fullPath = (pathToPro + currentProRes.getPath());

            switch (currentProRes.getType()) {
                case XML_RES:
                    XmlManager xmlManager = new XmlManager(pathToPro + currentProRes.getPath());
                    HashMap<String, String> list = xmlManager.getMapListValue();

                    resInners.clear();
                    list.forEach((o, o2) -> resInners.add(o + "    " + o2));

                    break;
                case XML_VECTOR:

                    resInners.clear();
                    resInners.add(currentProRes.getPath());

                    break;
                case PNG:
                    resInners.clear();
                    resInners.add(currentProRes.getPath());

                    break;
                case GRADLE:
                    resInners.clear();
                    GradleManager gradleManager = new GradleManager(fullPath);
                    FGradleModel gradleModel = gradleManager.getFGradleProp();

                    gradleModel.getExt().forEach((s1, extModel) ->
                            resInners.add(s1 + "    " + extModel.getValue()));
                    gradleModel.getVersion().forEach((version, versionModel) ->
                            resInners.add(version.get() + "    " + versionModel.getValue()));

                    break;
                case XML_VECTOR_FOLDER:

                    resInners.clear();

                    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(fullPath))) {
                        for (Path path : directoryStream) {
                            String name = path.getFileName().toString();
                            resInners.add(name);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }));
    }

    public void onChooseProBtnClick() {

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

                treeView.getSelectionModel()
                        .selectedItemProperty()
                        .addListener((ChangeListener<? super TreeItem<String>>) (observable, oldValue, newValue)
                                -> currentTreeItem = newValue);

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


    public void onAddResClick() {

        if (currentTreeItem == null) return;
        String relativePath = getRelativePath(currentTreeItem);

        String resType = vChoiceRes.getValue();
        if (resType.isEmpty()) return;

        String fullPath = pathToPro + relativePath;

        for (ResModel r : resources) if (r.getPath().equals(relativePath)) return;

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
            case GRADLE:
                if (file.isDirectory()) break;
                FileUtilsKt.checkFile(fullPath, "gradle");
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

    public void onDeleteResClick() {
        if (currentProRes == null) return;
        listProRes.getItems().remove(currentProRes);
        resources.remove(currentProRes);
    }
}
