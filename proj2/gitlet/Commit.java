package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static gitlet.Utils.join;
import static gitlet.Repository.CWD;
import static gitlet.Repository.GITLET_DIR;
import static gitlet.Repository.COMMITS;
import static gitlet.Repository.BRANCHES_DIR;
import static gitlet.Repository.OBJECTS_DIR;
import static gitlet.Repository.HEAD;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Xinxin
 */
public class Commit implements Serializable {
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
    private HashMap<String, String> blobs;
    private static final Logger LOGGER = Logger.getLogger(Commit.class.getName());

    /** For initial commit only. */
    public Commit(String message, Date timestamp) {
        this.message = message;
        this.timestamp = timestamp;
        /** The field blobs maps file names to their SHA-1 hash. */
        this.blobs = new HashMap<>();
        this.id = SerializeUtils.generateSHA1FromObject(this);
    }

    /** Creates a new commit, copying the blobs map from parent commit,
     * setting parent of itself by copying from CURRENT_COMMIT */
    public Commit(String message) {
        this.message = message;
        this.timestamp = new Date();
        Commit lastCommit = Repository.getCurrentCommit();
        this.blobs = new HashMap<>(lastCommit.blobs);
        this.parent1 = lastCommit.id;
        this.id = SerializeUtils.generateSHA1FromObject(this);
    }

    // Returns the commit object by the ID id, returns null if such commit doesn't exist.
    public static Commit getCommitByID(String id) {
        if (id.length() == 40) {
            String folder = SerializeUtils.getDirFromID(id);
            String fileName = SerializeUtils.getFileNameFromID(id);
            File commitFile = Utils.join(OBJECTS_DIR, folder, fileName);
            if (commitFile.exists()) {
                return Utils.readObject(commitFile, Commit.class);
            }
        } else if (id.length() < 40 && !id.isEmpty()) {
            String folder = SerializeUtils.getDirFromID(id);
            if (Utils.plainFilenamesIn(Utils.join(OBJECTS_DIR, folder)) != null) {
                for (String fileName : Utils.plainFilenamesIn(Utils.join(OBJECTS_DIR, folder))) {
                    if (fileName.contains(SerializeUtils.getFileNameFromID(id))) {
                        return Utils.readObject(Utils.join(OBJECTS_DIR, folder, fileName), Commit.class);
                    }
                }
            }
        }
        return null;
    }

    /* TODO: fill in the rest of this class. */
    //Saves the commit object.
    public void saveCommit() {
        String folder = SerializeUtils.getDirFromID(this.id);
        File dir = Utils.join(OBJECTS_DIR, folder);
        dir.mkdir();
        String fileName = SerializeUtils.getFileNameFromID(this.id);
        File outFile = Utils.join(dir, fileName);
        try {
            if (!outFile.createNewFile()) {
                LOGGER.log(Level.WARNING,
                        "File already exists or could not be created: {0}", outFile.getAbsolutePath());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create the file: " + outFile.getAbsolutePath(), e);
            return;  // Return early if the file couldn't be created
        }
        Utils.writeObject(outFile, this);
        // Updates the Commits file.
        String commits = Utils.readContentsAsString(COMMITS);
        commits = commits + this.id;
        Utils.writeContents(COMMITS, commits);
        // Update pointer in branch.
        File head = Utils.join(BRANCHES_DIR, Repository.getCurrentBranch());
        Utils.writeContents(head, this.id);
    }

    // Returns if this commit contains the file by the name fileName.
    public boolean containsFile(String fileName) {
        return this.blobs.containsKey(fileName);
    }

    // Add a file to this commit.
    public void stageFile(String file, String sha1) {
        this.blobs.put(file, sha1);
    }

    // Removes a file from current commit.
    public void removeFile(String file) {
        this.blobs.remove(file);
    }

    // Returns if this commit contains no mapping to files.
    public boolean containsNothing() {
        return this.blobs == null;
    }

    // Returns the SHA1 hash of a file in this commit.
    public String getSha1(String fileName) {
        return this.blobs.get(fileName);
    }

    // Write files in this commit into CWD, overwriting the files if they already exit.
    public void writeFiles() {
        for (Map.Entry<String, String> entry : this.blobs.entrySet()) {
            String fileName = entry.getKey();
            String sha1 = entry.getValue();
            File currentFile = Utils.join(CWD, fileName);
            File newFile = Utils.join(SerializeUtils.getDirFromID(sha1), SerializeUtils.getFileNameFromID(sha1));
            Utils.writeContents(currentFile, Utils.readContents(newFile));
        }

    }

    // Returns a File object with the name fileName.
    public File getFileByName(String fileName) {
        String sha1 = this.getSha1(fileName);
        return Utils.join(OBJECTS_DIR, SerializeUtils.getDirFromID(sha1), SerializeUtils.getFileNameFromID(sha1));
    }

    // Returns names of all files saved in this commit as array.
    public String[] getFileNames() {
        return this.blobs.keySet().toArray(new String[0]);
    }
}
