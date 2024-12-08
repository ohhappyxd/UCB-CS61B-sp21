package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;

import static gitlet.Utils.join;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Xinxin
 */
public class Commit implements Serializable {
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The commits directory. */
    static final File COMMITS = Utils.join(GITLET_DIR, "commits");
    /** File documenting current commit. */
    public static final File CURRENT_COMMIT = Utils.join(GITLET_DIR, "CURRENT_COMMIT");
    /** The branches directory. */
    public static final File BRANCHES = Utils.join(GITLET_DIR, "branches");
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
    public HashMap<String, String> blobs;

    /** For initial commit only. */
    public Commit(String message, Date timestamp) {
        this.message = message;
        this.timestamp = timestamp;
        this.id = Utils.sha1(SerializeUtils.toByteArray(this));
        /** The field blobs maps file names to their SHA-1 hash. */
        this.blobs = new HashMap<>();
        Utils.writeObject(CURRENT_COMMIT, this);
    }

    /** Creates a new commit, copying the blobs map from parent commit,
     * setting parent of itself by copying from CURRENT_COMMIT */
    public Commit(String message) {
        this.message = message;
        this.timestamp = new Date();
        Commit lastCommit = Utils.readObject(CURRENT_COMMIT, Commit.class);
        this.blobs = lastCommit.blobs;
        this.parent1 = lastCommit.id;
        this.id = Utils.sha1(SerializeUtils.toByteArray(this));
        Utils.writeObject(CURRENT_COMMIT, this);
        // Update master pointer.
        File master = Utils.join(BRANCHES, "master");
        Utils.writeContents(master, this.id);
    }



    /* TODO: fill in the rest of this class. */
    //Saves the commit object.
    public void saveCommit() throws IOException {
        File outFile = Utils.join(COMMITS, this.id);
        if (!outFile.exists()) {
            outFile.createNewFile();
        }
        Utils.writeObject(outFile, this);
    }
}
