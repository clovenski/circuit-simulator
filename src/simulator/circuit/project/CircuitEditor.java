package simulator.circuit.project;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class to handle editing circuits in the program.
 * <p>
 * Through this class, the user is able to add nodes, removes nodes, rename nodes,
 * add connections, remove connections and set input sequences.
 * 
 * @author Joel Tengco
 */
public class CircuitEditor {
    private Scanner inputSource;
    private CSEngine engine;
    private String circuitName;
    // if true, print nodes in condensed list in addNode method, otherwise print in long list
    private boolean printAltMode;
    // if true, user input connection data by node number, otherwise by node name
    private boolean inputAltMode;

    public CircuitEditor(CSEngine engine, String circuitName, Scanner inputSource) {
        this.engine = engine;
        this.circuitName = circuitName;
        this.inputSource = inputSource;
        printAltMode = false;
        inputAltMode = false;
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
        String[] nodeTypes;
        int fieldWidth;
        ArrayList<String> options = new ArrayList<String>();
        options.add("Add a node");
        options.add("Remove a node");
        options.add("Rename a node");
        options.add("Toggle print mode");
        options.add("Return");

        do {
            System.out.println("\nCS > Main Menu > Circuit Editor > Edit Nodes");
            System.out.printf("%17s nodes:\n\n", circuitName);
            nodeNames = engine.getCircuitNodeNames();
            nodeTypes = engine.getCircuitNodeTypes();

            if(!printAltMode) { // if printing normally, node names along with their type
                System.out.printf("%3s | %-6s | %s\n", "NUM", "TYPE", "NAME");
                System.out.println("----+--------+----------------------");
                for(int i = 1; i <= nodeNames.length; i++)
                    System.out.printf("%3d | %6s | %s\n", i, nodeTypes[i - 1], nodeNames[i - 1]);
                System.out.println("------------------------------------");

            } else { // print node names in a condensed list, without their type
                fieldWidth = engine.getLongestNameLength();
                for(int i = 1; i <= nodeNames.length; i++) {
                    System.out.printf("%3d. %-" + fieldWidth + "s ", i, nodeNames[i - 1]);
                    if(i % 4 == 0 && i != nodeNames.length)
                        System.out.println();
                }

                System.out.println();
            }

            System.out.println();
            CSUserInterface.displayOptions(options);
            userInput = CSUserInterface.getUserOptInput(options, inputSource);

            try {
                switch(userInput) {
                    case 1:     addNode();
                                break;
                    case 2:     removeNode();
                                break;
                    case 3:     renameNode();
                                break;
                    case 4:     togglePrintMode();
                                break;
                    case 5:     return;
                }
            } catch(IllegalArgumentException iae) {
                System.err.println("\n" + iae.getMessage());
            }
        } while(true);
    }

    private void addNode() {
        int userInput;
        String newNodeName = "";
        String sourceNodeID = "";
        final int ADD_INVERTER_OPT;
        final int RETURN_OPT;
        ArrayList<String> options = new ArrayList<String>();
        options.add("Add an input node");
        options.add("Add an output node");
        options.add("Add a D-flip-flop");
        options.add("Add an AND gate");
        options.add("Add a NAND gate");
        options.add("Add an OR gate");
        options.add("Add a NOR gate");
        options.add("Add an XOR gate");
        options.add("Add an NXOR gate");
        options.add("Add an inverter");
        options.add("Return");

        ADD_INVERTER_OPT = options.indexOf("Add an inverter") + 1;
        RETURN_OPT = options.size();

        do {
            System.out.println("\nCS > Main Menu > Circuit Editor > Edit Nodes > Add Node");
            CSUserInterface.displayOptions(options);
            userInput = CSUserInterface.getUserOptInput(options, inputSource);

            try {
                if(userInput == ADD_INVERTER_OPT) { // if user wants to add an inverter
                    if(engine.getCircuitSize() == 0)
                        throw new IllegalArgumentException("There are no nodes for you to invert");
                    else
                        sourceNodeID = CSUserInterface.getUserStringInput("Name of the node to invert: ", inputSource);
    
                } else if(userInput != RETURN_OPT) { // if user does not want to "return"
                    newNodeName = CSUserInterface.getUserStringInput("Node name: ", inputSource);
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
                    case 5:     engine.addNandGate(newNodeName);
                                break;
                    case 6:     engine.addOrGate(newNodeName);
                                break;
                    case 7:     engine.addNorGate(newNodeName);
                                break;
                    case 8:     engine.addXorGate(newNodeName);
                                break;
                    case 9:     engine.addNXorGate(newNodeName);
                                break;
                    case 10:    engine.addInverter(sourceNodeID);
                                break;
                    case 11:    return;
                }
            } catch(IllegalArgumentException iae) {
                System.err.println("\n" + iae.getMessage());
                continue;
            }
            System.out.println("\nSuccessfully created a new node in the circuit");
        } while(true);
    }

