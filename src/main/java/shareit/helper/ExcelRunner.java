package shareit.helper;

import java.io.IOException;

public class ExcelRunner {
    

    public static void runExcelCommand(String path) {

        try {
            Runtime.getRuntime().exec("cmd /c start excel.exe " + ".\\" + path);
        } catch (IOException e) {
            throw new RuntimeException("Error Running command!");
        }

    }
    
    public static boolean hasExcel() {
        
        try {
            
            if (Runtime.getRuntime().exec("where excel.exe").exitValue() == 0) {
                return true;
            }

        } catch (IOException e) {
            return false;
        }

        return false;

    }

}
