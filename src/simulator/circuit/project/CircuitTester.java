package simulator.circuit.project;

import java.util.ArrayList;
import java.util.Scanner;

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
        ArrayList<String> options = new ArrayList<String>();
        options.add("Edit tracked nodes");
        options.add("Test circuit");
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
                case 3:     return;
            }
        } while(true);
    }

    private void editTrackedNodes() {
        int userInput;
        String[] trackedNodes = engine.getTrackedNodeNames(); // TODO: resume progress here
        ArrayList<String> options = new ArrayList<String>();
        options.add("Track a node");
        options.add("Untrack a node");
        options.add("Track all nodes");
        options.add("Untrack all nodes");
        options.add("Return");

        do {
            System.out.println("\nCS > Main Menu > Circuit Tester > Edit Tracked Nodes");
            System.out.println("\tCurrently tracked nodes:");
            

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

    }

    private void untrackNode() {

    }

    private void printCircuitTest() {
        
    }
}