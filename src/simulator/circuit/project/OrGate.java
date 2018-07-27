package simulator.circuit.project;

/**
 * Class to represent OR gates in a circuit.
 * <p>
 * OR gates contain a value of 1 if at least one of its input
 * nodes contain a value of 1. Otherwise, they have a value of 0.
 */
public class OrGate extends Gate {
    /**
    * Needed to implement Serializable, and thus needed for saving circuits as files.
    */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an OR gate with the specified name.
     * <p>
     * The type of this node is "OR".
     * 
     * @param name string to represent this OR gate with
     */
    public OrGate(String name) {
        super(name, "OR");
    }

    /**
     * Update this OR gate's value.
     * <p>
     * Value will be updated to 1 if at least one of its input nodes contain
     * a value of 1; otherwise it is updated to 0.
     */
    public void updateValue() {
        for(CSNode node : inputNodes)
            if(node.value == 1) {
                value = 1;
                return;
            }
        // all input node values were 0
        value = 0;
    }
}