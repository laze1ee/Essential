/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import essential.functional.Do1;
import essential.progresive.Few;
import org.jetbrains.annotations.NotNull;
import essential.functional.Predicate1;
import essential.functional.Predicate2;
import essential.progresive.Lot;

import static essential.progresive.Pr.*;


public class RBTree {

private final Few rb_tree;

public RBTree(Predicate2 less, Predicate2 greater) {rb_tree = few(less, greater, RBTreeMate.makeNode());}

Predicate2 less() {return (Predicate2) rb_tree.ref(0);}

Predicate2 greater() {return (Predicate2) rb_tree.ref(1);}

Few root() {return (Few) rb_tree.ref(2);}

void setRoot(Few node) {rb_tree.set(2, node);}

@Override
public String toString() {
    return String.format("#[Red-Black-Tree %s]", RBTreeMate.stringify(root()));
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof RBTree tree) {
        return root().equals(tree.root());
    } else {
        return false;
    }
}

/**
 * Checks if the tree is empty.
 *
 * @return true if the tree is empty, false otherwise.
 */
public boolean isEmpty() {return RBTreeMate.isNil(root());}

/**
 * The number of items in the tree.
 *
 * @return number of items in the tree.
 */
public int size() {
    RBTreeMate.Counting inst = new RBTreeMate.Counting();
    return inst.process(root());
}

/**
 * Inserts a key-value pair into the Red-Black Tree.
 * If the key is already present in the tree, the insertion operation fails and returns false.
 *
 * @param key   the key to insert.
 * @param value the value to insert.
 * @return {@code true} if inserting succeeded, {@code false} otherwise.
 */
public boolean insert(@NotNull Object key, Object value) {
    Lot path = RBTreeMate.pathOf(this, key);
    Few node = (Few) path.car();
    if (RBTreeMate.isNil(node)) {
        RBTreeMate.setKey(node, key);
        RBTreeMate.setValue(node, value);
        RBTreeMate.setColor(node, true);
        RBTreeMate.setLeft(node, RBTreeMate.makeNode());
        RBTreeMate.setRight(node, RBTreeMate.makeNode());
        RBTreeMate.InsertFixing fixer = new RBTreeMate.InsertFixing(this, path);
        fixer.process();
        return true;
    } else {
        return false;
    }
}

/**
 * Checks if the given key is present in the Red-Black Tree.
 *
 * @param key the key to check.
 * @return true if the key is present, false otherwise.
 */
public boolean isPresent(@NotNull Object key) {
    Lot path = RBTreeMate.pathOf(this, key);
    Few node = (Few) path.car();
    return !RBTreeMate.isNil(node);
}


/**
 * Retrieves the value associated with the specified key in the Red-Black Tree.
 *
 * @param key the key whose associated value is to be returned.
 * @return the value associated with the specified key.
 * @throws RuntimeException if the key is not present in the tree.
 */
public @NotNull Object ref(@NotNull Object key) {
    Lot path = RBTreeMate.pathOf(this, key);
    Few node = (Few) path.car();
    if (RBTreeMate.isNil(node)) {
        throw new RuntimeException(String.format(Msg.NOT_PRESENT, key, this));
    } else {
        return RBTreeMate.value(node);
    }
}

/**
 * Sets the value associated with the given key.
 *
 * @param key       key whose associated value is to be set
 * @param new_value the new value
 * @throws RuntimeException if the key is not present in the tree
 */
public void set(@NotNull Object key, Object new_value) {
    Lot path = RBTreeMate.pathOf(this, key);
    Few node = (Few) path.car();
    if (RBTreeMate.isNil(node)) {
        throw new RuntimeException(String.format(Msg.NOT_PRESENT, key, this));
    } else {
        RBTreeMate.setValue(node, new_value);
    }
}

/**
 * Removes the key-value pair of the specified key.
 * If the key is present in the tree, the deletion operation succeeds and returns {@code true},
 * otherwise returns {@code false}.
 *
 * @param key the key whose associated value is to be removed.
 * @return {@code true} if the key was present and deleted, {@code false} otherwise.
 */
public boolean delete(@NotNull Object key) {
    if (isEmpty()) {
        return false;
    } else {
        return RBTreeMate.delete(this, key);
    }
}

/**
 * The minimum key in the tree.
 *
 * @return the minimum key in the tree.
 * @throws RuntimeException if the tree is empty.
 */
public Object minimum() {
    Lot path = RBTreeMate.minimum(root(), lot());
    if (path.isEmpty()) {
        throw new RuntimeException(Msg.EMPTY_TREE);
    } else {
        Few node = (Few) path.car();
        return RBTreeMate.key(node);
    }
}

/**
 * The maximum key in the tree.
 *
 * @return the maximum key in the tree.
 * @throws RuntimeException if the tree is empty.
 */
public Object maximum() {
    Lot path = RBTreeMate.maximum(root(), lot());
    if (path.isEmpty()) {
        throw new RuntimeException(Msg.EMPTY_TREE);
    } else {
        Few node = (Few) path.car();
        return RBTreeMate.key(node);
    }
}

/**
 * Traverses the Red-Black tree and returns a list of key-value pairs.
 * The key-value pairs are ordered in ascending order of keys.
 *
 * @return a list of key-value pairs.
 */
public Lot travel() {
    RBTreeMate.Traveling inst = new RBTreeMate.Traveling();
    return inst.process(root());
}

/**
 * Filters the key-value pairs in the Red-Black tree with the given predicate.
 *
 * @param fn the predicate to filter.
 * @return a filtered Red-Black tree.
 */
public RBTree filter(Predicate1 fn) {
    RBTreeMate.Filtering inst = new RBTreeMate.Filtering(fn);
    return inst.process(this);
}

/**
 * Maps the key-value pairs in the Red-Black tree with the given procedure.
 *
 * @param fn the procedure to modify value.
 * @return a mapped Red-Black tree.
 */
public RBTree map(Do1 fn) {
    RBTreeMate.Mapping inst = new RBTreeMate.Mapping(fn, this);
    return inst.process(root());
}

/**
 * Returns a list of depths for each leaf node in the Red-Black tree, in descending order.
 * Note that this order is different from the order of the leaf nodes in the tree.
 */
public static Lot depth(@NotNull RBTree tree) {
    return RBTreeMate.depthStatistic(tree.root());
}
}

