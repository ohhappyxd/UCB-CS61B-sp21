package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Formatter;
import java.util.Map;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  .gitlet
 *     ├── refs/
 *     │ ├── commits
 *     │ └── branches/
 *     ├── objects/
 *     ├── HEAD
 *     └── stage
 *
 *  @author Xinxin
 */
public class Repository{
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** Directories and files for persistence. */
    // The current working directory.
    public static final File CWD = new File(System.getProperty("user.dir"));
    // The .gitlet directory.
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    // The Staging area, contains blobs to be staged, status tracked by the index file.
    public static final File STAGE_DIR = Utils.join(GITLET_DIR, "stage");
    // The index file tracking the staging area.
    public static final File INDEX = Utils.join(GITLET_DIR, "index");
    //The Reference directory, contains branches directory and commits file.
    public static final File REFS_DIR = Utils.join(GITLET_DIR, "refs");
    // The commits file. Contains all commit IDs.
    public static final File COMMITS = Utils.join(REFS_DIR, "commits");
    // The branches directory. Contains one file for each branch. Current head of branch documented inside file.
    public static final File BRANCHES_DIR = Utils.join(REFS_DIR, "branches");
    // The objects directory. Contains blobs of files and commits.
    public static final File OBJECTS_DIR = Utils.join(GITLET_DIR, "objects");
    // The HEAD pointer. Points to current branch (in a detached HEAD state to a commit).
    public static final File HEAD = Utils.join(GITLET_DIR, "HEAD");

    public static void setupPersistence() throws IOException {
        /** Set up persistence. */
        GITLET_DIR.mkdir();
        REFS_DIR.mkdir();
        BRANCHES_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        STAGE_DIR.mkdir();
        INDEX.createNewFile();
        COMMITS.createNewFile();
        HEAD.createNewFile();
        Utils.writeObject(INDEX, new Stage());
    }

    /**
     * Initial commit. Creates a new Gitlet version-control system in the current directory. The system
     * automatically start with one commit: a commit that contains no files and has the commit message
     * "initial commit". It will have a single branch: master, which initially points to this initial commit,
     * and master will be the current branch.
     */
    public static void init() throws IOException {
        /** If there is already a Gitlet version-control system in the current directory, abort */
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        Repository.setupPersistence();

        Commit initialCommit = new Commit("initial commit", new Date(0));
        initialCommit.saveCommit();

        /* Set up master branch. */
        File master = Utils.join(BRANCHES_DIR, "master");
        Utils.writeContents(master, initialCommit.id);
        /** Set current branch to master. */
        Utils.writeContents(HEAD, "master");
    }

