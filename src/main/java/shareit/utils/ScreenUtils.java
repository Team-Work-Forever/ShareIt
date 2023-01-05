package shareit.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ScreenUtils {
    
    public static BufferedReader bufferInput = new BufferedReader(new InputStreamReader(System.in));

    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE

    public static void clear() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }

    public static String textField(String prompt) throws IOException {

        String value;

        System.out.print(prompt + ": ");
        value = bufferInput.readLine().trim();

        return value;

    }

    public static void resetColors() {
        System.out.print(ScreenUtils.BLACK);
        System.out.print(ScreenUtils.WHITE);
    }

    public static int menu(String title, String[] opt) throws IOException {

        int index = 0;

        System.out.println("\t  " + title);

        do {
            
            
            for (int i = 0; i < opt.length; i++) {
                System.out.println("\t " + (i+1) + " - " + opt[i]);
            }

            System.out.println("\t 0 - Go Back");
            System.out.print("# ");
            index = Integer.parseInt(bufferInput.readLine());

        } while (index <= 0 && index >= opt.length);

        return index;

    }

    public static void printInfo(String value) throws IOException {
        System.out.print(ScreenUtils.YELLOW);
        System.out.println(value);
        resetColors();
    }

    public static void printSuccess(String value) throws IOException {
        System.out.print(ScreenUtils.GREEN);
        System.out.println(value);
        resetColors();
    }

    public static void printError(String error) throws IOException {
        System.out.print(ScreenUtils.RED);
        System.out.println("Error : " + error);
        resetColors();

        waitForKeyEnter();
    }

    public static void waitForKeyEnter() throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        bf.readLine();
    }

    public static String[] comboBox(String prompt) throws IOException {

        String output;

        System.out.println(prompt);

        System.out.println("\r");
        output = bufferInput.readLine();
            
       String[] values = output.split(",");

        return values;

    }

}
