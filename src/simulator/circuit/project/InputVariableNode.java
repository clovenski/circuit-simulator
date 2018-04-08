package simulator.circuit.project;

import java.util.Arrays;

public class InputVariableNode extends CSNode {
    private int[] inputSeq;
    private int currentIndex;

    public InputVariableNode(String name) {
        super(name);
        currentIndex = 0;
    }

    public InputVariableNode(String name, int[] inputSeq) {
        super(name);
        this.inputSeq = inputSeq;
        currentIndex = 0;
    }

    public void setInputSeq(int[] inputSeq) {
        this.inputSeq = inputSeq;
        currentIndex = 0;
    }

    public String getInputSeq() {
        return Arrays.toString(inputSeq);
    }
    
    public void resetValue() {
        value = 0;
        currentIndex = 0;
    }

    public void updateValue() {
        if(inputSeq == null || currentIndex == inputSeq.length)
            value = 0;
        else {
            value = inputSeq[currentIndex];
            currentIndex++;
        }
    }
}