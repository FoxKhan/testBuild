package sample.model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class PackageChanger {
    private File fileFolder;
    String oldPackageName;
    String newPackageName;

    public PackageChanger(File fileFolder, String oldPackageName, String newPackageName) {
        this.fileFolder = fileFolder;
        this.oldPackageName = oldPackageName;
        this.newPackageName = newPackageName;
    }

    public void searchFile(File file) throws IOException {
        if (!file.exists())
            return;
        if (file.isDirectory()) {
            for (File f : file.listFiles())
                searchFile(f);
            String[] str = oldPackageName.split("\\.");
            for (int i = 0; i< str.length; i++){
                if (file.getName().equals(str[i])) {
                    file.renameTo(new File(file.getParent() + "\\" + newPackageName.split("\\.")[i]));
                    System.out.println(file.getName() + "      " + str[i]);
                    break;
                }
            }

        } else {
            rewriteFile(file);
        }
    }

    public void rewriteFile(File file) throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        Path path = Paths.get(file.toURI());
        Files.write(path,
                new String(Files.readAllBytes(path), charset).replace(oldPackageName, newPackageName)
                        .getBytes(charset));
    }


}
