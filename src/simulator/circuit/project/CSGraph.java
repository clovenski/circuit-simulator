package simulator.circuit.project;

import java.util.ArrayList;
import java.util.LinkedList;

/* Graph needs to be directed, unweighted that will take in CSNode objects as its nodes.
 * Represent this graph with adjacency lists.
 */
public class CSGraph {
    private ArrayList<CSNode> nodes;
    private ArrayList<LinkedList<CSNode>> edges;
    private ArrayList<FFOutNode> flipFlopOutNodes;
    private ArrayList<InputVariableNode> inputNodes;

    public CSGraph() {

    }

    private void addCSNode(CSNode newNode) {
        nodes.add(newNode);
        edges.add(new LinkedList<CSNode>());
    }

    private void removeCSNode(int targetIndex) {
        CSNode targetNode = nodes.get(targetIndex);
        
        nodes.remove(targetIndex);
        edges.remove(targetIndex);
        for(LinkedList<CSNode> adjacencyList : edges)
            adjacencyList.remove(targetNode);
    }

    public void removeNode(int targetIndex) {
        CSNode targetNode = nodes.get(targetIndex);

        if(targetNode instanceof FlipFlop || targetNode instanceof FFOutNode || targetNode instanceof Inverter)
            throw new Exception(); // replace, message is call appropriate remove node method instead of this

        // remove this node from dependent nodes
        Dependent depNode;
        for(CSNode node : edges.get(targetIndex)) {
            if(node instanceof Dependent) {
                depNode = (Dependent)node;
                depNode.removeInputNode(targetNode);
            }
        }
        nodes.remove(targetIndex);
        edges.remove(targetIndex);
        for(LinkedList<CSNode> adjacencyList : edges)
            adjacencyList.remove(targetNode);
    }

    public void addEdge(int sourceIndex, int targetIndex) {
        CSNode sourceNode = nodes.get(sourceIndex);
        CSNode targetNode = nodes.get(targetIndex);

        if(sourceNode instanceof FlipFlop || sourceNode instanceof OutputVariableNode)
            throw new Exception(); // replace this exception, message is this node cannot be a source for an edge
        if(targetNode instanceof FFOutNode || targetNode instanceof InputVariableNode)
            throw new Exception(); // replace this exception, message is this node cannot be a target for an edge

        Dependent targetDepNode = (Dependent)targetNode;
        LinkedList<CSNode> sourceAdjList = edges.get(sourceIndex);
        if(!sourceAdjList.contains(targetNode)) {
            sourceAdjList.add(targetNode);
            targetDepNode.addInputNode(sourceNode);
        }
    }

    public void removeEdge(int sourceIndex, int targetIndex) {
        CSNode sourceNode = nodes.get(sourceIndex);
        Dependent targetNode = nodes.get(targetIndex);

        if(sourceNode instanceof FlipFlop)
            throw new Exception(); // replace this exception, message is cannot remove edge between a flip flop node and its out nodes

        
        edges.get(sourceIndex).remove(targetNode);

        targetNode.removeInputNode(sourceNode);

    }

    public void addDFFNode(String nodeID) {
        DFlipFlop newNode = new DFlipFlop(nodeID);
        addCSNode(newNode);
        FFOutNode outNode = new FFOutNode(nodeID + "-out", newNode);
        addCSNode(outNode);
        FFOutNode outNodeNeg = new FFOutNode(nodeID + "-outnegated", newNode);
        addCSNode(outNodeNeg);

        // get adjacency list for newly created DFlipFlop,
        // add edges to its appropriate output nodes
        addEdge(edges.size() - 3, nodes.size() - 2);
        addEdge(edges.size() - 3, nodes.size() - 1);

        flipFlopOutNodes.add(outNode);
        flipFlopOutNodes.add(outNodeNeg);
    }

    public void removeDFFNode(int nodeIndex) {
        CSNode targetNode = nodes.get(nodeIndex);
        if(!(targetNode instanceof DFlipFlop))
            throw new Exception(); // replace, message is this node is not a d flip flop

        // targetNode is ensured to be a d flip flop
        // remove flip flop nodes from dependent nodes
        Dependent depNode;
        for(int i = 1; i <= 2; i++) {
            for(CSNode node : edges.get(nodeIndex + i)) {
                if(node instanceof Dependent) {
                    depNode = (Dependent)node;
                    depNode.removeInputNode(targetNode);
                }
            }
        }
        // remove flip flop out nodes first
        FFOutNode outNode = nodes.get(nodeIndex + 1);
        FFOutNode outNodeNeg = nodes.get(nodeIndex + 2);
        removeCSNode(nodeIndex + 2);
        removeCSNode(nodeIndex + 1);
        removeCSNode(nodeIndex);

        flipFlopOutNodes.remove(outNode);
        flipFlopOutNodes.remove(outNodeNeg);
    }

    public void addInputVarNode(String nodeID) {
        InputVariableNode newNode = new InputVariableNode(nodeID);
        addCSNode(newNode);
        inputNodes.add(newNode);
    }

    public void removeInputVarNode(int nodeIndex) {
        CSNode targetNode = nodes.get(nodeIndex);
        if(!(targetNode instanceof InputVariableNode))
            throw new Exception(); // replace, message is this node is not an input var node

        Dependent depNode;
        for(CSNode node : edges.get(nodeIndex + 2)) {
            if(node instanceof Dependent) {
                depNode = (Dependent)node;
                depNode.removeInputNode(targetNode);
            }
        }
        // targetNode is ensured to be an input var node
        inputNodes.remove((InputVariableNode)targetNode);
        removeCSNode(nodeIndex);
    }

    public void addOutputVarNode(String nodeID) {
        OutputVariableNode newNode = new OutputVariableNode(nodeID);
        addCSNode(newNode);
    }

    public void addGateNode(String nodeID, int gateType) {
        Gate newGate;

        switch(gateType) {
            case 0:
                newGate = AndGate(nodeID);
                break;
            case 1:
                newGate = OrGate(nodeID);
                break;
            default:
                throw new Exception(); // replace, message is illegal gate type argument
        }

        addCSNode(newGate);
    }

    public void addInverter(String nodeID, int sourceIndex, int targetIndex) {
        if(nodes.get(sourceIndex) instanceof FlipFlop)
            throw new Exception(); // replace, message is cannot invert connection between d flip flop and its out node
        
        Dependent targetNode = nodes.get(targetIndex);
        LinkedList<CSNode> adjList = edges.get(sourceIndex);
        for(CSNode node : adjList) {
            if(node == targetNode) {
                adjList.remove(targetNode);
                
            }
        }
    }
}