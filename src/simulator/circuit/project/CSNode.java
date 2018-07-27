package simulator.circuit.project;

import java.io.Serializable;

/**
 * Circuit Simulator node to represent entities within each circuit.
 * <p>
 * This is the super class to every single entity/node in a circuit.
 * Each node will have a value, name, track number and type. The purpose of this class
 * is to wrap all of the similarities between each entity within a graph such as a DFlipFlop,
 * AND gate, input variable, etc.
 * 
 * @author Joel Tengco
 */
public abstract class CSNode implements Serializable {
    /**
     * Needed to implement Serializable, and thus needed for saving circuits as files.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The current value that this node has within the circuit; either 1 or 0.
     */
    protected int value;        // note: package needs access for building truth and transition tables in CSEngine
    /**
     * String to identify this node with.
     */
    protected String name;
    /**
     * The node's track number within the program.
     * <p>
     * If this field is zero, this node is not being tracked by the program,
     * otherwise a positive number indicates its track number.
     */
    protected int trackNum;
    /**
     * String that is at most 6 characters to represent this node's type.
     * <p>
     * For example, input variables will have a node type of "INPUT".
     */
    private final String NODE_TYPE;         // node type can be at most 6 characters, for formatting in menus
    
    /**
     * Constructs a new CSNode object with the given name and type.
     * 
     * @param name identifier for this node
     * @param type type of this node; if a string longer than 6 characters is given,
     * then only the first 6 characters are used
     */
    public CSNode(String name, String type) {
        value = 0;
        this.name = name;
        if(type.length() <= 6)
            NODE_TYPE = type;
        else
            NODE_TYPE = type.substring(0, 6);
        trackNum = 0;
    }

    /**
     * Sets a new name for this node.
     * 
     * @param newName string representing the new name for this node
     */
    public void setName(String newName) {
        name = newName;
    }

    /**
     * Gets this node's name/identifier.
     * 
     * @return the current name of this node
     */
    public String getName() {
        return name;
    }

    /**
     * Gets this node's current value.
     * 
     * @return either 1 or 0 depending on this node's status in the circuit
     */
    public int getValue() {
        return value;
    }

    /**
     * Resets this node's value back to zero.
     */
    public void resetValue() {
        value = 0;
    }

    /**
     * Sets this node's track number.
     * 
     * @param trackNum positive integer representing this node's new track number
     */
    public void setTrackNum(int trackNum) {
        if(trackNum > 0)
            this.trackNum = trackNum;
    }

    /**
     * Gets this node's track number.
     * 
     * @return integer greater than or equal to zero; representing the track number
     */
    public int getTrackNum() {
        return trackNum;
    }

    /**
     * Resets this node's track number back to zero, indicating this node is not
     * being tracked.
     */
    public void resetTrackNum() {
        trackNum = 0;
    }

    /**
     * Gets this node's type.
     * 
     * @return a string that is at most 6 characters long, representing this node's
     * type
     */
    public String getNodeType() {
        return NODE_TYPE;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CSNode) {
            CSNode otherNode = (CSNode)obj;
            return NODE_TYPE.equals(otherNode.NODE_TYPE) && name.equals(otherNode.name)
                && trackNum == otherNode.trackNum;
        } else return false;
    }

    @Override
    public int hashCode() {
        return NODE_TYPE.hashCode() + name.hashCode() + trackNum;
    }

    /**
     * Update this node's value in the circuit.
     * <p>
     * In other words, this node's value field will be updated in a way that
     * corresponds to how this node functions within a working circuit.
     */
    public abstract void updateValue();
}