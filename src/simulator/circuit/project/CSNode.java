package simulator.circuit.project;

// wraps the similarities of all the types of nodes in the graph, ie. DFlipFlop, AND gate, input variables, etc.
// extend this class with appropriate functionality in its updateValue() method according to its purpose
public abstract class CSNode {
    protected int value;
    protected String name;
    
    public CSNode(String name) {
        value = 0;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public abstract void addInputNode();

    public abstract void removeInputNode();

    public abstract void updateValue();
}