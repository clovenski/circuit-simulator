package simulator.circuit.project;

/**
 * Objects that implement this interface are able to change their input node reference,
 * as in the {@code CSNode} that they are getting their input from can vary; hence they are able
 * to "add" or "remove" their input node reference.
 * 
 * @author Joel Tengco
 */
public interface VariableInput {
    /**
     * Adds the specified node to this {@code VariableInput} object.
     * 
     * @param node the node to be added
     */
    public void addInputNode(CSNode node);

    /**
     * Removes the specified node from this {@code VariableInput} object.
     * 
     * @param node the ndoe to be removed
     */
    public void removeInputNode(CSNode node);
}