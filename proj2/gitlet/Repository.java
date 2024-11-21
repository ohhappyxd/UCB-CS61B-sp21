package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File STAGE_ADD_FOLDER = Utils.join(GITLET_DIR, "add");
    public Commit currentCommit;

    /* TODO: fill in the rest of this class. */
    public static void setupPersistence() {
        GITLET_DIR.mkdir();
        /** Create folder for commits. */
        File COMMITS_FOLDER = Utils.join(GITLET_DIR, "commits");
        COMMITS_FOLDER.mkdir();
        /** Create folders for staging area. */
        STAGE_ADD_FOLDER.mkdir();
        /** Create folder for blobs. */
        File BLOBS_FOLDER = Utils.join(GITLET_DIR, "blobs");
        BLOBS_FOLDER.mkdir();
    }

    /** Initial commit. Creates a new Gitlet version-control system in the current directory. The system
     * automatically start with one commit: a commit that contains no files and has the commit message
     * "initial commit". It will have a single branch: master, which initially points to this initial commit,
     * and master will be the current branch.*/
    public static void init() throws IOException {
        /** If there is already a Gitlet version-control system in the current directory, abort */
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        setupPersistence();
        /* TODO: Something to represent master branch */

        Commit initialCommit = new Commit("initial commit", new Date(0));
        initialCommit.saveCommit();
    }

    /** Adds a copy of the file as it currently exists to the staging area.
     * If the current working version of the file is identical to the version in the current commit,
     * do not stage it to be added, and remove it from the staging area if it is already there.
     * The file will no longer be staged for removal, if it was at the time of the command.*/
    public static void add(String fileName) {
        File FileToAdd = Utils.join(CWD, fileName);
        if (! FileToAdd.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        String ContentToAdd = Utils.readContentsAsString(FileToAdd);
        String sha1ToAdd = Utils.sha1(ContentToAdd);
        File AddFile = Utils.join(STAGE_ADD_FOLDER, sha1ToAdd);
        Utils.writeContents(AddFile, ContentToAdd);
        HashMap<String, String> map = new HashMap<>();
        map.put(fileName, sha1ToAdd);

        Commit currentCommit;
        if (currentCommit.mappingToBlobs.containsKey(fileName)) {
            // TODO: if staged for removal, remove from removal
            return;
        }

        // TODO: remove from stage for removal if necessary.

    }

}
