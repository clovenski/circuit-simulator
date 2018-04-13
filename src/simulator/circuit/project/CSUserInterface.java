package simulator.circuit.project;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class CSUserInterface {
    public static void displayOptions(ArrayList<String> options) {
        int optNum = 1;

        for(String option : options) {
            System.out.println(optNum + ". " + option);
            optNum++;
        }
        System.out.println();
    }

    public static int getUserOptInput(ArrayList<String> options, Scanner inputSource) {
        StringTokenizer tokenizer;
        int userInput = 0;

        do {
            System.out.print("Enter the option number > ");
            try {
                tokenizer = new StringTokenizer(inputSource.nextLine());
                userInput = Integer.parseInt(tokenizer.nextToken());
            } catch(NumberFormatException nfe) {
                System.err.println("Invalid input. Please enter an integer.");
                continue;
            } catch(NoSuchElementException nsee) {
                System.err.println("Please enter an input.");
                continue;
            }

            if(userInput <= 0 || userInput > options.size()) {
                System.err.println("Invalid input.");
                continue;
            }

            // reachable only for valid inputs
            return userInput;
        } while(true);
    }
}