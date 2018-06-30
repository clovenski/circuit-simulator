package simulator.circuit.project;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import simulator.circuit.project.CSGraph.IllegalCircuitStateException;

public class CircuitTester {
    private CSEngine engine;
    private String circuitName;
    private Scanner inputSource;

    public CircuitTester(CSEngine engine, String circuitName, Scanner inputSource) {
        this.engine = engine;
        this.circuitName = circuitName;
        this.inputSource = inputSource;
    }

    public void start() {
        int userInput;

        String printTableOpt;
        if(engine.isCircuitSequential())
            printTableOpt = "Print transition table";
        else
            printTableOpt = "Print truth table";

        ArrayList<String> options = new ArrayList<String>();
        options.add("Edit tracked nodes");
        options.add("Test circuit");
        options.add(printTableOpt);
        options.add("Return");

        do {
            System.out.println("\nCS > Main Menu > Circuit Tester");

            CSUserInterface.displayOptions(options);
            userInput = CSUserInterface.getUserOptInput(options, inputSource);

            switch(userInput) {
                case 1:     editTrackedNodes();
                            break;
                case 2:     printCircuitTest();
                            break;
                case 3:     if(engine.isCircuitSequential())
                                printTransitionTable();
                            else
                                printTruthTable();
                            break;
                case 4:     return;
            }
        } while(true);
    }

    private void editTrackedNodes() {
        int userInput;
        String[] trackedNodes;
        ArrayList<String> options = new ArrayList<String>();
        options.add("Track a node");
        options.add("Untrack a node");
        options.add("Track all nodes");
        options.add("Untrack all nodes");
        options.add("Return");

        do {
            System.out.println("\nCS > Main Menu > Circuit Tester > Edit Tracked Nodes");
            System.out.println("\tCurrently tracked nodes:\n");
            trackedNodes = engine.getTrackedNodeNames();
            for(int i = 1; i <= trackedNodes.length; i++) {
                System.out.print(trackedNodes[i - 1] + " ");
                if(i % 5 == 0 && i != trackedNodes.length)
                    System.out.println();
            }

            System.out.println("\n");

            CSUserInterface.displayOptions(options);
            userInput = CSUserInterface.getUserOptInput(options, inputSource);

            switch(userInput) {
                case 1:     trackNode();
                            break;
                case 2:     untrackNode();
                            break;
                case 3:     engine.trackAllNodes();
                            System.out.println("\nSuccessfully tracked all nodes");
                            break;
                case 4:     engine.untrackAllNodes();
                            System.out.println("\nSuccessfully untracked all nodes");
                            break;
                case 5:     return;
            }
        } while(true);
    }

    private void trackNode() {
        String prompt = "Enter the number of the node to track: ";
        int nodeIndex;
        String[] nodeNames = engine.getCircuitNodeNames();
        int fieldWidth; // will store longest node name length

        if(nodeNames.length == 0) {
            System.err.println("\nThere are no nodes in the circuit to track");
            return;
        }

        System.out.println("\nCS > Main Menu > Circuit Tester > Edit Tracked Nodes > Track Node");

        // print list of nodes; 4 nodes per row
        fieldWidth = engine.getLongestNameLength();
        for(int i = 1; i <= nodeNames.length; i++) {
            System.out.printf("%3d. %-" + fieldWidth + "s ", i, nodeNames[i - 1]);
            if(i % 4 == 0 && i != nodeNames.length)
                System.out.println();
        }

        System.out.println("\n");

        nodeIndex = CSUserInterface.getUserIntInput(prompt, nodeNames.length, inputSource) - 1;
        try {
            engine.trackNode(nodeIndex);
            System.out.println("\nSuccessfully tracked the node");
        } catch(IllegalArgumentException iae) {
            System.err.println("\n" + iae.getMessage());
        } catch(Exception e) {
            System.err.println("\nUnknown error: " + e.getMessage());
        }
    }

    private void untrackNode() {
        String prompt = "Enter the number of the node to untrack: ";
        int nodeIndex;
        String[] trackedNodes = engine.getTrackedNodeNames();
        int fieldWidth; // will store longest tracked node name length

        if(trackedNodes.length == 0) {
            System.out.println("\nThere are no tracked nodes in this circuit");
            return;
        }

        System.out.println("\nCS > Main Menu > Circuit Tester > Edit Tracked Nodes > Untrack Node");

        // print tracked node names, 4 nodes per row
        fieldWidth = engine.getLongestTrackedNameLength();
        for(int i = 1; i <= trackedNodes.length; i++) {
            System.out.printf("%3d. %-" + fieldWidth + "s ", i, trackedNodes[i - 1]);
            if(i % 4 == 0 && i != trackedNodes.length)
                System.out.println();
        }

        System.out.println("\n");

        nodeIndex = CSUserInterface.getUserIntInput(prompt, trackedNodes.length, inputSource) - 1;
        try {
            engine.untrackNode(nodeIndex);
            System.out.println("\nSuccessfully untracked the node");
        } catch(IllegalArgumentException iae) {
            System.err.println("\n" + iae.getMessage());
        } catch(Exception e) {
            System.err.println("\nUnknown error: " + e.getMessage());
        }
    }