    private void removeNode() throws IllegalArgumentException {
        String targetNode;
        int circuitSize = engine.getCircuitSize();
        if(circuitSize == 0)
            throw new IllegalArgumentException("There are no nodes for you to remove");

        targetNode = CSUserInterface.getUserStringInput("Name of the node to remove: ", inputSource);
        try {
            engine.removeNode(targetNode);
            System.out.println("\nSuccessfully removed " + targetNode);
        } catch(IllegalArgumentException iae) {
            System.err.println("\n" + iae.getMessage());
        }
    }

    private void renameNode() throws IllegalArgumentException {
        int userIntInput;
        String newName;
        int circuitSize = engine.getCircuitSize();
        if(circuitSize == 0)
            throw new IllegalArgumentException("There are no nodes for you to rename");

        userIntInput = CSUserInterface.getUserIntInput("Enter the number of the node to rename: ", circuitSize, inputSource);
        newName = CSUserInterface.getUserStringInput("Enter the new name: ", inputSource);

        try {
            engine.renameNode(userIntInput - 1, newName);
            System.out.println("\nSuccessfully renamed node to: " + newName);
        } catch(IllegalArgumentException iae) {
            System.err.println("\n" + iae.getMessage());
        }
    }

    private void togglePrintMode() {
        if(printAltMode)
            System.out.println("\nNodes will now be printed normally, with one node per line");
        else
            System.out.println("\nNodes will now be printed in a condensed list, without their type");

        printAltMode = !printAltMode;
        System.out.println("\nPress [ENTER] to return");
        inputSource.nextLine();
    }

    private void editConnections() {
        int userInput;
        int fieldWidth;
        String[] nodeNames;
        String[] nodeTypes;
        String[] circuitConnections;
        ArrayList<String> options = new ArrayList<String>();
        options.add("Add a connection");
        options.add("Remove a connection");
        options.add("Toggle input mode");
        options.add("Return");

        do {
            System.out.println("\nCS > Main Menu > Circuit Editor > Edit Connections");
            System.out.printf("%30s %s\n", "Connection input mode:", (inputAltMode ? "by node number" : "by node name"));
            System.out.printf("%17s connections:\n\n", circuitName);

            nodeNames = engine.getCircuitNodeNames();
            fieldWidth = engine.getLongestNameLength();
            for(int i = 1; i <= nodeNames.length; i++) {
                System.out.printf("%3d. %-" + fieldWidth + "s ", i, nodeNames[i - 1]);
                if(i % 4 == 0 && i != nodeNames.length)
                    System.out.println();
            }

            System.out.println("\n");

            nodeTypes = engine.getCircuitNodeTypes();
            circuitConnections = engine.getCircuitConnectionStatus();
            System.out.printf("%-6s | %-6s | %s\n", "TYPE", "NUM", "ADJACENCY SET");
            System.out.println("-------+--------+----------------------");
            for(int i = 0; i < circuitConnections.length; i++)
                System.out.printf("%6s | %s\n", nodeTypes[i], circuitConnections[i]);
            System.out.println("---------------------------------------");

            System.out.println();
            CSUserInterface.displayOptions(options);
            userInput = CSUserInterface.getUserOptInput(options, inputSource);

            try {
                switch(userInput) {
                    case 1:     addConnection();
                                break;
                    case 2:     removeConnection();
                                break;
                    case 3:     toggleInputMode();
                                break;
                    case 4:     return;
                }
            } catch(IllegalArgumentException iae) {
                System.err.println("\n" + iae.getMessage());
            }
        } while(true);
    }

