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

static @NotNull Object key(Few node) {return ref0(node);}

static void setKey(Few node, Object key) {set0(node, key);}

static @NotNull Object value(Few node) {return ref1(node);}

static void setValue(Few node, Object value) {set1(node, value);}

static boolean color(Few node) {return (boolean) ref2(node);}

private static boolean isRed(Few node) {return color(node);}

private static boolean isBlack(Few node) {return !color(node);}

static void setColor(Few node, boolean color) {set2(node, color);}

static @NotNull Object left(Few node) {return ref3(node);}

static void setLeft(Few node, Object left) {set3(node, left);}

static @NotNull Object right(Few node) {return ref4(node);}

static void setRight(Few node, Object right) {set4(node, right);}

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

private static void leftRotate(RBTree tree, Lot path) {
    Few x = (Few) car(path);
    Few up = (Few) right(x);
    setRight(x, left(up));
    setLeft(up, x);

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
}

private static void rightRotate(RBTree tree, Lot path) {
    Few x = (Few) car(path);
    Few up = (Few) left(x);
    setLeft(x, right(up));
    setRight(up, x);

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
        job(path);
        setColor(tree.root(), false);
    }

    private void job(@NotNull Lot path) {
        if (2 < path.length() && isRed((Few) car1(path))) {
            Few p = (Few) car1(path);
            Few pp = (Few) car2(path);
            if (isLeftOf(p, pp)) {
                Few u = (Few) right(pp);
                if (isRed(u)) {
                    setColor(p, false);
                    setColor(u, false);
                    setColor(pp, true);
                    job(cddr(path));
                } else {
                    if (isRightOf((Few) car(path), p)) {
                        leftRotate(tree, cdr(path));
                        p = (Few) car(path);
                    }
                    rightRotate(tree, cddr(path));
                    setColor(p, false);
                    setColor(pp, true);
                }
            } else {
                Few u = (Few) left(pp);
                if (isRed(u)) {
                    setColor(p, false);
                    setColor(u, false);
                    setColor(pp, true);
                    job(cddr(path));
                } else {
                    if (isLeftOf((Few) car(path), p)) {
                        rightRotate(tree, cdr(path));
                        p = (Few) car(path);
                    }
                    leftRotate(tree, cddr(path));
                    setColor(p, false);
                    setColor(pp, true);
                }
            }
        }
    }
}


private static void transplant(RBTree tree, @NotNull Lot path, Few node) {
    if (1 == path.length()) {
        tree.setRoot(node);
    } else {
        Few p = (Few) car1(path);
        if (isLeftOf((Few) car(path), p)) {
            setLeft(p, node);
        } else {
            setRight(p, node);
        }
    }
}

static boolean delete(@NotNull RBTree tree, Object key) {
    Lot path = pathOf(tree, key);
    Few deleted = (Few) car(path);
    if (isNil(deleted)) {
        return false;
    } else {
        boolean color = color(deleted);
        Few x;
        if (isNil((Few) left(deleted))) {
            x = (Few) right(deleted);
            transplant(tree, path, x);
            path = cons(x, cdr(path));
        } else if (isNil((Few) right(deleted))) {
            x = (Few) left(deleted);
            transplant(tree, path, x);
            path = cons(x, cdr(path));
        } else {
            Lot min_path = minimum((Few) right(deleted), lot());
            Few replace = (Few) car(min_path);
            color = color(replace);
            x = (Few) right(replace);
            if (!isRightOf(replace, deleted)) {
                transplant(tree, min_path, x);
                setRight(replace, right(deleted));
            }
            transplant(tree, path, replace);
            setLeft(replace, left(deleted));
            setColor(replace, color(deleted));
            path = append(cons(x, cdr(min_path)), cons(replace, cdr(path)));
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
        x = (Few) car(path);
        job(cdr(path));
        setColor(x, false);
    }

    void job(@NotNull Lot path) {
        if (!path.isEmpty() && isBlack(x)) {
            Few p = (Few) car(path);
            if (isLeftOf(x, p)) {
                Few s = (Few) right(p);
                if (isRed(s)) {
                    leftRotate(tree, path);
                    setColor(s, false);
                    setColor(p, true);
                    path = cons(p, cons(s, cdr(path)));
                    s = (Few) right(p);
                }
                if (isBlack((Few) left(s)) && isBlack((Few) right(s))) {
                    setColor(s, true);
                    x = p;
                    job(cdr(path));
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
                }
            } else {
                Few s = (Few) left(p);
                if (isRed(s)) {
                    rightRotate(tree, path);
                    setColor(s, false);
                    setColor(p, true);
                    path = cons(p, cons(s, cdr(path)));
                    s = (Few) left(p);
                }
                if (isBlack((Few) left(s)) && isBlack((Few) right(s))) {
                    setColor(s, true);
                    x = p;
                    job(cdr(path));
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

    private void job(@NotNull Few node) {
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

    RBTree process(@NotNull RBTree tree) {
        RBTree new_tree = new RBTree(tree.less(), tree.greater());
        if (tree.isEmpty()) {return new_tree;}

        Queue que = new Queue(tree.root());
        Few node;
        while (!que.isEmpty()) {
            node = (Few) Queue.deQ(que);
            if (fn.apply(value(node))) {
                RBTree.insert(new_tree, key(node), value(node));
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

    private final RBTree tree;
    private final Do1 fn;

    Mapping(Do1 fn, @NotNull RBTree tree) {
        this.fn = fn;
        this.tree = new RBTree(tree.less(), tree.greater());
    }

    RBTree process(Few node) {
        if (isNil(node)) {return tree;}

        Queue que = new Queue(node);
        while (!que.isEmpty()) {
            node = (Few) Queue.deQ(que);
            RBTree.insert(tree, key(node), fn.apply(value(node)));
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
