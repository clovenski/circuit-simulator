package simulator.circuit.project;

import java.util.Comparator;

/**
 * Class to compare two {@code CSNode} objects by their track number.
 * 
 * @author Joel Tengco
 */
public class CSNodeTrackNumComparator implements Comparator<CSNode> {
    /**
     * Compare two Circuit Simulator nodes by their track numbers.
     * 
     * @param node1 first node to compare
     * @param node2 second node to compare
     * @return a positive number if first node's track number is greater, a
     * negative number is second node's track number is greater, or zero if
     * they are equal
     */
    public int compare(CSNode node1, CSNode node2) {
        return node1.getTrackNum() - node2.getTrackNum();
    }
}