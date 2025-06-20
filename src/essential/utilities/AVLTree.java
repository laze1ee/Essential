/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import essential.functional.Do1;
import essential.functional.Predicate1;
import essential.functional.Predicate2;
import essential.progressive.Few;
import essential.progressive.Lot;
import org.jetbrains.annotations.NotNull;


public class AVLTree {

private final Predicate2 less;
private final Predicate2 greater;

private Few root;

public AVLTree(Predicate2 less, Predicate2 greater) {
  this.less = less;
  this.greater = greater;
  this.root = AVLTreeMate.makeNode();
}

Predicate2 less() {
  return less;
}

Predicate2 greater() {
  return greater;
}

public Few root() {
  return root;
}

void _setRoot(Few node) {
  this.root = node;
}

public void setRoot(Few node) {
  if (AVLTreeMate.isValidNode(node)) {
    root = node;
  }
  else {
    String msg = String.format(Msg.INVALID_AVL_NODE, node);
    throw new RuntimeException(msg);
  }
}

@Override
public boolean equals(Object datum) {
  if (datum instanceof AVLTree tree) {
    return root().equals(tree.root());
  }
  else {
    return false;
  }
}

@Override
public String toString() {
  return String.format("«AVL-Tree %s»", AVLTreeMate.toString(root));
}

public boolean isValidNode() {
  return AVLTreeMate.isValidNode(root);
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
  return AVLTreeMate.size(root);
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
  }
  else {
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
    String msg = String.format(Msg.NOT_PRESENT, key, this);
    throw new RuntimeException(msg);
  }
  else {
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
    String msg = String.format(Msg.NOT_PRESENT, key, this);
    throw new RuntimeException(msg);
  }
  else {
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
  }
  else {
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
  Lot path = AVLTreeMate.minimum(root(), Lot.of());
  if (path.isEmpty()) {
    throw new RuntimeException(Msg.EMPTY_TREE);
  }
  else {
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
  Lot path = AVLTreeMate.maximum(root(), Lot.of());
  if (path.isEmpty()) {
    throw new RuntimeException(Msg.EMPTY_TREE);
  }
  else {
    Few node = (Few) path.car();
    return AVLTreeMate.key(node);
  }
}

/**
 * Creates a copy of the current AVL tree.
 * @return a new AVL tree that is a copy of the current tree.
 */
public AVLTree copy() {
  return AVLTreeMate.copy(this);
}

/**
 * Traverses the AVL tree and returns a list of key-value pairs.
 * The key-value pairs are ordered in ascending order of keys.
 *
 * @return a list of key-value pairs.
 */
public Lot travel() {
  return AVLTreeMate.travel(root);
}

/**
 * Filters the key-value pairs in the AVL tree with the given predicate.
 *
 * @param fn the predicate to filter.
 * @return a filtered AVL tree.
 */
public AVLTree filter(Predicate1 fn) {
  return AVLTreeMate.filter(fn, this);
}

/**
 * Maps the key-value pairs in the AVL tree with the given procedure.
 *
 * @param fn the procedure to modify value.
 * @return a mapped AVL tree.
 */
public AVLTree map(Do1 fn) {
  return AVLTreeMate.map(fn, this);
}

/**
 * This method analyzes the structure of the AVL tree and provides statistics about the depths of its
 * leaf nodes. The result is a Lot of the form {@code (depth count)}
 *
 * @return A Lot containing depth statistics, where each element is a Lot of the form {@code (depth count)}
 */
public Lot depthStatistic() {
  return AVLTreeMate.depthStatistic(root);
}
}
