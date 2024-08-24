package deque;

public class ArrayDeque<T> {
    private int size;
    private int totalSize;
    private T[] items;
    private int start;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        totalSize = 8;
        items = (T[]) new Object[totalSize];
        size = 0;
        start = 0;
        nextFirst = totalSize / 2;
        nextLast = totalSize / 2 + 1;
    }

    /** @param current the current nextFirst or nextLast
     *  Returns the index previous to the given one. Array is treated as circular */
    private int returnPrevious(int current) {
        if (current == 0) {
            return totalSize - 1;
        } else {
            return current - 1;
        }
    }

    /** @param current the current nextFirst or nextLast
     * Returns the index next to the given one. Array is treated as circular. */
    private int returnNext(int current) {
        if (current == totalSize - 1) {
            return 0;
        } else {
            return current + 1;
        }
    }

    /** Adds an item of type T to the front of the deque. */
    public void addFirst(T item) {
        items[nextFirst] = item;
        if (size == 0) {
            start = nextFirst;
        }
        size += 1;
        nextFirst = returnPrevious(nextFirst);
    }

    /** Adds an item of type T to the back of the deque. */
    public void addLast(T item) {
        items[nextLast] = item;
        if (size == 0) {
            start = nextLast;
        }
        size += 1;
        nextLast = returnNext(nextLast);
    }

    /** Returns true if deque is empty, false otherwise. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Returns the number of items in the deque. */
    public int size() {
        return size;
    }

    /** Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line. */
    public void printDeque() {
        for (int i = 0; i <= size - 1; i++) {
            System.out.print(get(i) + " ");
        }
        System.out.println();
    }

    /** Removes and returns the item at the front of the deque. If no such item exists, returns null. */
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T currentFirst = items[nextFirst + 1];
        nextFirst = returnNext(nextFirst);
        size -= 1;
        return currentFirst;
    }

    /** Removes and returns the item at the back of the deque. If no such item exists, returns null. */
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T currentLast = items[nextLast - 1];
        nextLast = returnPrevious(nextLast);
        size -= 1;
        return currentLast;
    }

    /** Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque! */
    public T get(int index) {
        if (start + index >= totalSize) {
            return items[start + index - totalSize];
        }
        return items[start + index];
    }


}