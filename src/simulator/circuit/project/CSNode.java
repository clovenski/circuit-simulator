package simulator.circuit.project;

// wraps the similarities of all the types of nodes in the graph, ie. DFlipFlop, AND gate, input variables, etc.
// extend this class with appropriate functionality in its updateValue() method according to its purpose
public abstract class CSNode {
    private int value;

    public abstract void updateValue();
}