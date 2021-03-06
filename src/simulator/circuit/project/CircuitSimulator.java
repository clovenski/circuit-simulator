package simulator.circuit.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 * Entry point for the program.
 * <p>
 * This class contains the main method to start the program.
 * Pass the file name as an argument to start the program with
 * the circuit already loaded.
 * 
 * @author Joel Tengco
 */
public class CircuitSimulator {
    /**
     * Source of input from the user.
     */
    private Scanner inputSource;
    /**
     * Holds the name of the circuit currently being worked on.
     */
    private String circuitName;
    /**
     * Circuit engine to interface with the circuit.
     */
    private CSEngine engine;
    /**
     * Circuit editor to handle editing the circuit.
     */
    private CircuitEditor editor;
    /**
     * Circuit tester to handle testing the circuit.
     */
    private CircuitTester tester;
    /**
     * True if the circuit currently being worked on is a new circuit, false otherwise.
     */
    private boolean circuitIsNew;
    /**
     * True if the user has previously opted to edit the circuit, false otherwise.
     * <p>
     * Note that this will be set to true if the user has simply entered the editor.
     * This boolean having a true value does not imply that the circuit has actually
     * been altered.
     */
    private boolean circuitEdited;

    /**
     * Constructs an instance of the program; ready to be started.
     * <p>
     * A new, empty circuit will be created to work on.
     */
    public CircuitSimulator() {
        engine = new CSEngine();
        circuitName = "new-circuit";
        circuitIsNew = true;
        circuitEdited = false;
        inputSource = new Scanner(System.in);
        editor = new CircuitEditor(engine, inputSource);
        tester = new CircuitTester(engine, inputSource);
    }

    /**
     * Constructs an instance of the program; ready to be started.
     * <p>
     * If a circuit saved as a file with the specified file name exists,
     * then that circuit is loaded and will be the circuit to be worked on.
     * Also, if any error occurs while attempting to load the circuit, then
     * error messages are printed out and a new circuit will be created and
     * worked on instead.
     * 
     * @param fileName the name of the file that contains the saved circuit
     */
    public CircuitSimulator(String fileName) {
        CSGraph circuit;

        try {
            circuit = CSFileIO.readSaveFile(fileName);
            engine = new CSEngine(circuit);
            circuitName = fileName;
            circuitIsNew = false;
            System.out.println("Successfully loaded circuit: " + fileName + ".");
        } catch(FileNotFoundException fnfe) {
            System.err.println("Error: could not find saved circuit named " + fileName + ".");
            System.err.println("Creating a new circuit . . .");
            engine = new CSEngine();
            circuitName = "new-circuit";
            circuitIsNew = true;
        } catch(ClassCastException cce) {
            System.err.println("Error: could not recognize this file");
            System.err.println("Creating a new circuit . . .");
            engine = new CSEngine();
            circuitName = "new-circuit";
            circuitIsNew = true;
        } catch(Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            System.err.println("Creating a new circuit . . .");
            engine = new CSEngine();
            circuitName = "new-circuit";
            circuitIsNew = true;
        }

        System.out.println();

        circuitEdited = false;
        inputSource = new Scanner(System.in);
        editor = new CircuitEditor(engine, inputSource);
        tester = new CircuitTester(engine, inputSource);
    }

