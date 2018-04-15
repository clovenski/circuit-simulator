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
    private boolean changesMade;

    public CircuitSimulator() {
        engine = new CSEngine();
        circuitName = "new-circuit";
        changesMade = false;
        inputSource = new Scanner(System.in);
    }

    public CircuitSimulator(String fileName) {
        try {
            engine = new CSEngine(fileName);
            circuitName = "fileName";
        } catch(FileNotFoundException fnfe) {
            System.err.println("Error: could not find saved circuit named " + fileName + ".");
            System.err.println("Creating a new circuit . . .");
            engine = new CSEngine();
            circuitName = "new-circuit";
        } catch(Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            System.err.println("Creating a new circuit . . .");
            engine = new CSEngine();
            circuitName = "new-circuit";
        }

        changesMade = false;
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
                System.out.println("-------------------- --------------------------");

                for(File file : files)
                    System.out.printf("%-20s %s\n", file.getName(), new Date(file.lastModified()).toString());
                System.out.println("-------------------- --------------------------");
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

        System.out.print("Save as: ");
        fileName = CSUserInterface.getUserStringInput(inputSource);

        for(int i = 0; i < files.length; i++) 
            if(files[0].getName().equals(fileName)) {
                overwriting = true;
                break;
            }

        if(overwriting) {
            ArrayList<String> options = new ArrayList<String>();
            options.add("Yes");
            options.add("No");

            System.out.println(fileName + " already exists. Would you like to overwrite it?");
            System.out.println("Enter 1 to overwrite, or 2 to cancel.");
            userInput = CSUserInterface.getUserOptInput(options, inputSource);

            if(userInput == 1) {
                try {
                    engine.saveCircuit(fileName);
                } catch(Exception e) {
                    System.err.println("Unknown error: " + e.getMessage());
                }
            }

        } else { // fileName does not exist in save folder, okay to save
            try {
                engine.saveCircuit(fileName);
            } catch(Exception e) {
                System.err.println("Unknown error: " + e.getMessage());
            }
        }
    }

    private void loadCircuit() {
        int userInput;
        String newCircuitName;
        File[] files = CSFileIO.getSaveDir().listFiles();

        if(files == null) {
            System.err.println("\nNo saves are available to load from.");
            return;
        }

        System.out.println("\nCS > Main Menu > Load");

        System.out.println("Warning: unsaved changes will not be saved.\n" +
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

        newCircuitName = options.get(userInput - 1);
        try {
            engine.loadCircuit(newCircuitName);
            circuitName = newCircuitName;
            changesMade = false;
        } catch(Exception e) {
            System.err.println("Unknown error: " + e.getMessage());
        }
    }

    public void exit() {
        ArrayList<String> options;
        int userInput;

        if(changesMade) {
            System.out.println("You have unsaved changes to " + circuitName + ".\n" +
                                "Would you like to save those changes?\n" +
                                "Press 1 for yes, 2 for no");
            
            options = new ArrayList<String>();
            options.add("Yes");
            options.add("No");

            userInput = CSUserInterface.getUserOptInput(options, inputSource);
            if(userInput == 1) {
                try {
                    engine.saveCircuit(circuitName);
                } catch(Exception e) {
                    System.err.println("Unknown error: " + e.getMessage());
                }
            }
        }
        inputSource.close();
    }

    public void testProgram() {
        engine.addInputNode("inputnode-1");
        engine.addDFFNode("dffnode-1");
        engine.addOrGate("orgate-1");
        engine.addAndGate("andgate-1");
        engine.addOutputNode("outputnode-1");
        engine.addOutputNode("outputnode-2");
        engine.addConnection("inputnode-1", "dffnode-1");
        engine.addConnection("inputnode-1", "orgate-1");
        engine.addConnection("inputnode-1", "andgate-1");
        engine.addConnection("dffnode-1-out", "andgate-1");
        engine.addConnection("dffnode-1-outnegated", "orgate-1");
        engine.addConnection("orgate-1", "outputnode-2");
        engine.addConnection("andgate-1", "outputnode-1");

        int[] sequence = new int[] {1, 0, 1, 1, 0, 0, 1};
        engine.setInputSeq("inputnode-1", sequence);


        String[] nodeNames = engine.getNodeNames();
        for(String name : nodeNames)
            System.out.printf("%23s", name);
        System.out.println();

        int[] nodeValues;
        for(int i = 0; i < sequence.length; i++) {
            nodeValues = engine.getNextCircuitState();
            for(int value : nodeValues)
                System.out.printf("%23d", value);
            System.out.println();
        }
        // always need to reset circuit before saving
        engine.resetCircuit();
        try {
            engine.saveCircuit("testsave");
        } catch(Exception e) {
            System.err.println("Error: " + e);
            return;
        }
        
        engine.removeNode("andgate-1");
        System.out.println(Arrays.toString(engine.getNodeNames()));

        try {
            engine.loadCircuit("testsave");
        } catch(Exception e) {
            System.err.println("Error: " + e);
            return;
        }
        System.out.println(Arrays.toString(engine.getNodeNames()));
    }

    public static void main(String[] args) {
        CircuitSimulator program = new CircuitSimulator();
        // program.testProgram();
        program.start();
        program.exit();
    }
}