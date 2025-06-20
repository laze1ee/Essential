/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import essential.functional.Do1;
import essential.functional.Predicate1;
import essential.progressive.Few;
import essential.progressive.Lot;
import org.jetbrains.annotations.NotNull;

import static essential.progressive.Pr.*;


@SuppressWarnings("DuplicatedCode")
class AVLTreeMate {

static @NotNull Few makeNode() {
  return Few.of(false, false, 0, 0, false, false);
}

static @NotNull Object key(@NotNull Few node) {
  return node.ref(0);
}

static void setKey(@NotNull Few node, Object key) {
  node.set(0, key);
}

static @NotNull Object value(@NotNull Few node) {
  return node.ref(1);
}

static void setValue(@NotNull Few node, Object value) {
  node.set(1, value);
}

static int height(@NotNull Few node) {
  return (int) node.ref(2);
}

static void setHeight(@NotNull Few node, int height) {
  node.set(2, height);
}

static int balance(@NotNull Few node) {
  return (int) node.ref(3);
}

static void setBalance(@NotNull Few node, int balance) {
  node.set(3, balance);
}

static @NotNull Object left(@NotNull Few node) {
  return node.ref(4);
}

static void setLeft(@NotNull Few node, Object left) {
  node.set(4, left);
}

static @NotNull Object right(@NotNull Few node) {
  return node.ref(5);
}

static void setRight(@NotNull Few node, Object right) {
  node.set(5, right);
}

static boolean isLeftOf(Few node, Few parent) {
  return eq(node, left(parent));
}

static boolean isRightOf(Few node, Few parent) {
  return eq(node, right(parent));
}

static boolean isNil(Few node) {
  return key(node) instanceof Boolean;
}

static boolean isValidNode(Few node) {
  NodeChecker checker = new NodeChecker(node);
  checker.route();
  return checker.r0;
}

private static class NodeChecker {
  private Few cont;
  private Few node;
  private boolean r0;

  private NodeChecker(Few node) {
    cont = Few.of(Label.END_CONT);
    this.node = node;
  }

  private void route() {
    //noinspection DuplicatedCode
    String next = Label.OF_NODE;
    while (true) {
      switch (next) {
        case Label.OF_NODE -> next = ofNode();
        case Label.APPLY_CONT -> next = applyCont();
        case Label.EXIT -> {return;}
      }
    }
  }

  private String ofNode() {
    if (node.length() == 6 &&
        node.ref(2) instanceof Integer &&
        node.ref(3) instanceof Integer) {
      if (node.ref(4) instanceof Few &&
          node.ref(5) instanceof Few) {
        cont = Few.of(Label.RIGHT_NODE, cont, node.ref(5));
        node = (Few) node.ref(4);
        return Label.OF_NODE;
      }
      else if (node.ref(4) instanceof Few &&
               node.ref(5) instanceof Boolean) {
        node = (Few) node.ref(4);
        return Label.OF_NODE;
      }
      else if (node.ref(4) instanceof Boolean &&
               node.ref(5) instanceof Few) {
        node = (Few) node.ref(5);
        return Label.OF_NODE;
      }
      else {
        r0 = node.ref(4) instanceof Boolean &&
             node.ref(5) instanceof Boolean;
        return Label.APPLY_CONT;
      }
    }
    else {
      r0 = false;
      return Label.APPLY_CONT;
    }
  }

  private String applyCont() {
    String label = (String) cont.ref(0);

    switch (label) {
      case Label.END_CONT -> {return Label.EXIT;}
      case Label.RIGHT_NODE -> {
        if (r0) {
          node = (Few) cont.ref(2);
          cont = (Few) cont.ref(1);
          return Label.OF_NODE;
        }
        else {
          cont = (Few) cont.ref(1);
          return Label.APPLY_CONT;
        }
      }
      default -> throw new RuntimeException("undefined continuation " + label);
    }
  }
}

static @NotNull String toString(Few node) {
  ToString inst = new ToString(node);
  inst.route();
  return inst.builder.toString();
}

private static class ToString {
  private Few cont;
  private Few node;
  private final StringBuilder builder;

  private ToString(Few node) {
    cont = Few.of(Label.END_CONT);
    this.node = node;
    builder = new StringBuilder();
  }

