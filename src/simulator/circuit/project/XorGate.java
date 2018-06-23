package simulator.circuit.project;

public class XorGate extends Gate {
    private static final long serialVersionUID = 1L;

    public XorGate(String name) {
        super(name);
    }

    // Note: implementing the XOR logic that odd number of 1's result in an output value of 1, 0 otherwise
    public void updateValue() {
        int count = 0;

        for(CSNode node : inputNodes)
            if(node.value == 1)
                count++;

        // value is assigned 1 if count is odd, 0 otherwise
        value = (count & 1) == 1 ? 1 : 0;
    }
}