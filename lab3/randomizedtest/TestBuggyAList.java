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

        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 2);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                System.out.println("size: " + size);
            }
        }
    }
}
