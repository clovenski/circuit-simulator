# circuit-simulator

A program that can help design simple circuits.

Initial ideas/Notes:

+ goal is to allow the user to design a circuit with inputs, outputs, gates and flip-flops and check the state of the entire circuit with each clock cycle
+ to start off small, only AND/OR/NOT gates and D flip-flops can be used, as well as user-defined input/output variables
+ the seven segment display problem can be a good milestone to target
+ the program itself will be in the command-line interface first, essentially being text-based, until fully functional
+ the ultimate end goal would be a complete GUI interface rich in functionality and features
+ ideally, the user would be able to set up the circuit by creating the objects and connecting them to represent their circuit and then be able to get information about their circuit, such as the states of the output variables and the flip-flops, for every clock cycle
+ a graphical representation of the circuit in the command-line interface would be ideal, but no idea for that so far
+ saving and loading to a file
+ expect the user interface to be full of options
+ classes: DFlipFlop, Gate, Input, Output, Clock, CircuitSimulator, etc.
+ entire program can be represented with a directed, unweighted graph represented with adjacency lists
+ for updating the graph, topological ordering? with DFlipFlop next state nodes updated from present state node
+ only DFlipFlop needs to be separate object that has references to the appropriate nodes in graph for data output?
+ DFlipFlops will consist of three nodes in the graph, one for present state input, which has edges to only two nodes, both being next state nodes

Progress Notes:

+ removal of nodes and thus updating dependent nodes cannot handle Inverter objects since it does not implement Dependent
+ some remove node methods need to implement updating dependent nodes
+ current design is a mess, can continue but may need to really redesign the entire representation of nodes in the graph
