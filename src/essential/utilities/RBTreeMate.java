/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import essential.functional.Do1;
import essential.progresive.Few;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import essential.functional.Predicate1;
import essential.progresive.Lot;

import static essential.progresive.Pr.*;


@SuppressWarnings("DuplicatedCode")
class RBTreeMate {

@Contract(value = " -> new", pure = true)
static @NotNull Few makeNode() {return few(false, false, false, false, false);}

static @NotNull Object key(@NotNull Few node) {return node.ref(0);}

static void setKey(@NotNull Few node, Object key) {node.set(0, key);}

static @NotNull Object value(@NotNull Few node) {return node.ref(1);}

static void setValue(@NotNull Few node, Object value) {node.set(1, value);}

static boolean color(@NotNull Few node) {return (boolean) node.ref(2);}

private static boolean isRed(Few node) {return color(node);}

private static boolean isBlack(Few node) {return !color(node);}

static void setColor(@NotNull Few node, boolean color) {node.set(2, color);}

static @NotNull Object left(@NotNull Few node) {return node.ref(3);}

static void setLeft(@NotNull Few node, Object left) {node.set(3, left);}

static @NotNull Object right(@NotNull Few node) {return node.ref(4);}

static void setRight(@NotNull Few node, Object right) {node.set(4, right);}

static boolean isLeftOf(Few node, Few parent) {return eq(node, left(parent));}

static boolean isRightOf(Few node, Few parent) {return eq(node, right(parent));}

static boolean isNil(Few node) {return key(node) instanceof Boolean;}

static @NotNull String _stringOf(Few node) {
    if (isNil(node)) {
        return "nil";
    } else {
        return String.format("(%s %s %s %s)", stringOf(key(node)), stringOf(value(node)),
                             _stringOf((Few) left(node)), _stringOf((Few) right(node)));
    }
}

static @NotNull Lot pathOf(@NotNull RBTree tree, Object key) {
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

private static void leftRotate(RBTree tree, @NotNull Lot path) {
    Few x = (Few) path.car();
    Few up = (Few) right(x);
    setRight(x, left(up));
    setLeft(up, x);

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
}

private static void rightRotate(RBTree tree, @NotNull Lot path) {
    Few x = (Few) path.car();
    Few up = (Few) left(x);
    setLeft(x, right(up));
    setRight(up, x);

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


record InsertFixing(RBTree tree, Lot path) {

    void process() {
        process(path);
        setColor(tree.root(), false);
    }

    private void process(@NotNull Lot path) {
        while (2 < path.length() && isRed((Few) path.ref(1))) {
            Few p = (Few) path.ref(1);
            Few pp = (Few) path.ref(2);
            if (isLeftOf(p, pp)) {
                Few u = (Few) right(pp);
                if (isRed(u)) {
                    setColor(p, false);
                    setColor(u, false);
                    setColor(pp, true);
                    path = path.cddr();
                } else {
                    if (isRightOf((Few) path.car(), p)) {
                        leftRotate(tree, path.cdr());
                        p = (Few) path.car();
                    }
                    rightRotate(tree, path.cddr());
                    setColor(p, false);
                    setColor(pp, true);
                    return;
                }
            } else {
                Few u = (Few) left(pp);
                if (isRed(u)) {
                    setColor(p, false);
                    setColor(u, false);
                    setColor(pp, true);
                    path = path.cddr();
                } else {
                    if (isLeftOf((Few) path.car(), p)) {
                        rightRotate(tree, path.cdr());
                        p = (Few) path.car();
                    }
                    leftRotate(tree, path.cddr());
                    setColor(p, false);
                    setColor(pp, true);
                    return;
                }
            }
        }
    }
}


private static void transplant(RBTree tree, @NotNull Lot path, Few node) {
    if (1 == path.length()) {
        tree.setRoot(node);
    } else {
        Few p = (Few) path.ref(1);
        if (isLeftOf((Few) path.car(), p)) {
            setLeft(p, node);
        } else {
            setRight(p, node);
        }
    }
}

static boolean delete(@NotNull RBTree tree, Object key) {
    Lot path = pathOf(tree, key);
    Few deleted = (Few) path.car();
    if (isNil(deleted)) {
        return false;
    } else {
        boolean color = color(deleted);
        Few x;
        if (isNil((Few) left(deleted))) {
            x = (Few) right(deleted);
            transplant(tree, path, x);
            path = cons(x, path.cdr());
        } else if (isNil((Few) right(deleted))) {
            x = (Few) left(deleted);
            transplant(tree, path, x);
            path = cons(x, path.cdr());
        } else {
            Lot min_path = minimum((Few) right(deleted), lot());
            Few replace = (Few) min_path.car();
            color = color(replace);
            x = (Few) right(replace);
            if (!isRightOf(replace, deleted)) {
                transplant(tree, min_path, x);
                setRight(replace, right(deleted));
            }
            transplant(tree, path, replace);
            setLeft(replace, left(deleted));
            setColor(replace, color(deleted));
            path = append(cons(x, min_path.cdr()), cons(replace, path.cdr()));
        }
        if (!color) {       // is the deleted color black?
            DeleteFixing fixer = new DeleteFixing(tree, path);
            fixer.process();
        }
        return true;
    }
}

private static class DeleteFixing {

    final RBTree tree;
    final Lot path;
    Few x;

    DeleteFixing(RBTree tree, Lot path) {
        this.tree = tree;
        this.path = path;
    }

    void process() {
        x = (Few) path.car();
        process(path.cdr());
        setColor(x, false);
    }

    void process(@NotNull Lot path) {
        while (!path.isEmpty() && isBlack(x)) {
            Few p = (Few) path.car();
            if (isLeftOf(x, p)) {
                Few s = (Few) right(p);
                if (isRed(s)) {
                    leftRotate(tree, path);
                    setColor(s, false);
                    setColor(p, true);
                    path = cons(p, cons(s, path.cdr()));
                    s = (Few) right(p);
                }
                if (isBlack((Few) left(s)) && isBlack((Few) right(s))) {
                    setColor(s, true);
                    x = p;
                    path = path.cdr();
                } else {
                    if (isBlack((Few) right(s))) {
                        rightRotate(tree, cons(s, path));
                        setColor(s, true);
                        setColor((Few) right(p), false);
                        s = (Few) right(p);
                    }
                    leftRotate(tree, path);
                    setColor(s, color(p));
                    setColor(p, false);
                    setColor((Few) right(s), false);
                    return;
                }
            } else {
                Few s = (Few) left(p);
                if (isRed(s)) {
                    rightRotate(tree, path);
                    setColor(s, false);
                    setColor(p, true);
                    path = cons(p, cons(s, path.cdr()));
                    s = (Few) left(p);
                }
                if (isBlack((Few) left(s)) && isBlack((Few) right(s))) {
                    setColor(s, true);
                    x = p;
                    path = path.cdr();
                } else {
                    if (isBlack((Few) left(s))) {
                        leftRotate(tree, cons(s, path));
                        setColor(s, true);
                        setColor((Few) left(p), false);
                        s = (Few) left(p);
                    }
                    rightRotate(tree, path);
                    setColor(s, color(p));
                    setColor(p, false);
                    setColor((Few) left(s), false);
                    return;
                }
            }
        }
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
                node = (Few) que.deQueue();
                size += 1;
                Few left = (Few) left(node);
                if (!isNil(left)) {
                    que.enQueue(left);
                }
                Few right = (Few) right(node);
                if (!isNil(right)) {
                    que.enQueue(right);
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
        if (isNil(node)) {
            return col;
        }
        job(node);
        return col;
    }

    private void job(@NotNull Few node) {
        Lot stack = lot(node);
        node = (Few) right(node);
        while (!stack.isEmpty()) {
            while (!isNil(node)) {
                stack = cons(node, stack);
                node = (Few) right(node);
            }
            node = (Few) stack.car();
            col = cons(lot(key(node), value(node)), col);
            stack = stack.cdr();
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

    RBTree process(@NotNull RBTree tree) {
        RBTree new_tree = new RBTree(tree.less(), tree.greater());
        if (tree.isEmpty()) {
            return new_tree;
        }

        Queue que = new Queue(tree.root());
        Few node;
        while (!que.isEmpty()) {
            node = (Few) que.deQueue();
            if (fn.apply(value(node))) {
                new_tree.insert(key(node), value(node));
            }
            Few left = (Few) left(node);
            if (!isNil(left)) {
                que.enQueue(left);
            }
            Few right = (Few) right(node);
            if (!isNil(right)) {
                que.enQueue(right);
            }
        }
        return new_tree;
    }
}

static class Mapping {

    private final RBTree tree;
    private final Do1 fn;

    Mapping(Do1 fn, @NotNull RBTree tree) {
        this.fn = fn;
        this.tree = new RBTree(tree.less(), tree.greater());
    }

    RBTree process(Few node) {
        if (isNil(node)) {
            return tree;
        }

        Queue que = new Queue(node);
        while (!que.isEmpty()) {
            node = (Few) que.deQueue();
            tree.insert(key(node), fn.apply(value(node)));
            Few left = (Few) left(node);
            if (!isNil(left)) {
                que.enQueue(left);
            }
            Few right = (Few) right(node);
            if (!isNil(right)) {
                que.enQueue(right);
            }
        }
        return tree;
    }
}

static Lot depthStatistic(Few root) {
    if (isNil(root)) {
        return lot();
    } else {
        Few node = root;
        Lot col = lot();
        int count = 0;
        int depth = 0;
        Queue ooo = new Queue(node);
        Queue xxx = new Queue(1);
        while (!ooo.isEmpty()) {
            node = (Few) ooo.deQueue();
            int n = (int) xxx.deQueue();
            Few left = (Few) left(node);
            Few right = (Few) right(node);
            if (isNil(right) && isNil(left)) {
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
            if (!isNil(left)) {
                ooo.enQueue(left);
                xxx.enQueue(n + 1);
            }
            if (!isNil(right)) {
                ooo.enQueue(right);
                xxx.enQueue(n + 1);
            }
        }
        return col;
    }
}
}
