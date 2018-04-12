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

    public CSFileIO() {

    }

    public static void writeSaveFile(CSGraph circuitGraph, String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileOutputStream fos;
        ObjectOutputStream oos;

        File dir = new File(saveDirName);
        if(!dir.exists() || !dir.isDirectory())
            dir.mkdir();

        fos = new FileOutputStream(dir.getName() + fileSeparator + fileName);
        oos = new ObjectOutputStream(fos);

        oos.writeObject(circuitGraph);
        oos.close();
        fos.close();        
    }

    public static CSGraph readSaveFile(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fis;
        ObjectInputStream ois;
        CSGraph circuitGraph;

        File file = new File(saveDirName + fileSeparator + fileName);

        fis = new FileInputStream(file);
        ois = new ObjectInputStream(fis);

        circuitGraph = (CSGraph)ois.readObject();

        ois.close();
        fis.close();

        return circuitGraph;
    }

    public static File getSaveDir() {
        return new File(saveDirName);
    }
}