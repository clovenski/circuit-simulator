package simulator.circuit.project;

public class AndGate extends Gate implements VariableInput {
    private static final long serialVersionUID = 1L;

    public AndGate(String name) {
        super(name);
    }

    public void updateValue() {
        for(CSNode node : inputNodes)
            if(node.value == 0) {
                value = 0;
                return;
            }
        // all input node values were 1
        value = 1;
    }
}