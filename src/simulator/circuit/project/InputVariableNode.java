package simulator.circuit.project;

public class InputVariableNode extends CSNode {
    public InputVariableNode(String name) {
        super(name);
    }

    public void updateValue(int input) {
        value = input;
    }
}