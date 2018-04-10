package simulator.circuit.project;

import java.util.ArrayList;
import java.util.LinkedList;

/* Graph needs to be directed, unweighted that will take in CSNode objects as its nodes.
 * Represent this graph with adjacency lists.
 */
public class CSGraph {
    private ArrayList<CSNode> nodes;
    private ArrayList<LinkedList<Integer>> edges;

    public CSGraph() {
        nodes = new ArrayList<CSNode>();
        edges = new ArrayList<LinkedList<Integer>>();
    }

    public void addNode(CSNode newNode) {
        nodes.add(newNode);
        edges.add(new LinkedList<Integer>());
    }

    public void removeNode(int targetIndex) {
        nodes.remove(targetIndex);
        edges.remove(targetIndex);
        for(LinkedList<Integer> adjacencyList : edges) {
            adjacencyList.remove(targetIndex);
            for(int i = 0; i < adjacencyList.size(); i++) {
                int targetNodeIndex = adjacencyList.get(i);
                if(targetNodeIndex > targetIndex)
                    adjacencyList.set(i, targetNodeIndex - 1);
            }
        }
    }

    public CSNode getNode(int nodeIndex) {
        return nodes.get(nodeIndex);
    }

    public CSNode getNode(String nodeID) throws IllegalArgumentException {
        for(CSNode node : nodes)
            if(node.getName().equals(nodeID))
                return node;

        throw new IllegalArgumentException(nodeID + " does not exist in this graph");
    }

    public boolean contains(String nodeID) {
        for(CSNode node : nodes)
            if(node.getName() == nodeID)
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

    public String getUpdatePath() throws IllegalCircuitStateException {
        char[] marks = new char[nodes.size()];
        LinkedList<Integer> flipFlopOutNodes = new LinkedList<Integer>();
        LinkedList<Integer> indexPath = new LinkedList<Integer>();
        String updatePath = "";

        for(int i = 0; i < marks.length; i++)
            marks[i] = 'U';

        // mark FFOutNodes as P to break cycles
        for(int i = 0; i < nodes.size(); i++)
            if(nodes.get(i) instanceof FFOutNode) {
                marks[i] = 'P';
                flipFlopOutNodes.add(i);
            }

        for(int i = 0; i < marks.length; i++)
            if(marks[i] == 'U')
                getUpdatePathUtil(marks, i, indexPath);

        for(int outNodeIndex : flipFlopOutNodes)
            indexPath.addFirst(outNodeIndex);

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
        for(int targetNodeIndex : edges.get(nodeIndex))
            getUpdatePathUtil(marks, targetNodeIndex, indexPath);
        marks[nodeIndex] = 'P';
        indexPath.addFirst(nodeIndex);
    }

    class IllegalCircuitStateException extends Exception {
        private static final long serialVersionUID = 1L;
        public IllegalCircuitStateException() {
            super("The current state of this circuit is invalid.");
        }
    }
}