package simulator.circuit.project;

import java.io.Serializable;

// wraps the similarities of all the types of nodes in the graph, ie. DFlipFlop, AND gate, input variables, etc.
// extend this class with appropriate functionality in its updateValue() method according to its purpose
public abstract class CSNode implements Serializable {
    protected int value;
    protected String name;
    private static final long serialVersionUID = 1L;
    
    public CSNode(String name) {
        value = 0;
        this.name = name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public void resetValue() {
        value = 0;
    }

    public abstract void updateValue();
}