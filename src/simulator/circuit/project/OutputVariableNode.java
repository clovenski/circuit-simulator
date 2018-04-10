package simulator.circuit.project;

public class OutputVariableNode extends CSNode implements VariableInput {
    private CSNode inputNode;

    public OutputVariableNode(String name) {
        super(name);
    }

    public void addInputNode(CSNode inputNode) {
        this.inputNode = inputNode;
    }

    public void removeInputNode(CSNode node) {
        if(node == inputNode)
            inputNode = null;
    }
    
    public void updateValue() {
        if(inputNode != null)
            value = inputNode.value;
        else
            value = 0;
    }
}