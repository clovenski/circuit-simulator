package simulator.circuit.project;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Class that handles interfacing with the user.
 * <p>
 * This class handles all of the various kinds of input needed
 * from the user, as well as displaying menu options for the user
 * to choose from.
 * 
 * @author Joel Tengco
 */
public class CSUserInterface {
    /**
     * Displays the given list of options as a menu for the user.
     * <p>
     * Each option is printed on one line and prepended by its number in the
     * list. For example, the 10th option will be printed as: 10. option 10
     * 
     * @param options list of options to print as a menu
     */
    public static void displayOptions(ArrayList<String> options) {
        int listSize = options.size();
        int optNumFormat = String.valueOf(listSize).length();
        int optNum = 1;

        for(String option : options) {
            System.out.printf("%" + optNumFormat + "d. %s\n", optNum, option);
            optNum++;
        }
        System.out.println();
    }

    /**
     * Gets the user's choice of an option corresponding to the list given.
     * <p>
     * The user will be prompted for an option number until a valid one is given.
     * Invalid inputs include an empty string, a non-integer, integers less than
     * or equal to zero and integers greater than the number of options in the list.
     * 
     * @param options list of options the user is choosing from
     * @param inputSource the source of the input from the user
     * @return a positive integer up to and including the number of options in the list
     */
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
            } catch(Exception e) {
                System.err.println("Unknown error. Please try again.");
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

    /**
     * Gets a string input from the user.
     * <p>
     * The user is prompted with the given prompt and the first token of the
     * string given by the user is returned.
     * 
     * @param prompt the prompt to print out for the user
     * @param inputSource the source of the input from the user
     * @return the first token of the string given by the user
     */
    public static String getUserStringInput(String prompt, Scanner inputSource) {
        StringTokenizer tokenizer;

        do {
            System.out.print(prompt);
            try {
                tokenizer = new StringTokenizer(inputSource.nextLine());
                return tokenizer.nextToken();
            } catch(NoSuchElementException nsee) {
                System.err.println("Please enter an input.");
            } catch(Exception e) {
                System.err.println("Unknown error. Please try again.");
            }
        } while(true);
    }

    /**
     * Gets an input sequence from the user.
     * <p>
     * The user is prompted with the given prompt and every token of the string
     * given by the user that is either "1" or "0" is added to the resulting
     * integer array to be returned.
     * 
     * @param prompt the prompt to print out for the user
     * @param inputSource the source of the input from the user
     * @return an integer array of ones and zeros according to the user's input
     */
    public static int[] getUserInputSeq(String prompt, Scanner inputSource) {
        ArrayList<Integer> sequence = new ArrayList<Integer>();
        String token;
        StringTokenizer tokenizer;
        int[] result;

        do {
            System.out.print(prompt);
            try {
                tokenizer = new StringTokenizer(inputSource.nextLine());
                while(tokenizer.hasMoreTokens()) {
                    token = tokenizer.nextToken();
                    if(token.equals("1") || token.equals("0"))
                        sequence.add(Integer.parseInt(token));
                }
            } catch(NoSuchElementException nsee) {
                System.err.println("Please enter an input.");
            } catch(Exception e) {
                System.err.println("Unknown error. Please try again.");
            }
        } while(sequence.size() == 0);

        result = new int[sequence.size()];
        for(int i = 0; i < sequence.size(); i++)
            result[i] = sequence.get(i);

        return result;
    }

    /**
     * Gets a positive integer from the user.
     * <p>
     * The user is prompted with the given prompt until a number
     * is given by the user that is positive and less than or equal to the
     * specified upper bound.
     * 
     * @param prompt the prompt to print out for the user
     * @param upperBound the largest integer to be considered a valid integer from the user
     * @param inputSource the source of the input from the user
     * @return a positive integer ranging from 1 to the specified upper bound, inclusive
     * @throws IllegalArgumentException if the upper bound specified is zero
     */
    public static int getUserIntInput(String prompt, int upperBound, Scanner inputSource) throws IllegalArgumentException {
        StringTokenizer tokenizer;
        int userInput;

        if(upperBound == 0)
            throw new IllegalArgumentException("Upper bound must be greater than zero");

        do {
            System.out.print(prompt);
            try {
                tokenizer = new StringTokenizer(inputSource.nextLine());
                userInput = Integer.parseInt(tokenizer.nextToken());

                if(userInput <= 0 || userInput > upperBound)
                    throw new IllegalArgumentException("Please enter an integer within the interval [1, " + upperBound + "]");
                
                return userInput;
            } catch(NumberFormatException nfe) {
                System.err.println("Invalid input. Please enter an integer.");
                continue;
            } catch(NoSuchElementException nsee) {
                System.err.println("Please enter an input.");
                continue;
            } catch(IllegalArgumentException iae) {
                System.err.println(iae.getMessage());
            } catch(Exception e) {
                System.err.println("Unknown error. Please try again.");
                continue;
            }
        } while(true);
    }
}