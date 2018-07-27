package simulator.circuit.project;

/**
 * Class that will represent AND gates in a circuit.
 * <p>
 * Depending on the set of nodes that provide input to this node,
 * this node will contain a value of 1 if and only if all of the input
 * node values are 1; otherwise this node will have a value of 0.
 * 
 * @author Joel Tengco
 */
public class AndGate extends Gate {
    /**
     * Needed to implement Serializable, and thus needed for saving circuits as files.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor to create the AND gate with the specified name.
     * <p>
     * The type of this node is "AND".
     * 
     * @param name the string to identify this AND gate with
     */
    public AndGate(String name) {
        super(name, "AND");
    }

    /**
     * Update this AND gate's value.
     * <p>
     * This node's value will be updated to 1 if and only if all of its input node
     * values are 1; otherwise it will update to 0. If no input nodes exist, then
     * the value is simply updated to 0.
     */
    public void updateValue() {
        if(inputNodes.size() == 0) {
            value = 0;
            return;
        }

        for(CSNode node : inputNodes)
            if(node.value == 0) {
                value = 0;
                return;
            }
        // all input node values were 1
        value = 1;
    }
}