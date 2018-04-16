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

    public static void writeSaveFile(CSEngine engine, String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileOutputStream fos;
        ObjectOutputStream oos;

        File dir = new File(saveDirName);
        if(!dir.exists() || !dir.isDirectory())
            dir.mkdir();

        fos = new FileOutputStream(dir.getName() + fileSeparator + fileName);
        oos = new ObjectOutputStream(fos);

        oos.writeObject(engine);
        oos.close();
        fos.close();        
    }

    public static CSEngine readSaveFile(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fis;
        ObjectInputStream ois;
        CSEngine circuitEngine;

        File file = new File(saveDirName + fileSeparator + fileName);

        fis = new FileInputStream(file);
        ois = new ObjectInputStream(fis);

        circuitEngine = (CSEngine)ois.readObject();

        ois.close();
        fis.close();

        return circuitEngine;
    }

    public static File getSaveDir() {
        return new File(saveDirName);
    }
}