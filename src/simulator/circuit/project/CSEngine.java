package simulator.circuit.project;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.StringTokenizer;

import simulator.circuit.project.CSGraph.IllegalCircuitStateException;

public class CSEngine implements Serializable {
    private static final long serialVersionUID = 1L;
    private CSGraph circuit;
    private ArrayList<String> inputNodeNames;
    private ArrayList<String> invertedNodes;

    public CSEngine() {
        circuit = new CSGraph();
        inputNodeNames = new ArrayList<String>();
        invertedNodes = new ArrayList<String>();
    }

    public void addInputNode(String nodeID) throws IllegalArgumentException {
        circuit.addNode(new InputVariableNode(nodeID));
        inputNodeNames.add(nodeID);
    }

    public void addOutputNode(String nodeID) throws IllegalArgumentException {
        circuit.addNode(new OutputVariableNode(nodeID));
    }
    
    public void addDFFNode(String nodeID) throws IllegalArgumentException {
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

    public void addAndGate(String nodeID) throws IllegalArgumentException {
        circuit.addNode(new AndGate(nodeID));
    }

    public void addOrGate(String nodeID) throws IllegalArgumentException {
        circuit.addNode(new OrGate(nodeID));
    }

    public void addInverter(String sourceNodeID) throws IllegalArgumentException {
        int sourceIndex = circuit.indexOf(sourceNodeID);

        if(sourceIndex == -1)
            throw new IllegalArgumentException("This circuit does not contain " + sourceNodeID);
        if(circuit.getNode(sourceIndex) instanceof FlipFlop)
            throw new IllegalArgumentException("Flip flops already have a negated output node");
        if(circuit.getNode(sourceIndex) instanceof OutputVariableNode)
            throw new IllegalArgumentException("Output nodes cannot be inverted");

        circuit.addNode(new Inverter(sourceNodeID + "-inverter", circuit.getNode(sourceIndex)));
        int newInverterIndex = circuit.getSize() - 1;

        circuit.addEdge(sourceIndex, newInverterIndex);
        invertedNodes.add(sourceNodeID);
    }

    public void addConnection(int sourceIndex, int targetIndex) throws IllegalArgumentException {
        CSNode sourceNode;
        CSNode targetNode;

        try {
            sourceNode = circuit.getNode(sourceIndex);
        } catch(IndexOutOfBoundsException ioobe) {
            throw new IllegalArgumentException(sourceIndex + " is an invalid index");
        }

        try {
            targetNode = circuit.getNode(targetIndex);
        } catch(IndexOutOfBoundsException ioobe) {
            throw new IllegalArgumentException(targetIndex + " is an invalid index");
        }

        if(sourceNode instanceof OutputVariableNode || sourceNode instanceof DFlipFlop)
            throw new IllegalArgumentException(sourceNode.getName() + " cannot be a source of a connection");
        if(!(targetNode instanceof VariableInput))
            throw new IllegalArgumentException(targetNode.getName() + " cannot be a target of a connection");

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

    public void renameNode(int nodeIndex, String newName) throws IllegalArgumentException {
        CSNode targetNode;
        String targetNodeName;

        if(nodeIndex < 0 || nodeIndex >= circuit.getSize())
            throw new IllegalArgumentException("The given node index is invalid");
        
        targetNode = circuit.getNode(nodeIndex);
        targetNodeName = targetNode.getName();

        if(targetNode instanceof FFOutNode)
            throw new IllegalArgumentException("Output nodes for flip flops cannot be renamed directly, try renaming the flip flop instead");
        if(targetNode instanceof Inverter)
            throw new IllegalArgumentException("Inverter nodes cannot be renamed directly, try renaming the node it inverts instead");
        
        targetNode.setName(newName);

        // rename target node's inverter as well, if it exists
        if(invertedNodes.contains(targetNodeName))
            circuit.getNode(targetNodeName + "-inverter").setName(newName + "-inverter");
        
        // if renamed a flip flop, also need to rename its output nodes
        if(targetNode instanceof FlipFlop) {
            circuit.getNode(nodeIndex + 1).setName(newName + "-out");
            circuit.getNode(nodeIndex + 2).setName(newName + "-outnegated");
        }
    }

    public void removeConnection(int sourceIndex, int targetIndex) throws IllegalArgumentException {
        CSNode sourceNode;
        CSNode targetNode;

        try {
            sourceNode = circuit.getNode(sourceIndex);
        } catch(IndexOutOfBoundsException ioobe) {
            throw new IllegalArgumentException(sourceIndex + " is an invalid index");
        }

        try {
            targetNode = circuit.getNode(targetIndex);
        } catch(IndexOutOfBoundsException ioobe) {
            throw new IllegalArgumentException(targetIndex + " is an invalid index");
        }
        
        if(!(circuit.containsEdge(sourceIndex, targetIndex)))
            throw new IllegalArgumentException("The specified connection does not exist");

        if(sourceNode instanceof FlipFlop)
            throw new IllegalArgumentException("Please refer to this flip flop's output nodes instead");

        circuit.removeEdge(sourceIndex, targetIndex);

        // inverters cannot exist without an input
        if(targetNode instanceof Inverter) {
            ArrayList<Integer> indecesToRemove = new ArrayList<Integer>();
            removeInverter(targetIndex, indecesToRemove);
            // no need to sort indecesToRemove list since inverters cannot point to other inverters,
            // thus only the inverter's index will be in the list as the first element
            circuit.removeNode(indecesToRemove.get(0));
            invertedNodes.remove(sourceNode.getName());
        } else { // only need to update input node reference to target node
            VariableInput varInputNode = (VariableInput)targetNode;
            varInputNode.removeInputNode(circuit.getNode(sourceIndex));
        }
    }

    private void removeInverter(int inverterIndex, ArrayList<Integer> indecesToRemove) {
        CSNode neighborNode;
        VariableInput varInputNode;

        indecesToRemove.add(inverterIndex);

        for(int neighborIndex : circuit.getAdjList(inverterIndex)) {
            neighborNode = circuit.getNode(neighborIndex);

            varInputNode = (VariableInput)neighborNode;
            varInputNode.removeInputNode(circuit.getNode(inverterIndex));
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
        if(node instanceof Inverter) {
            // need to remove the source of this inverter from the invertedNodes list
            Inverter inverterNode = (Inverter)node;
            invertedNodes.remove(inverterNode.getInputNode().getName());
        }
        if(node instanceof InputVariableNode)
            inputNodeNames.remove(node.getName());


        CSNode neighborNode;
        VariableInput varInputNode;
        ArrayList<Integer> indecesToRemove = new ArrayList<Integer>();
        indecesToRemove.add(nodeIndex);

        for(int neighborIndex : circuit.getAdjList(nodeIndex)) {
            neighborNode = circuit.getNode(neighborIndex);

            if(neighborNode instanceof Inverter) {
                removeInverter(neighborIndex, indecesToRemove);
                invertedNodes.remove(nodeID);
            } else {
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

        String outNodeID = circuit.getNode(nodeIndex + 1).getName();
        String outNodeNegatedID = circuit.getNode(nodeIndex + 2).getName();

        LinkedList<Integer> outNodeAdjList = circuit.getAdjList(nodeIndex + 1);
        LinkedList<Integer> outNodeNegAdjList = circuit.getAdjList(nodeIndex + 2);

        // update neighbors of outNode
        for(int neighborIndex : outNodeAdjList) {
            neighborNode = circuit.getNode(neighborIndex);

            if(neighborNode instanceof Inverter) {
                removeInverter(neighborIndex, indecesToRemove);
                invertedNodes.remove(outNodeID);
            } else {
                varInputNode = (VariableInput)neighborNode;
                varInputNode.removeInputNode(circuit.getNode(nodeIndex));
            }
        }

        // update neighbors of outNodeNegated
        for(int neighborIndex : outNodeNegAdjList) {
            neighborNode = circuit.getNode(neighborIndex);

            if(neighborNode instanceof Inverter) {
                removeInverter(neighborIndex, indecesToRemove);
                invertedNodes.remove(outNodeNegatedID);
            } else {
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

    public int[] getCurrentCircuitState() {
        int[] nodeValues = new int[circuit.getSize()];

        for(int i = 0; i < circuit.getSize(); i++)
            nodeValues[i] = circuit.getNode(i).getValue();

        return nodeValues;
    }

    public int[] getNextCircuitState() {
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
    
    public String[] getCircuitNodeNames() {
        String[] result = new String[circuit.getSize()];
        for(int i = 0; i < circuit.getSize(); i++)
            result[i] = circuit.getNode(i).getName();

        return result;
    }

    public String[] getInputNodeNames() {
        String[] result = new String[inputNodeNames.size()];

        for(int i = 0; i < inputNodeNames.size(); i++)
            result[i] = inputNodeNames.get(i);

        return result;
    }

    public int getCircuitSize() {
        return circuit.getSize();
    }

    public int[] getCircuitStatus() {
        int[] status = new int[7];
        CSNode node;
        int inputNodes = 0;
        int sequences = 0;
        int outNodes = 0;
        int flipFlops = 0;
        int gates = 0;
        int inverters = 0;
        int connections;

        for(int i = 0; i < circuit.getSize(); i++) {
            node = circuit.getNode(i);

            if(node instanceof InputVariableNode) {
                inputNodes++;
                InputVariableNode inputNode = (InputVariableNode)node;
                if(!inputNode.getInputSeq().equals("null"))
                    sequences++;
                
            } else if(node instanceof OutputVariableNode)
                outNodes++;
            else if(node instanceof FlipFlop)
                flipFlops++;
            else if(node instanceof Gate)
                gates++;
            else if(node instanceof Inverter)
                inverters++;
        }

        connections = circuit.getEdgeCount();

        status[0] = inputNodes;
        status[1] = sequences;
        status[2] = outNodes;
        status[3] = flipFlops;
        status[4] = gates;
        status[5] = inverters;
        status[6] = connections;

        return status;
    }

    public String[] getCircuitConnectionStatus() {
        String[] result = new String[circuit.getSize()];
        String adjListString;
        LinkedList<Integer> temp;

        for(int i = 0; i < circuit.getSize(); i++) {
            temp = circuit.getAdjList(i);
            adjListString = (i + 1) + " -> [";
            if(!temp.isEmpty()) {
                for(int j = 0; j < temp.size() - 1; j++)
                    adjListString += (temp.get(j) + 1) + ", ";
                adjListString += (temp.getLast() + 1) + "]";
            } else
                adjListString += "]";

            
            result[i] = adjListString;
        }

        return result;
    }

    public String[] getCircuitInputSeqStatus() {
        String[] result = new String[inputNodeNames.size()];
        String inputNodeName;
        String nodeSeq;
        InputVariableNode currentNode;
        for(int i = 0; i < inputNodeNames.size(); i++) {
            inputNodeName = inputNodeNames.get(i);
            currentNode = (InputVariableNode)circuit.getNode(inputNodeName);
            nodeSeq = currentNode.getInputSeq();
            if(nodeSeq.equals("null"))
                result[i] = String.format("%d. %-15s %s", (i + 1), inputNodeName, "[]");
            else
                result[i] = String.format("%d %-15s %s", (i + 1), inputNodeName, nodeSeq);
        }

        return result;
    }
}