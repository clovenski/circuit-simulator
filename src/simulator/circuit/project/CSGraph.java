package simulator.circuit.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Graph that will represent the circuits in the program.
 * <p>
 * This class contains the necessary data of a circuit and operations to
 * modify a circuit.
 * 
 * @author Joel Tengco
 */
public class CSGraph implements Serializable {
    /**
     * Needed to implement Serializable, and thus needed for saving circuits as files.
     */
    private static final long serialVersionUID = 1L;
    /**
     * List of nodes within this circuit.
     */
    private ArrayList<CSNode> nodes;
    /**
     * Adjacency lists for this circuit.
     */
    private ArrayList<LinkedList<Integer>> edges;

    /**
     * Constructs a new, empty circuit.
     */
    public CSGraph() {
        nodes = new ArrayList<CSNode>();
        edges = new ArrayList<LinkedList<Integer>>();
    }

    /**
     * Adds a node to this circuit.
     * <p>
     * Duplicate nodes are not allowed, as in nodes with the same name are not
     * allowed.
     * 
     * @param newNode the node to be added to this circuit
     * @throws IllegalArgumentException if a duplicate node is to be added
     */
    public void addNode(CSNode newNode) throws IllegalArgumentException {
        String newNodeName = newNode.getName();

        for(CSNode node : nodes)
            if(newNodeName.equals(node.getName()))
                throw new IllegalArgumentException("Node with that name already exists");
        
        nodes.add(newNode);
        edges.add(new LinkedList<Integer>());
    }

    /**
     * Removes a node from this circuit at the specified index.
     * <p>
     * The nodes in the circuit are ordered starting from zero.
     * So the first node in the circuit is located at index 0.
     * 
     * @param targetIndex the index of the node to be removed
     * @throws IndexOutOfBoundsException if the given index is negative or greater than or
     * equal to the number of nodes in this circuit
     */
    public void removeNode(int targetIndex) throws IndexOutOfBoundsException {
        int targetNodeIndex;

        nodes.remove(targetIndex);
        edges.remove(targetIndex);
        for(LinkedList<Integer> adjacencyList : edges) {
            adjacencyList.remove(Integer.valueOf(targetIndex));
            for(int i = 0; i < adjacencyList.size(); i++) {
                targetNodeIndex = adjacencyList.get(i);
                if(targetNodeIndex > targetIndex)
                    adjacencyList.set(i, targetNodeIndex - 1);
            }
        }
    }

    /**
     * Gets the node in this circuit at the specified index.
     * 
     * @param nodeIndex the index of the node to get
     * @return the node in the circuit at the given index
     * @throws IndexOutOfBoundsException if the given index is negative or greater than or
     * equal to the number of nodes in this circuit
     */
    public CSNode getNode(int nodeIndex) throws IndexOutOfBoundsException {
        return nodes.get(nodeIndex);
    }

    /**
     * Gets the node in this circuit with the specified node ID/name.
     * 
     * @param nodeID the ID/name of the node to get
     * @return the node in this circuit with the given ID/name
     * @throws IllegalArgumentException if this circuit does not contain a node with the
     * given ID/name
     */
    public CSNode getNode(String nodeID) throws IllegalArgumentException {
        for(CSNode node : nodes)
            if(node.getName().equals(nodeID))
                return node;

        throw new IllegalArgumentException(nodeID + " does not exist");
    }

    /**
     * Gets the adjacency list of a node corresponding at the given index.
     * 
     * @param nodeIndex the index of the node in this circuit
     * @return a copy of the adjacency list of the appropriate node
     * @throws IndexOutOfBoundsException if the given index is out of bounds; if it is negative
     * or greater than or equal to the number of nodes in this circuit
     */
    public LinkedList<Integer> getAdjList(int nodeIndex) throws IndexOutOfBoundsException {
        LinkedList<Integer> temp = new LinkedList<Integer>(edges.get(nodeIndex));
        return temp;
    }

    /**
     * Returns whether or not this circuit is sequential.
     * <p>
     * A circuit is defined to be sequential if it contains flip-flops,
     * because flip-flops allow next states and present states to be recorded
     * in a single clock cycle; allowing its logic to be sequential.
     * 
     * @return true if this circuit contains at least one flip-flop, false otherwise
     */
    public boolean isSequential() {
        for(CSNode node : nodes)
            if(node instanceof FlipFlop)
                return true;

        return false;
    }

    /**
     * Returns whether or not this circuit contains a node with the specified node ID/name.
     * 
     * @return true if a node with the specified ID/name exists in this circuit, false otherwise
     */
    public boolean contains(String nodeID) {
        for(CSNode node : nodes)
            if(node.getName().equals(nodeID))
                return true;

        return false;
    }

    /**
     * Returns whether or not this circuit contains the specified edge.
     * 
     * @param sourceIndex the index of the source of the edge
     * @param targetIndex the index of the target of the edge
     * @return true if the edge exists within this circuit, false otherwise
     * @throws IndexOutOfBoundsException if any of the given indeces are out of bounds; if either
     * of them are negative or greater than or equal to the number of nodes in this circuit
     */
    public boolean containsEdge(int sourceIndex, int targetIndex) throws IndexOutOfBoundsException {
        return edges.get(sourceIndex).contains(targetIndex);
    }

