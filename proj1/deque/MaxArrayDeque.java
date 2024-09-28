package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> defaultComparator;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        defaultComparator = c;
    }

    /** Returns the maximum element in the deque as governed by the previously given Comparator.
     * If the MaxArrayDeque is empty, returns null.*/
    public T max() {
        if (this.isEmpty()) {
            return null;
        }
        T maxItem = get(0);
        for (T item : this) {
            if (defaultComparator.compare(maxItem, item) < 0) {
                maxItem = item;
            }
        }
        return maxItem;

    }

    /** Returns the maximum element in the deque as governed by the parameter Comparator c.
     * If the MaxArrayDeque is empty, returns null.*/
    public T max(Comparator<T> c) {
        if (this.isEmpty()) {
            return null;
        }
        T maxItem = get(0);
        for (T item : this) {
            if (c.compare(maxItem, item) < 0) {
                maxItem = item;
            }
        }
        return maxItem;
    }
}
