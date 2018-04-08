package simulator.circuit.project;

public abstract class FlipFlop extends CSNode {
    // protected FFOutNode outNode;
    // protected FFOutNode outNodeNegated;

    public FlipFlop(String name) {
        super(name);
    }

    // public void setOutNodes(FFOutNode outNode, FFOutNode outNodeNegated) {
    //     this.outNode = outNode;
    //     this.outNodeNegated = outNodeNegated;
    // }

    public abstract int getValue(FFOutNode invokingOutNode);
}