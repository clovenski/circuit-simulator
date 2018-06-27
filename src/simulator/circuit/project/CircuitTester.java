package simulator.circuit.project;

import java.util.ArrayList;
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

        if(nodeNames.length == 0) {
            System.out.println("\nThere are no nodes in the circuit to track");
            return;
        }

        System.out.println("\nCS > Main Menu > Circuit Tester > Edit Tracked Nodes > Track Node");

        for(int i = 1; i <= nodeNames.length; i++) {
            System.out.print(i + ". " + nodeNames[i - 1] + " ");
            if(i % 5 == 0 && i != nodeNames.length)
                System.out.println();
        }

        System.out.println("\n");

        nodeIndex = CSUserInterface.getUserIntInput(prompt, nodeNames.length, inputSource) - 1;
        try {
            engine.trackNode(nodeIndex);
            System.out.println("\nSuccessfully tracked the node");
        } catch(IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
        } catch(Exception e) {
            System.err.println("\nUnknown error: " + e.getMessage());
        }
    }

    private void untrackNode() {
        String prompt = "Enter the number of the node to untrack: ";
        int nodeIndex;
        String[] trackedNodes = engine.getTrackedNodeNames();

        if(trackedNodes.length == 0) {
            System.out.println("\nThere are no tracked nodes in this circuit");
            return;
        }

        System.out.println("\nCS > Main Menu > Circuit Tester > Edit Tracked Nodes > Untrack Node");

        for(int i = 1; i <= trackedNodes.length; i++) {
            System.out.print(i + ". " + trackedNodes[i - 1] + " ");
            if(i % 5 == 0 && i != trackedNodes.length)
                System.out.println();
        }

        System.out.println("\n");

        nodeIndex = CSUserInterface.getUserIntInput(prompt, trackedNodes.length, inputSource) - 1;
        try {
            engine.untrackNode(nodeIndex);
            System.out.println("\nSuccessfully untracked the node");
        } catch(IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
        } catch(Exception e) {
            System.err.println("\nUnknown error: " + e.getMessage());
        }
    }

    private void printCircuitTest() {
        String[] trackedNodeNames;
        int[] trackedNodeValues;
        int headerWidth = engine.getLongestNodeNameLength();
        int testCycles = engine.getLongestSequenceLength();

        if(testCycles == 0) {
            System.out.println("\nThere are no input sequences to test this circuit with");
            return;
        }

        // if longest node name length is zero, then there are no tracked nodes
        if(headerWidth == 0) {
            System.out.println("\nTrack a node in order to display its values in the test");
            return;
        }

        System.out.println("Testing circuit: " + circuitName + "\n");

        trackedNodeNames = engine.getTrackedNodeNames();
        for(String trackedNodeName : trackedNodeNames)
            System.out.printf("%" + (headerWidth + 1) + "s", trackedNodeName);
        System.out.println();
        
        for(int i = 0; i < testCycles; i++) {
            trackedNodeValues = engine.getNextCircuitState();
            for(int value : trackedNodeValues)
                System.out.printf("%" + (headerWidth + 1) + "d", value);
            System.out.println();
        }

        engine.resetCircuit();

        // can implement feature [write test results to file] here

        System.out.println("\nPress [ENTER] to return");
        inputSource.nextLine();
    }

    private void printTransitionTable() {
        //testing
        ArrayList<ArrayList<String>> data = null;
        try {
            data = engine.getTransitionTableData();
        } catch(IllegalCircuitStateException icse) {

        }
        for(int i = 0; i < data.size(); i++) {
            for(int j = 0; j < data.get(0).size(); j++)
                System.out.print(data.get(i).get(j) + " ");
            System.out.println();
        }
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

        // print input and output nodes
        System.out.println();
        for(String name : inputNodeNames)
            System.out.printf("%" + fieldWidth + "s", name);
        System.out.print(" | ");
        for(String name : outputNodeNames)
            System.out.printf("%" + fieldWidth + "s", name);
        System.out.println();

        // print dashes to separate headers from data
        int dashesNeeded = fieldWidth * (inputNodeNames.length + outputNodeNames.length) + 3;
        for(int i = 0; i < dashesNeeded; i++)
            System.out.print("-");
        System.out.println();

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