package simulator.circuit.project;

/**
 * Class that will represent inverters in a circuit.
 * <p>
 * Inverters simply provide the complement value of the node it is
 * inverting.
 * 
 * @author Joel Tengco
 */
public class Inverter extends CSNode {
    /**
    * Needed to implement Serializable, and thus needed for saving circuits as files.
    */
    private static final long serialVersionUID = 1L;
    /**
     * The node that this inverter corresponds to.
     */
    private final CSNode inputNode;

    /**
     * Constructs a new inverter with the specified name and node to invert.
     * <p>
     * The type of this node is "INVERT".
     * 
     * @param name string to represent this inverter's name
     * @param inputNode the node whose value this inverter will invert
     */
    public Inverter(String name, CSNode inputNode) {
        super(name, "INVERT");
        this.inputNode = inputNode;
    }

    /**
     * Gets a reference to the node that this inverter corresponds to.
     * 
     * @return the node whose value this inverter is inverting
     */
    public CSNode getInputNode() {
        return inputNode;
    }

    /**
     * Update this inverter's value.
     * <p>
     * The new value will simply be the complement of its input node's value.
     */
    public void updateValue() {
        value = inputNode.value == 1 ? 0 : 1;
    }
}