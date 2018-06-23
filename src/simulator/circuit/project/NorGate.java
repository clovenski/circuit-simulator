package simulator.circuit.project;

public class NorGate extends Gate {
    private static final long serialVersionUID = 1L;

    public NorGate(String name) {
        super(name);
    }

    public void updateValue() {
        for(CSNode node : inputNodes)
            if(node.value == 1) {
                value = 0;
                return;
            }
        // all input node values were 0
        value = 1;
    }
}