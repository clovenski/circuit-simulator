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
        ArrayList<String> options = new ArrayList<String>();
        options.add("Edit tracked nodes");
        options.add(""); // TODO: implement testing circuit

        System.out.println("\nCS > Main Menu > Circuit Tester");
    }
}