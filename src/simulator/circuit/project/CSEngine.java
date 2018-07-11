package simulator.circuit.project;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.StringTokenizer;

import simulator.circuit.project.CSGraph.IllegalCircuitStateException;

public class CSEngine {
    private CSGraph circuit;
    private ArrayList<String> inputNodeNames;
    private ArrayList<String> outputNodeNames;
    private ArrayList<String> flipFlopNodeNames;
    private ArrayList<String> invertedNodes;
    private ArrayList<CSNode> trackedNodes;

    public CSEngine() {
        circuit = new CSGraph();
        inputNodeNames = new ArrayList<String>();
        outputNodeNames = new ArrayList<String>();
        flipFlopNodeNames = new ArrayList<String>();
        invertedNodes = new ArrayList<String>();
        trackedNodes = new ArrayList<CSNode>();
    }

    public CSEngine(CSGraph circuit) {
        CSNode node;
        Inverter inverterNode;

        this.circuit = circuit;

        inputNodeNames = new ArrayList<String>();
        outputNodeNames = new ArrayList<String>();
        flipFlopNodeNames = new ArrayList<String>();
        invertedNodes = new ArrayList<String>();
        trackedNodes = new ArrayList<CSNode>();

        for(int i = 0; i < circuit.getSize(); i++) {
            node = circuit.getNode(i);

            if(node instanceof InputVariableNode)
                inputNodeNames.add(node.getName());
            else if(node instanceof OutputVariableNode)
                outputNodeNames.add(node.getName());
            else if(node instanceof FlipFlop)
                flipFlopNodeNames.add(node.getName());
            else if(node instanceof Inverter) {
                inverterNode = (Inverter)node;
                invertedNodes.add(inverterNode.getInputNode().getName());
            }

            if(node.getTrackNum() > 0)
                trackedNodes.add(node);
        }

        trackedNodes.sort(new CSNodeTrackNumComparator());
    }

    public void addInputNode(String nodeID) throws IllegalArgumentException {
        circuit.addNode(new InputVariableNode(nodeID));
        inputNodeNames.add(nodeID);
    }

    public void addOutputNode(String nodeID) throws IllegalArgumentException {
        circuit.addNode(new OutputVariableNode(nodeID));
        outputNodeNames.add(nodeID);
    }
    
    public void addDFFNode(String nodeID) throws IllegalArgumentException {
        DFlipFlop newDFFNode = new DFlipFlop(nodeID);
        FFOutNode newDFFNodeOut = new FFOutNode(nodeID + "-out", newDFFNode);
        FFOutNode newDFFNodeOutNeg = new FFOutNode(nodeID + "-outneg", newDFFNode);
        circuit.addNode(newDFFNode);
        circuit.addNode(newDFFNodeOut);
        circuit.addNode(newDFFNodeOutNeg);
        newDFFNode.setOutNodes(newDFFNodeOut, newDFFNodeOutNeg);

        flipFlopNodeNames.add(nodeID);

        // add appropriate edges
        circuit.addEdge(circuit.getSize() - 3, circuit.getSize() - 2);
        circuit.addEdge(circuit.getSize() - 3, circuit.getSize() - 1);
    }

    public void addAndGate(String nodeID) throws IllegalArgumentException {
        circuit.addNode(new AndGate(nodeID));
    }

    public void addNandGate(String nodeID) throws IllegalArgumentException {
        circuit.addNode(new NandGate(nodeID));
    }

    public void addOrGate(String nodeID) throws IllegalArgumentException {
        circuit.addNode(new OrGate(nodeID));
    }

    public void addNorGate(String nodeID) throws IllegalArgumentException {
        circuit.addNode(new NorGate(nodeID));
    }

    public void addXorGate(String nodeID) throws IllegalArgumentException {
        circuit.addNode(new XorGate(nodeID));
    }

