package shareit.helper;

import java.io.IOException;

public class ExcelRunner {
    
    /**
     * Run Cmd Command
     * @param path Given Command
     */
    public static void runExcelCommand(String path) {

        try {
            Runtime.getRuntime().exec("cmd /c start excel.exe " + ".\\" + path);
        } catch (IOException e) {
            throw new RuntimeException("You don't have Excel!");
        }

    }
    
    /**
     * Verify if Has Excel
     * @return true if yes
     */
    public static boolean hasExcel() {
        
        try {
            
        Process command = Runtime.getRuntime().exec("where excel.exe");
        command.waitFor();

            if (command.exitValue() == 1) {
                return true;
            }

        } catch (IOException | InterruptedException e) {
            return false;
        }

        return false;

    }

}
