package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import static gitlet.Utils.join;
import static gitlet.Repository.CWD;
import static gitlet.Repository.STAGE_DIR;
import static gitlet.Repository.INDEX;

public class Stage implements Serializable {
    // Maps file names to their SHA1 hash.
    private HashMap<String, String> toAdd;
    private HashSet<String> toRemove;

    public Stage() {
        toAdd = new HashMap<>();
        toRemove = new HashSet<>();
    }

    /** Adds a copy of the file as it currently exists to the staging area.
     * If the current working version of the file is identical to the version in the current commit,
     * do not stage it to be added, and remove it from the staging area if it is already there.
     * The file will no longer be staged for removal, if it was at the time of the command.*/
    public static void add(String fileName) {
        // Read INDEX file.
        Stage stage = Utils.readObject(INDEX, Stage.class);
        stage.addFile(fileName);
    }

    private void addFile(String fileName) {
        File FileToAdd = Utils.join(CWD, fileName);
        String sha1ToAdd = SerializeUtils.generateSHA1FromFile(FileToAdd);

        if (Repository.getCurrentCommit().blobs != null &&
                Repository.getCurrentCommit().blobs.get(fileName).equals(sha1ToAdd)) {
            // if staged for adding, remove it
            this.toAdd.remove(fileName);
            return;
        }
        // remove from files for removal if necessary.
        if (this.toRemove.contains(fileName)) {
            this.toRemove.remove(fileName);
        }

        /** Write content of the file to be added to the staging area. */
        File AddFile = Utils.join(STAGE_DIR, sha1ToAdd);
        byte[] ContentToAdd= Utils.readContents(FileToAdd);
        Utils.writeContents(AddFile, ContentToAdd);
        /** Add mapping to the stage. */
        this.toAdd.put(fileName, sha1ToAdd);
        /** Update index. */
        Utils.writeObject(INDEX, this);
    }

    /**
    public static void clear() {
        toAdd.clear();
        toRemove.clear();
    }*/
}
