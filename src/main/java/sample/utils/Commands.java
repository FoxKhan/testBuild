package sample.utils;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import sample.exceptions.NotValidPasswordException;
import sample.model.Paths;
import sample.model.pojo.KeyProperty;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static sample.ProConstants.*;
import static sample.ProConstants.COLOR_RED;
import static sample.ProConstants.C_BACKGROUND_COLOR;

public class Commands {

    public static Single<KeyProperty> getKeyStoreProperties(String keyStoreName, String aliasName, String password) {

        String command = "keytool -v " +
                " -keystore " + PATH_TO_PRO + "/" + STORE_FOLDER + keyStoreName +
                " -storepass " + password +
                " -list -alias " + aliasName;

        return cmd(command)
                .map(s -> {
                    String[] lines = s.split("\n");
                    KeyProperty keyProperty = new KeyProperty();

                    for (String line : lines) {
                        if (line.contains("Owner")) {

                            keyProperty.setKeyStoreName(keyStoreName);
                            keyProperty.setKeyName(aliasName);
                            keyProperty.setPassword(password);

                            String[] properties = line.split(",");

                            for (String prop : properties) {
                                if (prop.contains("CN=")) {
                                    keyProperty.setFirstAndLastName(prop.split("=")[1]);
                                } else if (prop.contains("L=")) {
                                    keyProperty.setCity(prop.split("=")[1]);
                                } else if (prop.contains("C=")) {
                                    keyProperty.setCountryCode(prop.split("=")[1]);
                                }
                            }

//                            String firstLastName = properties[0];
//                            keyProperty.setFirstAndLastName(firstLastName.split("=")[1]);
//
//                            String city = properties[4];
//                            keyProperty.setCity(city.split("=")[1]);
//
//                            String countryCode = properties[5];
//                            keyProperty.setCountryCode(countryCode.split("=")[1]);

                            break;
                        }
                    }
                    return keyProperty;
                });


    }
    public static void addKeyStoreOrAlias(KeyProperty keyProperty, String aliasName){
        addKeyStoreOrAlias(keyProperty.getPassword(),
                keyProperty.getKeyStoreName().replace(".jks", ""),
                aliasName,
                keyProperty.getFirstAndLastName(),
                keyProperty.getCity(),
                keyProperty.getCountryCode());
    }

    public static void addKeyStore(KeyProperty keyProperty){
        addKeyStoreOrAlias(keyProperty.getPassword(),
                keyProperty.getKeyStoreName(),
                keyProperty.getAliasName(),
                keyProperty.getFirstAndLastName(),
                keyProperty.getCity(),
                keyProperty.getCountryCode());
    }

    private static void addKeyStoreOrAlias(
            String password,
            String keyStoreName,
            String aliasName,
            String firstLastName,
            String city,
            String countryCode) {

        File keyStoreFolder = new File("keyStores");
        if (!keyStoreFolder.isDirectory()) {
            if (!keyStoreFolder.mkdir()) return;
        }

        String command = "keytool -genkeypair -keystore " + PATH_TO_PRO + "/" + STORE_FOLDER + keyStoreName + ".jks"
                + " -validity 10000 -keyalg RSA -keysize 2048"
                + " -alias " + aliasName
                + " -storepass " + password
                + " -keypass " + password

                + " -dname \""
                + "CN=" + firstLastName
                + ", L=" + city
                + ", C=" + countryCode
                + "\"";

        cmd(command).subscribe();
    }

    public static Single<ArrayList<String>> getAliasList(String jks, String password) {

        String command = "keytool -keystore "
                + STORE_FOLDER + jks
                + " -storepass " + password
                + " -list";

        return cmd(command)
                .map(s -> {
                    String[] out = s.split("\n");
                    ArrayList<String> result = new ArrayList<>();
                    for (String line : out) {
                        if (line.contains("PrivateKeyEntry")) {
                            result.add(line.split(",")[0]);
                        } else isValidPassword(line);
                    }
                    return result;
                });
    }

    public static Single<ArrayList<String>> getSHAList(String jks, String password) {

        String command = "keytool -keystore "
                + STORE_FOLDER + jks
                + " -storepass " + password
                + " -list";

        return cmd(command)
                .map(s -> {
                    String[] out = s.split("\n");
                    ArrayList<String> result = new ArrayList<>();
                    for (String line : out) {
                        if (line.contains("PrivateKeyEntry")) {
                            result.add(line.split(",")[0]);
                        } else if (line.contains("SHA1")) {
                            result.add(line.substring(line.indexOf(":") + 2));
                            result.add("");
                        } else isValidPassword(line);
                    }
                    return result;
                });
    }


    private static void isValidPassword(String line) throws NotValidPasswordException {
        if (line.contains("Usage error") || line.contains("password was incorrect")) {
            throw new NotValidPasswordException();
        }
    }

    private static Single<String> cmd(String inCommand) {
        return Single.fromCallable(() -> {

            System.out.println("start command");

            ArrayList<String> commands = new ArrayList<>();
            commands.add("cmd.exe");
            commands.add("/c");
            commands.add(inCommand);

            String[] stringsCommands = commands.toArray(new String[0]);

            System.out.println(inCommand);
            ProcessBuilder builder = new ProcessBuilder(stringsCommands);
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                output.append(line).append("\n");
                System.out.println(line);
            }
            System.out.println("end command");
            return output.toString();
        });
    }
}