  private void route() {
    //noinspection DuplicatedCode
    String next = Label.OF_NODE;
    while (true) {
      switch (next) {
        case Label.OF_NODE -> next = ofNode();
        case Label.APPLY_CONT -> next = applyCont();
        case Label.EXIT -> {return;}
      }
    }
  }

  private String ofNode() {
    if (isNil(node)) {
      builder.append("nil");
      return Label.APPLY_CONT;
    }
    else {
      builder.append("(");
      builder.append(stringOf(key(node)));
      builder.append(" ");
      builder.append(stringOf(value(node)));
      builder.append(" ");
      cont = Few.of(Label.RIGHT_NODE, cont, right(node));
      node = (Few) left(node);
      return Label.OF_NODE;
    }
  }

  private String applyCont() {
    String label = (String) cont.ref(0);

    switch (label) {
      case Label.END_CONT -> {return Label.EXIT;}
      case Label.RIGHT_NODE -> {
        builder.append(" ");
        node = (Few) cont.ref(2);
        cont = Few.of(Label.END_NODE, cont.ref(1));
        return Label.OF_NODE;
      }
      case Label.END_NODE -> {
        builder.append(")");
        cont = (Few) cont.ref(1);
        return Label.APPLY_CONT;
      }
      default -> throw new RuntimeException("undefined continuation " + label);
    }
  }
}

static @NotNull Lot pathOf(@NotNull AVLTree tree, Object key) {
  Few node = tree.root();
  Lot path = Lot.of();
  while (!isNil(node)) {
    if (tree.less().apply(key, key(node))) {
      path = cons(node, path);
      node = (Few) left(node);
    }
    else if (tree.greater().apply(key, key(node))) {
      path = cons(node, path);
      node = (Few) right(node);
    }
    else {
      return cons(node, path);
    }
  }
  return cons(node, path);
}

private static void leftRotate(AVLTree tree, @NotNull Lot path) {
  Few x = (Few) path.car();
  Few up = (Few) right(x);
  if (balance(up) == -1) {
    rightRotate(tree, cons(up, path));
  }
  else {
    setRight(x, left(up));
    setLeft(up, x);
    reconnect(tree, path, x, up);
  }
}

private static void rightRotate(AVLTree tree, @NotNull Lot path) {
  Few x = (Few) path.car();
  Few up = (Few) left(x);
  if (balance(up) == 1) {
    leftRotate(tree, cons(up, path));
  }
  else {
    setLeft(x, right(up));
    setRight(up, x);
    reconnect(tree, path, x, up);
  }
}

private static void reconnect(AVLTree tree, @NotNull Lot path, Few x, Few up) {
  if (path.cdr().isEmpty()) {
    tree._setRoot(up);
  }
  else {
    Few parent = (Few) path.ref(1);
    if (isLeftOf(x, parent)) {
      setLeft(parent, up);
    }
    else {
      setRight(parent, up);
    }
  }
  path = cons(x, cons(up, path.cdr()));
  update(tree, path);
}

static void update(AVLTree tree, @NotNull Lot path) {
  while (!path.isEmpty()) {
    Few node = (Few) path.car();
    int b = height((Few) right(node)) - height((Few) left(node));
    if (b == 2) {
      leftRotate(tree, path);
      return;
    }
    else if (b == -2) {
      rightRotate(tree, path);
      return;
    }
    else {
      setHeight(node, 1 + Math.max(height((Few) left(node)), height((Few) right(node))));
      setBalance(node, b);
      path = path.cdr();
    }
  }
}

static Lot minimum(@NotNull Few node, Lot path) {
  while (!isNil(node)) {
    path = cons(node, path);
    node = (Few) left(node);
  }
  return path;
}

static Lot maximum(@NotNull Few node, Lot path) {
  while (!isNil(node)) {
    path = cons(node, path);
    node = (Few) right(node);
  }
  return path;
}

private static void transplant(AVLTree tree, @NotNull Lot path, Few node) {
  if (path.cdr().isEmpty()) {
    tree._setRoot(node);
  }
  else {
    Few parent = (Few) path.ref(1);
    if (isLeftOf((Few) path.car(), parent)) {
      setLeft(parent, node);
    }
    else {
      setRight(parent, node);
    }
  }
}

static boolean delete(@NotNull AVLTree tree, Object key) {
  Lot path = pathOf(tree, key);
  Few deleted = (Few) path.car();
  if (isNil(deleted)) {
    return false;
  }
  else {
    Few x;
    if (isNil((Few) left(deleted))) {
      x = (Few) right(deleted);
      transplant(tree, path, x);
      path = path.cdr();
    }
    else if (isNil((Few) right(deleted))) {
      x = (Few) left(deleted);
      transplant(tree, path, x);
      path = path.cdr();
    }
    else {
      Lot min_path = minimum((Few) right(deleted), Lot.of());
      Few replace = (Few) min_path.car();
      x = (Few) right(replace);
      if (!isRightOf(replace, deleted)) {
        transplant(tree, min_path, x);
        setRight(replace, right(deleted));
      }
      transplant(tree, path, replace);
      setLeft(replace, left(deleted));
      path = append(min_path.cdr(), cons(replace, path.cdr()));
    }
    if (isNil(x)) {
      update(tree, path);
    }
    else {
      update(tree, cons(x, path));
    }
    return true;
  }
}

static int size(Few root) {
  int count = 0;
  Queue que = new Queue(root);
  while (!que.isEmpty()) {
    Few node = (Few) que.dequeue();
    if (!isNil(node)) {
      count += 1;
      que.enqueue(left(node));
      que.enqueue(right(node));
    }
  }
  return count;
}

static @NotNull AVLTree copy(@NotNull AVLTree tree) {
  AVLTree new_tree = new AVLTree(tree.less(), tree.greater());
  Queue que = new Queue(tree.root());
  while (!que.isEmpty()) {
    Few node = (Few) que.dequeue();
    if (!isNil(node)) {
      new_tree.insert(key(node), value(node));
      que.enqueue(left(node));
      que.enqueue(right(node));
    }
  }
  return new_tree;
}

static Lot travel(Few root) {
  Lot col = Lot.of();
  Lot stack = Lot.of();
  Few node = root;
  while (!isNil(node)) {
    stack = cons(node, stack);
    node = (Few) right(node);
  }
  while (!stack.isEmpty()) {
    node = (Few) stack.car();
    col = cons(Lot.of(key(node), value(node)), col);
    stack = stack.cdr();
    node = (Few) left(node);
    while (!isNil(node)) {
      stack = cons(node, stack);
      node = (Few) right(node);
    }
  }
  return col;
}

static @NotNull AVLTree filter(Predicate1 fn, @NotNull AVLTree tree) {
  AVLTree new_tree = new AVLTree(tree.less(), tree.greater());
  Queue que = new Queue(tree.root());
  while (!que.isEmpty()) {
    Few node = (Few) que.dequeue();
    if (!isNil(node)) {
      if (fn.apply(value(node))) {
        new_tree.insert(key(node), value(node));
      }
      que.enqueue(left(node));
      que.enqueue(right(node));
    }
  }
  return new_tree;
}

static @NotNull AVLTree map(Do1 fn, @NotNull AVLTree tree) {
  AVLTree new_tree = new AVLTree(tree.less(), tree.greater());
  Queue que = new Queue(tree.root());
  while (!que.isEmpty()) {
    Few node = (Few) que.dequeue();
    if (!isNil(node)) {
      new_tree.insert(key(node), fn.apply(value(node)));
      que.enqueue(left(node));
      que.enqueue(right(node));
    }
  }
  return new_tree;
}

static Lot depthStatistic(Few root) {
  Lot col = Lot.of();
  Queue lll = new Queue(root);
  Queue xxx = new Queue(1);
  int depth = 0;
  int count = 0;
  while (!lll.isEmpty()) {
    Few node = (Few) lll.dequeue();
    int n = (int) xxx.dequeue();
    if (!isNil(node)) {
      if (isNil((Few) left(node)) && isNil((Few) right(node))) {
        if (depth == 0) {
          depth = n;
          count = 1;
        }
        else if (depth == n) {
          count += 1;
        }
        else {
          col = cons(Lot.of(depth, count), col);
          depth = n;
          count = 1;
        }
      }
      lll.enqueue(left(node));
      lll.enqueue(right(node));
      xxx.enqueue(n + 1);
      xxx.enqueue(n + 1);
    }
  }
  return col;
}
}
