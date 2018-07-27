package simulator.circuit.project;

/**
 * Class to represent the output nodes of a flip-flop.
 * <p>
 * This class, combined with {@code FlipFlop} objects, represent the flip-flop
 * entities within a circuit. These output nodes will represent a flip-flop's
 * present state.
 * 
 * @author Joel Tengco
 */
public class FFOutNode extends CSNode {
    /**
     * Needed to implement Serializable, and thus needed for saving circuits as files.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The flip-flop that this output node corresponds to.
     */
    private final FlipFlop inputNode;

    /**
     * Constructs a flip-flop output node with the specified name and flip-flop.
     * <p>
     * The type of this node is "FFOUT".
     * 
     * @param name string to identify this output node with
     * @param inputNode the flip-flop that this output node corresponds to
     */
    public FFOutNode(String name, FlipFlop inputNode) {
        super(name, "FFOUT");
        this.inputNode = inputNode;
    }

    /**
     * Update this output node's value.
     * <p>
     * The new value for this node will depend on its relationship
     * with the flip-flop that it corresponds to.
     */
    public void updateValue() {
        value = inputNode.getValue(this);
    }
}