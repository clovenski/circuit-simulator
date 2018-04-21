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

    public static int getUserIntInput(String prompt, int upperBound, Scanner inputSource) {
        StringTokenizer tokenizer;
        int userInput;

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