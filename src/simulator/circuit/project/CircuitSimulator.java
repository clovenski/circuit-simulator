package simulator.circuit.project;

import java.util.Arrays; // imported for testing, remove later

public class CircuitSimulator {
    private CSEngine engine;

    public CircuitSimulator() {
        engine = new CSEngine();
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
        new CircuitSimulator().testProgram();
    }
}