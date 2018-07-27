package simulator.circuit.project;

import simulator.circuit.project.DFlipFlop.IllegalDFFStateException;

/**
 * Abstract class that encapsulates the common properties of a flip-flop.
 * <p>
 * Each flip-flop has two {@code FFOutNode} objects corresponding to it.
 * Thus, subclasses of this class will represent a flip-flop's next state,
 * while its two output nodes represent a flip-flop's present state; with one
 * specifically being the negated output of the flip-flop.
 * 
 * @author Joel Tengco
 */
public abstract class FlipFlop extends CSNode {
    /**
     * Needed to implement Serializable, and thus needed for saving circuits as files.
     */
    private static final long serialVersionUID = 1L;
    /**
     * One of the output nodes for this flip-flop.
     */
    protected FFOutNode outNode;
    /**
     * One of the output nodes for this flip-flop, specifically being the flip-flop's negated output.
     */
    protected FFOutNode outNodeNegated;

    /**
     * Constructs a flip-flop with the specified name and node type.
     * 
     * @param name string to represent this flip-flop's name
     * @param type the string to classify this node with
     */
    public FlipFlop(String name, String type) {
        super(name, type);
    }

    /**
     * Sets the output nodes of this flip-flop.
     * 
     * @param outNode the normal, non-negated output node for this flip-flop
     * @param outNodeNegated the negated output node for this flip-flop
     */
    public void setOutNodes(FFOutNode outNode, FFOutNode outNodeNegated) {
        this.outNode = outNode;
        this.outNodeNegated = outNodeNegated;
    }

    /**
     * Abstract method that should handle the connection between a flip-flop and its
     * output nodes. Depending on the output node given as an argument, return the
     * appropriate value.
     * 
     * @param invokingOutNode corresponding output node of the flip-flop pertaining to this method call
     * @return the value of this flip-flop according to its relationship with the output node given
     * as an argument
     * @throws IllegalDFFStateException if the invoking output node does not correspond to this flip-flop
     */
    protected abstract int getValue(FFOutNode invokingOutNode) throws IllegalDFFStateException;
}