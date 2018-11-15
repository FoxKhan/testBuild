package sample.utils;

import sample.model.Paths;

import java.io.*;

public class FileController {
    public static void delete(File file) {
        if (!file.exists())
            return;
        if (file.isDirectory()) {
            for (File f : file.listFiles())
                delete(f);
            file.delete();
        } else {
            file.delete();
        }
    }

    public static <T extends Serializable> T openSerializable(File file) {

        Serializable result;

        FileInputStream fin = null;
        ObjectInputStream oin = null;

        try {
            fin = new FileInputStream(file);
            oin = new ObjectInputStream(fin);
            result = (Serializable) oin.readObject();

        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
//                    e.printStackTrace();
                }
            }
            if (oin != null) {
                try {
                    oin.close();
                } catch (IOException e) {
//                    e.printStackTrace();
                }
            }
        }
        return (T) result;
    }

    public static void save(Paths paths, String filePath) {
        FileOutputStream fout = null;
        ObjectOutputStream oos = null;

        File file = new File(filePath);

        try {
            fout = new FileOutputStream(file, false);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(paths);

        } catch (IOException e) {
            //
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }
}