    public void addNXorGate(String nodeID) throws IllegalArgumentException {
        circuit.addNode(new NXorGate(nodeID));
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

        if(sourceIndex == targetIndex)
            throw new IllegalArgumentException("The specified connection is illegal");
        if(circuit.containsEdge(sourceIndex, targetIndex))
            throw new IllegalArgumentException((sourceIndex + 1) + " to " + (targetIndex + 1) + " connection already exists");

        try {
            sourceNode = circuit.getNode(sourceIndex);
        } catch(IndexOutOfBoundsException ioobe) {
            throw new IllegalArgumentException((sourceIndex + 1) + " is an invalid index");
        }

        try {
            targetNode = circuit.getNode(targetIndex);
        } catch(IndexOutOfBoundsException ioobe) {
            throw new IllegalArgumentException((targetIndex + 1) + " is an invalid index");
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

    public void addConnection(String sourceName, String targetName) throws IllegalArgumentException {
        CSNode sourceNode;
        CSNode targetNode;

        int sourceIndex = circuit.indexOf(sourceName);
        int targetIndex = circuit.indexOf(targetName);
        
        if(sourceIndex == -1)
            throw new IllegalArgumentException(sourceName + " does not exist in the circuit");
        if(targetIndex == -1)
            throw new IllegalArgumentException(targetName + " does not exist in the circuit");

        if(sourceIndex == targetIndex)
            throw new IllegalArgumentException("The specified connection is illegal");
        if(circuit.containsEdge(sourceIndex, targetIndex))
            throw new IllegalArgumentException((sourceIndex + 1) + " to " + (targetIndex + 1) + " connection already exists");

        try {
            sourceNode = circuit.getNode(sourceIndex);
        } catch(IndexOutOfBoundsException ioobe) {
            throw new IllegalArgumentException((sourceIndex + 1) + " is an invalid index");
        }

        try {
            targetNode = circuit.getNode(targetIndex);
        } catch(IndexOutOfBoundsException ioobe) {
            throw new IllegalArgumentException((targetIndex + 1) + " is an invalid index");
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

    public void removeNode(String nodeID) throws IllegalArgumentException {
        int nodeIndex = circuit.indexOf(nodeID);

        if(nodeIndex == -1)
            throw new IllegalArgumentException(nodeID + " does not exist in this circuit");

        CSNode node = circuit.getNode(nodeIndex);

        if(node instanceof FFOutNode)
            throw new IllegalArgumentException("Output nodes for flip flops cannot be removed, try to remove the flip flop itself instead");
        else if(node instanceof FlipFlop) {
            flipFlopNodeNames.remove(node.getName());
            removeDFFNode(nodeIndex);
            return;
        } else if(node instanceof Inverter) {
            // need to remove the source of this inverter from the invertedNodes list
            Inverter inverterNode = (Inverter)node;
            invertedNodes.remove(inverterNode.getInputNode().getName());
        } else if(node instanceof InputVariableNode)
            inputNodeNames.remove(node.getName());
        else if(node instanceof OutputVariableNode)
            outputNodeNames.remove(node.getName());


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
        for(int i = indecesToRemove.size() - 1; i >= 0; i--) {
            trackedNodes.remove(circuit.getNode(indecesToRemove.get(i)));
            circuit.removeNode(indecesToRemove.get(i));
        }
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
        for(int i = indecesToRemove.size() - 1; i >= 0; i--) {
            trackedNodes.remove(circuit.getNode(indecesToRemove.get(i)));
            circuit.removeNode(indecesToRemove.get(i));
        }
    }

    private void removeInverter(int inverterIndex, ArrayList<Integer> indecesToRemove) {
        CSNode neighborNode;
        VariableInput varInputNode;
        Inverter targetInverter = (Inverter)circuit.getNode(inverterIndex);

        indecesToRemove.add(inverterIndex);

        for(int neighborIndex : circuit.getAdjList(inverterIndex)) {
            neighborNode = circuit.getNode(neighborIndex);

            if(neighborNode instanceof Inverter) {
                removeInverter(neighborIndex, indecesToRemove);
                invertedNodes.remove(targetInverter.getName());
            } else {
                varInputNode = (VariableInput)neighborNode;
                varInputNode.removeInputNode(circuit.getNode(inverterIndex));
            }
        }
    }

    public void renameNode(int nodeIndex, String newName) throws IllegalArgumentException {
        CSNode targetNode;
        String targetNodeName;
        String inverterNodeName;
        String newInvertNodeName;

        if(circuit.contains(newName))
            throw new IllegalArgumentException(newName + " already exists");

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
        if(invertedNodes.remove(targetNodeName)) {
            invertedNodes.add(newName);
            inverterNodeName = targetNodeName + "-inverter";
            newInvertNodeName = newName + "-inverter";
            circuit.getNode(inverterNodeName).setName(newInvertNodeName);
            // the inverter may also have an inverter, and so on
            while(invertedNodes.remove(inverterNodeName)) {
                invertedNodes.add(newInvertNodeName);
                inverterNodeName += "-inverter";
                newInvertNodeName += "-inverter";
                circuit.getNode(inverterNodeName).setName(newInvertNodeName);
            }
        }

        // if renamed an input node, update the inputNodeNames array
        if(inputNodeNames.remove(targetNodeName))
            inputNodeNames.add(newName);
        
        // if renamed an output node, update the outputNodeNames array
        if(outputNodeNames.remove(targetNodeName))
            outputNodeNames.add(newName);

        // if renamed a flip flop, also need to rename its output nodes and update the flipFlopNodeNames array
        if(flipFlopNodeNames.remove(targetNodeName)) {
            circuit.getNode(nodeIndex + 1).setName(newName + "-out");
            circuit.getNode(nodeIndex + 2).setName(newName + "-outneg");
            flipFlopNodeNames.add(newName);

            // flip flop output nodes may have inverter as well
            String tempTargetName = targetNodeName;     // temporary storage for targetNodeName
            String tempNewName = newName;               // temporary storage for newName
            targetNodeName = targetNodeName + "-out";
            newName = newName + "-out";
            // check if flip flop output node has inverter
            if(invertedNodes.remove(targetNodeName)) {
                invertedNodes.add(newName);
                inverterNodeName = targetNodeName + "-inverter";
                newInvertNodeName = newName + "-inverter";
                circuit.getNode(inverterNodeName).setName(newInvertNodeName);
                // the inverter may also have an inverter, and so on
                while(invertedNodes.remove(inverterNodeName)) {
                    invertedNodes.add(newInvertNodeName);
                    inverterNodeName += "-inverter";
                    newInvertNodeName += "-inverter";
                    circuit.getNode(inverterNodeName).setName(newInvertNodeName);
                }
            }

            targetNodeName = tempTargetName + "-outneg";
            newName = tempNewName + "-outneg";
            // check if flip flop output negated node has inverter
            if(invertedNodes.remove(targetNodeName)) {
                invertedNodes.add(newName);
                inverterNodeName = targetNodeName + "-inverter";
                newInvertNodeName = newName + "-inverter";
                circuit.getNode(inverterNodeName).setName(newInvertNodeName);
                // the inverter may also have an inverter, and so on
                while(invertedNodes.remove(inverterNodeName)) {
                    invertedNodes.add(newInvertNodeName);
                    inverterNodeName += "-inverter";
                    newInvertNodeName += "-inverter";
                    circuit.getNode(inverterNodeName).setName(newInvertNodeName);
                }
            }

            // restore original names from temp storage before exiting this if-block
            targetNodeName = tempTargetName;
            newName = tempNewName;
        }
    }

    public void removeConnection(int sourceIndex, int targetIndex) throws IllegalArgumentException {
        CSNode sourceNode;
        CSNode targetNode;

        if(!(circuit.containsEdge(sourceIndex, targetIndex)))
            throw new IllegalArgumentException("The specified connection does not exist");

        try {
            sourceNode = circuit.getNode(sourceIndex);
        } catch(IndexOutOfBoundsException ioobe) {
            throw new IllegalArgumentException((sourceIndex + 1) + " is an invalid index");
        }

        try {
            targetNode = circuit.getNode(targetIndex);
        } catch(IndexOutOfBoundsException ioobe) {
            throw new IllegalArgumentException((targetIndex + 1) + " is an invalid index");
        }

        if(sourceNode instanceof FlipFlop)
            throw new IllegalArgumentException("Flip flops cannot exist without their output nodes");

        circuit.removeEdge(sourceIndex, targetIndex);

        // inverters cannot exist without an input
        if(targetNode instanceof Inverter) {
            ArrayList<Integer> indecesToRemove = new ArrayList<Integer>();
            removeInverter(targetIndex, indecesToRemove);

            Collections.sort(indecesToRemove);
            for(int i = indecesToRemove.size() - 1; i >= 0; i--) {
                trackedNodes.remove(circuit.getNode(indecesToRemove.get(i)));
                circuit.removeNode(indecesToRemove.get(i));
            }

            invertedNodes.remove(sourceNode.getName());
        } else { // only need to update input node reference to target node
            VariableInput varInputNode = (VariableInput)targetNode;
            varInputNode.removeInputNode(sourceNode);
        }
    }

    public void removeConnection(String sourceName, String targetName) throws IllegalArgumentException {
        CSNode sourceNode;
        CSNode targetNode;

        int sourceIndex = circuit.indexOf(sourceName);
        int targetIndex = circuit.indexOf(targetName);

        if(sourceIndex == -1)
            throw new IllegalArgumentException(sourceName + " does not exist in the circuit");
        if(targetIndex == -1)
            throw new IllegalArgumentException(targetName + " does not exist in the circuit");

        if(!(circuit.containsEdge(sourceIndex, targetIndex)))
            throw new IllegalArgumentException("The specified connection does not exist");

        try {
            sourceNode = circuit.getNode(sourceIndex);
        } catch(IndexOutOfBoundsException ioobe) {
            throw new IllegalArgumentException((sourceIndex + 1) + " is an invalid index");
        }

        try {
            targetNode = circuit.getNode(targetIndex);
        } catch(IndexOutOfBoundsException ioobe) {
            throw new IllegalArgumentException((targetIndex + 1) + " is an invalid index");
        }

        if(sourceNode instanceof FlipFlop)
            throw new IllegalArgumentException("Flip flops cannot exist without their output nodes");

        circuit.removeEdge(sourceIndex, targetIndex);

        // inverters cannot exist without an input
        if(targetNode instanceof Inverter) {
            ArrayList<Integer> indecesToRemove = new ArrayList<Integer>();
            removeInverter(targetIndex, indecesToRemove);

            Collections.sort(indecesToRemove);
            for(int i = indecesToRemove.size() - 1; i >= 0; i--) {
                trackedNodes.remove(circuit.getNode(indecesToRemove.get(i)));
                circuit.removeNode(indecesToRemove.get(i));
            }

            invertedNodes.remove(sourceNode.getName());
        } else { // only need to update input node reference to target node
            VariableInput varInputNode = (VariableInput)targetNode;
            varInputNode.removeInputNode(sourceNode);
        }
    }

    public void setInputSeq(String inputNodeID, int[] newSeq) {
        int nodeIndex = circuit.indexOf(inputNodeID);
        InputVariableNode node = (InputVariableNode)circuit.getNode(nodeIndex);

        node.setInputSeq(newSeq);
    }

    public String[] getTrackedNodeNames() {
        String[] nodeNames = new String[trackedNodes.size()];

        for(int i = 0; i < trackedNodes.size(); i++)
            nodeNames[i] = trackedNodes.get(i).getName();

        return nodeNames;
    }

    public void trackNode(int nodeIndex) throws IllegalArgumentException {
        CSNode node;

        if(nodeIndex < 0 || nodeIndex >= circuit.getSize())
            throw new IllegalArgumentException((nodeIndex + 1) + " is an invalid index");
        else if(trackedNodes.contains(circuit.getNode(nodeIndex)))
            throw new IllegalArgumentException("That node is already being tracked");
        else {
            node = circuit.getNode(nodeIndex);
            node.setTrackNum(trackedNodes.size() + 1);
            trackedNodes.add(node);
        }
            
    }

    public void trackAllNodes() {
        // number of nodes that are untracked
        int untrackedNodes = circuit.getSize() - trackedNodes.size();
        CSNode node;

        while(untrackedNodes != 0)
            for(int i = 0; i < circuit.getSize(); i++) {
                node = circuit.getNode(i);
                if(node.getTrackNum() == 0) {
                    node.setTrackNum(trackedNodes.size() + 1);
                    trackedNodes.add(node);
                    untrackedNodes--;
                }
            }
    }

    // nodeIndex refers to node's index in trackedNodes ArrayList
    public void untrackNode(int nodeIndex) throws IllegalArgumentException {
        if(nodeIndex < 0 || nodeIndex >= trackedNodes.size())
            throw new IllegalArgumentException((nodeIndex + 1) + " is an invalid index");

        // reset the target node's track number to zero
        trackedNodes.get(nodeIndex).resetTrackNum();

        // for each node after the target node to untrack, subtract one from their track number
        // in order for it to correspond to its spot in the array
        for(int i = nodeIndex + 1; i < trackedNodes.size(); i++)
            trackedNodes.get(i).setTrackNum(i - 1);

        trackedNodes.remove(nodeIndex);
    }

    public void untrackAllNodes() {
        for(CSNode node : trackedNodes)
            node.resetTrackNum();

        trackedNodes.clear();
    }

    public int getLongestInputSeqLength() {
        int longestLength = 0;
        int compareLength;
        InputVariableNode inputNode;

        for(String inputNodeName : inputNodeNames) {
            inputNode = (InputVariableNode)circuit.getNode(inputNodeName);
            compareLength = inputNode.getInputSeqLength();
            if(longestLength < compareLength)
                longestLength = compareLength;
        }

        return longestLength;
    }

    public int getLongestNameLength() {
        int longestNameLength = 0;
        int compareLength;
        CSNode node;

        for(int i = 0; i < circuit.getSize(); i++) {
            node = circuit.getNode(i);
            compareLength = node.getName().length();
            if(compareLength > longestNameLength)
                longestNameLength = compareLength;
        }

        return longestNameLength;
    }

    public int getLongestTrackedNameLength() {
        int longestNameLength = 0;
        int compareLength;
        CSNode node;

        for(int i = 0; i < trackedNodes.size(); i++) {
            node = trackedNodes.get(i);
            compareLength = node.getName().length();
            if(compareLength > longestNameLength)
                longestNameLength = compareLength;
        }

        return longestNameLength;
    }

    public int[] getCurrentCircuitState() throws IllegalCircuitStateException {
        int[] nodeValues = new int[trackedNodes.size()];

        for(int i = 0; i < trackedNodes.size(); i++)
            nodeValues[i] = trackedNodes.get(i).getValue();

        return nodeValues;
    }

    public int[] getNextCircuitState() throws IllegalCircuitStateException {
        int[] nodeValues = new int[trackedNodes.size()];

        updateCircuit();

        for(int i = 0; i < trackedNodes.size(); i ++)
            nodeValues[i] = trackedNodes.get(i).getValue();

        return nodeValues;

    }

    private void updateCircuit() throws IllegalCircuitStateException {
        String updatePath = "";

        updatePath = circuit.getUpdatePath();

        StringTokenizer tokenizer = new StringTokenizer(updatePath);

        while(tokenizer.hasMoreTokens())
            circuit.getNode(Integer.valueOf(tokenizer.nextToken())).updateValue();
    }

    public void resetCircuit() {
        circuit.reset();
    }

    public boolean isCircuitSequential() {
        return circuit.isSequential();
    }

    public boolean isCircuitValid() {
        try {
            circuit.getUpdatePath();
        } catch(IllegalCircuitStateException icse) {
            return false;
        }

        return true;
    }

    public ArrayList<ArrayList<Integer>> getTruthTableData() throws IllegalStateException, IllegalCircuitStateException {
        // int double array formatted as:   rows = 2^n where n is number of input variables,
        //                                  cols = number of input variables + number of output variables

        if(circuit.isSequential())
            throw new IllegalStateException("This circuit is a sequential circuit, it does not have a truth table");
        if(inputNodeNames.size() == 0)
            throw new IllegalStateException("Input variables are needed to build the truth table");
        if(outputNodeNames.size() == 0)
            throw new IllegalStateException("Output variables are needed to build the truth table");

        int rowSize = (int)Math.pow(2.0, inputNodeNames.size());
        int colSize = inputNodeNames.size() + outputNodeNames.size();

        // initialize the result double array to appropriate size
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>(rowSize);
        for(int i = 0; i < rowSize; i++) {
            result.add(new ArrayList<Integer>(colSize));
            for(int j = 0; j < colSize; j++)
                result.get(i).add(Integer.valueOf(0));
        }

        // array lists to store references to input and output variables in circuit
        ArrayList<InputVariableNode> inputVariables = new ArrayList<InputVariableNode>();
        ArrayList<OutputVariableNode> outputVariables = new ArrayList<OutputVariableNode>();

        // add all input and output variables in circuit into the appropriate list above
        for(String inputNodeName : inputNodeNames)
            inputVariables.add((InputVariableNode)circuit.getNode(inputNodeName));
        for(String outputNodeName : outputNodeNames)
            outputVariables.add((OutputVariableNode)circuit.getNode(outputNodeName));

        // store the original sequences in temporary storage
        ArrayList<String> originalSequences = new ArrayList<String>(inputVariables.size());
        for(InputVariableNode inputNode : inputVariables)
            originalSequences.add(inputNode.getInputSeq().replaceAll("[^01]", "")); // instead of [0, 1, 1, 0], store 0110

        // fill input variable columns to store all possible combinations; along with setting the variables to have
        // the corresponding input sequences
        int numInputVariables = inputVariables.size();
        int[] tempSequence = new int[rowSize];
        int freq;   // indicates how many 0's or 1's to store before switching to the other
        int count;  // counts how many 0's or 1's have been stored this cycle
        boolean storeZero;
        for(int j = numInputVariables - 1; j >= 0; j--) {
            count = 0;
            storeZero = true;
            freq = (int)Math.pow(2.0, numInputVariables - j - 1);
            for(int i = 0; i < rowSize; i++) {
                result.get(i).set(j, Integer.valueOf((storeZero ? 0 : 1)));
                tempSequence[i] = storeZero ? 0 : 1;
                if(++count == freq) {
                    storeZero = !storeZero;
                    count = 0;
                }
            }
            inputVariables.get(j).setInputSeq(Arrays.copyOf(tempSequence, tempSequence.length));
        }

        // fill output variables section in array; for each input variable combination, store the values in the output variables
        circuit.reset();

        int startColumn = inputVariables.size();
        for(int i = 0; i < rowSize; i++) {
            updateCircuit();
            for(int j = startColumn; j < result.get(0).size(); j++)
                result.get(i).set(j, Integer.valueOf((outputVariables.get(j - startColumn).getValue())));
        }

        circuit.reset();

        // restore original sequences
        String seqString;
        for(int i = 0; i < originalSequences.size(); i++) {
            seqString = originalSequences.get(i);
            if(seqString.equals("null"))
                inputVariables.get(i).setInputSeq(null);
            else {
                tempSequence = new int[seqString.length()];
                for(int n = 0; n < seqString.length(); n++)
                    tempSequence[n] = Integer.parseInt(String.valueOf(seqString.charAt(n)));
                inputVariables.get(i).setInputSeq(tempSequence);
            }
        }

        return result;
    }

    public ArrayList<ArrayList<String>> getTransitionTableData() throws IllegalStateException, IllegalCircuitStateException {
        // double array formatted as:   rows = 2^n where n is number of D flip flops,
        //                              cols = 1 + 2^m where m is number of input variables
        // first column represents present state combination;
        // for every column after that:
        //      each string formatted as: [FF...F, ZZ..Z]   such that each F for a D flip flop, each Z for an output variable,
        //                                                  both being either 0 or 1, representing their next states

        if(!circuit.isSequential())
            throw new IllegalStateException("This circuit is a combinational circuit, it does not have a transition table");
        if(inputNodeNames.size() == 0)
            throw new IllegalStateException("Input variables are needed to build the transition table");

        int rowSize = (int)Math.pow(2.0, flipFlopNodeNames.size());
        int colSize = 1 + (int)Math.pow(2.0, inputNodeNames.size());

        // initialize result double array to appropriate size
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>(rowSize);
        for(int i = 0; i < rowSize; i++) {
            result.add(new ArrayList<String>(colSize));
            for(int j = 0; j < colSize; j++)
                result.get(i).add("null");
        }

        // array lists to store references to appropriate nodes
        ArrayList<InputVariableNode> inputVariables = new ArrayList<InputVariableNode>();
        ArrayList<OutputVariableNode> outputVariables = new ArrayList<OutputVariableNode>();
        ArrayList<DFlipFlop> flipFlops = new ArrayList<DFlipFlop>();

        // add all input, output and D flip flop nodes from circuit to respective list above
        for(String inputNodeName : inputNodeNames)
            inputVariables.add((InputVariableNode)circuit.getNode(inputNodeName));
        for(String outputNodeName : outputNodeNames)
            outputVariables.add((OutputVariableNode)circuit.getNode(outputNodeName));
        for(String flipFlopNodeName : flipFlopNodeNames)
            flipFlops.add((DFlipFlop)circuit.getNode(flipFlopNodeName));

        // reset the circuit, then build the result array
        circuit.reset();

        // preparation variables
        LinkedList<String> presentStates = new LinkedList<String>(); // queue to store the values to set for present states
        String flipFlopStates;  // string to store binary number of row number; starting at zero
        String temp = "";       // temp storage to store into result[i][0]: the present state values; temp used again for reference
        DFlipFlop targetDFFNode;
        String inputStates;
        InputVariableNode targetInputNode;
        // update variables
        String updatePath = "";
        StringTokenizer tokenizer;
        CSNode node;
        // recording variables
        String elementString1 = ""; // stores the flip flop next state values
        String elementString2 = ""; // stores the output variable values

        // prepare first column with all combinations of present state values
        for(int n = 0; n < flipFlops.size(); n++)
            presentStates.add("0");
        for(int i = 0; i < rowSize; i++) {
            flipFlopStates = Integer.toBinaryString(i);
            for(int k = 0; k < flipFlopStates.length(); k++) {
                presentStates.add(Character.toString(flipFlopStates.charAt(k)));
                presentStates.remove();
            }
            
            for(int l = 0; l < flipFlops.size(); l++) {
                temp += presentStates.remove();
                presentStates.add("0");
            }

            result.get(i).set(0, temp);
            temp = "";
        }

        for(int j = 1; j < colSize; j++) {
            for(int i = 0; i < rowSize; i++) {
                // prepare the flip flop present states
                for(int k = 0; k < flipFlops.size(); k++) {
                    targetDFFNode = flipFlops.get(k);
                    temp = result.get(i).get(0);
                    assert flipFlops.size() == temp.length();
                    targetDFFNode.value = Integer.parseInt(Character.toString(temp.charAt(k)));
                }

                // prepare the input variables
                inputStates = Integer.toBinaryString(j - 1);
                for(int l = 0; l < inputStates.length(); l++) {
                    // target input node is accessed from right to left in input variables array list
                    targetInputNode = inputVariables.get(inputVariables.size() - 1 - l);
                    // inputStates string is accessed from right to left
                    targetInputNode.value = Integer.parseInt(Character.toString(inputStates.charAt(inputStates.length() - 1 - l)));
                }

                // update the circuit with prepared node values from above
                updatePath = circuit.getUpdatePath();
        
                tokenizer = new StringTokenizer(updatePath);
        
                while(tokenizer.hasMoreTokens()) {
                    node = circuit.getNode(Integer.valueOf(tokenizer.nextToken()));
                    // do not update input variables, since doing so overwrites the desired value to be used
                    if(!(node instanceof InputVariableNode))
                        node.updateValue();
                }

                // record the results
                for(int n = 0; n < flipFlops.size(); n++)
                    elementString1 += String.valueOf(flipFlops.get(n).getValue());
                for(int m = 0; m < outputVariables.size(); m++)
                    elementString2 += String.valueOf(outputVariables.get(m).getValue());

                if(elementString2.length() > 0)
                    result.get(i).set(j, elementString1 + ", " + elementString2);
                else
                    result.get(i).set(j, elementString1);

                // reset for next loop iteration
                circuit.reset();
                updatePath = elementString1 = elementString2 = "";
            }
        }

        return result;
    }

    public String[] getCircuitNodeNames() {
        String[] result = new String[circuit.getSize()];
        for(int i = 0; i < circuit.getSize(); i++)
            result[i] = circuit.getNode(i).getName();

        return result;
    }

    public String[] getCircuitNodeTypes() {
        String[] result = new String[circuit.getSize()];
        for(int i = 0; i < circuit.getSize(); i++)
            result[i] = circuit.getNode(i).getNodeType();

        return result;
    }

    public String[] getInputNodeNames() {
        String[] result = new String[inputNodeNames.size()];

        for(int i = 0; i < inputNodeNames.size(); i++)
            result[i] = inputNodeNames.get(i);

        return result;
    }

    public String[] getOutputNodeNames() {
        String[] result = new String[outputNodeNames.size()];

        for(int i = 0; i < outputNodeNames.size(); i++)
            result[i] = outputNodeNames.get(i);

        return result;
    }

    public String[] getFlipFlopNodeNames() {
        String[] result = new String[flipFlopNodeNames.size()];

        for(int i = 0; i < flipFlopNodeNames.size(); i++)
            result[i] = flipFlopNodeNames.get(i);

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
            adjListString = String.format("%3d -> [", (i + 1));
            if(!temp.isEmpty()) {
                for(int j = 0; j < temp.size() - 1; j++)
                    adjListString += (temp.get(j) + 1) + ", ";
                adjListString += (temp.getLast() + 1) + "]";

            } else if(circuit.getNode(i) instanceof OutputVariableNode)
                adjListString += "output]";
            else
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
        int fieldWidth = 0;

        // prepare field width, maximum between longest input node name length and 15
        for(String nodeName : inputNodeNames)
            if(fieldWidth < nodeName.length())
                fieldWidth = nodeName.length();
        fieldWidth = Math.max(fieldWidth, 15);

        // build the result array
        for(int i = 0; i < inputNodeNames.size(); i++) {
            inputNodeName = inputNodeNames.get(i);
            currentNode = (InputVariableNode)circuit.getNode(inputNodeName);
            nodeSeq = currentNode.getInputSeq();
            if(nodeSeq.equals("null"))
                result[i] = String.format("%d. %-" + fieldWidth + "s %s", (i + 1), inputNodeName, "[]");
            else
                result[i] = String.format("%d. %-" + fieldWidth + "s %s", (i + 1), inputNodeName, nodeSeq);
        }

        return result;
    }

    public void saveCircuit(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        CSFileIO.writeSaveFile(circuit, fileName);
    }

    public void loadCircuit(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException, ClassCastException {
        CSNode node;
        Inverter inverterNode;

        circuit = CSFileIO.readSaveFile(fileName);

        inputNodeNames.clear();
        outputNodeNames.clear();
        flipFlopNodeNames.clear();
        invertedNodes.clear();
        trackedNodes.clear();

        for(int i = 0; i < circuit.getSize(); i++) {
            node = circuit.getNode(i);

            if(node instanceof InputVariableNode)
                inputNodeNames.add(node.getName());
            else if(node instanceof OutputVariableNode)
                outputNodeNames.add(node.getName());
            else if(node instanceof FlipFlop)
                flipFlopNodeNames.add(node.getName());
            else if(node instanceof Inverter) {
                inverterNode = (Inverter)node;
                invertedNodes.add(inverterNode.getInputNode().getName());
            }

            if(node.getTrackNum() > 0)
                trackedNodes.add(node);
        }

        trackedNodes.sort(new CSNodeTrackNumComparator());
    }

    public void newCircuit() {
        circuit = new CSGraph();
        inputNodeNames.clear();
        outputNodeNames.clear();
        flipFlopNodeNames.clear();
        invertedNodes.clear();
        trackedNodes.clear();
    }
}