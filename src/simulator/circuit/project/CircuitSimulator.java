package simulator.circuit.project;

import java.util.ArrayList;

public class CircuitSimulator {
    private CSGraph circuit;
    private ArrayList<String> inputNodes;

    public CircuitSimulator() {

    }

    private void addInputNode(String nodeID) {
        circuit.addNode(new InputVariableNode(nodeID));
    }

    private void addOutputNode(String nodeID) {
        circuit.addNode(new OutputVariableNode(nodeID));
    }
    
    private void addDFFNode(String nodeID) {
        DFlipFlop newNode = new DFlipFlop(nodeID);
        circuit.addNode(newNode);
        circuit.addNode(new FFOutNode(nodeID + "-out", newNode));
        circuit.addNode(new FFOutNode(nodeID + "-outnegated", newNode));

        // add appropriate edges
        circuit.addEdge(circuit.getSize() - 3, circuit.getSize() - 2);
        circuit.addEdge(circuit.getSize() - 3, circuit.getSize() - 1);
    }

    private void addAndGate(String nodeID) {
        circuit.addNode(new AndGate(nodeID));
    }

    private void addOrGate(String nodeID) {
        circuit.addNode(new OrGate(nodeID));
    }

    private void addInverter(String sourceNodeID, String targetNodeID) throws IllegalArgumentException {
        if(!circuit.contains(sourceNodeID))
            throw new IllegalArgumentException("This circuit does not contain node: " + sourceNodeID);
        if(!circuit.contains(targetNodeID))
            throw new IllegalArgumentException("This circuit does not contain node: " + targetNodeID);

        int sourceIndex = circuit.indexOf(sourceNodeID);
        int targetIndex = circuit.indexOf(targetNodeID);
        if(!circuit.containsEdge(sourceIndex, targetIndex))
            throw new IllegalArgumentException("There does not exist an edge from " + sourceNodeID + " to " + targetNodeID);

        circuit.removeEdge(sourceIndex, targetIndex);
        circuit.addNode(new Inverter(sourceIndex + "-" + targetIndex + "inverter", circuit.getNode(sourceIndex)));
        int newInverterIndex = circuit.getSize() - 1;

        circuit.addEdge(sourceIndex, newInverterIndex);
        circuit.addEdge(newInverterIndex, targetIndex);
        
        Dependent depNode = (Dependent)circuit.getNode(targetIndex);
        depNode.addInputNode(circuit.get(newInverterIndex));
    }

    private void setInputSeq(String inputNodeID, int[] newSeq) {
        int nodeIndex = circuit.indexOf(inputNodeID);
        InputVariableNode node = (InputVariableNode)circuit.getNode(nodeIndex);

        node.setInputSeq(newSeq);
    }

    public static void main(String[] args) {

    }
}