    /**
     * Gets the index of a node in this circuit with the specified ID/name.
     * 
     * @param nodeID the ID/name of the node
     * @return the index of the specified node, if it exists, otherwise -1 is returned
     */
    public int indexOf(String nodeID) {
        for(int i = 0; i < nodes.size(); i++)
            if(nodes.get(i).getName().equals(nodeID))
                return i;

        return -1;
    }

    /**
     * Adds an edge to this circuit.
     * 
     * @param sourceIndex the index of the node that is the source of the edge
     * @param targetIndex the index of the node that is the target of the edge
     * @throws IndexOutOfBoundsException if any of the given indeces are out of bounds; if either
     * of them are negative or greater than or equal to the number of nodes in this circuit
     */
    public void addEdge(int sourceIndex, int targetIndex) throws IndexOutOfBoundsException {
        if(targetIndex < 0 || targetIndex >= nodes.size())
            throw new IndexOutOfBoundsException(targetIndex + " is an invalid index");

        edges.get(sourceIndex).add(targetIndex);
    }

    /**
     * Removes an edge from this circuit, if it exists.
     * 
     * @param sourceIndex the index of the node that is the source of the edge
     * @param targetIndex the index of the node that is the target of the edge
     * @throws IndexOutOfBoundsException if any of the given indeces are out of bounds; if either
     * of them are negative or greater than or equal to the number of nodes in this circuit
     */
    public void removeEdge(int sourceIndex, int targetIndex) {
        if(targetIndex < 0 || targetIndex >= nodes.size())
            throw new IndexOutOfBoundsException(targetIndex + " is an invalid index");

        edges.get(sourceIndex).remove(Integer.valueOf(targetIndex));
    }

    /**
     * Gets the size of this circuit, as in the number of nodes it contains.
     * 
     * @return the number of nodes within this circuit
     */
    public int getSize() {
        return nodes.size();
    }

    /**
     * Gets the number of edges within this circuit.
     * 
     * @return the number of edges within this circuit
     */
    public int getEdgeCount() {
        int count = 0;

        for(LinkedList<Integer> adjList : edges)
            count += adjList.size();

        return count;
    }

    /**
     * Gets the update path for this circuit.
     * <p>
     * An update path corresponds to the order in which each node is visited
     * to properly update the circuit. For instance, input variable nodes
     * are the entry points to a circuit and thus need to be visited before
     * all other types of nodes. This method provides the critical information
     * needed to simulate a circuit.
     * 
     * @return the update path; a string of node indeces, each separated by a whitespace
     * @throws IllegalCircuitStateException if this circuit was found to be cyclic, even with disregard
     * to any cycles created by flip-flops
     */
    public String getUpdatePath() throws IllegalCircuitStateException {
        char[] marks = new char[nodes.size()];
        LinkedList<Integer> indexPath = new LinkedList<Integer>();
        String updatePath = "";

        for(int i = 0; i < marks.length; i++)
            marks[i] = 'U';

        for(int i = 0; i < marks.length; i++)
            if(marks[i] == 'U')
                getUpdatePathUtil(marks, i, indexPath);

        for(int index : indexPath)
            updatePath += index + " ";

        return updatePath;
    }

    /**
     * Utility method for getting the update path.
     * <p>
     * Implements a modified version of topological ordering to find a circuit's update path.
     * Sequential circuits almost certainly contain cycles, but these cycles need to be disregarded.
     * Solution is to simply skip the recursive call of this method when dealing with flip-flops,
     * allowing the update path to be properly found.
     * 
     * @param marks character array representing the marks of this circuit's nodes
     * @param nodeIndex the index of the current node
     * @param indexPath resulting list of indeces to contain the update path
     * @throws IllegalCircuitStateException if this circuit was found to be cyclic, even with disregard
     * to any cycles created by flip-flops
     */
    private void getUpdatePathUtil(char[] marks, int nodeIndex, LinkedList<Integer> indexPath) throws IllegalCircuitStateException {
        if(marks[nodeIndex] == 'P')
            return;
        if(marks[nodeIndex] == 'T')
            throw new IllegalCircuitStateException(); // current graph is cyclic
        marks[nodeIndex] = 'T';
        // skip for-loop for FlipFlop objects to break cycles
        if(!(nodes.get(nodeIndex) instanceof FlipFlop))
            for(int targetNodeIndex : edges.get(nodeIndex))
                getUpdatePathUtil(marks, targetNodeIndex, indexPath);
        marks[nodeIndex] = 'P';
        indexPath.addFirst(nodeIndex);
    }

    /**
     * Resets the nodes in this circuit.
     * <p>
     * Each node in this circuit will be reset to their initial states,
     * with values of 0.
     */
    public void reset() {
        for(CSNode node : nodes)
            node.resetValue();
    }

    /**
     * Exception to indicate that a circuit is in an illegal state.
     * <p>
     * A circuit in an illegal state means that the circuit contains logic
     * that does not make any physical sense, such as cycles within a
     * non-sequential circuit.
     * 
     * @author Joel Tengco
     */
    public class IllegalCircuitStateException extends Exception {
        /**
         * Needed to implement Serializable, and thus needed for saving circuits as files.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Constructs the exception with the specified message: "The current state of this circuit is invalid".
         */
        public IllegalCircuitStateException() {
            super("The current state of this circuit is invalid");
        }
    }
}