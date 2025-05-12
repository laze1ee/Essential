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
    return few(false, false, 0, 0, false, false);
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

static @NotNull String _stringOf(Few node) {
    if (isNil(node)) {
        return "nil";
    } else {
        return String.format("(%s %s %s %s)", stringOf(key(node)), stringOf(value(node)),
                             _stringOf((Few) left(node)), _stringOf((Few) right(node)));
    }
}

static @NotNull Lot pathOf(@NotNull AVLTree tree, Object key) {
    Few node = tree.root();
    Lot path = lot();
    while (!isNil(node)) {
        if (tree.less().apply(key, key(node))) {
            path = cons(node, path);
            node = (Few) left(node);
        } else if (tree.greater().apply(key, key(node))) {
            path = cons(node, path);
            node = (Few) right(node);
        } else {
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
    } else {
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
    } else {
        setLeft(x, right(up));
        setRight(up, x);
        reconnect(tree, path, x, up);
    }
}

private static void reconnect(AVLTree tree, @NotNull Lot path, Few x, Few up) {
    if (path.cdr().isEmpty()) {
        tree.setRoot(up);
    } else {
        Few parent = (Few) path.ref(1);
        if (isLeftOf(x, parent)) {
            setLeft(parent, up);
        } else {
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
        } else if (b == -2) {
            rightRotate(tree, path);
            return;
        } else {
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
        tree.setRoot(node);
    } else {
        Few parent = (Few) path.ref(1);
        if (isLeftOf((Few) path.car(), parent)) {
            setLeft(parent, node);
        } else {
            setRight(parent, node);
        }
    }
}

static boolean delete(@NotNull AVLTree tree, Object key) {
    Lot path = pathOf(tree, key);
    Few deleted = (Few) path.car();
    if (isNil(deleted)) {
        return false;
    } else {
        Few x;
        if (isNil((Few) left(deleted))) {
            x = (Few) right(deleted);
            transplant(tree, path, x);
            path = path.cdr();
        } else if (isNil((Few) right(deleted))) {
            x = (Few) left(deleted);
            transplant(tree, path, x);
            path = path.cdr();
        } else {
            Lot min_path = minimum((Few) right(deleted), lot());
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
        } else {
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

static Lot travel(Few root) {
    Lot col = lot();
    Lot stack = lot();
    Few node = root;
    while (!isNil(node)) {
        stack = cons(node, stack);
        node = (Few) right(node);
    }
    while (!stack.isEmpty()) {
        node = (Few) stack.car();
        col = cons(lot(key(node), value(node)), col);
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
    Lot col = lot();
    Queue ooo = new Queue(root);
    Queue xxx = new Queue(1);
    int depth = 0;
    int count = 0;
    while (!ooo.isEmpty()) {
        Few node = (Few) ooo.dequeue();
        int n = (int) xxx.dequeue();
        if (!isNil(node)) {
            if (isNil((Few) left(node)) && isNil((Few) right(node))) {
                if (depth == 0) {
                    depth = n;
                    count = 1;
                } else if (depth == n) {
                    count += 1;
                } else {
                    col = cons(lot(depth, count), col);
                    depth = n;
                    count = 1;
                }
            }
            ooo.enqueue(left(node));
            ooo.enqueue(right(node));
            xxx.enqueue(n + 1);
            xxx.enqueue(n + 1);
        }
    }
    return col;
}
}
