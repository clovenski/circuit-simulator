package simulator.circuit.project;

public class DFlipFlop extends FlipFlop implements Dependent {
    private CSNode inputNode;

    public DFlipFlop(String name) {
        super(name);
    }

    public void addInputNode(CSNode inputNode) {
        this.inputNode = inputNode;
    }

    public void removeInputNode(CSNode node) {
        if(node == inputNode)
            inputNode = null;
    }

    public int getValue(FFOutNode invokingOutNode) {
        if(invokingOutNode == outNode)
            return value;
        else if(invokingOutNode == outNodeNegated)
            return value == 1 ? 0 : 1;
        else throw new Exceptiopn(); // REPLACE THIS EXCEPTION LATER
    }

    public void updateValue() {
        value = inputNode.value;
    }
}