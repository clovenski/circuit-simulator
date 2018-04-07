package simulator.circuit.project;

public class FFOutNode extends CSNode {
    protected FlipFlop inputNode;

    public FFOutNode(String name, FlipFlop inputNode) {
        super(name);
        this.inputNode = inputNode;
    }

    public void addInputNode(FlipFlop inputNode) {
        this.inputNode = inputNode;
    }

    public void removeInputNode() {
        inputNode = null;
    }

    public void updateValue() {
        inputNode.getValue(this);
    }
}