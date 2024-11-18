package gitlet;

// TODO: any imports you need here

import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Xinxin
 */
public class Commit {
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
    public Commit parent1;
    /** Second parent of the Commit. */
    public Commit parent2;
    /** A mapping of SHA-1 hash of blobs to the files in the blobs directory. */
    public HashMap mappingToBlob;

    public Commit(String message, Date timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    /** Initial commit. Creates a new Gitlet version-control system in the current directory. The system
     * automatically start with one commit: a commit that contains no files and has the commit message
     * "initial commit". It will have a single branch: master, which initially points to this initial commit,
     * and master will be the current branch.*/
    public static void init() {
        Commit initialCommit = new Commit("initial commit", new Date(0));
    }

    /* TODO: fill in the rest of this class. */
}
