package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> listNR = new AListNoResizing<>();
        BuggyAList<Integer> listB = new BuggyAList<>();
        listNR.addLast(4);
        listB.addLast(4);
        listNR.addLast(5);
        listB.addLast(5);
        listNR.addLast(6);
        listB.addLast(6);
        assertEquals(listNR.removeLast(), listB.removeLast());
        assertEquals(listNR.removeLast(), listB.removeLast());
        assertEquals(listNR.removeLast(), listB.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> Buggy = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                Buggy.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int bSize = Buggy.size();
                assertEquals(size, bSize);
            } else if (operationNumber == 2){
                // getLast
                if (L.size() == 0) {
                    break;
                }
                if (Buggy.size() == 0) {
                    break;
                }
                int item = L.getLast();
                int bItem = Buggy.getLast();
                assertEquals(item, bItem);
            } else if (operationNumber == 3) {
                //removeLast
                if (L.size() == 0) {
                    break;
                }
                if (Buggy.size() == 0) {
                    break;
                }
                int item = L.removeLast();
                int bItem = Buggy.removeLast();
                assertEquals(item, bItem);
            }
        }
    }
}
