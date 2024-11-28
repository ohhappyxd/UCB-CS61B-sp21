package gitlet;

import java.io.File;
import java.util.HashMap;

import static gitlet.Utils.join;

public class Stage {
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The Staging area */
    public static final File STAGE = Utils.join(GITLET_DIR, "stage");
    /** The blobs directory. */
    public static final File BLOBS = Utils.join(GITLET_DIR, "blobs");

    private static HashMap<String, String> toAdd;
    private static HashMap<String, String> toRemove;

    /** Adds a copy of the file as it currently exists to the staging area.
     * If the current working version of the file is identical to the version in the current commit,
     * do not stage it to be added, and remove it from the staging area if it is already there.
     * The file will no longer be staged for removal, if it was at the time of the command.*/
    public static void add(String fileName) {
        File FileToAdd = Utils.join(CWD, fileName);
        byte[] ContentToAdd = Utils.readContents(FileToAdd);
        String sha1ToAdd = Utils.sha1(ContentToAdd);

        if (Repository.getCurrentCommit().blobs != null &&
                Repository.getCurrentCommit().blobs.get(fileName).equals(sha1ToAdd)) {
            // if staged for adding, remove it
            Stage.toAdd.remove(fileName);
            // remove from files for removal if necessary.
            Stage.toRemove.remove(fileName);
            return;
        }

        /** Write content to blols. */
        File AddFile = Utils.join(BLOBS, sha1ToAdd);
        Utils.writeContents(AddFile, ContentToAdd);
        /** Add mapping to stage. */
        Stage.toAdd.put(fileName, sha1ToAdd);


    }
}
