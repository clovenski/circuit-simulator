package simulator.circuit.project;

public class CircuitSimulator {
    private CSEngine engine;

    public CircuitSimulator() {
        engine = new CSEngine();
    }

    public void testProgram() {
        engine.addInputNode("inputnode-1");
        engine.addDFFNode("dffnode-1");
        engine.addOutputNode("outputnode-1");
        engine.addOutputNode("outputnode-2");
        engine.addAndGate("andgate-1");
        engine.addConnection("inputnode-1", "andgate-1");
        engine.addConnection("inputnode-1", "dffnode-1");
        engine.addConnection("dffnode-1-out", "andgate-1");
        engine.addConnection("andgate-1", "outputnode-1");
        engine.addConnection("dffnode-1-outnegated", "outputnode-2");
        engine.addOrGate("orgate-1");
        engine.addConnection("orgate-1", "outputnode-2");
        engine.addConnection("dffnode-1-outnegated", "orgate-1");
        engine.addConnection("inputnode-1", "orgate-1");
        engine.addInverter("orgate-1", "outputnode-2");
        // engine.removeConnection("orgate-1", ""); uncomment when implement a way to remove a connection that includes an inverter
        engine.removeConnection("inputnode-1", "orgate-1");
        engine.removeNode("7-5-inverter");
        int[] sequence = new int[] {1, 1, 0, 0, 1, 0, 1, 1, 1};
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
    }

    public static void main(String[] args) {
        new CircuitSimulator().testProgram();
    }
}