package shareit.utils;

import java.io.File;
import java.time.LocalDate;
import java.util.UUID;

public class StoreUtils {
    
    public static final String APPDATA = System.getenv("APPDATA");
    public static final String MAIN_FOLDER = "ShareIt";
    public static final String DATA_FILE = getStorageFolder() + "\\" + "data.dat";
    public static String generateRandomFile() {
        return getReportFolder() + "\\" + UUID.randomUUID().toString() + "-" + LocalDate.now() + ".csv";
    }

    public static boolean verifyFile(String file) {
        File verify = new File(file);

        return verify.exists();
    }
    
    public static final String getStorageFolder() {
        
        String path = APPDATA + "\\" + MAIN_FOLDER;

        File storageFolder = new File(path);

        if (!storageFolder.exists()) {
            storageFolder.mkdir();
        }
        
        return path;
    }

    public static final String getReportFolder() {
        
        String path = getStorageFolder() + "\\" + "reports";

        File storageFolder = new File(path);

        if (!storageFolder.exists()) {
            storageFolder.mkdir();
        }
        
        return path;
    }

}