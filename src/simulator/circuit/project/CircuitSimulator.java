package simulator.circuit.project;

import java.util.ArrayList;
import java.util.StringTokenizer;

import simulator.circuit.project.CSGraph.IllegalCircuitStateException;

public class CircuitSimulator {
    private CSGraph circuit;
    private ArrayList<String> inputNodes;

    public CircuitSimulator() {
        circuit = new CSGraph();
        inputNodes = new ArrayList<String>();
    }

    private void addInputNode(String nodeID) {
        circuit.addNode(new InputVariableNode(nodeID));
        inputNodes.add(nodeID);
    }

    private void addOutputNode(String nodeID) {
        circuit.addNode(new OutputVariableNode(nodeID));
    }
    
    private void addDFFNode(String nodeID) {
        DFlipFlop newDFFNode = new DFlipFlop(nodeID);
        FFOutNode newDFFNodeOut = new FFOutNode(nodeID + "-out", newDFFNode);
        FFOutNode newDFFNodeOutNeg = new FFOutNode(nodeID + "-outnegated", newDFFNode);
        circuit.addNode(newDFFNode);
        circuit.addNode(newDFFNodeOut);
        circuit.addNode(newDFFNodeOutNeg);
        newDFFNode.setOutNodes(newDFFNodeOut, newDFFNodeOutNeg);

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

    private void addConnection(String sourceNodeID, String targetNodeID) throws IllegalArgumentException {
        CSNode sourceNode = circuit.getNode(sourceNodeID);
        CSNode targetNode = circuit.getNode(targetNodeID);

        if(sourceNode instanceof OutputVariableNode || sourceNode instanceof DFlipFlop)
            throw new IllegalArgumentException(sourceNodeID + " cannot be a source of a connection");
        if(!(targetNode instanceof VariableInput))
            throw new IllegalArgumentException(targetNodeID + " cannot be a target of a connection");
        
        int sourceIndex = circuit.indexOf(sourceNodeID);
        int targetIndex = circuit.indexOf(targetNodeID);

        // update target node's input reference
        VariableInput variableInputNode = (VariableInput)targetNode;
        variableInputNode.addInputNode(sourceNode);

        // if target node was not a Gate, then first need to remove previous connections, if any
        if(!(variableInputNode instanceof Gate))
            for(int i = 0; i < circuit.getSize(); i++)
                circuit.removeEdge(i, targetIndex);
                
        // now add the edge
        circuit.addEdge(sourceIndex, targetIndex);
    }

    private void addInverter(String sourceNodeID, String targetNodeID) throws IllegalArgumentException {
        int sourceIndex = circuit.indexOf(sourceNodeID);
        int targetIndex = circuit.indexOf(targetNodeID);

        if(sourceIndex == -1)
            throw new IllegalArgumentException("This graph does not contain " + sourceNodeID);
        if(targetIndex == -1)
            throw new IllegalArgumentException("This graph does not contain " + targetNodeID);
        if(!circuit.containsEdge(sourceIndex, targetIndex))
            throw new IllegalArgumentException("There does not exist an edge from " + sourceNodeID + " to " + targetNodeID);

        circuit.removeEdge(sourceIndex, targetIndex);
        circuit.addNode(new Inverter(sourceIndex + "-" + targetIndex + "inverter", circuit.getNode(sourceIndex)));
        int newInverterIndex = circuit.getSize() - 1;

        circuit.addEdge(sourceIndex, newInverterIndex);
        circuit.addEdge(newInverterIndex, targetIndex);
        
        VariableInput variableInputNode = (VariableInput)circuit.getNode(targetIndex);
        variableInputNode.addInputNode(circuit.getNode(newInverterIndex));
    }

    private void setInputSeq(String inputNodeID, int[] newSeq) {
        int nodeIndex = circuit.indexOf(inputNodeID);
        InputVariableNode node = (InputVariableNode)circuit.getNode(nodeIndex);

        node.setInputSeq(newSeq);
    }

    private void printCircuitHeader() {
        for(int i = 0; i < circuit.getSize(); i++) {
            String nodeID = circuit.getNode(i).getName();
            System.out.printf("%20s", nodeID);
        }
        System.out.println();
    }

    private void printCircuitStatus() {
        for(int i = 0; i < circuit.getSize(); i++)
            System.out.printf("%20d", circuit.getNode(i).getValue());

        System.out.println();
    }

    private void updateCircuit() {
        String updatePath = "";

        try {
            updatePath = circuit.getUpdatePath();
        } catch(IllegalCircuitStateException icse) {
            System.err.println(icse.getMessage());
            return;
        } catch(Exception e) {
            System.err.println(e.getMessage());
            return;
        }

        StringTokenizer tokenizer = new StringTokenizer(updatePath);

        while(tokenizer.hasMoreTokens())
            circuit.getNode(Integer.valueOf(tokenizer.nextToken())).updateValue();
    }
    
    public void testProgram() {
        addInputNode("inputnode-1");
        addDFFNode("dffnode-1");
        addOutputNode("outputnode-1");
        addOutputNode("outputnode-2");
        addAndGate("andgate-1");
        addConnection("inputnode-1", "andgate-1");
        addConnection("inputnode-1", "dffnode-1");
        addConnection("dffnode-1-out", "andgate-1");
        addConnection("andgate-1", "outputnode-1");
        addConnection("dffnode-1-outnegated", "outputnode-2");
        int[] sequence = new int[] {1, 1, 0, 0, 1, 0, 1, 1, 1};
        setInputSeq("inputnode-1", sequence);

        printCircuitHeader();
        for(int i = 0; i < sequence.length; i++) {
            updateCircuit();
            printCircuitStatus();
        }
        try{
        System.out.println(circuit.getUpdatePath()); }catch(IllegalCircuitStateException icse) {
            
        }
        for(int i = 0; i < circuit.edges.size(); i++)
            System.out.println(circuit.edges.get(i));
    }

    public static void main(String[] args) {
        new CircuitSimulator().testProgram();
    }
}