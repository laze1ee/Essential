/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import essential.functional.Do1;
import essential.functional.Predicate1;
import essential.progresive.Few;
import essential.progresive.Lot;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static essential.progresive.Pr.*;


@SuppressWarnings("DuplicatedCode")
class AVLTreeMate {

@Contract(value = " -> new", pure = true)
static @NotNull Few makeNode() {
    return few(false, false, 0, 0, false, false);
}

static @NotNull Object key(Few node) {return ref0(node);}

static void setKey(Few node, Object key) {set0(node, key);}

static @NotNull Object value(Few node) {return ref1(node);}

static void setValue(Few node, Object value) {set1(node, value);}

static int height(Few node) {return (int) ref2(node);}

static void setHeight(Few node, int height) {set2(node, height);}

static int balance(Few node) {return (int) ref3(node);}

static void setBalance(Few node, int balance) {set3(node, balance);}

static @NotNull Object left(Few node) {return ref4(node);}

static void setLeft(Few node, Object left) {set4(node, left);}

static @NotNull Object right(Few node) {return ref5(node);}

static void setRight(Few node, Object right) {set5(node, right);}

static boolean isLeftOf(Few node, Few parent) {return eq(node, left(parent));}

static boolean isRightOf(Few node, Few parent) {return eq(node, right(parent));}

static boolean isNil(Few node) {return key(node) instanceof Boolean;}

static @NotNull String stringify(Few node) {
    if (isNil(node)) {
        return "nil";
    } else {
        return String.format("(%s %s %s %s)", stringOf(key(node)), stringOf(value(node)),
                             stringify((Few) left(node)), stringify((Few) right(node)));
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

private static void leftRotate(AVLTree tree, Lot path) {
    Few x = (Few) car(path);
    Few up = (Few) right(x);
    if (balance(up) == -1) {
        rightRotate(tree, cons(up, path));
    } else {
        setRight(x, left(up));
        setLeft(up, x);
        reconnect(tree, path, x, up);
    }
}

private static void rightRotate(AVLTree tree, Lot path) {
    Few x = (Few) car(path);
    Few up = (Few) left(x);
    if (balance(up) == 1) {
        leftRotate(tree, cons(up, path));
    } else {
        setLeft(x, right(up));
        setRight(up, x);
        reconnect(tree, path, x, up);
    }
}

private static void reconnect(AVLTree tree, Lot path, Few x, Few up) {
    if (cdr(path).isEmpty()) {
        tree.setRoot(up);
    } else {
        Few parent = (Few) car1(path);
        if (isLeftOf(x, parent)) {
            setLeft(parent, up);
        } else {
            setRight(parent, up);
        }
    }
    path = cons(x, cons(up, cdr(path)));
    update(tree, path);
}

static void update(AVLTree tree, @NotNull Lot path) {
    while (!path.isEmpty()) {
        Few node = (Few) car(path);
        int b = balance((Few) right(node)) - balance((Few) left(node));
        if (b == 2) {
            leftRotate(tree, path);
            return;
        } else if (b == -2) {
            rightRotate(tree, path);
            return;
        } else {
            setHeight(node, 1 + Math.max(height((Few) left(node)), height((Few) right(node))));
            setBalance(node, b);
            path = cdr(path);
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
    if (cdr(path).isEmpty()) {
        tree.setRoot(node);
    } else {
        Few parent = (Few) car1(path);
        if (isLeftOf((Few) car(path), parent)) {
            setLeft(parent, node);
        } else {
            setRight(parent, node);
        }
    }
}

static boolean delete(@NotNull AVLTree tree, Object key) {
    Lot path = pathOf(tree, key);
    Few deleted = (Few) car(path);
    if (isNil(deleted)) {
        return false;
    } else {
        Few x;
        if (isNil((Few) left(deleted))) {
            x = (Few) right(deleted);
            transplant(tree, path, x);
            path = cdr(path);
        } else if (isNil((Few) right(deleted))) {
            x = (Few) left(deleted);
            transplant(tree, path, x);
            path = cdr(path);
        } else {
            Lot min_path = minimum((Few) right(deleted), lot());
            Few replace = (Few) car(min_path);
            x = (Few) right(replace);
            if (!isRightOf(replace, deleted)) {
                transplant(tree, min_path, x);
                setRight(replace, right(deleted));
            }
            transplant(tree, path, replace);
            setLeft(replace, left(deleted));
            path = append(cdr(min_path), cons(replace, cdr(path)));
        }
        if (isNil(x)) {
            update(tree, path);
        } else {
            update(tree, cons(x, path));
        }
        return true;
    }
}

static class Counting {

    private int size;

    Counting() {
        size = 0;
    }

    int process(Few node) {
        if (isNil(node)) {
            return 0;
        } else {
            Queue que = new Queue(node);
            while (!que.isEmpty()) {
                node = (Few) Queue.deQ(que);
                size += 1;
                Few left = (Few) left(node);
                if (!isNil(left)) {
                    Queue.enQ(que, left);
                }
                Few right = (Few) right(node);
                if (!isNil(right)) {
                    Queue.enQ(que, right);
                }
            }
            return size;
        }
    }
}

static class Traveling {

    private Lot col;

    Traveling() {
        col = lot();
    }

    Lot process(Few node) {
        if (isNil(node)) {return col;}
        job(node);
        return col;
    }

    private void job(Few node) {
        Lot stack = lot(node);
        node = (Few) right(node);
        while (!stack.isEmpty()) {
            while (!isNil(node)) {
                stack = cons(node, stack);
                node = (Few) right(node);
            }
            node = (Few) car(stack);
            col = cons(lot(key(node), value(node)), col);
            stack = cdr(stack);
            node = (Few) left(node);
            while (!isNil(node)) {
                stack = cons(node, stack);
                node = (Few) right(node);
            }
        }
    }
}

static class Filtering {

    private final Predicate1 fn;

    Filtering(Predicate1 fn) {
        this.fn = fn;
    }

    AVLTree process(@NotNull AVLTree tree) {
        AVLTree new_tree = new AVLTree(tree.less(), tree.greater());
        if (tree.isEmpty()) {return new_tree;}

        Queue que = new Queue(tree.root());
        Few node;
        while (!que.isEmpty()) {
            node = (Few) Queue.deQ(que);
            if (fn.apply(value(node))) {
                AVLTree.insert(new_tree, key(node), value(node));
            }
            Few left = (Few) left(node);
            if (!isNil(left)) {
                Queue.enQ(que, left);
            }
            Few right = (Few) right(node);
            if (!isNil(right)) {
                Queue.enQ(que, right);
            }
        }
        return new_tree;
    }
}

static class Mapping {

    private final AVLTree tree;
    private final Do1 fn;

    Mapping(Do1 fn, @NotNull AVLTree tree) {
        this.tree = new AVLTree(tree.less(), tree.greater());
        this.fn = fn;
    }

    AVLTree process(Few node) {
        if (isNil(node)) {return tree;}

        Queue que = new Queue(node);
        while (!que.isEmpty()) {
            node = (Few) Queue.deQ(que);
            AVLTree.insert(tree, key(node), fn.apply(value(node)));
            Few left = (Few) left(node);
            if (!isNil(left)) {
                Queue.enQ(que, left);
            }
            Few right = (Few) right(node);
            if (!isNil(right)) {
                Queue.enQ(que, right);
            }
        }
        return tree;
    }
}
}
