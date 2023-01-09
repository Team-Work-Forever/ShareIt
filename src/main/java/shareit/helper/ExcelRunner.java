package shareit.helper;

import java.io.IOException;

public class ExcelRunner {
    

    public static void runExcelCommand(String path) {

        try {
            Runtime.getRuntime().exec("cmd /c start excel.exe " + ".\\" + path);
        } catch (IOException e) {
            throw new RuntimeException("You don't have Excel!");
        }

    }
    
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