    private void printCircuitTest() {
        String[] trackedNodeNames;
        int[] trackedNodeValues;
        int headerWidth = engine.getLongestTrackedNameLength();
        int testCycles = engine.getLongestInputSeqLength();

        // if the longest input sequence is zero; in other words there are no input sequences
        if(testCycles == 0) {
            System.err.println("\nThere are no input sequences to test this circuit with");
            return;
        }

        // if header width is zero, then there are no tracked nodes
        if(headerWidth == 0) {
            System.err.println("\nTrack a node in order to display its values in the test");
            return;
        }

        // if circuit is in an invalid state; containing loops that do not have a flip flop in them
        if(!engine.isCircuitValid()) {
            System.err.println("\nThe current state of this circuit is invalid");
            return;
        }

        System.out.println("Testing circuit: " + circuitName + "\n");

        // print headers, names of the tracked nodes
        trackedNodeNames = engine.getTrackedNodeNames();
        for(String trackedNodeName : trackedNodeNames)
            System.out.printf("%" + (headerWidth + 1) + "s", trackedNodeName);
        System.out.println();

        // print header and test data separator
        int tableWidth = (headerWidth + 1) * trackedNodeNames.length + 1;
        for(int i = 0; i < tableWidth; i++)
            System.out.print("-");
        System.out.println();
        
        // print test data
        for(int i = 0; i < testCycles; i++) {
            try {
                trackedNodeValues = engine.getNextCircuitState();
            } catch(IllegalCircuitStateException icse) {
                System.err.println("\nCircuit has reached an invalid state");
                return;
            }

            for(int value : trackedNodeValues)
                System.out.printf("%" + (headerWidth + 1) + "d", value);
            System.out.println();
        }

        engine.resetCircuit();

        System.out.println("\nPress [ENTER] to return");
        inputSource.nextLine();
    }

    private void printTransitionTable() {
        ArrayList<ArrayList<String>> data;
        String[] flipFlopNodeNames;
        String[] outputNodeNames;
        String[] inputNodeNames;
        String[] inputNodeCombs; // store the combinations of the input variable values
        String temp = ""; // will store the header for input variable combinations
        int fieldWidth1; // field width of PS
        int fieldWidth2; // field width of NS
        int nextStateSecLength; // stores how wide the next state section is in the table

        try {
            data = engine.getTransitionTableData();
        } catch(IllegalStateException ise) {
            System.err.println("\n" + ise.getMessage());
            return;
        } catch(IllegalCircuitStateException icse) {
            System.err.println("\n" + icse.getMessage());
            return;
        } catch(Exception e) {
            System.err.println("\nUnknown error: " + e.getMessage());
            return;
        }

        flipFlopNodeNames = engine.getFlipFlopNodeNames();
        outputNodeNames = engine.getOutputNodeNames();
        inputNodeNames = engine.getInputNodeNames();

        inputNodeCombs = getBinaryNumSeq(inputNodeNames.length);

        // print name of circuit
        System.out.println("Transition table for circuit: " + circuitName);

        // print format of PS and NS and input combinations
        System.out.print("\nFormat of PS: ");
        for(String nodeName : flipFlopNodeNames)
            System.out.print(nodeName + " ");
        System.out.println();
        System.out.print("Format of NS: ");
        for(String nodeName : flipFlopNodeNames)
            System.out.print(nodeName + " ");
        if(outputNodeNames.length != 0) {
            System.out.print(", ");
            for(String nodeName : outputNodeNames)
                System.out.print(nodeName + " ");
        }
        System.out.println();
        System.out.print("Format of input combinations: ");
        for(String nodeName : inputNodeNames)
            System.out.print(nodeName + " ");
        System.out.println("\n");

        fieldWidth1 = Math.max(data.get(0).get(0).length(), 2) + 1;
        fieldWidth2 = Math.max(data.get(0).get(1).length(), inputNodeNames.length);
        nextStateSecLength = (fieldWidth2 + 3) * inputNodeCombs.length;

        // print headers
        System.out.printf("%" + fieldWidth1 + "s | %" + (nextStateSecLength / 2) + "s\n", "PS", "NS");  // print top header: PS | NS
        System.out.printf("%" + fieldWidth1 + "s | %" + (nextStateSecLength / 2) + "s\n", "", "input"); // print second row
        for(String comb : inputNodeCombs)
            temp += String.format("| %-" + fieldWidth2 + "s ", comb);               // prepare input combos string, then
        System.out.printf("%" + fieldWidth1 + "s %s\n", "", temp);                  // print third row: input combos
        // print header and table separator (dashes and pluses)
        for(int i = 0; i < fieldWidth1 + 1; i++)
            System.out.print("-");
        for(int i = 0; i < inputNodeCombs.length; i++) {
            System.out.print("+");
            for(int j = 0; j < fieldWidth2 + 2; j++)
                System.out.print("-");
        }
        System.out.println();

        // print table
        for(int i = 0; i < data.size(); i++) {
            System.out.printf("%" + fieldWidth1 + "s ", data.get(i).get(0));
            for(int j = 1; j < data.get(0).size(); j++)
                System.out.printf("| %" + fieldWidth2 + "s ", data.get(i).get(j));
            System.out.println();
        }

        System.out.println("\nPress [ENTER] to return");
        inputSource.nextLine();
    }

