package deque;

public class LinkedListDeque<T> {
    private class ItemNode {
        public T item;
        public ItemNode previous;
        public ItemNode next;

        public ItemNode(T i, ItemNode p, ItemNode n) {
            item = i;
            previous = p;
            next = n;
        }
    }

    private int size;
    private ItemNode sentinel;

    public LinkedListDeque() {
        sentinel = new ItemNode(null, null, null);
        sentinel.previous = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }
    /** Adds an item of type T to the front of the deque. */
    public void addFirst(T item) {
        ItemNode first = sentinel.next;
        ItemNode newItem = new ItemNode(item, sentinel, first);
        first.previous = newItem;
        sentinel.next = newItem;
        size += 1;
    }

    /** Adds an item of type T to the back of the deque. */
    public void addLast(T item) {
        ItemNode last = sentinel.previous;
        ItemNode newItem = new ItemNode(item, last, sentinel);
        sentinel.previous = newItem;
        last.next = newItem;
        size += 1;
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
        ItemNode item = sentinel.next;
        if (item != sentinel) {
            sentinel.next = item.next;
            item.next.previous = sentinel;
            size -= 1;
            return item.item;
        }
        return null;
    }

    /** Removes and returns the item at the back of the deque. If no such item exists, returns null. */
    public T removeLast() {
        ItemNode item = sentinel.previous;
        if (item != sentinel) {
            sentinel.previous = item.previous;
            item.previous.next = sentinel;
            size -= 1;
            return item.item;
        }
        return null;
    }

    /** Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque! */
    public T get(int index) {
        if (index > size - 1) {
            return null;
        }
        ItemNode item = sentinel;
        for (int i = 0; i <= index; i++) {
            item = item.next;
        }
        return item.item;
    }
}