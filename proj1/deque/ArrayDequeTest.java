package deque;

import org.junit.Test;
import static org.junit.Assert.*;


public class ArrayDequeTest {
    @Test
    public void testEmptySize() {
        ArrayDeque<String> L = new ArrayDeque<>();
        assertEquals(0, L.size());
    }

    @Test
    public void testAddAndSize() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        L.addLast(99);
        L.addLast(99);
        assertEquals(2, L.size());
    }


    @Test
    public void testGet() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        L.addLast(99);
        assertEquals(99, (int) L.get(0));
        L.addLast(36);
        assertEquals(99, (int) L.get(0));
        assertEquals(36, (int) L.get(1));
    }


    @Test
    public void testRemove() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        L.addLast(99);
        assertEquals(99, (int) L.get(0));
        L.addLast(36);
        assertEquals(99, (int) L.get(0));
        L.removeLast();
        assertEquals(99, (int) L.get(0));
        L.addLast(100);
        assertEquals(100, (int) L.get(1));
        assertEquals(2, L.size());
    }

    /** Tests insertion of a large number of items.
    @Test
    public void testMegaInsert() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        int N = 1000000;
        for (int i = 0; i < N; i += 1) {
            L.addLast(i);
        }

        for (int i = 0; i < N; i += 1) {
            L.addLast(L.get(i));
        }
    }
*/
    public static void main(String[] args) {
        jh61b.junit.TestRunner.runTests("all", ArrayDequeTest.class);
    }
}
