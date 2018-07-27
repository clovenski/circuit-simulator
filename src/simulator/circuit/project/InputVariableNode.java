package simulator.circuit.project;

import java.util.Arrays;

/**
 * Class to represent an input variable node in a circuit.
 * <p>
 * These type of nodes are the entry points in a circuit, where they
 * do not have other nodes in the circuit to determine its value.
 * Instead, they depend on values set by the user.
 * 
 * @author Joel Tengco
 */
public class InputVariableNode extends CSNode {
    /**
    * Needed to implement Serializable, and thus needed for saving circuits as files.
    */
    private static final long serialVersionUID = 1L;
    /**
     * Array of integers to represent this input variable node's input sequence.
     */
    private int[] inputSeq;
    /**
     * Contains the current index within the input sequence array.
     */
    private int currentIndex;

    /**
     * Constructs a new input variable node with the specified name.
     * <p>
     * The type of this node is "INPUT".
     * 
     * @param name string to represent this input variable node with
     */
    public InputVariableNode(String name) {
        super(name, "INPUT");
        currentIndex = 0;
    }

    /**
     * Constructs a new input variable node with the specified name and input sequence.
     * <p>
     * The type of this node is "INPUT".
     * 
     * @param name string to represent this input variable node with
     * @param inputSeq the input sequence for this input variable node
     */
    public InputVariableNode(String name, int[] inputSeq) {
        super(name, "INPUT");
        this.inputSeq = inputSeq;
        currentIndex = 0;
    }

    /**
     * Sets a new input sequence for this input variable node.
     * <p>
     * Note that any nonzero integer in the given array will result in updating
     * this input variable node's value to 1, and a zero will simply update the
     * value to 0.
     * 
     * @param inputSeq the new input sequence for this input variable node
     */
    public void setInputSeq(int[] inputSeq) {
        this.inputSeq = inputSeq;
        currentIndex = 0;
    }

    /**
     * Gets a string representation of this input variable node's input sequence.
     * <p>
     * The string is surrounded by square brackets ("[" and "]"), with each integer separated by ", ".
     * If an input sequence does not exist, then "null" is returned.
     * 
     * @return a string representation of the input sequence
     */
    public String getInputSeq() {
        return Arrays.toString(inputSeq);
    }

    /**
     * Gets the number of integers within this input variable node's input sequence.
     * 
     * @return the size of the input sequence
     */
    public int getInputSeqLength() {
        if(inputSeq == null)
            return 0;
        else
            return inputSeq.length;
    }

    /**
     * Resets this input variable node's value.
     * <p>
     * It's value will become 0, and its next value will be the first integer in
     * its input sequence; assuming it exists.
     */
    public void resetValue() {
        value = 0;
        currentIndex = 0;
    }

    /**
     * Update this input variable node's value.
     * <p>
     * Note that any nonzero integer in the input sequence will update the value
     * to 1, otherwise a zero will simply update the value to 0.
     * If this input variable node has been already been updated to the last
     * integer in its input sequence, then its value is updated to 0 and any more
     * invocations of this method will result in no change until the value is reset
     * with {@linkplain #resetValue()}.
     */
    public void updateValue() {
        if(inputSeq == null || currentIndex == inputSeq.length)
            value = 0;
        else {
            value = (inputSeq[currentIndex] == 0 ? 0 : 1);
            currentIndex++;
        }
    }
}