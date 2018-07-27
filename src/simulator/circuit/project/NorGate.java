package simulator.circuit.project;

/**
 * Class to represent NOR gates in a circuit.
 * <p>
 * This node will contain a value of 1 if and only if all of its
 * input nodes contain a value of 0. Otherwise, it has a value of 0.
 */
public class NorGate extends Gate {
    /**
    * Needed to implement Serializable, and thus needed for saving circuits as files.
    */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a NOR gate with the specified name.
     * <p>
     * The type of this node is "NOR".
     * 
     * @param name string to represent this NOR gate with
     */
    public NorGate(String name) {
        super(name, "NOR");
    }

    /**
     * Update this NOR gate's value.
     * <p>
     * The value will be updated to 1 if and only if all of its
     * input nodes contain a value of 0; otherwise it is updated to 0.
     */
    public void updateValue() {
        for(CSNode node : inputNodes)
            if(node.value == 1) {
                value = 0;
                return;
            }
        // all input node values were 0
        value = 1;
    }
}