# Example

The seven segment display problem involves designing a circuit that will implement displaying the numbers 0 through 9 with seven segments arranged in a way that combinations of those segments being turned "on" will present the appropriate number.

Below is an example that solves this problem using a saved circuit created by the circuit simulator program.

![Alt text](captures/7seg_example.gif)

## Reproduction

To reproduce this example, compile and run the `CSExample.java` file located within this directory. To do so, ensure you are in this directory, then run this command

Windows:

`javac -cp ..\build CSExample.java`

Then run the program with

`java -cp ".;..\build" CSExample`

## Usage

Now `CSExample.java` was programmed to expect a specific save file (one that I have prepared for the example) to feed it the appropriate data, but you can also use it to test your very own circuit. In order to do this, follow these steps.

![Alt text](captures/7seg_format.PNG)

+ according to the format above, name your output variables "a" through "g" appropriately
+ ensure that your input sequences combined represent the appropriate binary number to show for each clock tick
+ save your circuit with the file name "SevenSegmentExample" or whatever you like
+ copy the file into the "cs-saves" folder located inside this directory (it should be a folder that already includes "SevenSegmentExample")
+ if you named your file something other than "SevenSegmentExample" then use your circuit file name as an argument when running the example program
