package simulator.circuit.project;

import java.util.ArrayList;
import java.util.Scanner;

public class CircuitEditor {
    private Scanner inputSource;
    private CSEngine engine;
    private String circuitName;

    public CircuitEditor(CSEngine engine, String circuitName, Scanner inputSource) {
        this.engine = engine;
        this.circuitName = circuitName;
        this.inputSource = inputSource;
    }

    public void start() {
        char[] statusLetters = new char[] {'i', 's', 'o', 'f', 'g', 'n', 'c'};
        String statusBar;
        int[] circuitStatus;
        int userInput;
        ArrayList<String> options = new ArrayList<String>();
        options.add("Edit circuit nodes");
        options.add("Edit circuit connections");
        options.add("Edit input sequences");
        options.add("Return");

        do {
            circuitStatus = engine.getCircuitStatus();
            statusBar = "";
            for(int i = 0; i < circuitStatus.length; i++) {
                if(circuitStatus[i] > 0)
                    statusBar += statusLetters[i];
                else
                    statusBar += "-";
            }

            int index = 0;
            System.out.println("\nCS > Main Menu > Circuit Editor");
            System.out.printf("%17s status: %s\n", circuitName, statusBar);
            System.out.printf("%-15s : %d\n", "Input Nodes", circuitStatus[index++]);
            System.out.printf("%-15s : %d\n", "Sequences", circuitStatus[index++]);
            System.out.printf("%-15s : %d\n", "Output Nodes", circuitStatus[index++]);
            System.out.printf("%-15s : %d\n", "Flip Flops", circuitStatus[index++]);
            System.out.printf("%-15s : %d\n", "Gates", circuitStatus[index++]);
            System.out.printf("%-15s : %d\n", "Inverters", circuitStatus[index++]);
            System.out.printf("%-15s : %d\n", "Connections", circuitStatus[index++]);

            CSUserInterface.displayOptions(options);
            userInput = CSUserInterface.getUserOptInput(options, inputSource);

            switch(userInput) {
                case 1:     editNodes();
                            break;
                case 2:     editConnections();
                            break;
                case 3:     setInputSeq();
                            break;
                case 4:     return;
            }
        } while(true);

    }

    private void editNodes() {
        int userInput;
        String[] nodeNames;
        ArrayList<String> options = new ArrayList<String>();
        options.add("Add a node");
        options.add("Remove a node");
        options.add("Rename a node");
        options.add("Return");

        do {
            System.out.println("\nCS > Main Menu > Circuit Editor > Edit Nodes");
            System.out.printf("%17s nodes:\n", circuitName);
            nodeNames = engine.getCircuitNodeNames();
            for(int i = 1; i <= nodeNames.length; i++) {
                System.out.print(i + ". " + nodeNames[i - 1] + "  ");
                if(i % 5 == 0)
                    System.out.println();
            }

            System.out.println("\n");
            CSUserInterface.displayOptions(options);
            userInput = CSUserInterface.getUserOptInput(options, inputSource);

            switch(userInput) {
                case 1:     addNode();
                            break;
                case 2:     removeNode();
                            break;
                case 3:     renameNode();
                            break;
                case 4:     return;
            }
        } while(true);
    }

    private void addNode() {
        int userInput;
        String newNodeName = "";
        ArrayList<String> options = new ArrayList<String>();
        options.add("Add an input node");
        options.add("Add an output node");
        options.add("Add a D-flip-flop");
        options.add("Add an AND gate");
        options.add("Add an OR gate");
        options.add("Add an inverter");
        options.add("Return");

        System.out.println("\nCS > Main Menu > Circuit Editor > Edit Nodes > Add Node");
        CSUserInterface.displayOptions(options);
        userInput = CSUserInterface.getUserOptInput(options, inputSource);

        if(userInput == 6) { // if user wants to add an inverter

        } else if(userInput != 7) {// if user does not want to "return"
            System.out.print("Node name: ");
            newNodeName = CSUserInterface.getUserStringInput(inputSource);
        }

        switch(userInput) {
            case 1:     engine.addInputNode(newNodeName);
                        break;
            case 2:     engine.addOutputNode(newNodeName);
                        break;
            case 3:     engine.addDFFNode(newNodeName);
                        break;
            case 4:     engine.addAndGate(newNodeName);
                        break;
            case 5:     engine.addOrGate(newNodeName);
                        break;
            case 6:     
        }
    }

    private void removeNode() {
        System.out.println("CS > Main Menu > Circuit Editor > Edit Nodes > Remove Node");
    }

    private void renameNode() {

    }

    private void editConnections() {
        System.out.println("CS > Main Menu > Circuit Editor > Edit Connections");
    }

    private void addConnection() {
        System.out.println("CS > Main Menu > Circuit Editor > Edit Connections > Add Connection");
    }

    private void removeConnection() {
        System.out.println("CS > Main Menu > Circuit Editor > Edit Connections > Remove Connection");
    }

    private void setInputSeq() {
        System.out.println("CS > Main Menu > Circuit Editor > Set Input Sequence");
    }
}