package gitlet;

import java.io.IOException;
import java.util.Arrays;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Xinxin
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                validateNumArgs(args, 1);
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                validateNumArgs(args, 2);
                Repository.add(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                validateNumArgs(args, 2);
                Repository.commit(args[1]);
                break;
            case "checkout":
                // TODO: fill this in
                validateNumArgs(args,2, 4);
                //Repository.checkout(Arrays.copyOfRange(args, 1, args.length - 1));
            case "rm":
                validateNumArgs(args, 2);
                Repository.rm(args[1]);
            case "log":
                validateNumArgs(args, 1);
                Repository.log();
            case "global-log":
                validateNumArgs(args, 1);
                Repository.globalLog();
            case "find":
                validateNumArgs(args, 2);
                Repository.find(args[1]);
            case "status":
                validateNumArgs(args, 1);
                Repository.status();
        }
    }

    /**
     * Checks the number of arguments versus the expected number,
     * prints a message and exits if they do not match.
     *
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    /**
     * Checks the number of arguments versus the expected range,
     * prints a message and exits if they do not match.
     *
     * @param args Argument array from command line
     * @param n Minimal number of expected arguments
     * @param m Maximum number of expected arguments
     */
    public static void validateNumArgs(String[] args, int n, int m) {
        if (args.length < n || args.length > m) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
