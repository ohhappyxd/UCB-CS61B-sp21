package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static gitlet.Repository.*;
import static gitlet.Utils.join;

public class Stage implements Serializable {
    // Maps file names to their SHA1 hash.
    private HashMap<String, String> toAdd;
    private HashSet<String> toRemove;

    public Stage() {
        toAdd = new HashMap<>();
        toRemove = new HashSet<>();
    }

    private void addFile(String fileName) throws IOException {
        File FileToAdd = Utils.join(CWD, fileName);
        String sha1ToAdd = SerializeUtils.generateSHA1FromFile(FileToAdd);
        /** If the current working version of the file is identical to the version in the current commit,
         * do not stage it to be added, and remove it from the staging area if it is already there.
         * The file will no longer be staged for removal, if it was at the time of the command.
         */
         if (!Repository.getCurrentCommit().containsNothing() &&
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
        File Folder = Utils.join(STAGE_DIR, SerializeUtils.getDirFromID(sha1ToAdd));
        Folder.mkdir();
        File AddFile = Utils.join(Folder,SerializeUtils.getFileNameFromID(sha1ToAdd));
        AddFile.createNewFile();
        byte[] ContentToAdd= Utils.readContents(FileToAdd);
        Utils.writeContents(AddFile, ContentToAdd);
        /** Add mapping to the stage. */
        this.toAdd.put(fileName, sha1ToAdd);
        /** Update index. */
        Utils.writeObject(INDEX, this);
    }

    /** Public interface for adding file*/
    public static void add(String fileName) throws IOException {
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
        // Read INDEX file.
        Stage stage = Utils.readObject(INDEX, Stage.class);
        this.toAdd.clear();
        this.toRemove.clear();
        /** Update index. */
        Utils.writeObject(INDEX, this);
    }

    public void saveFiles() throws IOException {
        for (Map.Entry<String, String> entry : this.toAdd.entrySet()) {
            String file = entry.getKey();
            String sha1 = entry.getValue();
            String folder = SerializeUtils.getDirFromID(sha1);
            String fileName = SerializeUtils.getFileNameFromID(sha1);
            Files.move(Utils.join(STAGE_DIR, folder, fileName).toPath(),
                    Utils.join(OBJECTS_DIR, folder, fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

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
        File Folder = Utils.join(STAGE_DIR, SerializeUtils.getDirFromID(sha1ToDel));
        File FileToDelete = Utils.join(Folder,SerializeUtils.getFileNameFromID(sha1ToDel));
        Utils.restrictedDelete(FileToDelete);
    }

    public String getStagedFiles() {
    }

    public String getRemovedFiles() {
    }
}
