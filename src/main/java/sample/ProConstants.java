package sample;

public class ProConstants {

    public static final String PATH_TO_PRO = System.getProperty("user.dir");

    public static final String STORE_FOLDER = "keyStores/";

    public static final String COLOR_GREEN = "#008000;";
    public static final String COLOR_RED = "#742222;";

    public static final String C_BACKGROUND_COLOR = "-fx-background-color: ";

    public static final String KEY_KEYTOOL = "keytool";
    public static final String KEY_ZIPALIGN = "zipalign";
    public static final String KEY_GRADLEW = "gradlew";
    public static final String KEY_APKSIGNER = "apksigner";

    public final static String ZIPA = "zipalign -v -p 4 c:/users/artk5/documents/testapplication/app/build/outputs/apk/release/app-release-unsigned.apk c:/app/app-release-unsigned-aligned.apk";
    public final static String PATH_TO_ZIPA = "C:\\Users\\nika-\\AppData\\Local\\Android\\Sdk\\build-tools\\27.0.3\\";
}
