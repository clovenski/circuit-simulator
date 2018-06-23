package simulator.circuit.project;

public class OrGate extends Gate {
    private static final long serialVersionUID = 1L;
    
    public OrGate(String name) {
        super(name);
    }

    public void updateValue() {
        for(CSNode node : inputNodes)
            if(node.value == 1) {
                value = 1;
                return;
            }
        // all input node values were 0
        value = 0;
    }
}