    /**
     * Starts Circuit Simulator.
     * <p>
     * This starts the program and does not return until the user
     * opts to exit the program through the main menu printed
     * directly from this method.
     * <p>
     * The main menu prints a welcome title, a list of saved circuits
     * with their last modified property (if any saved circuits exist),
     * and a menu for the user to choose from.
     */
    public void start() {
        ArrayList<String> options = new ArrayList<String>();
        int userInput;
        File[] files;
        
        // add options to main menu
        options.add("Check status of this circuit");
        options.add("Edit this circuit");
        options.add("Test this circuit");
        options.add("Save");
        options.add("Save as");
        options.add("Load");
        options.add("New");
        options.add("Exit");

        // print welcome message
        System.out.println("//////// Welcome to Circuit Simulator.");
        System.out.println("\\\\\\\\\\\\\\\\-----------------------------");
        
        do {
            files = CSFileIO.getSaveDir().listFiles();
            if(files != null) {
                System.out.printf("\n%-20s %s\n", "Saved Circuits", "Last Modified");
                System.out.println("-------------------- ------------------------------");

                for(File file : files)
                    System.out.printf("%-20s %s\n", file.getName(), new Date(file.lastModified()).toString());
                System.out.println("-------------------- ------------------------------");
                System.out.println();
            }
            
            System.out.println("Selected circuit: " + circuitName + "\n");

            System.out.println("CS > Main Menu");
            CSUserInterface.displayOptions(options);
            userInput = CSUserInterface.getUserOptInput(options, inputSource);

            switch(userInput) {
                case 1:     printCircuitInfo();
                            break;
                case 2:     editor.edit(circuitName);
                            circuitEdited = true;
                            break;
                case 3:     tester.test(circuitName);
                            break;
                case 4:     saveCircuit();
                            break;
                case 5:     saveCircuitAs();
                            break;
                case 6:     loadCircuit();
                            break;
                case 7:     newCircuit();
                            break;
                case 8:     exit();
                            return; // exit the program
            }
        } while(true);
    }

    /**
     * Prints the general information about the circuit.
     */
    private void printCircuitInfo() {
        int[] circuitStatus = engine.getCircuitStatus();
        String circuitType = engine.isCircuitSequential() ? "Sequential" : "Combinational";
        int index = 0;
        // circuit is ready to test if a sequence exists in the circuit
        boolean testReady = circuitStatus[1] > 0;

        // print general info of circuit
        System.out.printf("%23s: %s\n", "Circuit name", circuitName);
        System.out.printf("%23s: %d\n", "Number of nodes", engine.getCircuitSize());
        System.out.printf("%23s: %s\n", "Ready to test", (testReady ? "Yes" : "No"));
        System.out.printf("%23s: %s\n", "Circuit type", circuitType);
        // print number of nodes for each type of node
        System.out.printf("%-15s : %d\n", "Input Nodes", circuitStatus[index++]);
        System.out.printf("%-15s : %d\n", "Sequences", circuitStatus[index++]);
        System.out.printf("%-15s : %d\n", "Output Nodes", circuitStatus[index++]);
        System.out.printf("%-15s : %d\n", "Flip Flops", circuitStatus[index++]);
        System.out.printf("%-15s : %d\n", "Gates", circuitStatus[index++]);
        System.out.printf("%-15s : %d\n", "Inverters", circuitStatus[index++]);
        System.out.printf("%-15s : %d\n", "Connections", circuitStatus[index++]);
    }

    /**
     * Saves the current circuit.
     * <p>
     * If the circuit is new then the user will be prompted for the file name
     * of the new save file, otherwise the state of the circuit is saved to
     * its corresponding save file.
     */
    private void saveCircuit() {
        if(circuitIsNew) {
            saveCircuitAs();
            return;
        }

        try {
            engine.saveCircuit(circuitName);
            circuitEdited = false;
            System.out.println("\nSuccessfully saved the circuit");
        } catch(Exception e) {
            System.err.println("\nUnknown error: " + e.getMessage());
        }
    }

    /**
     * Saves the circuit with a user-specified file name.
     * <p>
     * Prompts the user for a file name and if it does not already
     * exist in the saves folder, then it is properly saved. Otherwise
     * the user is asked to confirm they want to overwrite the old file
     * or cancel.
     */
    private void saveCircuitAs() {
        File[] files = CSFileIO.getSaveDir().listFiles();
        String fileName;
        int userInput;
        boolean overwriting = false;

        System.out.println("\nCS > Main Menu > Save As");

        fileName = CSUserInterface.getUserStringInput("Save as: ", inputSource);

        if(files != null)
            for(int i = 0; i < files.length; i++) 
                if(files[i].getName().equals(fileName)) {
                    overwriting = true;
                    break;
                }

        if(overwriting) {
            System.out.println(fileName + " already exists. Would you like to overwrite it?");
            userInput = CSUserInterface.getUserIntInput("Enter 1 to overwrite, or 2 to cancel: ", 2, inputSource);

            if(userInput == 1) {
                try {
                    engine.saveCircuit(fileName);
                    circuitEdited = false;
                    circuitName = fileName;
                    circuitIsNew = false;
                    System.out.println("\nSuccessfully saved the circuit");
                } catch(Exception e) {
                    System.err.println("\nUnknown error: " + e.getMessage());
                }
            }

        } else { // fileName does not exist in save folder, okay to save
            try {
                engine.saveCircuit(fileName);
                circuitEdited = false;
                circuitName = fileName;
                circuitIsNew = false;
                System.out.println("\nSuccessfully saved the circuit");
            } catch(Exception e) {
                System.err.println("\nUnknown error: " + e.getMessage());
            }
        }
    }

