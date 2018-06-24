package simulator.circuit.project;

import java.io.Serializable;

// wraps the similarities of all the types of nodes in the graph, ie. DFlipFlop, AND gate, input variables, etc.
public abstract class CSNode implements Serializable {
    protected int value;
    protected String name;
    protected int trackNum;
    private final String NODE_TYPE;
    private static final long serialVersionUID = 1L;
    
    public CSNode(String name, String type) {
        value = 0;
        this.name = name;
        NODE_TYPE = type;
        trackNum = 0;
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

    public void setTrackNum(int trackNum) {
        this.trackNum = trackNum;
    }

    public int getTrackNum() {
        return trackNum;
    }

    public void resetTrackNum() {
        trackNum = 0;
    }

    public String getNodeType() {
        return NODE_TYPE;
    }

    public abstract void updateValue();
}