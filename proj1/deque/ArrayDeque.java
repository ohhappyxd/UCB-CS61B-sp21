package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    private int totalSize;
    private T[] items;
    private int start;
    private int nextFirst;
    private int nextLast;
    private static final int RESIZE_LIMIT = 16;

    public ArrayDeque() {
        totalSize = 8;
        items = (T[]) new Object[totalSize];
        size = 0;
        start = 0;
        nextFirst = totalSize / 2;
        nextLast = totalSize / 2 + 1;
    }

    /** Resizes the items array if usage factor is under 25%. */
    private void resize(int newSize) {
        T[] a = (T[]) new Object[newSize];
        int newStart = newSize / 2;
        for (int i = 0; i < size; i++) {
            a[newStart + i] = get(i);
        }
        items = a;
        start = newStart;
        totalSize = newSize;
        nextFirst = returnPrevious(newStart);
        nextLast = returnNext(start + size - 1);
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
        if (size == totalSize) {
            resize(size * 2);
        }
        items[nextFirst] = item;
        start = nextFirst;
        size += 1;
        nextFirst = returnPrevious(nextFirst);
    }

    /** Adds an item of type T to the back of the deque. */
    public void addLast(T item) {
        if (size == totalSize) {
            resize(size * 2);
        }
        items[nextLast] = item;
        if (size == 0) {
            start = nextLast;
        }
        size += 1;
        nextLast = returnNext(nextLast);
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

    /** Removes and returns the item at the front of the deque.
     * If no such item exists, returns null.
     *  Resizes the items array if usage factor is under 25%.*/
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        if ((size < items.length / 4) && (items.length >= RESIZE_LIMIT)) {
            resize((int) (items.length / 4));
        }
        int next = returnNext(nextFirst);
        T currentFirst = items[next];
        nextFirst = next;
        start = next;
        size -= 1;
        return currentFirst;
    }

    /** Removes and returns the item at the back of the deque. If no such item exists, returns null.
     *  Resizes the items array if usage factor is under 25%. */
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        if ((size < items.length / 4) && (items.length >= RESIZE_LIMIT)) {
            resize((int) (items.length / 4));
        }
        int prev = returnPrevious(nextLast);
        T currentLast = items[prev];
        nextLast = prev;
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

    /** Returns an iterator into Me */
    public Iterator<T> iterator() {
        return new ArrayDeque.ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int index;

        ArrayDequeIterator() {
            index = 0;
        }
        public boolean hasNext() {
            return index < size;
        }

        public T next() {
            T returnItem = get(index);
            index += 1;
            return returnItem;
        }
    }

    /** Returns whether or not the parameter o is equal to the Deque.
     * o is considered equal if it is a Deque and if it contains the same contents
     * (as goverened by the generic T’s equals method) in the same order. */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof Deque)) {
            return false;
        }
        Deque<T> o = (Deque<T>) other;
        if (o.size() != size) {
            return false;
        }
        for (int i = 0; i < size; i += 1) {
            if (!o.get(i).equals(this.get(i))) {
                return false;
            }
        }
        return true;
    }
}
