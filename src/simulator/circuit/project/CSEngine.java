package simulator.circuit.project;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.StringTokenizer;

import simulator.circuit.project.CSGraph.IllegalCircuitStateException;

/**
 * Class to provide the interface to a circuit.
 * <p>
 * This class handles building the circuit, updating the circuit,
 * obtaining necessary information about the circuit, saving the circuit,
 * loading another circuit and creating a new circuit to work on. All the
 * work to be done on a circuit is done through this class.
 * 
 * @author Joel Tengco
 */
public class CSEngine {
    /**
     * Circuit that will be worked on.
     */
    private CSGraph circuit;
    /**
     * Contains the names of the circuit's input variable nodes.
     */
    private ArrayList<String> inputNodeNames;
    /**
     * Contains the names of the circuit's output variable nodes.
     */
    private ArrayList<String> outputNodeNames;
    /**
     * Contains the names of the circuit's flip-flop nodes.
     */
    private ArrayList<String> flipFlopNodeNames;
    /**
     * Contains the names of the nodes that currently have a corresponding
     * {@code Inverter} object.
     */
    private ArrayList<String> invertedNodes;
    /**
     * Contains references to the nodes that are currently being tracked.
     * The references in this list are ordered as in the first element in
     * the list being the node with a track number of 1, and so on.
     */
    private ArrayList<CSNode> trackedNodes;

    /**
     * Constructs a new engine with a new, empty circuit to work on.
     */
    public CSEngine() {
        circuit = new CSGraph();
        // initialize array list fields
        initArrayLists();
    }

    /**
     * Constructs a new engine with the given circuit to work on.
     * 
     * @param circuit the circuit that the engine will correspond to
     */
    public CSEngine(CSGraph circuit) {
        CSNode node;
        Inverter inverterNode;

        this.circuit = circuit;

        // initialize array list fields
        initArrayLists();

        // fill the fields with appropriate data
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

        // sort the trackedNodes list because nodes are not necessarily
        // ordered by their track number
        trackedNodes.sort(new CSNodeTrackNumComparator());
    }

    /**
     * Utility method for constructors to initialize the array list fields.
     */
    private void initArrayLists() {
        inputNodeNames = new ArrayList<String>();
        outputNodeNames = new ArrayList<String>();
        flipFlopNodeNames = new ArrayList<String>();
        invertedNodes = new ArrayList<String>();
        trackedNodes = new ArrayList<CSNode>();
    }

    /**
     * Adds an input variable node to the circuit.
     * 
     * @param nodeID the ID/name of the input variable node to be added
     * @throws IllegalArgumentException if another node with the given ID/name
     * already exists in the circuit
     */
    public void addInputNode(String nodeID) throws IllegalArgumentException {
        circuit.addNode(new InputVariableNode(nodeID));
        inputNodeNames.add(nodeID);
    }

    /**
     * Adds an output variable node to the circuit.
     * 
     * @param nodeID the ID/name of the output variable node to be added
     * @throws IllegalArgumentException if another node with the given ID/name
     * already exists in the circuit
     */
    public void addOutputNode(String nodeID) throws IllegalArgumentException {
        circuit.addNode(new OutputVariableNode(nodeID));
        outputNodeNames.add(nodeID);
    }

    /**
     * Adds a D flip-flop entity to the circuit.
     * <p>
     * D flip-flop entities consist of three nodes in the circuit. One node
     * is the D flip-flop's next state, while the other two are its output
     * nodes. The next state node is added to the circuit first, then the
     * normal, non-negated output node is added and finally the negated
     * output node is added.
     * <p>
     * The names of the output nodes will be the given node ID concatenated with
     * "-out" for the non-negated output node and "-outneg" for the negated output
     * node.
     * 
     * @param nodeID the name of the D flip-flop to be added
     * @throws IllegalArgumentException if a node with the given ID/name already
     * exists in the circuit
     */
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

    /**
     * Adds an AND gate to the circuit.
     * 
     * @param nodeID the ID/name of the AND gate to be added
     * @throws IllegalArgumentException if a node with the given ID/name already
     * exists in the circuit
     */
    public void addAndGate(String nodeID) throws IllegalArgumentException {
        circuit.addNode(new AndGate(nodeID));
    }

