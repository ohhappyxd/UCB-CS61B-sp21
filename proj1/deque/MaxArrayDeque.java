package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T>{
    public Comparator<T> defaultComparator;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        defaultComparator = c;
    }

    /** Returns the maximum element in the deque as governed by the previously given Comparator.
     * If the MaxArrayDeque is empty, returns null.*/
    public T max() {
        return null;
    }

    /** Returns the maximum element in the deque as governed by the parameter Comparator c.
     * If the MaxArrayDeque is empty, returns null.*/
    public T max(Comparator<T> c) {
        return null;
    }
}
