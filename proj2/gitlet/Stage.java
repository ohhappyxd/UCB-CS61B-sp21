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
    public static void add(String fileName) {
        File FileToAdd = Utils.join(CWD, fileName);
        if (!FileToAdd.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        if (Repository.getCurrentCommit().blobs != null && Repository.getCurrentCommit().blobs.containsValue(sha1ToAdd)) {
            // TODO: if staged for removal, remove from removal
            if (toStage().containsKey(fileName)) {
                toStage.remove(fileName);
            }
            // TODO: remove from stage for removal if necessary.
            if (toRemove().containsKey(fileName)) {
                toRemove.remove(fileName);
            }
            return;
        }
        byte[] ContentToAdd = Utils.readContents(FileToAdd);
        String sha1ToAdd = Utils.sha1(ContentToAdd);
        File AddFile = Utils.join(STAGE, sha1ToAdd);
        Utils.writeContents(AddFile, ContentToAdd);
        HashMap<String, String> map = new HashMap<>();
        map.put(fileName, sha1ToAdd);


    }
}
