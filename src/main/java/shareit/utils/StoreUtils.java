package shareit.utils;

import java.io.File;
import java.time.LocalDate;
import java.util.UUID;

public class StoreUtils {
    
    private static final String APPDATA = System.getenv("APPDATA");
    
    private static final String MAIN_FOLDER = "ShareIt";

    /**
     * Constructs the path to Main storage file of the application
     */
    public static final String DATA_FILE = getStorageFolder() + "\\" + "data.dat";
    
    
    /**
     * Generates a Random string ,that contains an generated UUID and concatenated with LocalDate.now(), and constructs the path to the file of the report ending with .csv
     * @return path
     */
    public static String generateRandomFile() {
        return getReportFolder() + "\\" + UUID.randomUUID().toString() + "-" + LocalDate.now() + ".csv";
    }

    /**
     * Verify the existence of a given file path
     * @param file path
     * @return if file exists
     */
    public static boolean verifyFile(String file) {
        File verify = new File(file);

        return verify.exists();
    }
    
    /**
     * Constructs the path to the Storage Folder on APP_DATA
     * @return path
     */
    public static final String getStorageFolder() {
        
        String path = APPDATA + "\\" + MAIN_FOLDER;

        File storageFolder = new File(path);

        if (!storageFolder.exists()) {
            storageFolder.mkdir();
        }
        
        return path;
    }

    /**
     * Constructs the path to Report Folder
     * @return path
     */
    public static final String getReportFolder() {
        
        String path = getStorageFolder() + "\\" + "reports";

        File storageFolder = new File(path);

        if (!storageFolder.exists()) {
            storageFolder.mkdir();
        }
        
        return path;
    }

}