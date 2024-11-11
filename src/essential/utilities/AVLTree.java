/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import essential.functional.Do1;
import essential.functional.Predicate1;
import essential.functional.Predicate2;
import essential.progresive.Few;
import essential.progresive.Lot;
import org.jetbrains.annotations.NotNull;

import static essential.progresive.Pr.*;


public class AVLTree {

private final Few avl_tree;

public AVLTree(Predicate2 less, Predicate2 greater) {
    avl_tree = few(less, greater, AVLTreeMate.makeNode());
}

Predicate2 less() {return (Predicate2) avl_tree.ref(0);}

Predicate2 greater() {return (Predicate2) avl_tree.ref(1);}

Few root() {return (Few) avl_tree.ref(2);}

void setRoot(Few node) {avl_tree.set(2, node);}

@Override
public String toString() {return String.format("#[AVL-Tree %s]", AVLTreeMate.stringify(root()));}

@Override

public boolean equals(Object datum) {
    if (datum instanceof AVLTree tree) {
        return root().equals(tree.root());
    } else {
        return false;
    }
}

/**
 * Is the AVL tree empty?
 *
 * @return if the tree is empty, return true else false.
 */
public boolean isEmpty() {
    return AVLTreeMate.isNil(root());
}

/**
 * The number of items in the AVL tree.
 *
 * @return number of items in the tree.
 */
public int size() {
    AVLTreeMate.Counting inst = new AVLTreeMate.Counting();
    return inst.process(root());
}

/**
 * Inserts a key-value pair into the AVL tree.
 * If the key is already present in the tree, the insertion operation fails and returns false.
 *
 * @param key   the key to insert.
 * @param value the value to insert.
 * @return true if inserting succeeded, false otherwise.
 */
public boolean insert(@NotNull Object key, Object value) {
    Lot path = AVLTreeMate.pathOf(this, key);
    Few node = (Few) path.car();
    if (AVLTreeMate.isNil(node)) {
        AVLTreeMate.setKey(node, key);
        AVLTreeMate.setValue(node, value);
        AVLTreeMate.setHeight(node, 1);
        AVLTreeMate.setLeft(node, AVLTreeMate.makeNode());
        AVLTreeMate.setRight(node, AVLTreeMate.makeNode());
        AVLTreeMate.update(this, path);
        return true;
    } else {
        return false;
    }
}

/**
 * Checks if the given key is present in the AVL tree.
 *
 * @param key the key to check.
 * @return true if the key is present, false otherwise.
 */
public boolean isPresent(@NotNull Object key) {
    Lot path = AVLTreeMate.pathOf(this, key);
    Few node = (Few) path.car();
    return !AVLTreeMate.isNil(node);
}

/**
 * Retrieves the value associated with the specified key in the AVL tree.
 *
 * @param key the key whose associated value is to be returned.
 * @return the value associated with the specified key.
 * @throws RuntimeException if the key is not present in the tree.
 */
public @NotNull Object ref(@NotNull Object key) {
    Lot path = AVLTreeMate.pathOf(this, key);
    Few node = (Few) path.car();
    if (AVLTreeMate.isNil(node)) {
        throw new RuntimeException(String.format(Msg.NOT_PRESENT, key, this));
    } else {
        return AVLTreeMate.value(node);
    }
}

/**
 * Sets the value associated with the given key in the AVL tree.
 *
 * @param key       the key whose associated value is to be set.
 * @param new_value the new value to be associated with the specified key.
 * @throws RuntimeException if the key is not present in the tree.
 */
public void set(@NotNull Object key, Object new_value) {
    Lot path = AVLTreeMate.pathOf(this, key);
    Few node = (Few) path.car();
    if (AVLTreeMate.isNil(node)) {
        throw new RuntimeException(String.format(Msg.NOT_PRESENT, key, this));
    } else {
        AVLTreeMate.setValue(node, new_value);
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
        return AVLTreeMate.delete(this, key);
    }
}

/**
 * The minimum key in the tree.
 *
 * @return the minimum key in the tree.
 * @throws RuntimeException if the tree is empty.
 */
public Object minimum() {
    Lot path = AVLTreeMate.minimum(root(), lot());
    if (path.isEmpty()) {
        throw new RuntimeException(Msg.EMPTY_TREE);
    } else {
        Few node = (Few) path.car();
        return AVLTreeMate.key(node);
    }
}

/**
 * The maximum key in the tree.
 *
 * @return the maximum key in the tree.
 * @throws RuntimeException if the tree is empty.
 */
public Object maximum() {
    Lot path = AVLTreeMate.maximum(root(), lot());
    if (path.isEmpty()) {
        throw new RuntimeException(Msg.EMPTY_TREE);
    } else {
        Few node = (Few) path.car();
        return AVLTreeMate.key(node);
    }
}

/**
 * Traverses the AVL tree and returns a list of key-value pairs.
 * The key-value pairs are ordered in ascending order of keys.
 *
 * @return a list of key-value pairs.
 */
public Lot travel() {
    AVLTreeMate.Traveling inst = new AVLTreeMate.Traveling();
    return inst.process(root());
}

/**
 * Filters the key-value pairs in the AVL tree with the given predicate.
 *
 * @param fn the predicate to filter.
 * @return a filtered AVL tree.
 */
public AVLTree filter(Predicate1 fn) {
    AVLTreeMate.Filtering inst = new AVLTreeMate.Filtering(fn);
    return inst.process(this);
}

/**
 * Maps the key-value pairs in the AVL tree with the given procedure.
 *
 * @param fn the procedure to modify value.
 * @return a mapped AVL tree.
 */
public AVLTree map(Do1 fn) {
    AVLTreeMate.Mapping inst = new AVLTreeMate.Mapping(fn, this);
    return inst.process(root());
}


/**
 * Returns a list of depths for each leaf node in the AVL tree, in descending order.
 * Note that this order is different from the order of the leaf nodes in the tree.
 */
public static Lot depth(@NotNull AVLTree tree) {
    return AVLTreeMate.depthStatistic(tree.root());
}
}