    /**
     * Loads a circuit from a save file.
     * <p>
     * If save files exist, the list of circuits to load is printed
     * and the user can choose to load a circuit or cancel. A warning
     * is also given mentioning that any unsaved changes to the current
     * circuit will be lost.
     */
    private void loadCircuit() {
        int userInput;
        String fileName;
        File[] files = CSFileIO.getSaveDir().listFiles();

        if(files == null) {
            System.err.println("\nNo saves are available to load from.");
            return;
        }

        System.out.println("\nCS > Main Menu > Load");

        System.out.println("Warning: any unsaved changes will be lost.\n" +
                            "If you want to save any changes, go back and save first.");

        ArrayList<String> options = new ArrayList<String>();
        for(File file : files)
            options.add(file.getName());

        options.add("Return to Main Menu");

        System.out.println("Choose a file to load from:");
        CSUserInterface.displayOptions(options);
        userInput = CSUserInterface.getUserOptInput(options, inputSource);

        if(userInput == options.size()) // last option is to return/cancel
            return;

        fileName = options.get(userInput - 1);
        try {
            engine.loadCircuit(fileName);
            circuitName = fileName;
            circuitIsNew = false;
            circuitEdited = false;
            System.out.println("\nSuccessfully loaded file " + fileName);
        } catch(ClassCastException cce) {
            System.err.println("\nCannot read from the file " + fileName);
        } catch(Exception e) {
            System.err.println("\nUnknown error: " + e.getMessage());
        }
    }

    /**
     * Creates a new circuit for the program to work on.
     * <p>
     * If the user has possibly edited the current circuit, then they
     * are asked to either save the current circuit or continue without saving.
     */
    private void newCircuit() {
        int userInput;

        if(circuitEdited) {
            System.out.println("You have opted to edit your circuit and haven't saved since then.\n" +
                                "Any unsaved changes will be lost.\n");

            userInput = CSUserInterface.getUserIntInput("Enter 1 to save, or 2 to continue without saving: ", 2, inputSource);
            if(userInput == 1)
                saveCircuit();
        }

        engine.newCircuit();
        circuitName = "new-circuit";
        circuitIsNew = true;
        circuitEdited = false;

        System.out.println("\nSuccessfully created new circuit");
    }

    /**
     * Properly exits the program.
     * <p>
     * If the user has possible edited the current circuit, then they
     * are asked if they want to save before exiting.
     */
    private void exit() {
        int userInput;

        if(circuitEdited) {
            System.out.println("You have opted to edit your circuit and haven't saved since then.\n" +
                                "Any unsaved changes will be lost.\n");

            userInput = CSUserInterface.getUserIntInput("Enter 1 to save before exiting, or 2 to exit without saving: ", 2, inputSource);
            if(userInput == 1)
                saveCircuit();
        }

        inputSource.close();
    }

    /**
     * Main method for the program.
     * <p>
     * If arguments exist, then the program is started with the first
     * argument being the name of the file to load from. Otherwise, a new
     * circuit is created for the program to work on.
     * 
     * @param args first argument being the name of the file to load a circuit from
     */
    public static void main(String[] args) {
        CircuitSimulator program;

        if(args.length > 0)
            program = new CircuitSimulator(args[0]);
        else
            program = new CircuitSimulator();

        program.start();
    }
}