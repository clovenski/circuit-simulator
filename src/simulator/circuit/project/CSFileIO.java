package simulator.circuit.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CSFileIO {
    private static final String fileSeparator = System.getProperty("file.separator");
    private static final String saveDirName = "cs-saves";

    public static void writeSaveFile(CSGraph circuit, String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileOutputStream fos;
        ObjectOutputStream oos;

        File dir = new File(saveDirName);
        if(!dir.exists() || !dir.isDirectory())
            dir.mkdir();

        fos = new FileOutputStream(dir.getName() + fileSeparator + fileName);
        oos = new ObjectOutputStream(fos);

        oos.writeObject(circuit);
        oos.close();
        fos.close();        
    }

    public static CSGraph readSaveFile(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException, ClassCastException {
        FileInputStream fis;
        ObjectInputStream ois;
        CSGraph circuit;

        File file = new File(saveDirName + fileSeparator + fileName);

        fis = new FileInputStream(file);
        ois = new ObjectInputStream(fis);

        circuit = (CSGraph)ois.readObject();

        ois.close();
        fis.close();

        return circuit;
    }

    public static File getSaveDir() {
        return new File(saveDirName);
    }
}