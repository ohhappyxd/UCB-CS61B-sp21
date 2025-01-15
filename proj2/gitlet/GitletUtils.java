package gitlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;

/** Converts an object to a byte array, used for generating of SHA-1 hash. */
public class GitletUtils {
    public static byte[] toByteArray(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Internal error serializing commit.");
        }
    }

    public static String generateSHA1FromFile(File file) {
        byte[] content = Utils.readContents(file);
        return Utils.sha1(content);
    }

    public static String generateSHA1FromObject(Object obj) {
        byte[] content = toByteArray(obj);
        return Utils.sha1(content);
    }

    public static String getDirFromID(String id) {
        return id.substring(0,2);
    }

    public static String getFileNameFromID(String id) {
        return id.substring(2);
    }

}
