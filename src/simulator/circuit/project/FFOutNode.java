package simulator.circuit.project;

public class FFOutNode extends CSNode {
    private static final long serialVersionUID = 1L;
    private final FlipFlop inputNode;

    public FFOutNode(String name, FlipFlop inputNode) {
        super(name);
        this.inputNode = inputNode;
    }

    public void updateValue() {
        value = inputNode.getValue(this);
    }
}