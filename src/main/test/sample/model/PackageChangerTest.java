package sample.model;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class PackageChangerTest {

    @Test
    public void rewriteFile() throws IOException {
        File file = new File("d:\\1");
        PackageChanger packageChanger =
                new PackageChanger(file,
                        "iceblood.computercomponents","com.bla");
        packageChanger.rewriteFile(file);
    }

    @Test
    public void searchFile() throws IOException {
        File file = new File("d:\\1");
        PackageChanger packageChanger =
                new PackageChanger(file,
                        "iceblood.computercomponents","com.bla");
        packageChanger.searchFile(file);
    }
}