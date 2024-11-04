package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author Xinxin
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    private final int DEFAULT_LENGTH = 16;
    private final double DEFAULT_LOAD_FACTOR = 0.75;

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int size;
    private int length;
    private double maxLoad;
    private Set<K> keys;

    /** Constructors */
    public MyHashMap() {
        buckets = createTable(DEFAULT_LENGTH);
        maxLoad = DEFAULT_LOAD_FACTOR;
        keys = new HashSet<>();
        length = DEFAULT_LENGTH;
    }

    public MyHashMap(int initialSize) {
        buckets = createTable(initialSize);
        maxLoad = DEFAULT_LOAD_FACTOR;
        keys = new HashSet<>();
        length = initialSize;
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        maxLoad = maxLoad;
        keys = new HashSet<>();
        length = initialSize;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    /** Removes all of the mappings from this map. */
    @Override
    public void clear() {
        size = 0;
        keys.clear();
    }

    /** Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        if (keys != null) {
            return keys.contains(key);
        } else {
            return false;
        }

    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        if (containsKey(key)) {
            int x = key.hashCode();
            int index = Math.floorMod(x, length);
            for (Node pair : buckets[index]) {
                if (pair.key.equals(key)) {
                    return pair.value;
                }
            }
        }
        return null;
    }

    /** Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    @Override
    public void put(K key, V value) {
        int x = key.hashCode();
        int index = Math.floorMod(x, length);
        if (containsKey(key)) {
            for (Node pair : buckets[index]) {
                if (pair.key.equals(key)) {
                    pair.value = value;
                }
            }
        } else {
            if (buckets[index] == null) {
                buckets[index] = createBucket();
            }
            buckets[index].add(new Node(key, value));
            keys.add(key);
            size += 1;
        }
        /* Resize if the load factor is exceeded. */
        if ((double) size / length > maxLoad) {
            Collection<Node>[] t = createTable(length * 2);
            System.arraycopy(buckets, 0, t, 0, length);
            buckets = t;
        }
    }

    /** Returns a Set view of the keys contained in this map. */
    @Override
    public Set<K> keySet() {
        return keys;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    @Override
    public V remove(K key) {
        if (containsKey(key)) {
            int x = key.hashCode();
            int index = Math.floorMod(x, length);
            for (Node pair : buckets[index]) {
                if (pair.key == key) {
                    V value = pair.value;
                    keys.remove(key);
                    buckets[index].remove(pair);
                    return value;
                }
            }
        }
        return null;
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    @Override
    public V remove(K key, V value) {
        if (get(key) == value) {
            return remove(key);
        } else {
            return null;
        }
    }

    @Override
    public Iterator<K> iterator() {
        return keys.iterator();
    }
}
