package simulator.circuit.project;

public abstract class FlipFlop extends CSNode {
    public FlipFlop(String name) {
        super(name);
    }

    public abstract int getValue(FFOutNode invokingOutNode);
}