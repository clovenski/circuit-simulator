package simulator.circuit.project;

public class NandGate extends Gate {
    private static final long serialVersionUID = 1L;

    public NandGate(String name) {
        super(name, "NAND");
    }

    public void updateValue() {
        for(CSNode node : inputNodes)
            if(node.value == 0) {
                value = 1;
                return;
            }
        // all input node values were 1
        value = 0;
    }
}