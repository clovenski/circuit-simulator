package simulator.circuit.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

/* Graph date structure to represent the circuits created by the user.
 */
public class CSGraph implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<CSNode> nodes;
    private ArrayList<LinkedList<Integer>> edges;

    public CSGraph() {
        nodes = new ArrayList<CSNode>();
        edges = new ArrayList<LinkedList<Integer>>();
    }

    public void addNode(CSNode newNode) throws IllegalArgumentException {
        String newNodeName = newNode.getName();

        for(CSNode node : nodes)
            if(newNodeName.equals(node.getName()))
                throw new IllegalArgumentException("Node with that name already exists");
        
        nodes.add(newNode);
        edges.add(new LinkedList<Integer>());
    }

    public void removeNode(int targetIndex) {
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

    public CSNode getNode(int nodeIndex) throws IndexOutOfBoundsException {
        return nodes.get(nodeIndex);
    }

    public CSNode getNode(String nodeID) throws IllegalArgumentException {
        for(CSNode node : nodes)
            if(node.getName().equals(nodeID))
                return node;

        throw new IllegalArgumentException(nodeID + " does not exist");
    }

    public LinkedList<Integer> getAdjList(int nodeIndex) {
        LinkedList<Integer> temp = new LinkedList<Integer>(edges.get(nodeIndex));
        return temp;
    }

    public boolean contains(String nodeID) {
        for(CSNode node : nodes)
            if(node.getName().equals(nodeID))
                return true;

        return false;
    }

    public boolean containsEdge(int sourceIndex, int targetIndex) {
        return edges.get(sourceIndex).contains(targetIndex);
    }

    public int indexOf(String nodeID) {
        for(int i = 0; i < nodes.size(); i++)
            if(nodes.get(i).getName().equals(nodeID))
                return i;

        return -1;
    }

    public void addEdge(int sourceIndex, int targetIndex) {
        edges.get(sourceIndex).add(targetIndex);
    }

    public void removeEdge(int sourceIndex, int targetIndex) {
        edges.get(sourceIndex).remove(Integer.valueOf(targetIndex));
    }

    public int getSize() {
        return nodes.size();
    }

    public int getEdgeCount() {
        int count = 0;

        for(LinkedList<Integer> adjList : edges)
            count += adjList.size();

        return count;
    }

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

    public void reset() {
        for(CSNode node : nodes)
            node.resetValue();
    }

    class IllegalCircuitStateException extends Exception {
        private static final long serialVersionUID = 1L;
        public IllegalCircuitStateException() {
            super("The current state of this circuit is invalid.");
        }
    }
}