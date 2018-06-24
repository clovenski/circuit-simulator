package simulator.circuit.project;

import java.util.ArrayList;

public abstract class Gate extends CSNode implements VariableInput {
    private static final long serialVersionUID = 1L;
    protected ArrayList<CSNode> inputNodes;

    public Gate(String name, String type) {
        super(name, type);
        inputNodes = new ArrayList<CSNode>();
    }

    public void addInputNode(CSNode node) {
        inputNodes.add(node);
    }

    public void removeInputNode(CSNode node) {
        inputNodes.remove(node);
    }
}