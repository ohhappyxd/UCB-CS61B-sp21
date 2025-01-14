package gitlet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final Logger LOGGER = Logger.getLogger(Repository.class.getName());

    public static void setupPersistence() {
        /** Set up persistence. */
        GITLET_DIR.mkdir();
        REFS_DIR.mkdir();
        BRANCHES_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        STAGE_DIR.mkdir();
        try {
            if (!INDEX.createNewFile()) {
                LOGGER.log(Level.WARNING,
                        "File already exists or could not be created: {0}", INDEX.getAbsolutePath());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create the file: " + INDEX.getAbsolutePath(), e);
            return;
        }
        try {
            if (!COMMITS.createNewFile()) {
                LOGGER.log(Level.WARNING,
                        "File already exists or could not be created: {0}", COMMITS.getAbsolutePath());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create the file: " + COMMITS.getAbsolutePath(), e);
            return;
        }
        try {
            if (!HEAD.createNewFile()) {
                LOGGER.log(Level.WARNING,
                        "File already exists or could not be created: {0}", HEAD.getAbsolutePath());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create the file: " + HEAD.getAbsolutePath(), e);
            return;
        }
        Utils.writeObject(INDEX, new Stage());
    }

    /**
     * Initial commit. Creates a new Gitlet version-control system in the current directory. The system
     * automatically start with one commit: a commit that contains no files and has the commit message
     * "initial commit". It will have a single branch: master, which initially points to this initial commit,
     * and master will be the current branch.
     */
    public static void init() {
        /** If there is already a Gitlet version-control system in the current directory, abort */
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        Repository.setupPersistence();
        Commit initialCommit = new Commit("initial commit", new Date(0));
        /* Set up master branch. */
        File master = Utils.join(BRANCHES_DIR, "master");
        Utils.writeContents(master, initialCommit.id);
        /** Set current branch to master. */
        Utils.writeContents(HEAD, "master");
        initialCommit.saveCommit();
    }

    /**
     * Adds a copy of the file as it currently exists to the staging area.
     */
    public static void add(String fileName) {
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

    public static void commit(String message) {
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
        String log = "";

        while (currentCommitId != null) {
            Commit currentCommit = Utils.readObject(Utils.join(OBJECTS_DIR, GitletUtils.getDirFromID(currentCommitId),
                    GitletUtils.getFileNameFromID(currentCommitId)), Commit.class);
            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
            String formattedDate = formatter.format(currentCommit.timestamp);
            log = log + "===" + "\n" + "commit " + currentCommitId + "\n";
            // For merge commits (those that have two parent commits), add a line just below the first, as in
            // Merge: 4975af1 2c1ead1
            if (currentCommit.parent2 != null) {
                log = log + "Merge: " + currentCommit.parent1.substring(0, 7) + currentCommit.parent2.substring(0, 7);
            }
            log = log + "Date: " + formattedDate + "\n" + currentCommit.message + "\n";
            currentCommitId = currentCommit.parent1;
            if (currentCommitId != null) {
                log = log + "\n";
            }
        }
        System.out.println(log);
    }

    public static void globalLog() {
        String commitIds = Utils.readContentsAsString(COMMITS);
        String log = "";
        while (!commitIds.isEmpty()) {
            String nextCommit = commitIds.substring(commitIds.length()-40);
            Commit commit = Utils.readObject(Utils.join(OBJECTS_DIR,
                    GitletUtils.getDirFromID(nextCommit),
                    GitletUtils.getFileNameFromID(nextCommit)), Commit.class);
            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
            String formattedDate = formatter.format(commit.timestamp);
            log = log + "===" + "\n" + "commit " + nextCommit + "\n";
            // For merge commits (those that have two parent commits), add a line just below the first, as in
            // Merge: 4975af1 2c1ead1
            if (commit.parent2 != null) {
                log = log + "Merge: " + commit.parent1.substring(0, 7) + commit.parent2.substring(0, 7);
            }
            log = log + "Date: " + formattedDate + "\n" + commit.message + "\n";
            commitIds = commitIds.substring(0, commitIds.length()-40);
            if (!commitIds.isEmpty()) {
                log = log + "\n";
            }
        }
        System.out.println(log);
    }

    public static void find(String message) {
        String result = "";
        String commitIds = Utils.readContentsAsString(COMMITS);
        Set<String> Ids = new HashSet<>();
        while (!commitIds.isEmpty()) {
            Ids.add(commitIds.substring(0, 40));
            commitIds = commitIds.substring(40);
        }
        for (String id : Ids) {
            Commit commit = Commit.getCommitByID(id);
            if (commit.message.equals(message)) {
                result = result + commit.id;
            }
        }
        if (result.isEmpty()) {
            System.out.println("Found no commit with that message.");
        }
        System.out.println(result);
    }

    /** Status Format:
     === Branches ===
     *master
     other-branch

     === Staged Files ===
     wug.txt
     wug2.txt

     === Removed Files ===
     goodbye.txt

     === Modifications Not Staged For Commit ===
     junk.txt (deleted)
     wug3.txt (modified)

     === Untracked Files ===
     random.stuff*/
    // TODO: Check necessary changes with branching
    public static void status() {
        // Thoughts: implement multiple functions in Stage and Repo to produce sections of status
        String allBranches = Repository.getAllBranch();
        String markedBranches = allBranches.replace(Repository.getCurrentBranch(),
                "*" + Repository.getCurrentBranch());
        String branches = "/n" + "=== Branches ===" + "/n" + markedBranches + "/n";
        Stage stage = Utils.readObject(INDEX, Stage.class);
        String stagedFiles = "/n" + "=== Staged Files ===" + "/n" + stage.getStagedFiles() + "/n";
        String removedFiles = "/n" + "=== Removed Files ===" + "/n" + stage.getRemovedFiles() + "/n";
        String mod = "/n" + "=== Modifications Not Staged For Commit ===" + "/n";
        String untracked = "/n" + "=== Untracked Files ===" + "/n" + "/n";
        String output = branches + stagedFiles + removedFiles + mod + untracked;
        System.out.println(output);
    }

    private static String getAllBranch() {
        List<String> branchNames = Utils.plainFilenamesIn(BRANCHES_DIR);
        if (branchNames != null) {
            List<String> sortedBranches = branchNames.stream()
                    .sorted()
                    .collect(Collectors.toList());
            return String.join("/n", sortedBranches);
        }
        return null;
    }

    public static String getCurrentBranch() {
        return Utils.readContentsAsString(HEAD);
    }

    public static void checkout(String[] args) {
        if (args.length == 1) {
            String branch = args[0];
            if (Repository.untrackedFileExist()) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
            if (branch.equals(Utils.readContentsAsString(HEAD))) {
                System.out.println("No need to checkout the current branch.");
                return;
            }
            if (Utils.plainFilenamesIn(BRANCHES_DIR).contains(branch)) {
                checkoutBranch(branch);
            } else {
                System.out.println("No such branch exists.");
            }
        } else if (args.length == 2 && Objects.equals(args[0], "--")) {
            checkoutFile(args[1]);
        } else if (args.length == 3 && Objects.equals(args[1], "--")) {
            checkoutFileInCommit(args[0], args[2]);
        } else {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    private static boolean untrackedFileExist() {
        for (String fileName : Utils.plainFilenamesIn(CWD)) {
            if (!Repository.getCurrentCommit().containsFile(fileName)) {
                Stage stage = Utils.readObject(INDEX, Stage.class);
                if (!stage.fileIsToAdd(fileName)) {
                    return true;
                }
            }
        }
        return false;
    }


    /** Takes the version of the file as it exists in the commit with the given id,
     * and puts it in the working directory, overwriting the version of the file
     * that’s already there if there is one. The new version of the file is not staged. */
    private static void checkoutFileInCommit(String commitId, String fileName) {
        Commit commit = Commit.getCommitByID(commitId);
        if (commit == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        if (!commit.containsFile(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        File newFile = commit.getFileByName(fileName);
        File currentFile = Utils.join(CWD, fileName);
        Utils.writeContents(currentFile, Utils.readContents(newFile));
    }

    /** Takes the version of the file as it exists in the head commit and puts it
     * in the working directory, overwriting the version of the file that’s already there
     * if there is one. The new version of the file is not staged. */
    private static void checkoutFile(String fileName) {
        File currentFile = Utils.join(CWD, fileName);
        Commit currentCommit = Repository.getCurrentCommit();
        if (!currentCommit.containsFile(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        File newFile = currentCommit.getFileByName(fileName);
        Utils.writeContents(currentFile, Utils.readContents(newFile));
    }

    /** checkout branch. Takes all files in the commit at the head of the given branch,
     * and puts them in the working directory, overwriting the versions of the files that are
     * already there if they exist. Also, at the end of this command, the given branch will
     * now be considered the current branch (HEAD). Any files that are tracked in the
     * current branch but are not present in the checked-out branch are deleted.
     * The staging area is cleared, unless the checked-out branch is the current branch. */
    private static void checkoutBranch(String branch) {
        File branchFile = Utils.join(BRANCHES_DIR, branch);
        String commitId = Utils.readContentsAsString(branchFile);
        Commit commit = Commit.getCommitByID(commitId);
        Repository.removeUntrackedFiles();
        commit.writeFiles();
        Stage stage = Utils.readObject(INDEX, Stage.class);
        stage.clear();
        Utils.writeContents(HEAD, branch);
    }

    private static void removeUntrackedFiles() {
        for (String fileName : Utils.plainFilenamesIn(CWD)) {
            if (!Repository.getCurrentCommit().containsFile(fileName)) {
                Utils.restrictedDelete(Utils.join(CWD, fileName));
            }
        }
    }

    public static void branch(String branchName) {
        if (Repository.branchExists(branchName)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        File branch = Utils.join(BRANCHES_DIR, branchName);
        try {
            if (!branch.createNewFile()) {
                LOGGER.log(Level.WARNING,
                        "File already exists or could not be created: {0}", branch.getAbsolutePath());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create the file: " + branch.getAbsolutePath(), e);
            return;
        }
        Utils.writeContents(branch, Repository.getCurrentCommit());
    }

    private static boolean branchExists(String branchName) {
        for (String branch : Utils.plainFilenamesIn(BRANCHES_DIR)) {
            if (branch.equals(branchName)) {
                return true;
            }
        }
        return false;
    }

    public static void rmBranch(String branchName) {
        if (!branchExists(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (branchName.equals(Repository.getCurrentBranch())) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        File branch = Utils.join(BRANCHES_DIR, branchName);
        Utils.restrictedDelete(branch);
    }

    public static void reset(String commitId) {
        Commit commit = Commit.getCommitByID(commitId);
        if (commit == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        for (String fileName : commit.getFileNames()) {
            String[] args = new String[]{commitId, "--", fileName};
            Repository.checkout(args);
        }
        String branch = Repository.getCurrentBranch();
        Utils.writeContents(Utils.join(BRANCHES_DIR, branch), commit.id);
        Stage stage = Utils.readObject(INDEX, Stage.class);
        stage.clear();

    }

    public static void merge(String branchName) {
        Stage stage = Utils.readObject(INDEX, Stage.class);
        if (!stage.isToAddEmpty() || !stage.isToRemoveEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        Boolean conflict = false;
        String currentBranch = Utils.readContentsAsString(HEAD);
        String givenBranch = Utils.readContentsAsString(Utils.join(BRANCHES_DIR, branchName));
        String commonAncestor = findCmnAncestor(givenBranch, currentBranch);
        if (commonAncestor != null && commonAncestor.equals(givenBranch)) {
            System.out.println("Given branch is an ancestor of the current branch.");
        } else if (commonAncestor != null && commonAncestor.equals(currentBranch)) {
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
        } else {
            Commit givenCommit = Commit.getCommitByID(givenBranch);
            Commit currentCommit = Commit.getCommitByID(currentBranch);
            Commit ancestorCommit = Commit.getCommitByID(commonAncestor);
            if (givenCommit == null || currentCommit == null || ancestorCommit == null) {
                System.out.println("Failed to get commit file");
            } else {
                for (String fileName : givenCommit.getFileNames()) {
                    String givenSha1 = givenCommit.getSha1(fileName);
                    String ancestorSha1 = ancestorCommit.getSha1(fileName);
                    String currentSha1 = currentCommit.getSha1(fileName);
                    // Any files that have been modified in the given branch since the split point,
                    // but not modified in the current branch since the split point should be changed to
                    // their versions in the given branch (checked out from the commit at the front of the given branch).
                    // These files should then all be automatically staged.
                    if (!givenSha1.equals(ancestorSha1) && currentSha1.equals(ancestorSha1)) {
                        checkoutFileInCommit(givenBranch, fileName);
                        Stage.add(fileName);
                    }
                    // Any files that were not present at the split point and are present
                    // only in the given branch should be checked out and staged.
                    if (ancestorSha1.isEmpty() && currentSha1.isEmpty()) {
                        checkoutFileInCommit(givenBranch, fileName);
                        Stage.add(fileName);
                    }
                    // Any files modified in different ways in the current and given branches are in conflict.
                    // “Modified in different ways” can mean that the contents of both are changed and
                    // different from other, or the contents of one are changed and the other file is deleted,
                    // or the file was absent at the split point and has different contents in the given
                    // and current branches. In this case, replace the contents of the conflicted file with
                    if (!currentSha1.equals(givenSha1)) {
                        String currContent = Utils.readContentsAsString(Utils.join(OBJECTS_DIR,
                                GitletUtils.getDirFromID(currentSha1), GitletUtils.getFileNameFromID(currentSha1)));
                        String gvnContent = Utils.readContentsAsString(Utils.join(OBJECTS_DIR,
                                GitletUtils.getDirFromID(givenSha1), GitletUtils.getFileNameFromID(givenSha1)));
                        Utils.writeContents(Utils.join(CWD, fileName), "<<<<<<< HEAD", "/n", currContent,
                                "=======", gvnContent, ">>>>>>>");
                        conflict = true;
                        Stage.add(fileName);
                    }
                    Repository.commit("Merged " + givenBranch + "into" + currentBranch + ".");
                    if (conflict) {
                        System.out.println("Encountered a merge conflict.");
                    }
                }
                for (String fileName : currentCommit.getFileNames()) {
                    String givenSha1 = givenCommit.getSha1(fileName);
                    String ancestorSha1 = ancestorCommit.getSha1(fileName);
                    String currentSha1 = currentCommit.getSha1(fileName);
                    // Any files present at the split point, unmodified in the current branch,
                    // and absent in the given branch should be removed (and untracked).
                    if (!ancestorSha1.isEmpty() && ancestorSha1.equals(currentSha1) && givenSha1.isEmpty()) {
                        Repository.rm(fileName);

                    }
                }
            }

        }

    }

    public static String findCmnAncestor(String gvnBranch, String currBranch) {
        ArrayList<String> historyGB = new ArrayList<>();
        ArrayList<String> historyCB = new ArrayList<>();
        Commit gvnCommit = Commit.getCommitByID(gvnBranch);
        Commit currCommit = Commit.getCommitByID(currBranch);
        while (gvnCommit != null) {
            historyGB.add(gvnCommit.parent1);
            gvnCommit = Commit.getCommitByID(gvnCommit.parent1);
        }
        while (currCommit != null) {
            historyCB.add(gvnCommit.parent1);
            currCommit = Commit.getCommitByID(currCommit.parent1);
        }
        for (String commitId : historyCB) {
            if (historyGB.contains(commitId)) {
                return commitId;
            }
        }
        return null;
        }

}