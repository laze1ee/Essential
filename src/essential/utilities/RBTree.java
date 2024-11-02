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

public RBTree(Predicate2 less, Predicate2 greater) {
    rb_tree = few(less, greater, RBTreeMate.makeNode());
}

Predicate2 less() {return (Predicate2) ref0(rb_tree);}

Predicate2 greater() {return (Predicate2) ref1(rb_tree);}

Few root() {return (Few) ref2(rb_tree);}

void setRoot(Few node) {set2(rb_tree, node);}

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
 * Inserts a key-value pair into the Red-Black Tree.
 *
 * @param tree  the Red-Black Tree to insert into.
 * @param key   the key to insert.
 * @param value the value to insert.
 * @return true if inserting succeeded, false otherwise.
 */
public static boolean insert(@NotNull RBTree tree, @NotNull Object key, Object value) {
    Lot path = RBTreeMate.pathOf(tree, key);
    Few node = (Few) car(path);
    if (RBTreeMate.isNil(node)) {
        RBTreeMate.setKey(node, key);
        RBTreeMate.setValue(node, value);
        RBTreeMate.setColor(node, true);
        RBTreeMate.setLeft(node, RBTreeMate.makeNode());
        RBTreeMate.setRight(node, RBTreeMate.makeNode());
        RBTreeMate.InsertFixing fixer = new RBTreeMate.InsertFixing(tree, path);
        fixer.process();
        return true;
    } else {
        return false;
    }
}

/**
 * Checks if the given key is present in the Red-Black Tree.
 *
 * @param tree the Red-Black Tree to check.
 * @param key  the key to check.
 * @return true if the key is present, false otherwise.
 */
public static boolean isPresent(@NotNull RBTree tree, @NotNull Object key) {
    Lot path = RBTreeMate.pathOf(tree, key);
    Few node = (Few) car(path);
    return !RBTreeMate.isNil(node);
}

/**
 * Returns the value associated with the given key.
 *
 * @param tree the Red-Black Tree to access.
 * @param key  the key to refer.
 * @return the value associated with the given key.
 */
public static @NotNull Object ref(@NotNull RBTree tree, @NotNull Object key) {
    Lot path = RBTreeMate.pathOf(tree, key);
    Few node = (Few) car(path);
    if (RBTreeMate.isNil(node)) {
        throw new RuntimeException(String.format(Msg.NOT_PRESENT, key, tree));
    } else {
        return RBTreeMate.value(node);
    }
}

/**
 * Sets the value associated with the given key in the Red-Black Tree.
 *
 * @param tree      the Red-Black Tree to be modified
 * @param key       the key whose associated value is to be set
 * @param new_value the new value to be associated with the specified key
 * @throws RuntimeException if the key is not present in the tree
 */
public static void set(@NotNull RBTree tree, @NotNull Object key, Object new_value) {
    Lot path = RBTreeMate.pathOf(tree, key);
    Few node = (Few) car(path);
    if (RBTreeMate.isNil(node)) {
        throw new RuntimeException(String.format(Msg.NOT_PRESENT, key, tree));
    } else {
        RBTreeMate.setValue(node, new_value);
    }
}

/**
 * Deletes the mapping of the specified key (if such mapping exists).
 * If this Red-Black Tree maps the specified key to a value, returns {@code true},
 * else returns {@code false}.
 *
 * @param tree the Red-Black Tree to be modified.
 * @param key  the key and whose associated value is to be deleted.
 * @return true if the value is deleted, false otherwise.
 */
public static boolean delete(@NotNull RBTree tree, @NotNull Object key) {
    if (tree.isEmpty()) {
        return false;
    } else {
        return RBTreeMate.delete(tree, key);
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
        Few node = (Few) car(path);
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
        Few node = (Few) car(path);
        return RBTreeMate.key(node);
    }
}

/**
 * Traverses the Red-Black tree and returns a list of key-value pairs.
 * The key-value pairs are ordered in ascending order of keys.
 *
 * @param tree the AVL tree to traverse.
 * @return a list of key-value pairs.
 */
public static Lot travel(@NotNull RBTree tree) {
    RBTreeMate.Traveling inst = new RBTreeMate.Traveling();
    return inst.process(tree.root());
}

/**
 * Filters the key-value pairs in the Red-Black tree with the given predicate.
 *
 * @param fn   the predicate to filter.
 * @param tree the Red-Black tree to filter.
 * @return a filtered Red-Black tree.
 */
public static RBTree filter(Predicate1 fn, @NotNull RBTree tree) {
    RBTreeMate.Filtering inst = new RBTreeMate.Filtering(fn);
    return inst.process(tree);
}

/**
 * Maps the key-value pairs in the Red-Black tree with the given procedure.
 *
 * @param fn   the procedure to modify value.
 * @param tree the Red-Black tree to map.
 * @return a mapped Red-Black tree.
 */
public static RBTree map(Do1 fn, @NotNull RBTree tree) {
    RBTreeMate.Mapping inst = new RBTreeMate.Mapping(fn, tree);
    return inst.process(tree.root());
}
}

