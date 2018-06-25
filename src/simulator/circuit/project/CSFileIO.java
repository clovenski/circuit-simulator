package simulator.circuit.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CSFileIO {
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String SAVE_DIR_NAME = "cs-saves";

    public static void writeSaveFile(CSGraph circuit, String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileOutputStream fos;
        ObjectOutputStream oos;

        File dir = new File(SAVE_DIR_NAME);
        if(!dir.exists() || !dir.isDirectory())
            dir.mkdir();

        fos = new FileOutputStream(dir.getName() + FILE_SEPARATOR + fileName);
        oos = new ObjectOutputStream(fos);

        oos.writeObject(circuit);
        oos.close();
    }

    public static CSGraph readSaveFile(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException, ClassCastException {
        FileInputStream fis;
        ObjectInputStream ois;
        CSGraph circuit;

        File file = new File(SAVE_DIR_NAME + FILE_SEPARATOR + fileName);

        fis = new FileInputStream(file);
        ois = new ObjectInputStream(fis);

        circuit = (CSGraph)ois.readObject();

        ois.close();

        return circuit;
    }

    public static File getSaveDir() {
        return new File(SAVE_DIR_NAME);
    }
}