    /**
     * Adds a NAND gate to the circuit.
     * 
     * @param nodeID the ID/name of the NAND gate to be added
     * @throws IllegalArgumentException if a node with the given ID/name already
     * exists in the circuit
     */
    public void addNandGate(String nodeID) throws IllegalArgumentException {
        circuit.addNode(new NandGate(nodeID));
    }

    /**
     * Adds an OR gate to the circuit.
     * 
     * @param nodeID the ID/name of the OR gate to be added
     * @throws IllegalArgumentException if a node with the given ID/name already
     * exists in the circuit
     */
    public void addOrGate(String nodeID) throws IllegalArgumentException {
        circuit.addNode(new OrGate(nodeID));
    }

    /**
     * Adds a NOR gate to the circuit.
     * 
     * @param nodeID the ID/name of the NOR gate to be added
     * @throws IllegalArgumentException if a node with the given ID/name already
     * exists in the circuit
     */
    public void addNorGate(String nodeID) throws IllegalArgumentException {
        circuit.addNode(new NorGate(nodeID));
    }

    /**
     * Adds an XOR gate to the circuit.
     * 
     * @param nodeID the ID/name of the XOR gate to be added
     * @throws IllegalArgumentException if a node with the given ID/name already
     * exists in the circuit
     */
    public void addXorGate(String nodeID) throws IllegalArgumentException {
        circuit.addNode(new XorGate(nodeID));
    }

    /**
     * Adds an NXOR gate to the circuit.
     * 
     * @param nodeID the ID/name of the NXOR gate to be added
     * @throws IllegalArgumentException if a node with the given ID/name already
     * exists in the circuit
     */
    public void addNXorGate(String nodeID) throws IllegalArgumentException {
        circuit.addNode(new NXorGate(nodeID));
    }

    /**
     * Adds an inverter to the circuit.
     * <p>
     * An inverter cannot exist in the circuit without its source node.
     * Thus, the node it is going to invert must exist in the circuit
     * before adding its inverter. Flip-flops cannot be inverted since
     * by default they already have their own inverted output. Also,
     * output variable nodes cannot be inverted.
     * <p>
     * The inverter node's name will be the given source node ID concatenated with
     * "-inverter".
     * <p>
     * Note that although seemingly redundant, inverters themselves can have its own
     * inverter. The condition of the source node's existence still applies, so the
     * removal of the first inverter will result in the removal of its own inverter.
     * 
     * @param sourceNodeID the ID/name of the node that the inverter will correspond to
     * @throws IllegalArgumentException if the given source node ID does not exist in the
     * circuit, or the source node is either a flip-flop or output variable node, or the
     * inverter to be added to the circuit already exists (name duplicate)
     */
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

    /**
     * Adds a connection in the circuit.
     * <p>
     * The indeces start from zero, so use index 0 for the first node in the
     * circuit, and so on.
     * <p>
     * Nodes cannot have a connection to itself. In other words, the two parameters
     * cannot be equal. The source node cannot be an output variable node or a flip-flop.
     * The target node needs to implement the {@linkplain VariableInput} interface.
     * <p>
     * The exceptions pertaining to indeces will contain a message regarding the
     * node number. For instance, if a connection from the first to second node
     * already exists, then an exception with the message "1 to 2 connection already
     * exists" will be thrown.
     * 
     * @param sourceIndex the index of the source node for the connection
     * @param targetIndex the index of the target node for the connection
     * @throws IllegalArgumentException if the two indeces are equal, or the specified
     * connection already exists, or either of the indeces are out of bounds, or the
     * source node is either an output variable node or a flip-flop, or the target
     * node does not implement {@linkplain VariableInput}
     */
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

        if(sourceNode instanceof OutputVariableNode || sourceNode instanceof FlipFlop)
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

    /**
     * Adds a connection in the circuit.
     * <p>
     * Nodes cannot have a connection to itself. In other words, the
     * two parameters cannot be equal. The source node cannot be an output
     * variable node or a flip-flop. The target node needs to implement the
     * {@linkplain VariableInput} interface.
     * 
     * @param sourceName the name of the source node of the connection
     * @param targetName the name of the target node of the connection
     * @throws IllegalArgumentException if either of the two names do not exist in
     * the circuit, or the two names are equal, or the connection already exists, or
     * the source node is either an output variable node or a flip-flop, or the target
     * node does not implement {@linkplain VariableInput}
     */
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
            throw new IllegalArgumentException("A connection from " + sourceName + " to " + targetName + " already exists");
        
