package simulator.circuit.project;

/* This can be the superclass to all the objects in the simulator such as the gates, flip-flops, inputs and outputs.
 * What would mainly be defined here are all properties it needs in order for the program itself be able to interact with it.
 */
public class CSObject {
    // number of inputs
    private int inputs;
    // number of outputs
    private int outputs;
    // name of the object
    private String name;
}