package simulator.circuit.project;

/*
 * CSNodes that implement this interface are able to change their input node reference,
 * as in the CSNode that they are getting their input from can vary; hence they are able
 * to "add" or "remove" their input node reference.
 */
public interface VariableInput {
    public void addInputNode(CSNode node);
    public void removeInputNode(CSNode node);
}