        // source and target indeces are valid at this point
        sourceNode = circuit.getNode(sourceIndex);
        targetNode = circuit.getNode(targetIndex);

        if(sourceNode instanceof OutputVariableNode || sourceNode instanceof FlipFlop)
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

    /**
     * Removes a node from the circuit.
     * <p>
     * Flip-flop output nodes cannot be removed using this method.
     * If the node to be removed has an inverter, that inverter is
     * also removed.
     * 
     * @param nodeID the ID/name of the node to be removed
     * @throws IllegalArgumentException if the node does not exist or a flip-flop
     * output node was specified
     */
    public void removeNode(String nodeID) throws IllegalArgumentException {
        int nodeIndex = circuit.indexOf(nodeID);

        if(nodeIndex == -1)
            throw new IllegalArgumentException(nodeID + " does not exist in this circuit");

        CSNode node = circuit.getNode(nodeIndex);

        if(node instanceof FFOutNode)
            throw new IllegalArgumentException("Output nodes for flip flops cannot be removed, try to remove the flip flop itself instead");
        else if(node instanceof FlipFlop) {
            flipFlopNodeNames.remove(node.getName());
            // if more flip-flops become supported, implement switch-case
            // here to choose what kind of flip-flop is being removed
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

    /**
     * Utility method for removing a D flip-flop from the circuit.
     * <p>
     * This method will remove both the {@code DFlipFlop} object and its
     * corresponding {@code FFOutNode} objects.
     * 
     * @param nodeIndex the index of the D flip-flop node to remove; indeces start from zero
     */
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

    /**
     * Utility method for removing an inverter from the circuit.
     * 
     * @param inverterIndex the index of the inverter to remove; indeces start from zero
     * @param indecesToRemove reference to a list that, after this method's execution, will
     * contain the indeces of the nodes to be removed
     */
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

    /**
     * Renames a node in the circuit.
     * <p>
     * The given new name cannot already exist in the circuit. The node to
     * be renamed cannot be a flip-flop output node or an inverter. If the
     * node to be renamed has an inverter or output node (for flip-flops),
     * then they will be renamed as well.
     * 
     * @param nodeIndex the index of the node to be renamed; indeces start from zero
     * @param newName the new name of the node
     * @throws IllegalArgumentException if the new name already exists, or the given index is
     * out of bounds, or the node to be renamed is either a flip-flop output node or an
     * inverter
     */
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

    /**
     * Removes a connection from this circuit.
     * <p>
     * The indeces start from zero, so use index 0 for the first node in
     * the circuit, and so on.
     * <p>
     * The connection between a {@code FlipFlop} object and its corresponding
     * {@code FFOutNode} objects cannot be removed.
     * <p>
     * The exceptions pertaining to indeces will contain a message regarding the
     * node number. For instance, if the given index is 3 and is invalid, then the
     * exception will contain the message "4 is an invalid index".
     * 
     * @param sourceIndex the index of the source node of the connection
     * @param targetIndex the index of the target node of the connection
     * @throws IllegalArgumentException if the specified connection does not exist, or
     * any of the indeces are out of bounds, or the source node is a flip-flop
     */
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

    /**
     * Removes a connection from this circuit.
     * <p>
     * The connection between a {@code FlipFlop} object and its corresponding
     * {@code FFOutNode} objects cannot be removed.
     * 
     * @param sourceName the name of the source node of the connection
     * @param targetName the name of the target node of the connection
     * @throws IllegalArgumentException if either of the two nodes does not
     * exist in the circuit, the connection does not exist, or the source node
     * is a flip-flop
     */
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
        
        // source and target indeces are valid at this point
        sourceNode = circuit.getNode(sourceIndex);
        targetNode = circuit.getNode(targetIndex);

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

    /**
     * Sets an input sequence to a specified input variable node in the circuit.
     * 
     * @param inputNodeID the ID/name of the input varaible node
     * @param newSeq the new sequence to be set
     */
    public void setInputSeq(String inputNodeID, int[] newSeq) {
        int nodeIndex = circuit.indexOf(inputNodeID);
        InputVariableNode node = (InputVariableNode)circuit.getNode(nodeIndex);

        node.setInputSeq(newSeq);
    }

    /**
     * Gets the names of the nodes currently being tracked.
     * 
     * @return an array containing the names of the tracked nodes
     */
    public String[] getTrackedNodeNames() {
        String[] nodeNames = new String[trackedNodes.size()];

        for(int i = 0; i < trackedNodes.size(); i++)
            nodeNames[i] = trackedNodes.get(i).getName();

        return nodeNames;
    }

    /**
     * Tracks a node in the circuit.
     * <p>
     * If the index given is 3 and is invalid, then an exception
     * is thrown with the message "4 is an invalid index".
     * An exception is also thrown when the specified node is
     * already being tracked.
     * 
     * @param nodeIndex the index of the node to be tracked; indeces start from zero
     * @throws IllegalArgumentException if the index is out of bounds or the node
     * is already being tracked
     */
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

    /**
     * Tracks all the nodes in the circuit.
     * <p>
     * Nodes already being tracked will retain their track numbers.
     * The untracked nodes become tracked in order from first node
     * to last node in the circuit.
     */
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

    /**
     * Untracks a node in the circuit.
     * <p>
     * The parameter refers to the tracked node's index in the list
     * of tracked nodes, not the index in the circuit. So out of bounds
     * would be a negative integer or an integer greater than or equal to
     * the number of nodes currently being tracked.
     * <p>
     * To be clear, pass the integer 0 as an argument to untrack the node
     * whose track number is 1; and so on.
     * 
     * @param nodeIndex the index of the tracked node in the list of tracked
     * nodes; indeces start from zero
     * @throws IllegalArgumentException if the index is out of bounds
     */
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

    /**
     * Untracks all nodes that are currently being tracked.
     */
    public void untrackAllNodes() {
        for(CSNode node : trackedNodes)
            node.resetTrackNum();

        trackedNodes.clear();
    }

    /**
     * Gets the length of the longest input sequence in the circuit.
     * 
     * @return zero if there are no input sequences, otherwise the maximum
     * between all input sequence lengths
     */
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

    /**
     * Gets the length of the longest name in the circuit.
     * 
     * @return length of the longest name between all nodes
     */
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

    /**
     * Gets the length of the longest name between the tracked nodes.
     * 
     * @return length of the longest name between all tracked nodes
     */
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

    /**
     * Gets the current state of the circuit.
     * <p>
     * The current state of the circuit corresponds to the current
     * values of all of its nodes.
     * <p>
     * The first element of the array will contain the value of the first node
     * in the circuit, and so on.
     * 
     * @return array containing the values of all the nodes
     */
    public int[] getCurrentCircuitState() {
        int[] nodeValues = new int[trackedNodes.size()];

        for(int i = 0; i < trackedNodes.size(); i++)
            nodeValues[i] = trackedNodes.get(i).getValue();

        return nodeValues;
    }

    /**
     * Updates the circuit and gets the resulting state of the circuit.
     * <p>
     * Updating the circuit consists of updating each node in the circuit
     * in an order that simulates a working circuit. The resulting values
     * of all the nodes in the circuit are then recorded in the array to
     * be returned.
     * <p>
     * The first element of the array will contain the value of the first node
     * in the circuit, and so on.
     * 
     * @return array containing the values of all the nodes
     * @throws IllegalCircuitStateException if the circuit could not be updated
     * due to it being in an invalid state
     */
    public int[] getNextCircuitState() throws IllegalCircuitStateException {
        int[] nodeValues = new int[trackedNodes.size()];

        updateCircuit();

        for(int i = 0; i < trackedNodes.size(); i ++)
            nodeValues[i] = trackedNodes.get(i).getValue();

        return nodeValues;

    }

    /**
     * Updates the circuit.
     * 
     * @throws IllegalCircuitStateException if the circuit is in an invalid state
     */
    private void updateCircuit() throws IllegalCircuitStateException {
        String updatePath = "";

        updatePath = circuit.getUpdatePath();

        StringTokenizer tokenizer = new StringTokenizer(updatePath);

        while(tokenizer.hasMoreTokens())
            circuit.getNode(Integer.valueOf(tokenizer.nextToken())).updateValue();
    }

    /**
     * Resets the circuit.
     * <p>
     * Each node will have a value of 0.
     */
    public void resetCircuit() {
        circuit.reset();
    }

    /**
     * Returns whether or not the circuit is sequential.
     * <p>
     * A circuit is sequential if and only if it contains at least
     * one flip-flop.
     * 
     * @return true if the circuit contains a flip-flop, false otherwise
     */
    public boolean isCircuitSequential() {
        return circuit.isSequential();
    }

    /**
     * Returns whether the circuit is currently in a valid state.
     * <p>
     * In other words, true will be returned if the circuit is able
     * to be updated.
     * @return true if the circuit is in a valid state
     */
    public boolean isCircuitValid() {
        try {
            circuit.getUpdatePath();
        } catch(IllegalCircuitStateException icse) {
            return false;
        }

        return true;
    }

    /**
     * Gets the data of the circuit's truth table.
     * <p>
     * This method applies only to circuits that are not sequential. For
     * sequential circuits, refer to {@linkplain #getTransitionTableData()}.
     * <p>
     * The integer 2D array returned is formatted as:
     * <p>
     * rows = 2^n where n is number of input variables,<br>
     * columns = number of input variables + number of output variables<br>
     * 
     * @return an integer 2D array containing the data of the truth table
     * @throws IllegalStateException if the circuit is sequential, or input
     * variable nodes do not exist, or output variable nodes do not exist
     * @throws IllegalCircuitStateException if the circuit could not be updated
     * due to it being in an invalid state
     */
    public ArrayList<ArrayList<Integer>> getTruthTableData() throws IllegalStateException, IllegalCircuitStateException {
        // int 2D array formatted as:   rows = 2^n where n is number of input variables,
        //                              cols = number of input variables + number of output variables

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

        circuit.reset();

        // fill output variables section in array; for each input variable combination, store the values in the output variables
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
            if(seqString.equals(""))
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

    /**
     * Gets the data of the circuit's transition table.
     * <p>
     * This method applies only to circuits that are sequential. For
     * non-sequential circuits, refer to {@linkplain #getTruthTableData()}.
     * <p>
     * The string 2D array returned is formatted as:
     * <p>
     * rows = 2^n where n is number of D flip flops,<br>
     * cols = 1 + 2^m where m is number of input variables<br>
     * first column represents present state combination<br>
     * every column after first as: [FF..F, ZZ..Z] such that each F for a D
     * flip-flop, each Z for an output variable, both being either 0 or 1,
     * representing their next states<br>
     * 
     * @return a string 2D array containing the data of the transition table
     * @throws IllegalStateException if the circuit is not sequential or input
     * variable nodes do not exist
     * @throws IllegalCircuitStateException if the circuit could not be updated
     * due to it being in an invalid state
     */
    public ArrayList<ArrayList<String>> getTransitionTableData() throws IllegalStateException, IllegalCircuitStateException {
        // double array formatted as:   rows = 2^n where n is number of D flip flops,
        //                              cols = 1 + 2^m where m is number of input variables
        // first column represents present state combination;
        // for every column after that:
        //      each string formatted as: [FF..F, ZZ..Z]   such that each F for a D flip-flop, each Z for an output variable,
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

    /**
     * Gets the names of all the nodes in the circuit.
     * 
     * @return an array containing the names of all the nodes
     */
    public String[] getCircuitNodeNames() {
        String[] result = new String[circuit.getSize()];
        for(int i = 0; i < circuit.getSize(); i++)
            result[i] = circuit.getNode(i).getName();

        return result;
    }

    /**
     * Gets the node types of all the nodes in the circuit.
     * 
     * @return an array containing the node types of all the nodes
     */
    public String[] getCircuitNodeTypes() {
        String[] result = new String[circuit.getSize()];
        for(int i = 0; i < circuit.getSize(); i++)
            result[i] = circuit.getNode(i).getNodeType();

        return result;
    }

    /**
     * Gets the names of all the input variable nodes in the circuit.
     * 
     * @return an array containing the names of all the input variable nodes
     */
    public String[] getInputNodeNames() {
        String[] result = new String[inputNodeNames.size()];

        for(int i = 0; i < inputNodeNames.size(); i++)
            result[i] = inputNodeNames.get(i);

        return result;
    }

    /**
     * Gets the names of all the output variable nodes in the circuit.
     * 
     * @return an array containing the names of all the output variable nodes
     */
    public String[] getOutputNodeNames() {
        String[] result = new String[outputNodeNames.size()];

        for(int i = 0; i < outputNodeNames.size(); i++)
            result[i] = outputNodeNames.get(i);

        return result;
    }

    /**
     * Gets the names of all the flip-flops in the circuit.
     * 
     * @return an array containing the names of all the flip-flops
     */
    public String[] getFlipFlopNodeNames() {
        String[] result = new String[flipFlopNodeNames.size()];

        for(int i = 0; i < flipFlopNodeNames.size(); i++)
            result[i] = flipFlopNodeNames.get(i);

        return result;
    }

    /**
     * Gets the size of the circuit.
     * 
     * @return the number of nodes in the circuit
     */
    public int getCircuitSize() {
        return circuit.getSize();
    }

    /**
     * Gets general information about the circuit.
     * <p>
     * The format of the integer array to be returned (from first element to last):
     * <ol>
     *   <li>Number of input variable nodes</li>
     *   <li>Number of input sequences</li>
     *   <li>Number of output variable nodes</li>
     *   <li>Number of flip-flops</li>
     *   <li>Number of gates</li>
     *   <li>Number of inverters</li>
     *   <li>Number of connections</li>
     * </ol>
     * 
     * @return an array containing information about the circuit
     */
    public int[] getCircuitStatus() {
        int[] status = new int[7];
        CSNode node;
        InputVariableNode inputNode;
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
                inputNode = (InputVariableNode)node;
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

    /**
     * Gets the connection status of the circuit.
     * <p>
     * First element in the string array to be returned corresponds
     * to the first node in the circuit, and so on.
     * <p>
     * Each string in the array is formatted as (for example, first node):
     * "  1 -&gt; [2, 4]" (first node has connection to second and fourth node)<br>
     * Note the 3-character wide field to hold the node number.<br>
     * Output variable nodes will instead have " -&gt; [output]"<br>
     * 
     * @return array containing the connection status for each node
     */
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

    /**
     * Gets the status of the circuit's input sequences.
     * <p>
     * Format of each string in the array:
     * <p>
     * "1. name            [1, 0, 0, 1]"<br>
     * Note the 15-character wide field to hold the input variable
     * name.<br>
     * This field width is actually the maximum between 15 and the
     * longest input variable node name.<br>
     * Empty input sequences will simply have "[]".<br>
     * 
     * @return array containing the input sequence statuses
     */
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

    /**
     * Saves the current circuit as a file.
     * 
     * @param fileName the name of the save file
     * @throws FileNotFoundException if something went wrong in setting up saving the circuit
     * @throws IOException if an error occurred when attempting to save the circuit
     */
    public void saveCircuit(String fileName) throws FileNotFoundException, IOException {
        CSFileIO.writeSaveFile(circuit, fileName);
    }

    /**
     * Loads a circuit from a file.
     * 
     * @param fileName the name of the file
     * @throws FileNotFoundException if something went wrong in locating the file
     * @throws IOException if an error occurred when attemping to read the file
     * @throws ClassNotFoundException if a circuit could not be read from the file
     * @throws ClassCastException if the object read from the file is not a circuit
     */
    public void loadCircuit(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException, ClassCastException {
        CSNode node;
        Inverter inverterNode;

        circuit = CSFileIO.readSaveFile(fileName);

        // clear the list fields of their contents
        clearArrayLists();

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

    /**
     * Creates a new circuit for this engine to work on.
     */
    public void newCircuit() {
        circuit = new CSGraph();
        // clear the list fields of their contents
        clearArrayLists();
    }

    /**
     * Utility method for loading a circuit and creating a new circuit.
     */
    private void clearArrayLists() {
        inputNodeNames.clear();
        outputNodeNames.clear();
        flipFlopNodeNames.clear();
        invertedNodes.clear();
        trackedNodes.clear();
    }
}