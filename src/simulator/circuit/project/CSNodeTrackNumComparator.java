package simulator.circuit.project;

import java.util.Comparator;

public class CSNodeTrackNumComparator implements Comparator<CSNode> {
    public int compare(CSNode node1, CSNode node2) {
        return node1.getTrackNum() - node2.getTrackNum();
    }
}