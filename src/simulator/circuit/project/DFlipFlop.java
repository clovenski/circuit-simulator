package simulator.circuit.project;

public class DFlipFlop extends FlipFlop implements VariableInput {
    private static final long serialVersionUID = 1L;
    private CSNode inputNode;

    public DFlipFlop(String name) {
        super(name, "DFF");
    }

    public void addInputNode(CSNode inputNode) {
        this.inputNode = inputNode;
    }

    public void removeInputNode(CSNode node) {
        if(node == inputNode)
            inputNode = null;
    }

    protected int getValue(FFOutNode invokingOutNode) throws IllegalDFFStateException {
        if(invokingOutNode == outNode)
            return value;
        else if(invokingOutNode == outNodeNegated)
            return value == 1 ? 0 : 1;
        else throw new IllegalDFFStateException(this.name);
    }

    public void updateValue() {
        if(inputNode != null)
            value = inputNode.value;
        else
            value = 0;
    }

    class IllegalDFFStateException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public IllegalDFFStateException() {
            super("The current state of this graph has been corrupted, some flip flop does not have a reference to its appropriate output nodes.");
        }
        public IllegalDFFStateException(String nodeID) {
            super("The current state of this graph has been corrupted, " + nodeID + " does not have a reference to its appropriate output nodes.");
        }
    }
}