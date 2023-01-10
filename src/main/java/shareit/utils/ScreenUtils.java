package shareit.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ScreenUtils {
    
    private static BufferedReader bufferInput = new BufferedReader(new InputStreamReader(System.in));
    
    public static final String DEFAULT_COLOR = "\u001B[0m"; // DEFAULT
    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE

    /**
     * Reset color to default colors of console
     */
    public static void resetColors() {
        System.out.println(ScreenUtils.DEFAULT_COLOR);
    }

    /**
     * Write a custom menu dependent of the options and includes
     * an option by default that returns 0, meaning Go Back
     * @param title Menu Title
     * @param opt Options Names
     * @param authUser Authenticated User Name
     * @return Index of the option chosen
     * @throws IOException
     */
    public static int menu(String title, String[] opt, String authUser) throws IOException {

        int index = 0;

        System.out.println("\t  " + title);

        do {
            
            for (int i = 0; i < opt.length; i++) {
                System.out.println("\t " + (i+1) + " - " + opt[i]);
            }

            System.out.println("\t 0 - Go Back");
            System.out.print(authUser.isEmpty() ? "# " : "(" + authUser + ")# ");
            index = Integer.parseInt(bufferInput.readLine());

        } while (index <= 0 && index >= opt.length);

        return index;

    }

    /**
     * Captures the input from the user and removes any extra spaces
     * @param prompt title of the question
     * @return the value of the input of the user
     * @throws IOException
     */
    public static String textField(String prompt) throws IOException {

        String value;

        System.out.print(prompt + ": ");
        value = bufferInput.readLine().trim();

        return value;
    }

    /**
     * Print to screen on information format in Yellow
     * @param value is the information needed to print
     * @throws IOException
     */
    public static void printInfo(String value) throws IOException {
        System.out.print(ScreenUtils.YELLOW);
        System.out.println(value);
        resetColors();
    }

    /**
     * Print to screen on information format in Purple
     * @param value is the information needed to print
     * @throws IOException
     */
    public static void alternativePrint(String value) throws IOException {
        System.out.print(ScreenUtils.PURPLE);
        System.out.println(value);
        resetColors();
    }

    /**
     * Print to screen on Success format in Green
     * @param value is the Success needed to print
     * @throws IOException
     */
    public static void printSuccess(String value) throws IOException {
        System.out.print(ScreenUtils.GREEN);
        System.out.println(value);
        resetColors();
    }

    /**
     * Print to screen on Error format in Red
     * @param value is the Error needed to print
     * @throws IOException
     */
    public static void printError(String error) throws IOException {
        System.out.print(ScreenUtils.RED);
        System.out.println("Error : " + error);
        resetColors();

        waitForKeyEnter();
    }

    /**
     * Captures the input from the user (separated by (,)) and removes any extra spaces
     * @param prompt title of choice
     * @return String[] is a set that holds all selected choices that were separated by (,)
     * @throws IOException
     */
    public static String[] comboBox(String prompt) throws IOException {

        String output;

        System.out.println(prompt);

        System.out.println("\r");
        output = bufferInput.readLine().trim();
            
       String[] values = output.split(",");

        return values;

    }

    /**
     * Captures the input from the user (separated by (,) and separated by (any signal)) and removes any extra spaces
     * @param prompt title of choice
     * @param plus character separator
     * @return Map<String, Integer> is a set that holds all selected choices that were separated by (,)
     * @throws IOException
    */
    public static Map<String, Integer> comboBox(String prompt, String plus) throws IOException {

        Map<String, Integer> set = new HashMap<>();
        String output;

        System.out.println(prompt);

        System.out.println("\r");
        output = bufferInput.readLine().trim();
            
        String[] values = output.split(",");
        String[] subValues = output.split(plus);

        for (int i = 0; i < values.length; i++) {
            set.put(subValues[0], subValues[i + 1] == null ? 0 : Integer.parseInt(subValues[i + 1]));
        }

        return set;

    }

    /**
     * Clear screen
     */
    public static void clear() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }
    
    /**
     * Holds Execution of thread until the key enter is pressed
     * @throws IOException
     */
    public static void waitForKeyEnter() throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        bf.readLine();
    }

    /**
     * Closes the BufferReader created early
     * @throws IOException
     */
    public static void closeBuffer() throws IOException {
        bufferInput.close();
    }

}
