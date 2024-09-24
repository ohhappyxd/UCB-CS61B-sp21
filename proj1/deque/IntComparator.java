package deque;

import java.util.Comparator;

public class IntComparator implements Comparator<Integer>{
    public int compare(Integer a, Integer b) {
        return a.compareTo(b);
    }
}
