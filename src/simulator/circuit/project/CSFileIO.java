package simulator.circuit.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class to handle file input and output.
 * <p>
 * This class handles saving a circuit to a file on disk as well as
 * loading a circuit from a file on disk. The folder in which circuits
 * are saved to and loaded from is named "cs-saves".
 * 
 * @author Joel Tengco
 */
public class CSFileIO {
    /**
     * File separator of the user's working environment.
     */
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    /**
     * Save directory name where circuit files are saved to and loaded from.
     */
    private static final String SAVE_DIR_NAME = "cs-saves";

    /**
     * Saves a given circuit as a file on disk with the given file name.
     * 
     * @param circuit the circuit to be saved as a file
     * @param fileName the file name of the saved circuit
     * @throws FileNotFoundException if something went wrong in setting up saving the circuit
     * @throws IOException if an error occurred when attempting to save the circuit
     */
    public static void writeSaveFile(CSGraph circuit, String fileName) throws FileNotFoundException, IOException {
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

    /**
     * Loads a circuit from a file with the given file name.
     * 
     * @param fileName the name of the file to load a circuit with
     * @return the circuit loaded by the specified file
     * @throws FileNotFoundException if something went wrong in locating the file
     * @throws IOException if an error occurred when attemping to read the file
     * @throws ClassNotFoundException if a circuit could not be read from the file
     * @throws ClassCastException if the object read from the file is not a circuit
     */
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

    /**
     * Gets a {@code File} object corresponding to the save folder.
     * 
     * @return a {@code File} object that represents the save folder
     */
    public static File getSaveDir() {
        return new File(SAVE_DIR_NAME);
    }
}