    private void addConnection() throws IllegalArgumentException {
        String promptSource;
        String promptTarget;
        String sourceName;
        String targetName;
        int sourceNodeIndex;
        int targetNodeIndex;
        int circuitSize = engine.getCircuitSize();
        if(circuitSize <= 1)
            throw new IllegalArgumentException("There are not enough nodes to add a connection");

        // if inputting connection data normally, by node name
        if(!inputAltMode) {
            promptSource = "Enter the name of the node to be the source of this connection: ";
            promptTarget = "Enter the name of the node to be the target of this connection: ";
            sourceName = CSUserInterface.getUserStringInput(promptSource, inputSource);
            targetName = CSUserInterface.getUserStringInput(promptTarget, inputSource);

            try {
                engine.addConnection(sourceName, targetName);
                System.out.println("\nSuccessfully added connection from " + sourceName + " to " + targetName);
            } catch(IllegalArgumentException iae) {
                System.err.println("\n" + iae.getMessage());
            }

        } else { // user will input connection data by node numbers
            promptSource = "Enter the number of the node to be the source of this connection: ";
            promptTarget = "Enter the number of the node to be the target of this connection: ";

            sourceNodeIndex = CSUserInterface.getUserIntInput(promptSource, circuitSize, inputSource) - 1;
            targetNodeIndex = CSUserInterface.getUserIntInput(promptTarget, circuitSize, inputSource) - 1;

            try {
                engine.addConnection(sourceNodeIndex, targetNodeIndex);
                System.out.println("\nSuccessfully added connection from " + (sourceNodeIndex + 1) + " to " + (targetNodeIndex + 1));
            } catch(IllegalArgumentException iae) {
                System.err.println("\n" + iae.getMessage());
            }
        }
    }

    private void removeConnection() throws IllegalArgumentException {
        String promptSource;
        String promptTarget;
        String sourceName;
        String targetName;
        int sourceNodeIndex;
        int targetNodeIndex;
        int circuitSize = engine.getCircuitSize();
        if(circuitSize <= 1)
            throw new IllegalArgumentException("There are no connections for you to remove");

        // if inputting connection data normally, by node name
        if(!inputAltMode) {
            promptSource = "Enter the name of the node that is the source of this connection: ";
            promptTarget = "Enter the name of the node that is the target of this connection: ";

            sourceName = CSUserInterface.getUserStringInput(promptSource, inputSource);
            targetName = CSUserInterface.getUserStringInput(promptTarget, inputSource);

            try {
                engine.removeConnection(sourceName, targetName);
                System.out.println("\nSuccessfully removed connection from " + sourceName + " to " + targetName);
            } catch(IllegalArgumentException iae) {
                System.err.println("\n" + iae.getMessage());
            }

        } else { // user will input connection data by node numbers
            promptSource = "Enter the number of the node that is the source of this connection: ";
            promptTarget = "Enter the number of the node that is the target of this connection: ";

            sourceNodeIndex = CSUserInterface.getUserIntInput(promptSource, circuitSize, inputSource) - 1;
            targetNodeIndex = CSUserInterface.getUserIntInput(promptTarget, circuitSize, inputSource) - 1;

            try {
                engine.removeConnection(sourceNodeIndex, targetNodeIndex);
                System.out.println("\nSuccessfully removed connection from " + (sourceNodeIndex + 1) + " to " + (targetNodeIndex + 1));
            } catch(IllegalArgumentException iae) {
                System.err.println("\n" + iae.getMessage());
            }
        }
    }

    private void toggleInputMode() {
        if(inputAltMode)
            System.out.println("\nConnection input will now be inputted normally, by node name");
        else
            System.out.println("\nConnection input will now be inputted differently, by node number");

        inputAltMode = !inputAltMode;
        System.out.println("\nPress [ENTER] to return");
        inputSource.nextLine();
    }

    private void setInputSeq() {
        int userInput;
        int inputNodeIndex;
        int[] sequence;
        String prompt = "Enter the number of the node to set a sequence to: ";
        String[] circuitInputSeqStatus = engine.getCircuitInputSeqStatus();
        String[] inputNodeNames = engine.getInputNodeNames();
        
        if(circuitInputSeqStatus.length != 0) {
            do {
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

                circuitInputSeqStatus = engine.getCircuitInputSeqStatus();
            } while(true);
        } else {
            System.out.println("\nThere are no input nodes to set a sequence to");
        }
    }
}