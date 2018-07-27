package simulator.circuit.project;

import java.util.ArrayList;

/**
 * Abstract class to encapsulate the common properties of a logical gate.
 * <p>
 * Each gate has a set of input nodes that altogether determine the value of
 * the gate.
 * 
 * @author Joel Tengco
 */
public abstract class Gate extends CSNode implements VariableInput {
    /**
     * Needed to implement Serializable, and thus needed for saving circuits as files.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Set of input nodes that this gate's value depends on.
     */
    protected ArrayList<CSNode> inputNodes;

    /**
     * Constructs a new gate with the specified name and node type.
     * 
     * @param name string to represent this gate's name
     * @param type string to represent this node's type
     */
    public Gate(String name, String type) {
        super(name, type);
        inputNodes = new ArrayList<CSNode>();
    }

    /**
     * Adds a new input node to this gate's set of input nodes.
     * 
     * @param node the new node that this gate will also depend on
     */
    public void addInputNode(CSNode node) {
        inputNodes.add(node);
    }

    /**
     * Remove a node from this gate's set of input nodes, if it exists.
     * 
     * @param node the node to remove from this gate's set of input nodes
     */
    public void removeInputNode(CSNode node) {
        inputNodes.remove(node);
    }
}