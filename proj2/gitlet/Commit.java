package gitlet;

// TODO: any imports you need here

import java.util.Date; // TODO: You'll likely use this in this class

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
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
    public Date timestamp;

    public Commit(String message, Date timestamp) {
        this.message = message;
    }

    /** Initial commit. */
    public static void init() {
        Commit initialCommit = new Commit("initial commit", new Date(0));
    }

    /* TODO: fill in the rest of this class. */
}
