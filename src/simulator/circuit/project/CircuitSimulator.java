package simulator.circuit.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays; // imported for testing, remove later
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class CircuitSimulator {
    private Scanner input;
    private String circuitName;
    private CSEngine engine;
    private boolean changesMade;

    public CircuitSimulator() {
        engine = new CSEngine();
        circuitName = "new-circuit";
        changesMade = false;
        input = new Scanner(System.in);
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
        input = new Scanner(System.in);
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
        System.out.println("\\\\\\\\\\\\\\\\-----------------------------\n");
        
        do {
            files = CSFileIO.getSaveDir().listFiles();
            if(files != null) {
                System.out.printf("%-20s %s\n", "Saved Circuits", "Last Modified");
                System.out.println("-------------------- --------------------------");

                for(File file : files)
                    System.out.printf("%-20s %s\n", file.getName(), new Date(file.lastModified()).toString());
                System.out.println("-------------------- --------------------------");
                System.out.println();
            }
            
            System.out.println("Selected circuit: " + circuitName + "\n");

            System.out.println("CS > Main Menu");
            displayOptions(options);
            userInput = getUserOptInput(options);

            switch(userInput) {
                case 1:     printCircuitStatus();
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

    private void printCircuitStatus() {

    }

    private void editCircuit() {

    }

    private void testCircuit() {

    }

    private void saveCircuit() {
        File[] files = CSFileIO.getSaveDir().listFiles();
        String fileName;
        boolean overwriting = false;

        System.out.print("Save as: ");
        fileName = input.nextLine();

        for(int i = 0; i < files.length; i++) 
            if(files[0].getName().equals(fileName)) {
                overwriting = true;
                break;
            }

        if(overwriting) {
            System.out.println(""); // user options: yes overwrite that file, or not
        }
    }

    private void loadCircuit() {
        int userInput;
        String newCircuitName;
        File[] files = CSFileIO.getSaveDir().listFiles();

        if(files == null) {
            System.err.println("No saves are available to load from.");
            return;
        }

        System.out.println("Warning: unsaved changes will not be saved.\n" +
                            "If you want to save any changes, go back and save first.");

        ArrayList<String> options = new ArrayList<String>();
        for(File file : files)
            options.add(file.getName());

        System.out.println("Choose a file to load from:");
        displayOptions(options);
        userInput = getUserOptInput(options);

        newCircuitName = options.get(userInput - 1);
        try {
            engine.loadCircuit(newCircuitName);
            circuitName = newCircuitName;
            changesMade = false;
        } catch(Exception e) {
            System.err.println("Unknown error: " + e.getMessage());
        }
    }

    private void displayOptions(ArrayList<String> options) {
        int optNum = 1;

        for(String option : options) {
            System.out.println(optNum + ". " + option);
            optNum++;
        }
        System.out.println();
    }

    private int getUserOptInput(ArrayList<String> options) {
        StringTokenizer tokenizer;
        int userInput = 0;

        do {
            System.out.print("Enter the option number > ");
            try {
                tokenizer = new StringTokenizer(input.nextLine());
                userInput = Integer.parseInt(tokenizer.nextToken());
            } catch(NumberFormatException nfe) {
                System.err.println("Invalid input. Please enter an integer.");
                continue;
            } catch(NoSuchElementException nsee) {
                System.err.println("Please enter an input.");
                continue;
            }

            if(userInput <= 0 || userInput > options.size()) {
                System.err.println("Invalid input.");
                continue;
            }

            // reachable only for valid inputs
            return userInput;
        } while(true);
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

            userInput = getUserOptInput(options);
            if(userInput == 1) {
                try {
                    engine.saveCircuit(circuitName);
                } catch(Exception e) {
                    System.err.println("Unknown error: " + e.getMessage());
                }
            }
        }
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
            nodeValues = engine.getNextCircuitStatus();
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