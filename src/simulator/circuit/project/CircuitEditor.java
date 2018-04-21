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
            System.out.printf("%17s nodes:\n\n", circuitName);
            nodeNames = engine.getCircuitNodeNames();
            for(int i = 1; i <= nodeNames.length; i++) {
                System.out.print(i + ". " + nodeNames[i - 1] + "  ");
                if(i % 5 == 0 && i != nodeNames.length)
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
        String sourceNodeID = "";
        ArrayList<String> options = new ArrayList<String>();
        options.add("Add an input node");
        options.add("Add an output node");
        options.add("Add a D-flip-flop");
        options.add("Add an AND gate");
        options.add("Add an OR gate");
        options.add("Add an inverter");
        options.add("Return");

        do {
            System.out.println("\nCS > Main Menu > Circuit Editor > Edit Nodes > Add Node");
            CSUserInterface.displayOptions(options);
            userInput = CSUserInterface.getUserOptInput(options, inputSource);

            if(userInput == 6) { // if user wants to add an inverter
                sourceNodeID = CSUserInterface.getUserStringInput("Name of the node to invert: ", inputSource);
            } else if(userInput != 7) {// if user does not want to "return"
                newNodeName = CSUserInterface.getUserStringInput("Node name: ", inputSource);
            }

            try {
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
                    case 6:     engine.addInverter(sourceNodeID);
                                break;
                    case 7:     return;
                }
            } catch(IllegalArgumentException iae) {
                System.err.println(iae.getMessage());
                continue;
            }
            System.out.println("\nSuccessfully created a new node in the circuit");
        } while(true);
    }

    private void removeNode() {
        String targetNode;

        targetNode = CSUserInterface.getUserStringInput("Name of the node to remove: ", inputSource);
        try {
            engine.removeNode(targetNode);
            System.out.println("\nSuccessfully removed " + targetNode);
        } catch(IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
        }
    }

    private void renameNode() {
        int userIntInput;
        String newName;

        userIntInput = CSUserInterface.getUserIntInput("Enter the number of the node to rename: ", engine.getCircuitSize(), inputSource);
        newName = CSUserInterface.getUserStringInput("Enter the new name: ", inputSource);

        try {
            engine.renameNode(userIntInput - 1, newName);
            System.out.println("\nSuccessfully renamed node to: " + newName);
        } catch(IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
        }
    }

    private void editConnections() {
        int userInput;
        String[] nodeNames;
        String[] circuitConnections;
        ArrayList<String> options = new ArrayList<String>();
        options.add("Add a connection");
        options.add("Remove a connection");
        options.add("Return");

        do {
            System.out.println("\nCS > Main Menu > Circuit Editor > Edit Connections");
            System.out.printf("%17s connections:\n\n", circuitName);

            nodeNames = engine.getCircuitNodeNames();
            for(int i = 1; i <= nodeNames.length; i++) {
                System.out.print(i + ". " + nodeNames[i - 1] + "  ");
                if(i % 5 == 0 && i != nodeNames.length)
                    System.out.println();
            }

            System.out.println("\n");

            circuitConnections = engine.getCircuitConnectionStatus();
            for(String connectionStatus : circuitConnections)
                System.out.println(connectionStatus);

            System.out.println();
            CSUserInterface.displayOptions(options);
            userInput = CSUserInterface.getUserOptInput(options, inputSource);

            switch(userInput) {
                case 1:     addConnection();
                            break;
                case 2:     removeConnection();
                            break;
                case 3:     return;
            }
        } while(true);
    }

    private void addConnection() {
        int sourceNodeIndex;
        int targetNodeIndex;
        int circuitSize = engine.getCircuitSize();
        String promptSource = "Enter the number of the node to be the source of this connection: ";
        String promptTarget = "Enter the number of the node to be the target of this connection: ";

        sourceNodeIndex = CSUserInterface.getUserIntInput(promptSource, circuitSize, inputSource) - 1;
        targetNodeIndex = CSUserInterface.getUserIntInput(promptTarget, circuitSize, inputSource) - 1;

        try {
            engine.addConnection(sourceNodeIndex, targetNodeIndex);
            System.out.println("\nSuccessfully added connection from " + (sourceNodeIndex + 1) + " to " + (targetNodeIndex + 1));
        } catch(IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
        }
    }

    private void removeConnection() {
        int sourceNodeIndex;
        int targetNodeIndex;
        int circuitSize = engine.getCircuitSize();
        String promptSource = "Enter the number of the node that is the source of this connection: ";
        String promptTarget = "Enter the number of the node that is the target of this connection: ";

        sourceNodeIndex = CSUserInterface.getUserIntInput(promptSource, circuitSize, inputSource) - 1;
        targetNodeIndex = CSUserInterface.getUserIntInput(promptTarget, circuitSize, inputSource) - 1;

        try {
            engine.removeConnection(sourceNodeIndex, targetNodeIndex);
            System.out.println("\nSuccessfully removed connection from " + (sourceNodeIndex + 1) + " to " + (targetNodeIndex + 1));
        } catch(IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
        }
    }

    private void setInputSeq() {
        int userInput;
        int inputNodeIndex;
        int[] sequence;
        String prompt = "Enter the number of the node to set a sequence to: ";
        String[] circuitInputSeqStatus = engine.getCircuitInputSeqStatus();
        String[] inputNodeNames = engine.getInputNodeNames();
        
        if(circuitInputSeqStatus.length != 0) {
            System.out.println("\nCS > Main Menu > Circuit Editor > Set Input Sequence");

            for(String status : circuitInputSeqStatus)
                System.out.println(status);

            System.out.println((circuitInputSeqStatus.length + 1) + ". Enter this number to return");
            System.out.println();

            userInput = CSUserInterface.getUserIntInput(prompt, circuitInputSeqStatus.length + 1, inputSource);
            if(userInput == (circuitInputSeqStatus.length + 1))
                return;

            inputNodeIndex = userInput - 1;
            sequence = CSUserInterface.getUserInputSeq("Enter a sequence of 0's and 1's separated by spaces, anything other than a zero or one will be ignored: ", inputSource);

            engine.setInputSeq(inputNodeNames[inputNodeIndex], sequence);
            System.out.println("\nSuccessfully set a new input sequence");
        } else {
            System.out.println("\nThere are no input nodes to set a sequence to\n");
        }
    }
}