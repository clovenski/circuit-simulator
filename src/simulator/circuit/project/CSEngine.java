package simulator.circuit.project;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.StringTokenizer;

import simulator.circuit.project.CSGraph.IllegalCircuitStateException;

public class CSEngine {
    private CSGraph circuit;
    private ArrayList<String> inputNodes;

    public CSEngine() {
        circuit = new CSGraph();
        inputNodes = new ArrayList<String>();
    }

    public void addInputNode(String nodeID) {
        circuit.addNode(new InputVariableNode(nodeID));
        inputNodes.add(nodeID);
    }

    public void addOutputNode(String nodeID) {
        circuit.addNode(new OutputVariableNode(nodeID));
    }
    
    public void addDFFNode(String nodeID) {
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

    public void addAndGate(String nodeID) {
        circuit.addNode(new AndGate(nodeID));
    }

    public void addOrGate(String nodeID) {
        circuit.addNode(new OrGate(nodeID));
    }

    public void addInverter(String sourceNodeID, String targetNodeID) throws IllegalArgumentException {
        int sourceIndex = circuit.indexOf(sourceNodeID);
        int targetIndex = circuit.indexOf(targetNodeID);

        if(sourceIndex == -1)
            throw new IllegalArgumentException("This circuit does not contain " + sourceNodeID);
        if(targetIndex == -1)
            throw new IllegalArgumentException("This circuit does not contain " + targetNodeID);
        if(!circuit.containsEdge(sourceIndex, targetIndex))
            throw new IllegalArgumentException("There does not exist an edge from " + sourceNodeID + " to " + targetNodeID);

        circuit.removeEdge(sourceIndex, targetIndex);
        circuit.addNode(new Inverter(sourceIndex + "-" + targetIndex + "-inverter", circuit.getNode(sourceIndex)));
        int newInverterIndex = circuit.getSize() - 1;

        circuit.addEdge(sourceIndex, newInverterIndex);
        circuit.addEdge(newInverterIndex, targetIndex);
        
        VariableInput variableInputNode = (VariableInput)circuit.getNode(targetIndex);
        variableInputNode.addInputNode(circuit.getNode(newInverterIndex));
    }

    public void addConnection(String sourceNodeID, String targetNodeID) throws IllegalArgumentException {
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

    public void removeConnection(String sourceNodeID, String targetNodeID) throws IllegalArgumentException {
        int sourceIndex = circuit.indexOf(sourceNodeID);
        int targetIndex = circuit.indexOf(targetNodeID);
        
        if(sourceIndex == -1)
            throw new IllegalArgumentException(sourceNodeID + " does not exist in this circuit");
        if(targetIndex == -1)
            throw new IllegalArgumentException(targetNodeID + " does not exist in this circuit");
        
        if(!(circuit.containsEdge(sourceIndex, targetIndex)))
            throw new IllegalArgumentException("The specified connection does not exist");

        CSNode sourceNode = circuit.getNode(sourceIndex);
        CSNode targetNode = circuit.getNode(targetIndex);

        if(sourceNode instanceof FlipFlop)
            throw new IllegalArgumentException("Please refer to this flip flop's output nodes instead");

        circuit.removeEdge(sourceIndex, targetIndex);
        VariableInput varInputNode = (VariableInput)targetNode;
        varInputNode.removeInputNode(circuit.getNode(sourceIndex));

        // inverters cannot exist without an input
        if(targetNode instanceof Inverter) {
            ArrayList<Integer> indecesToRemove = new ArrayList<Integer>();
            removeInverter(targetIndex, indecesToRemove);
            Collections.sort(indecesToRemove);
            for(int i = indecesToRemove.size() - 1; i >= 0; i--)
                circuit.removeNode(indecesToRemove.get(i));
        }
    }

    private void removeInverter(int inverterIndex, ArrayList<Integer> indecesToRemove) {
        CSNode neighborNode;
        VariableInput varInputNode;

        indecesToRemove.add(inverterIndex);

        for(int neighborIndex : circuit.getAdjList(inverterIndex)) {
            neighborNode = circuit.getNode(neighborIndex);

            if(neighborNode instanceof Inverter)
                removeInverter(neighborIndex, indecesToRemove);
            else {
                varInputNode = (VariableInput)neighborNode;
                varInputNode.removeInputNode(circuit.getNode(inverterIndex));
            }
        }
    }

    public void removeNode(String nodeID) throws IllegalArgumentException {
        int nodeIndex = circuit.indexOf(nodeID);

        if(nodeIndex == -1)
            throw new IllegalArgumentException(nodeID + " does not exist in this circuit");

        CSNode node = circuit.getNode(nodeIndex);
        if(node instanceof FFOutNode)
            throw new IllegalArgumentException("Output nodes for flip flops cannot be removed, try to remove the flip flop itself instead");
        if(node instanceof FlipFlop) {
            removeDFFNode(nodeIndex);
            return;
        }

        CSNode neighborNode;
        VariableInput varInputNode;
        ArrayList<Integer> indecesToRemove = new ArrayList<Integer>();
        indecesToRemove.add(nodeIndex);

        for(int neighborIndex : circuit.getAdjList(nodeIndex)) {
            neighborNode = circuit.getNode(neighborIndex);

            if(neighborNode instanceof Inverter)
                removeInverter(neighborIndex, indecesToRemove);
            else {
                varInputNode = (VariableInput)neighborNode;
                varInputNode.removeInputNode(circuit.getNode(nodeIndex));
            }
        }

        Collections.sort(indecesToRemove);
        for(int i = indecesToRemove.size() - 1; i >= 0; i--)
                circuit.removeNode(indecesToRemove.get(i));
    }

    private void removeDFFNode(int nodeIndex) {
        CSNode neighborNode;
        VariableInput varInputNode;
        ArrayList<Integer> indecesToRemove = new ArrayList<Integer>();
        indecesToRemove.add(nodeIndex);
        indecesToRemove.add(nodeIndex + 1);
        indecesToRemove.add(nodeIndex + 2);

        LinkedList<Integer> outNodeAdjList = circuit.getAdjList(nodeIndex + 1);
        LinkedList<Integer> outNodeNegAdjList = circuit.getAdjList(nodeIndex + 2);

        // update neighbors of outNode
        for(int neighborIndex : outNodeAdjList) {
            neighborNode = circuit.getNode(neighborIndex);

            if(neighborNode instanceof Inverter)
                removeInverter(neighborIndex, indecesToRemove);
            else {
                varInputNode = (VariableInput)neighborNode;
                varInputNode.removeInputNode(circuit.getNode(nodeIndex));
            }
        }

        // update neighbors of outNodeNegated
        for(int neighborIndex : outNodeNegAdjList) {
            neighborNode = circuit.getNode(neighborIndex);

            if(neighborNode instanceof Inverter)
                removeInverter(neighborIndex, indecesToRemove);
            else {
                varInputNode = (VariableInput)neighborNode;
                varInputNode.removeInputNode(circuit.getNode(nodeIndex));
            }
        }

        Collections.sort(indecesToRemove);
        for(int i = indecesToRemove.size() - 1; i >= 0; i--)
                circuit.removeNode(indecesToRemove.get(i));
    }

    public void setInputSeq(String inputNodeID, int[] newSeq) {
        int nodeIndex = circuit.indexOf(inputNodeID);
        InputVariableNode node = (InputVariableNode)circuit.getNode(nodeIndex);

        node.setInputSeq(newSeq);
    }

    public String[] getNodeNames() {
        String[] nodeNames = new String[circuit.getSize()];

        for(int i = 0; i < circuit.getSize(); i++)
            nodeNames[i] = circuit.getNode(i).getName();

        return nodeNames;
    }

    public int[] getCurrentCircuitStatus() {
        int[] nodeValues = new int[circuit.getSize()];

        for(int i = 0; i < circuit.getSize(); i++)
            nodeValues[i] = circuit.getNode(i).getValue();

        return nodeValues;
    }

    public int[] getNextCircuitStatus() {
        int[] nodeValues = new int[circuit.getSize()];

        updateCircuit();

        for(int i = 0; i < circuit.getSize(); i ++)
            nodeValues[i] = circuit.getNode(i).getValue();

        return nodeValues;

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

    public void resetCircuit() {
        circuit.reset();
    }
    
    public void saveCircuit(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        CSFileIO.writeSaveFile(circuit, fileName);
    }

    public void loadCircuit(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        circuit = CSFileIO.readSaveFile(fileName);
        
    }
}