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

Predicate2 less() {
    return (Predicate2) ref0(avl_tree);
}

Predicate2 greater() {
    return (Predicate2) ref1(avl_tree);
}

Few root() {
    return (Few) ref2(avl_tree);
}

void setRoot(Few node) {
    set2(avl_tree, node);
}

/**
 * Is the tree empty?
 *
 * @return if the tree is empty, return true else false.
 */
public boolean isEmpty() {
    return AVLTreeMate.isNil(root());
}

/**
 * The number of items in the tree.
 *
 * @return number of items in the tree.
 */
public int size() {
    AVLTreeMate.Counting inst = new AVLTreeMate.Counting();
    return inst.process(root());
}

@Override
public String toString() {
    return String.format("#[AVL-Tree %s]", AVLTreeMate.stringify(root()));
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof AVLTree tree) {
        return root().equals(tree.root());
    } else {
        return false;
    }
}

/**
 * Inserts a key-value pair into the AVL tree.
 *
 * @param tree  the AVL tree to insert to.
 * @param key   the key to insert.
 * @param value the value to insert.
 * @return true if inserting succeeded, false otherwise.
 */
public static boolean insert(@NotNull AVLTree tree, @NotNull Object key, Object value) {
    Lot path = AVLTreeMate.pathOf(tree, key);
    Few node = (Few) car(path);
    if (AVLTreeMate.isNil(node)) {
        AVLTreeMate.setKey(node, key);
        AVLTreeMate.setValue(node, value);
        AVLTreeMate.setHeight(node, 1);
        AVLTreeMate.setLeft(node, AVLTreeMate.makeNode());
        AVLTreeMate.setRight(node, AVLTreeMate.makeNode());
        AVLTreeMate.update(tree, path);
        return true;
    } else {
        return false;
    }
}

/**
 * Checks if the given key is present in the AVL tree.
 *
 * @param tree the AVL tree to check.
 * @param key  the key to check.
 * @return true if the key is present, false otherwise.
 */
public static boolean isPresent(@NotNull AVLTree tree, @NotNull Object key) {
    Lot path = AVLTreeMate.pathOf(tree, key);
    Few node = (Few) car(path);
    return !AVLTreeMate.isNil(node);
}

/**
 * Returns the value associated with the given key.
 *
 * @param tree the AVL tree to access.
 * @param key  the key to refer.
 * @return the value associated with the given key.
 */
public static @NotNull Object ref(@NotNull AVLTree tree, @NotNull Object key) {
    Lot path = AVLTreeMate.pathOf(tree, key);
    Few node = (Few) car(path);
    if (AVLTreeMate.isNil(node)) {
        throw new RuntimeException(String.format(Msg.NOT_PRESENT, key, tree));
    } else {
        return AVLTreeMate.value(node);
    }
}

/**
 * Sets the value associated with the given key.
 *
 * @param tree      the AVL tree to be modified
 * @param key       key whose associated value is to be set
 * @param new_value the new value
 */
public static void set(@NotNull AVLTree tree, @NotNull Object key, Object new_value) {
    Lot path = AVLTreeMate.pathOf(tree, key);
    Few node = (Few) car(path);
    if (AVLTreeMate.isNil(node)) {
        throw new RuntimeException(String.format(Msg.NOT_PRESENT, key, tree));
    } else {
        AVLTreeMate.setValue(node, new_value);
    }
}

/**
 * Deletes the mapping of the specified key (if such mapping exists).
 * If this AVL tree maps the specified key to a value, returns {@code true},
 * else returns {@code false}.
 *
 * @param tree the AVL tree to be modified.
 * @param key  the key and whose associated value is to be removed.
 * @return true if the value is deleted, false otherwise.
 */
public static boolean delete(@NotNull AVLTree tree, @NotNull Object key) {
    if (tree.isEmpty()) {
        return false;
    } else {
        return AVLTreeMate.delete(tree, key);
    }
}

/**
 * Finds the minimum key in the AVL tree.
 *
 * @return the minimum key in the tree.
 * @throws RuntimeException if the tree is empty.
 */
public Object minimum() {
    Lot path = AVLTreeMate.minimum(root(), lot());
    if (path.isEmpty()) {
        throw new RuntimeException(Msg.EMPTY_TREE);
    } else {
        Few node = (Few) car(path);
        return AVLTreeMate.key(node);
    }
}

/**
 * Finds the maximum key in the AVL tree.
 *
 * @return the maximum key in the tree.
 * @throws RuntimeException if the tree is empty.
 */
public Object maximum() {
    Lot path = AVLTreeMate.maximum(root(), lot());
    if (path.isEmpty()) {
        throw new RuntimeException(Msg.EMPTY_TREE);
    } else {
        Few node = (Few) car(path);
        return AVLTreeMate.key(node);
    }
}

/**
 * Traverses the AVL tree and returns a list of key-value pairs.
 * The key-value pairs are ordered in ascending order of keys.
 *
 * @param tree the AVL tree to traverse.
 * @return a list of key-value pairs.
 */
public static Lot travel(@NotNull AVLTree tree) {
    AVLTreeMate.Traveling inst = new AVLTreeMate.Traveling();
    return inst.process(tree.root());
}

/**
 * Filters the key-value pairs in the AVL tree with the given predicate.
 *
 * @param fn   the predicate to filter.
 * @param tree the AVL tree to filter.
 * @return a filtered AVL tree.
 */
public static AVLTree filter(Predicate1 fn, @NotNull AVLTree tree) {
    AVLTreeMate.Filtering inst = new AVLTreeMate.Filtering(fn);
    return inst.process(tree);
}

/**
 * Maps the key-value pairs in the AVL tree with the given procedure.
 *
 * @param fn   the procedure to modify value.
 * @param tree the AVL tree to map.
 * @return a mapped AVL tree.
 */
public static AVLTree map(Do1 fn, @NotNull AVLTree tree) {
    AVLTreeMate.Mapping inst = new AVLTreeMate.Mapping(fn, tree);
    return inst.process(tree.root());
}
}
