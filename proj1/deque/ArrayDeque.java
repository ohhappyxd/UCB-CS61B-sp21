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
        start = totalSize / 2;
        nextFirst = start;
        nextLast = start + 1;
    }
    /** Adds an item of type T to the front of the deque. */
    public void addFirst(T item) {

        items[nextFirst] = item;
        size += 1;
        if (nextFirst == 0) {
            nextFirst = totalSize;
        } else {
            nextFirst -= 1;
        }

    }

    /** Adds an item of type T to the back of the deque. */
    public void addLast(T item) {
        items[nextLast] = item;
        size += 1;
        if (nextLast == 8) {
            nextLast = 0;
        } else {
            nextLast += 1;
        }

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
        T currentFirst = items[nextFirst + 1];
        nextFirst -= 1;
        return currentFirst;
    }

    /** Removes and returns the item at the back of the deque. If no such item exists, returns null. */
    public T removeLast() {
        T currentLast = items[nextLast + 1];
        nextLast += 1;
        return currentLast;
    }

    /** Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque! */
    public T get(int index) {
        if (start + index > totalSize) {
            return items[start + index - totalSize];
        }
        return items[start + index];
    }


}