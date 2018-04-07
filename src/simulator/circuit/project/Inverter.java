package simulator.circuit.project;

public class Inverter extends CSNode {
    private final CSNode inputNode;

    public Inverter(String name, CSNode inputNode) {
        super(name);
        this.inputNode = inputNode;
    }

    public void updateValue() {
        value = inputNode.value == 1 ? 0 : 1;
    }
}