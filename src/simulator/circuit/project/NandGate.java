package simulator.circuit.project;

/**
 * Class that will represent NAND gates in a circuit.
 * <p>
 * This node will have a value of 0 if and only if all of its
 * input nodes contain a value of 1. Otherwise, it has a value of 1.
 * 
 * @author Joel Tengco
 */
public class NandGate extends Gate {
    /**
    * Needed to implement Serializable, and thus needed for saving circuits as files.
    */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new NAND gate with the specified name.
     * <p>
     * The type of this node is "NAND".
     * 
     * @param name string to represent this NAND gate with
     */
    public NandGate(String name) {
        super(name, "NAND");
    }

    /**
     * Update this NAND gate's value.
     * <p>
     * The node's value will be updated to 0 if and only if all of
     * its input nodes contain a value of 1; otherwise it is updated to 1.
     * If no input nodes exist, the value is simply updated to 0.
     */
    public void updateValue() {
        if(inputNodes.size() == 0) {
            value = 0;
            return;
        }

        for(CSNode node : inputNodes)
            if(node.value == 0) {
                value = 1;
                return;
            }
        // all input node values were 1
        value = 0;
    }
}