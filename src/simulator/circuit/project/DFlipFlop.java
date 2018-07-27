package simulator.circuit.project;

/**
 * Class to represent D flip-flops in a circuit.
 * <p>
 * D flip-flops behave in a way such that its two output nodes' values in
 * the next clock cycle will correspond to this node's current value.
 * For example, if this node has a value of 1 and its output nopde (non-negated)
 * has a value of 0, then in the next clock cycle, the output node will
 * contain a value of 1.
 * <p>
 * This type of delay is what allows for sequential circuits to be designed.
 * 
 * @author Joel Tengco
 */
public class DFlipFlop extends FlipFlop implements VariableInput {
    /**
     * Needed to implement Serializable, and thus needed for saving circuits as files.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Node that this D flip-flop's value will depend on.
     */
    private CSNode inputNode;

    /**
     * Constructor to create a D flip-flop with the specified name.
     * <p>
     * The type of this node is "DFF".
     * 
     * @param name the name to identify this D flip-flop with
     */
    public DFlipFlop(String name) {
        super(name, "DFF");
    }

    /**
     * Adds an input node for this D flip-flop's value to depend on.
     * <p>
     * This method will overwrite the current input node reference, if it exists.
     * 
     * @param inputNode the new node that this D flip-flop's value will depend on
     */
    public void addInputNode(CSNode inputNode) {
        this.inputNode = inputNode;
    }

    /**
     * Removes the specified node as a dependency for this D flip-flop.
     * <p>
     * If this D flip-flop's value depends on the specified node, then that
     * dependency is removed. Otherwise, no change is made.
     * 
     * @param node the node to remove as a dependency for this D flip-flop
     */
    public void removeInputNode(CSNode node) {
        if(node == inputNode)
            inputNode = null;
    }

    /**
     * Gets the current value of this D flip-flop.
     * <p>
     * This method is designed to provide the interface between a {@code DFlipFlop} object
     * and its corresponding {@code FFOutNode} objects. The value returned depends on which
     * output node is given as an argument; whether the output node is negated or not. For
     * non-negated output nodes, the value of this D flip-flop is returned, otherwise the
     * complement of the value is returned for negated output nodes.
     * <p>
     * If an output node is given that does not correspond to this D flip-flop, then an
     * exception is thrown.
     * 
     * @param invokingOutNode the output node that is requesting this D flip-flop's value
     * @return this D flip-flop's current value if the invoking output node is non-negated,
     * otherwise the complement is returned for negated output nodes
     * @throws IllegalDFFStateException if the given {@code FFOutNode} object does not correspond
     * to this D flip-flop
     */
    protected int getValue(FFOutNode invokingOutNode) throws IllegalDFFStateException {
        if(invokingOutNode == outNode)
            return value;
        else if(invokingOutNode == outNodeNegated)
            return value == 1 ? 0 : 1;
        else throw new IllegalDFFStateException(this.name);
    }

    /**
     * Updates the value of this D flip-flop.
     * <p>
     * If this D flip-flop's input node exists, then the D flip-flop's value is updated to the input
     * node's value. Otherwise the value is updated to 0.
     */
    public void updateValue() {
        if(inputNode != null)
            value = inputNode.value;
        else
            value = 0;
    }

    /**
     * Exception to represent that the graph is corrupted, where the defect lies in the connection between
     * a D flip-flop and its corresponding output nodes; or a lack thereof.
     * 
     * @author Joel Tengco
     */
    class IllegalDFFStateException extends RuntimeException {
        /**
         * Needed to implement Serializable, and thus needed for saving circuits as files.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Constructor for this exception.
         * <p>
         * The exception is constructed with the message:
         * <p>
         * "The current state of this graph has been corrupted, some flip flop does not have a reference to its appropriate output nodes."
         */
        public IllegalDFFStateException() {
            super("The current state of this graph has been corrupted, some flip flop does not have a reference to its appropriate output nodes.");
        }

        /**
         * Constructs the exception with the specified node ID.
         * <p>
         * The exception is constructed with the message:
         * <p>
         * "The current state of this graph has been corrupted, [nodeID] does not have a reference to its appropriate output nodes."
         */
        public IllegalDFFStateException(String nodeID) {
            super("The current state of this graph has been corrupted, " + nodeID + " does not have a reference to its appropriate output nodes.");
        }
    }
}