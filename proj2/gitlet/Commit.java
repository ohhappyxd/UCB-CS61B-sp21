package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Xinxin
 */
public class Commit implements Serializable {
    static final File COMMITS_FOLDER = Utils.join(".gitlet", "commits");
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    public String message;
    /** The time the Commit was created. */
    public Date timestamp;
    /** A unique id of the Commit. */
    public String id;
    /** First parent of the Commit. */
    public String parent1;
    /** Second parent of the Commit. */
    public String parent2;
    /** A mapping of SHA-1 hash of blobs to the files in the blobs directory. */
    public HashMap mappingToBlob;

    public Commit(String message, Date timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    /* TODO: fill in the rest of this class. */
    //Saves the commit object.
    public void saveCommit() {
        File outFile = Utils.join(COMMITS_FOLDER, this.id);
        if (!outFile.exists()) {
            outFile.createNewFile();
        }
        Utils.writeObject(outFile, this);
    }
}
