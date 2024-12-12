package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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

    /** Adds a copy of the file as it currently exists to the staging area.
     * If the current working version of the file is identical to the version in the current commit,
     * do not stage it to be added, and remove it from the staging area if it is already there.
     * The file will no longer be staged for removal, if it was at the time of the command.*/
    public static void add(String fileName) {
        // Read INDEX file.
        Stage stage = Utils.readObject(INDEX, Stage.class);
        stage.addFile(fileName);
    }

    public boolean isToAddEmpty() {
        return this.toAdd.isEmpty();
    }

    public boolean isToRemoveEmpty() {
        return this.toRemove.isEmpty();
    }



    public void updateCommit(Commit commit) {
        for (Map.Entry<String, String> entry : this.toAdd.entrySet()) {
            String file = entry.getKey();
            String sha1 = entry.getValue();
            commit.blobs.put(file, sha1);
        }
        // Todo: files tracked in the current commit may be untracked in the new commit
        //  as a result being staged for removal by the rm command
        for (String fileName : this.toRemove) {
            commit.blobs.remove(fileName);
        }
    }


    public void clear() {
        // Read INDEX file.
        Stage stage = Utils.readObject(INDEX, Stage.class);
        this.toAdd.clear();
        this.toRemove.clear();
        /** Update index. */
        Utils.writeObject(INDEX, this);
    }
}
