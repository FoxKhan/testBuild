package sample.utils;

import io.reactivex.Single;
import sample.ProConstants;
import sample.exceptions.NotValidPasswordException;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static sample.ProConstants.STORE_FOLDER;

public class Commands {

    public static void addKeyStore(
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

        String command = "keytool -genkeypair -keystore " + ProConstants.PATH_TO_PRO + "/" + STORE_FOLDER + keyStoreName + ".jks"
                + " -alias " + aliasName
                + " -storepass " + password
                + " -keypass " + password
                + " -dname \""
                + "CN=" + firstLastName
                + ", OU=" + ""
                + ", O=" + ""
                + ", L=" + ""
                + ", S=" + city
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

            System.out.println("jj");
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