    private String[] getBinaryNumSeq(int bits) {
        String[] result = new String[(int)Math.pow(2.0, bits)];
        LinkedList<String> queue = new LinkedList<String>();
        for(int i = 0; i < bits; i++)
            queue.add("0");
        
        String binaryNum;
        String temp = "";
        for(int i = 0; i < result.length; i++) {
            binaryNum = Integer.toBinaryString(i);
            for(int n = 0; n < binaryNum.length(); n++) {
                queue.add(Character.toString(binaryNum.charAt(n)));
                queue.remove();
            }

            for(int m = 0; m < bits; m++) {
                temp += queue.remove();
                queue.add("0");
            }

            result[i] = temp;
            temp = "";
        }

        return result;
    }

    private void printTruthTable() {
        ArrayList<ArrayList<Integer>> truthTableData;
        String[] inputNodeNames;
        String[] outputNodeNames;
        int fieldWidth = 0;

        try {
            truthTableData = engine.getTruthTableData();
        } catch(IllegalStateException ise) {
            System.err.println("\n" + ise.getMessage());
            return;
        } catch(IllegalCircuitStateException icse) {
            System.err.println("\n" + icse.getMessage());
            return;
        } catch(Exception e) {
            System.err.println("\nUnknown error: " + e.getMessage());
            return;
        }

        inputNodeNames = engine.getInputNodeNames();
        outputNodeNames = engine.getOutputNodeNames();
        // fieldWidth = longest name in the two arrays above + 1
        for(String name : inputNodeNames)
            if(name.length() > fieldWidth)
                fieldWidth = name.length();
        for(String name : outputNodeNames)
            if(name.length() > fieldWidth)
                fieldWidth = name.length();
        fieldWidth = fieldWidth + 1;

        // print name of circuit
        System.out.println("Truth table for circuit: " + circuitName);

        // print input and output nodes
        System.out.println();
        for(String name : inputNodeNames)
            System.out.printf("%" + fieldWidth + "s", name);
        System.out.print(" | ");
        for(String name : outputNodeNames)
            System.out.printf("%" + fieldWidth + "s", name);
        System.out.println();

        // print dashes to separate headers from data
        int dashesNeeded1 = fieldWidth * inputNodeNames.length;     // stores length of input variables section (left section) of table
        int dashesNeeded2 = fieldWidth * outputNodeNames.length;    // stores length of output variables section (right section) of table
        for(int i = 0; i < dashesNeeded1; i++)
            System.out.print("-");
        System.out.print("-+-");
        for(int i = 0; i < dashesNeeded2; i++)
            System.out.print("-");
        System.out.println("-");

        // print the table
        for(int i = 0; i < truthTableData.size(); i++) {
            for(int j = 0; j < inputNodeNames.length; j++)
                System.out.printf("%" + fieldWidth + "d", truthTableData.get(i).get(j).intValue());

            System.out.print(" | ");

            for(int j = inputNodeNames.length; j < truthTableData.get(0).size(); j++)
                System.out.printf("%" + fieldWidth + "d", truthTableData.get(i).get(j).intValue());

            System.out.println();
        }

        System.out.println("\nPress [ENTER] to return");
        inputSource.nextLine();
    }
}