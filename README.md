# circuit-simulator

A program that can help design simple circuits.

This is currently a work in progress.

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

Progress Notes/To-do list:

+ implement text-based UI
  + implement testing circuit in CircuitTester class
+ testing the circuit can most likely be dump data of the states of the nodes for all clock cycles
+ design a way to demonstrate a circuit that solves the seven segment display problem
+ considering reverting save object to be CSGraph instead of CSEngine
