package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Stream;
import java.util.logging.Level;
import java.util.logging.Logger;

import static gitlet.Repository.*;
import static gitlet.Utils.join;


public class Stage implements Serializable {
    // Maps file names to their SHA1 hash.
    private HashMap<String, String> toAdd;
    private HashSet<String> toRemove;
    private static final Logger LOGGER = Logger.getLogger(Stage.class.getName());

    public Stage() {
        toAdd = new HashMap<>();
        toRemove = new HashSet<>();
    }

    private void addFile(String fileName) {
        File FileToAdd = Utils.join(CWD, fileName);
        String sha1ToAdd = GitletUtils.generateSHA1FromFile(FileToAdd);
        /** If the current working version of the file is identical to the version in the current commit,
         * do not stage it to be added, and remove it from the staging area if it is already there.
         * The file will no longer be staged for removal, if it was at the time of the command.
         */
         if (Repository.getCurrentCommit().getSha1(fileName) != null &&
                Repository.getCurrentCommit().getSha1(fileName).equals(sha1ToAdd)) {
            // if staged for adding, remove it
            this.toAdd.remove(fileName);
            return;
        }
        // remove from files for removal if necessary.
        if (this.toRemove.contains(fileName)) {
            this.toRemove.remove(fileName);
        }
        /** Write content of the file to be added to the staging area. */
        File Folder = Utils.join(STAGE_DIR, GitletUtils.getDirFromID(sha1ToAdd));
        Folder.mkdir();
        File AddFile = Utils.join(Folder, GitletUtils.getFileNameFromID(sha1ToAdd));
        try {
            if (!AddFile.createNewFile()) {
                LOGGER.log(Level.WARNING,
                        "File already exists or could not be created: {0}", AddFile.getAbsolutePath());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create the file: " + AddFile.getAbsolutePath(), e);
            return;  // Return early if the file couldn't be created
        }
        byte[] ContentToAdd= Utils.readContents(FileToAdd);
        Utils.writeContents(AddFile, ContentToAdd);
        /** Add mapping to the stage. */
        this.toAdd.put(fileName, sha1ToAdd);
        /** Update index. */
        Utils.writeObject(INDEX, this);
    }

    /** Public interface for adding file*/
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

    // Updates the blobs mapping in the commit which is passed in.
    public void updateCommit(Commit commit) {
        for (Map.Entry<String, String> entry : this.toAdd.entrySet()) {
            String file = entry.getKey();
            String sha1 = entry.getValue();
            commit.stageFile(file, sha1);
        }
        // Todo: files tracked in the current commit may be untracked in the new commit
        //  as a result being staged for removal by the rm command
        for (String fileName : this.toRemove) {
            commit.removeFile(fileName);
        }
    }


    public void clear() {
        this.toAdd.clear();
        this.toRemove.clear();
        /** Update index. */
        Utils.writeObject(INDEX, this);
    }

    public void saveFiles() {
        for (Map.Entry<String, String> entry : this.toAdd.entrySet()) {
            String file = entry.getKey();
            String sha1 = entry.getValue();
            File fileSrc = Utils.join(CWD, file);
            File dirDst = Utils.join(OBJECTS_DIR, GitletUtils.getDirFromID(sha1));
            dirDst.mkdir();
            File fileDst = Utils.join(dirDst, GitletUtils.getFileNameFromID(sha1));
            try {
                if (!fileDst.createNewFile()) {
                    LOGGER.log(Level.WARNING, "File already exists or could not be created: {0}", fileDst.getAbsolutePath());
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to create the file: " + fileDst.getAbsolutePath(), e);
                return;  // Return early if the file couldn't be created
            }

            Utils.writeContents(fileDst, Utils.readContents(fileSrc));
        }
    }

    // Returns true if the file fileName is staged for addition.
    public boolean fileIsToAdd(String fileName) {
        return this.toAdd.containsKey(fileName);
    }

    public void removeFileToAdd(String fileName) {
        this.toAdd.remove(fileName);
    }

    public void addFileToRemove(String fileName) {
        this.toRemove.add(fileName);
    }

    public void deleteFileFromStage(String fileName) {
        /** Write content of the file to be added to the staging area. */
        String sha1ToDel = this.toAdd.get(fileName);
        File Folder = Utils.join(STAGE_DIR, GitletUtils.getDirFromID(sha1ToDel));
        File FileToDelete = Utils.join(Folder, GitletUtils.getFileNameFromID(sha1ToDel));
        Utils.restrictedDelete(FileToDelete);
    }

    // TODO: consider factor out an util function
    public String getStagedFiles() {
        String[] files = toAdd.keySet().toArray(new String[toAdd.size()]);
        files = Stream.of(files)
                .sorted()
                .toArray(String[]::new);
        return String.join("/n", files);
    }

    public String getRemovedFiles() {
        String[] files = toRemove.toArray(new String[toRemove.size()]);
        files = Stream.of(files)
                .sorted()
                .toArray(String[]::new);
        return String.join("/n", files);
    }
}
