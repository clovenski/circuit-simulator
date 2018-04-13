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
        System.out.println("CS > Main Menu > Circuit Editor > Edit Nodes");
    }

    private void addNode() {
        System.out.println("CS > Main Menu > Circuit Editor > Edit Nodes > Add Node");
    }

    private void removeNode() {
        System.out.println("CS > Main Menu > Circuit Editor > Edit Nodes > Remove Node");
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