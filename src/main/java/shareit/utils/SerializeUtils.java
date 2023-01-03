package shareit.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeUtils {
    
    public static void serialize(Object object, String filename) throws IOException, ClassNotFoundException {

        FileOutputStream fileOutputStream = new FileOutputStream(filename);
        ObjectOutputStream ObjectOutputStream = new ObjectOutputStream(fileOutputStream);
        ObjectOutputStream.writeObject(object);

        ObjectOutputStream.close();

    }

    public static Object deserialize(String filename) throws IOException, ClassNotFoundException {

        Object object = null;

        FileInputStream fileOutputStream = new FileInputStream(filename);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileOutputStream);

        object = objectInputStream.readObject();
        objectInputStream.close();

        return object;

    }

}