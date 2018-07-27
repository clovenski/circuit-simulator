package simulator.circuit.project;

/**
 * Class to represent XOR gates in a circuit.
 * <p>
 * XOR gates contain a value of 1 if and only if an odd number of its input
 * nodes contain a value of 1. Otherwise, it contains a value of 0.
 */
public class XorGate extends Gate {
    /**
    * Needed to implement Serializable, and thus needed for saving circuits as files.
    */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an XOR gate with the specified name.
     * <p>
     * The type of this node is "XOR".
     * 
     * @param name string to represent this XOR gate with
     */
    public XorGate(String name) {
        super(name, "XOR");
    }

    // Note: implementing the XOR logic that odd number of 1's result in an output value of 1, 0 otherwise
    /**
     * Update this XOR gate's value.
     * <p>
     * Value will be updated to 1 if and only if an odd number of its input
     * nodes contain a value of 1, otherwise it is updated to 0.
     * If no input nodes exist, the value is simply updated to 0.
     */
    public void updateValue() {
        int count = 0;

        if(inputNodes.size() == 0) {
            value = 0;
            return;
        }

        for(CSNode node : inputNodes)
            if(node.value == 1)
                count++;

        // value is assigned 1 if count is odd, 0 otherwise
        value = (count & 1) == 1 ? 1 : 0;
    }
}