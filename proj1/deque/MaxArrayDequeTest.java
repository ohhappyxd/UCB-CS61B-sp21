package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class MaxArrayDequeTest {

    @Test
    /** Tests if max(Comparator<T> c) works for strings. */
    public void maxStringTest() {
        StringComparator c = new StringComparator();
        MaxArrayDeque<String> maxADeque= new MaxArrayDeque<>(c);
        maxADeque.addFirst("I");
        maxADeque.addFirst("am");
        maxADeque.addLast("right");
        String max = maxADeque.max();
        assertEquals("right", max);
    }


    @Test
    /** Test if max() works for integers. */
    public void maxIntTest() {
        IntComparator c = new IntComparator();
        MaxArrayDeque<Integer> maxADeque= new MaxArrayDeque<>(c);
        maxADeque.addFirst(10);
        maxADeque.addFirst(15);
        maxADeque.addLast(5);
        int max = maxADeque.max();
        assertEquals(15, max);
    }
}
