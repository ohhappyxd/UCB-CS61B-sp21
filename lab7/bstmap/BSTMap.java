package bstmap;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private BSTNode root;

    private class BSTNode {
        private K key;
        private V val;
        private BSTNode left, right;
        private int size;

        public BSTNode(K k, V v, int size) {
            this.key = k;
            this.val = v;
            this.size = size;
        }
    }

    /** Initializes an empty BSTMap. */
    public BSTMap() {
    }

    /** Removes all of the mappings from this map. */
    @Override
    public void clear() {
        root = null;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Argument cannot be null.");
        }
        return containsKey(root, key);
    }

    private boolean containsKey(BSTNode x, K key) {
        if (x == null) {
            return false;
        }
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            return containsKey(x.left, key);
        } else if (cmp > 0) {
            return containsKey(x.right, key);
        } else {
            return true;
        }
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Cannot call put with a null key.");
        }
        return get(root, key);
    }

    private V get(BSTNode x, K key) {
        if (x == null) {
            return null;
        }
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            return get(x.left, key);
        } else if (cmp > 0) {
            return get(x.right, key);
        } else {
            return x.val;
        }
    }

    /* Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size(root);
    }

    private int size(BSTNode x) {
        if (x == null) {
            return 0;
        } else {
            return x.size;
        }
    }

    /* Associates the specified value with the specified key in this map. */
    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Cannot call put with a null key.");
        }
        root = put(root, key, value);
    }

    private BSTNode put(BSTNode x, K key, V value) {
        if (x == null) {
            return new BSTNode(key, value, 1);
        }
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            x.left = put(x.left, key, value);
        } else if (cmp > 0) {
            x.right = put(x.right, key, value);
        } else {
            x.val = value;
        }
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    @Override
    public Set<K> keySet() {
        Set<K> keySet = new TreeSet<>();
        addKeys(root, keySet);
        return keySet;
    }

    private void addKeys(BSTNode x, Set<K> keySet) {
        if (x == null) {
            return;
        }
        keySet.add(x.key);
        addKeys(x.left, keySet);
        addKeys(x.right, keySet);
    }

    /* Removes the mapping for the specified key from this map if present.*/
    @Override
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Cannot call remove with a null key.");
        }
        V val = get(key);
        root = remove(root, key);
        return val;
    }

    private BSTNode remove(BSTNode x, K key) {
        if (x == null) {
            return null;
        }
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            x.left = remove(x.left, key);
        } else if (cmp > 0) {
            x.right = remove(x.right, key);
        } else {
            if (x.left == null) {
                return x.right;
            }
            if (x.right == null) {
                return x.left;
            }
            BSTNode t = x;
            x = min(t.right);
            x.right = deleteMin(t.right);
            x.left = t.left;
        }
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }

    private BSTNode min(BSTNode x) {
        if (x.left == null) {
            return x;
        } else {
            return min(x.left);
        }
    }

    /** Returns the node x with the smallest key removed. */
    private BSTNode deleteMin(BSTNode x) {
        if (x.left == null) {
            return x.right;
        }
        x.left = deleteMin(x.left);
        x.size = size(x.left) + size(x.right) + 1;
        return x;
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value.*/
    @Override
    public V remove(K key, V value){
        if (get(key) == value) {
            return remove(key);
        } else {
            throw new NoSuchElementException("Key with specified value " +
                    "doesn't exist.");
        }
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

    public void printInOrder() {

    }
}
