package datastructures;

import java.util.ArrayList;

/**
 * My totally rewritten implementation of a hash table in Java.
 *
 * @author Bradley Moore
 * @param <K> The key of type K used to find the array index.
 * @param <V> Value associated with the key of type V.
 */
public class HashTable<K, V> {

  private Node<K, V>[] table;
  private int tableSize;
  private final double loadFactorLimit = .7;
  private int loadLimit;
  private int load = 0;

  /**
   * Public constructor with no parameters, calls parameterized constructor with
   * a parameter of 256.
   */
  public HashTable() {
    this(256);
  }

  /**
   * Public constructor.
   *
   * @param tableSize Int for the length of the table array. If tableSize < 2 it
   * is resized to 2.
   */
  public HashTable(int tableSize) {
    this.tableSize = tableSize;
    if (this.tableSize < 2) {
      this.tableSize = 2;
    }
    table = new Node[this.tableSize];

    setLoadLimit();
  }

  /**
   * Sets all nodes in the array to null.
   */
  public final void clear() {
    for (int i = 0; i < tableSize; i++) {
      table[i] = null;
    }

    load = 0;
  }

  /**
   * If a key exists return it's value. Otherwise return null.
   *
   * @param key Type K used to find array index.
   * @return The value of a given key of the specified type.
   */
  public V get(K key) {

    // find correct key even after collisions
    int hash = getHash(key);
    while (table[hash] != null && table[hash].key != key) {
      hash += 1;
      if (hash >= tableSize) {
        hash = 0;
      }
    }

    // return null or the actual value requested
    if (table[hash] == null) {
      return null;
    } else {
      return table[hash].value;
    }
  }

  /**
   * Iterate and get an ArrayList of the keys.
   *
   * @return An ArrayList of the available keys.
   */
  public ArrayList<K> getKeys() {
    ArrayList<K> keys = new ArrayList<>();
    for (int i = 0; i < tableSize; i++) {
      if (table[i] != null) {
        keys.add(table[i].key);
      }
    }
    return keys;
  }

  /**
   * Iterate and get an ArrayList of the values.
   *
   * @return An ArrayList of the available values.
   */
  public ArrayList<V> getValues() {
    ArrayList<V> values = new ArrayList<>();
    for (int i = 0; i < tableSize; i++) {
      if (table[i] != null) {
        values.add(table[i].value);
      }
    }
    return values;
  }

  /**
   * Insert a Node, including its key and value into the array.
   *
   * @param key Type K used to find array index.
   * @param value The information stored with the key of type V.
   */
  public void put(K key, V value) {
    // find first null node
    int hash = getHash(key);
    while (table[hash] != null && table[hash].key != key) {
      hash += 1;
      if (hash >= tableSize) {
        hash = 0;
      }
    }

    // if null create new; else overwrite previous node
    if (table[hash] == null) {
      table[hash] = new Node(key, value);
    } else {
      table[hash].value = value;
    }

    load++;
    checkForResize();
  }

  /**
   * Sets specific node to null.
   *
   * @param key Type K used to find array index.
   */
  public void remove(K key) {
    // find correct key even after collisions
    int hash = getHash(key);
    while (table[hash] != null && table[hash].key != key) {
      hash += 1;
      if (hash >= tableSize) {
        hash = 0;
      }
    }
    table[hash] = null;

    load--;
  }

  /**
   * Checks if increasing the array size is necessary.
   */
  private void checkForResize() {
    if (load > loadLimit) {
      resize();
    }
  }

  /**
   * Hash the key to get a specific location in the array. Called from {@link #put(K key, V value)},
   * {@link #get(K key)}, and {@link #remove(K key)}.
   *
   * @param key Type K used to find array index.
   * @return An int is returned that is between 0 and tableSize.
   */
  private int getHash(K key) {
    final int hash = Math.abs(key.hashCode() % tableSize);
    return hash;
  }

  /**
   * Insert a Node, including its key and value into the array during resize.
   * Does not call checkForResize like in public put.
   *
   * @param key Type K used to find array index.
   * @param value The information stored with the key of type V.
   */
  private void put(K key, V value, boolean resize) {
    // find first null node
    int hash = getHash(key);
    while (table[hash] != null && table[hash].key != key) {
      hash += 1;
      if (hash >= tableSize) {
        hash = 0;
      }
    }

    // if null create new; else overwrite previous node
    if (table[hash] == null) {
      table[hash] = new Node(key, value);
    } else {
      table[hash].value = value;
    }

    load++;
  }

  /**
   * Double the array size if the load factor is above the load factor limit.
   * Called from {@link #checkForResize()}.
   */
  private void resize() {
    final int oldTableSize = tableSize;
    tableSize *= 2;

    Node<K, V>[] tmpTable = table;
    table = new Node[tableSize];
    clear();

    // fill table with nodes at new hashed keys and null the old table
    for (int i = 0; i < oldTableSize; i++) {
      if (tmpTable[i] != null) {
        put(tmpTable[i].key, tmpTable[i].value, true);
        tmpTable[i] = null;
      }
    }

    setLoadLimit();
  }

  /**
   * Sets the load limit so it doesn't have to be repeatedly calculated.
   */
  private void setLoadLimit() {
    double dLoadLimit = tableSize * loadFactorLimit;
    loadLimit = (int) dLoadLimit;
    System.out.println("setLoadLimit: dLoadLimit: " + dLoadLimit + " : tableSize: " + tableSize + " : loadFactorLimit: " + loadFactorLimit + " : loadLimit: " + loadLimit);
  }

  /**
   * Inner class that can only be accessed by HashTable. Node stores the actual
   * information the user is interested in.
   *
   * @param <K> The key of type K used to find the array index.
   * @param <V> Value associated with the key of type V.
   */
  private class Node<K, V> {

    private final K key;
    private V value;

    /**
     * Node constructor.
     *
     * @param <K> The key of type K used to find the array index.
     * @param <V> Value associated with the key of type V.
     */
    private Node(K key, V value) {
      this.key = key;
      this.value = value;
    }
  }
}