    /**
     * Adds a copy of the file as it currently exists to the staging area.
     */
    public static void add(String fileName) throws IOException {
        File FileToAdd = Utils.join(CWD, fileName);
        if (!FileToAdd.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Stage.add(fileName);
    }

    public static Commit getCurrentCommit() {
        String head = Utils.readContentsAsString(HEAD);
        File branch = Utils.join(BRANCHES_DIR, head);
        String commitID = Utils.readContentsAsString(branch);
        return Commit.getCommitByID(commitID);
    }

    public static void commit(String message) throws IOException {
        // Read INDEX file.
        Stage stage = Utils.readObject(INDEX, Stage.class);
        if (stage.isToAddEmpty() && stage.isToRemoveEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            return;
        }
        Commit newCommit = new Commit(message);
        stage.updateCommit(newCommit);
        stage.saveFiles();
        newCommit.saveCommit();
        stage.clear();
    }

    /**
     * Unstage the file if it is currently staged for addition. If the file is
     * tracked in the current commit, stage it for removal and remove the file from
     * the working directory if the user has not already done so (do not remove
     * it unless it is tracked in the current commit).
     */
    public static void rm(String fileName) {
        // Read INDEX file.
        Stage stage = Utils.readObject(INDEX, Stage.class);
        if (stage.fileIsToAdd(fileName)) {
            stage.removeFileToAdd(fileName);
            //Remove file also from staging area
            stage.deleteFileFromStage(fileName);

        }
        // If the file is neither staged nor tracked by the head commit, print the error message
        if (!stage.fileIsToAdd(fileName) && !Repository.getCurrentCommit().containsFile(fileName)) {
            System.out.println("No reason to remove the file.");
            return;
        }
        if (Repository.getCurrentCommit().containsFile(fileName)) {
            stage.addFileToRemove(fileName);
            Utils.restrictedDelete(Utils.join(CWD, fileName));
        }

    }

    /**
     * Starting at the current head commit, display information about each commit backwards
     * along the commit tree until the initial commit, following the first parent commit links,
     * ignoring any second parents found in merge commits. For every node in this history,
     * the information it should display is the commit id, the time the commit was made,
     * and the commit message. Here is an example of the exact format it should follow:
     * ===
     * commit a0da1ea5a15ab613bf9961fd86f010cf74c7ee48
     * Date: Thu Nov 9 20:00:05 2017 -0800
     * A commit message.
     */
    public static void log() {
        String head = Utils.readContentsAsString(HEAD);
        File branch = new File(BRANCHES_DIR, head);
        String currentCommitId = Utils.readContentsAsString(branch);
        Commit currentCommit = Utils.readObject(Utils.join(COMMITS,
                SerializeUtils.getDirFromID(currentCommitId),
                SerializeUtils.getFileNameFromID(currentCommitId)), Commit.class);
        String log = "";

        while (currentCommitId != null) {
            Formatter formatter = new Formatter();
            String formattedDate = formatter.format("%ta %1$tb %1$td %1$tT %1$tY %1$tz", currentCommit.timestamp)
                    .toString();
            log = "===" + "\n" + "commit " + currentCommitId + "\n";
            // For merge commits (those that have two parent commits), add a line just below the first, as in
            // Merge: 4975af1 2c1ead1
            if (currentCommit.parent2 != null) {
                log = log + "Merge: " + currentCommit.parent1.substring(0, 6) + currentCommit.parent2.substring(0, 6);
            }
            log = log + formattedDate + "\n" + currentCommit.message + "\n";
            currentCommitId = currentCommit.parent1;
            currentCommit = Utils.readObject(Utils.join(COMMITS, SerializeUtils.getDirFromID(currentCommitId),
                    SerializeUtils.getFileNameFromID(currentCommitId)), Commit.class);
        }
        System.out.println(log);
    }

    public static void globalLog() {
        if (Utils.plainFilenamesIn(COMMITS) != null) {
            String log = "";
            for (String commitId : Utils.plainFilenamesIn(COMMITS)) {
                Commit commit = Utils.readObject(Utils.join(COMMITS, commitId), Commit.class);
                Formatter formatter = new Formatter();
                String formattedDate = formatter.format("%ta %1$tb %1$td %1$tT %1$tY %1$tz", commit.timestamp)
                        .toString();
                log = "===" + "\n" + "commit " + commitId + "\n";
                // For merge commits (those that have two parent commits), add a line just below the first, as in
                // Merge: 4975af1 2c1ead1
                if (commit.parent2 != null) {
                    log = log + "Merge: " + commit.parent1.substring(0, 6) + commit.parent2.substring(0, 6);
                }
                log = log + formattedDate + "\n" + commit.message + "\n";
            }
            System.out.println(log);
        }

    }

    public static void find(String message) {
        String result = "";
        if (Utils.plainFilenamesIn(COMMITS) != null) {
            for (String commitId : Utils.plainFilenamesIn(COMMITS)) {
                Commit commit = Utils.readObject(Utils.join(COMMITS, commitId), Commit.class);
                if (commit.message.equals(message)) {
                    result = result + commit.id + "\n";
                }
            }
        }
        if (result.isEmpty()) {
            System.out.println("Found no commit with that message.");
        }
        System.out.println(result);
    }

    public static void status() {
    }

    public static String getCurrentBranch() {
        return Utils.readContentsAsString(HEAD);
    }
/**
    public static void checkout(String[] args) {
        if (args.length == 1) {
            checkoutBranch();
        } else if (args.length == 2 && args[1] == "--") {
            checkoutFile();
        } else if (args.length == 3 && args[2 == "--"]) {
            checkoutFileInCommit();
        } else {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    } */
}