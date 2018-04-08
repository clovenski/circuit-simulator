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

    public CSNode getNode(int targetIndex) {
        return nodes.get(targetIndex);
    }

    public void addEdge(int sourceIndex, int targetIndex) {
        edges.get(sourceIndex).add(targetIndex);
    }

    public void removeEdge(int sourceIndex, int targetIndex) {
        edges.get(sourceIndex).remove(targetIndex);
    }

    public String getUpdatePath() throws Exception { // replace with proper exception, message is circuit has infinite loop
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

    private void getUpdatePathUtil(char[] marks, int nodeIndex, LinkedList<Integer> indexPath) {
        if(marks[nodeIndex] == 'P')
            return;
        if(marks[nodeIndex] == 'T')
            throw new Exception(); // current graph is cyclic
        marks[nodeIndex] = 'T';
        for(int targetNodeIndex : edges.get(nodeIndex))
            getUpdatePathUtil(marks, targetNodeIndex, indexPath);
        marks[nodeIndex] = 'P';
        indexPath.addFirst(nodeIndex);
    }
}