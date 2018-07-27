package simulator.circuit.project;

/**
 * Class to represent the output variable nodes in a circuit.
 * <p>
 * These type of nodes represent the end points of a circuit, typically
 * used to observe the results of the circuit.
 * 
 * @author Joel Tengco
 */
public class OutputVariableNode extends CSNode implements VariableInput {
    /**
    * Needed to implement Serializable, and thus needed for saving circuits as files.
    */
    private static final long serialVersionUID = 1L;
    /**
     * The node that this output variable node's value depends on.
     */
    private CSNode inputNode;

    /**
     * Constructs a new output variable node with the specified name.
     * <p>
     * The type of this node is "OUTPUT".
     * 
     * @param name string to represent this output variable node with
     */
    public OutputVariableNode(String name) {
        super(name, "OUTPUT");
    }

    /**
     * Adds a new input node for this output variable node.
     * <p>
     * The previous input node, if it exists, will be replaced.
     * 
     * @param inputNode the new input node
     */
    public void addInputNode(CSNode inputNode) {
        this.inputNode = inputNode;
    }

    /**
     * Removes the specified node from this output variable node.
     * <p>
     * If the specified node is the one that this output variable node
     * currently depends on, it is then removed as a dependency; otherwise
     * no change is made.
     * 
     * @param node the node to remove from this output variable node
     */
    public void removeInputNode(CSNode node) {
        if(node == inputNode)
            inputNode = null;
    }

    /**
     * Update this output variable node's value.
     * <p>
     * The value will simply become what its input node's value currently
     * is. If there does not exist an input node, then the value is updated
     * to 0.
     */
    public void updateValue() {
        if(inputNode != null)
            value = inputNode.value;
        else
            value = 0;
    }
}