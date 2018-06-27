package simulator.circuit.project;

import simulator.circuit.project.DFlipFlop.IllegalDFFStateException;

public abstract class FlipFlop extends CSNode {
    private static final long serialVersionUID = 1L;
    protected FFOutNode outNode;
    protected FFOutNode outNodeNegated;

    public FlipFlop(String name, String type) {
        super(name, type);
    }

    public void setOutNodes(FFOutNode outNode, FFOutNode outNodeNegated) {
        this.outNode = outNode;
        this.outNodeNegated = outNodeNegated;
    }

    protected abstract int getValue(FFOutNode invokingOutNode) throws IllegalDFFStateException;
}