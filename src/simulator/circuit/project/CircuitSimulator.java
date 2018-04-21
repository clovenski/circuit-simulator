package simulator.circuit.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays; // imported for testing, remove later
import java.util.Date;
import java.util.Scanner;

public class CircuitSimulator {
    private Scanner inputSource;
    private String circuitName;
    private CSEngine engine;
    private boolean circuitEdited;

    public CircuitSimulator() {
        engine = new CSEngine();
        circuitName = "new-circuit";
        circuitEdited = false;
        inputSource = new Scanner(System.in);
    }

    public CircuitSimulator(String fileName) {
        try {
            engine.loadCircuit(fileName);
            circuitName = "fileName";
        } catch(FileNotFoundException fnfe) {
            System.err.println("Error: could not find saved circuit named " + fileName + ".");
            System.err.println("Creating a new circuit . . .");
            engine = new CSEngine();
            circuitName = "new-circuit";
        } catch(ClassCastException cce) {
            System.err.println("Error: could not recognize this file");
            System.err.println("Creating a new circuit . . .");
            engine = new CSEngine();
            circuitName = "new-circuit";
        } catch(Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            System.err.println("Creating a new circuit . . .");
            engine = new CSEngine();
            circuitName = "new-circuit";
        }

        circuitEdited = false;
        inputSource = new Scanner(System.in);
    }

    public void start() {
        ArrayList<String> options = new ArrayList<String>();
        int userInput;
        File[] files;
        
        // add options to main menu
        options.add("Check the status of this circuit");
        options.add("Edit this circuit");
        options.add("Test this circuit");
        options.add("Save this circuit");
        options.add("Load a circuit");
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
                case 2:     editCircuit();
                            circuitEdited = true;
                            break;
                case 3:     testCircuit();
                            break;
                case 4:     saveCircuit();
                            break;
                case 5:     loadCircuit();
                            break;
                case 6:     return; // exit the program
            }
        } while(true);
    }

    private void printCircuitInfo() {

    }

    private void editCircuit() {
        CircuitEditor editor = new CircuitEditor(engine, circuitName, inputSource);
        editor.start();
    }

    private void testCircuit() {

    }

    private void saveCircuit() {
        File[] files = CSFileIO.getSaveDir().listFiles();
        String fileName;
        int userInput;
        boolean overwriting = false;

        System.out.println("CS > Main Menu > Save");

        fileName = CSUserInterface.getUserStringInput("Save as: ", inputSource);

        if(files != null)
            for(int i = 0; i < files.length; i++) 
                if(files[0].getName().equals(fileName)) {
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
                    System.out.println("\nSuccessfully saved the circuit");
                } catch(Exception e) {
                    System.err.println("Unknown error: " + e.getMessage());
                }
            }

        } else { // fileName does not exist in save folder, okay to save
            try {
                engine.saveCircuit(fileName);
                circuitEdited = false;
                System.out.println("\nSuccessfully saved the circuit");
            } catch(Exception e) {
                System.err.println("Unknown error: " + e.getMessage());
            }
        }
    }

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
            circuitEdited = false;
            System.out.println("\nSuccessfully loaded file " + fileName);
        } catch(ClassCastException cce) {
            System.err.println("\nCannot read from the file " + fileName);
        } catch(Exception e) {
            System.err.println("\nUnknown error: please try again");
        }
    }

    public void exit() {
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

    public static void main(String[] args) {
        CircuitSimulator program = new CircuitSimulator();
        // program.testProgram();
        program.start();
        program.exit();
    }
}