package essential.utilities;

import org.jetbrains.annotations.NotNull;
import essential.functional.IsWithOne;
import essential.progresive.Lot;

import static essential.progresive.Pr.*;


class RBTreeMate {

static @NotNull Lot pathOf(@NotNull RBTree tree, Object key) {
    RBNode node = tree.root;
    Lot path = lot();
    while (!node.isNil()) {
        if (tree.less.apply(key, node.key)) {
            path = cons(node, path);
            node = node.left;
        } else if (tree.greater.apply(key, node.key)) {
            path = cons(node, path);
            node = node.right;
        } else {
            return cons(node, path);
        }
    }
    return cons(node, path);
}

private static boolean isLeftOf(Object node1, @NotNull RBNode node2) {
    return eq(node1, node2.left);
}

private static boolean isRightOf(Object node1, @NotNull RBNode node2) {
    return eq(node1, node2.right);
}

private static void leftRotate(RBTree tree, Lot path) {
    RBNode x = (RBNode) car(path);
    RBNode up = x.right;
    x.right = up.left;
    up.left = x;

    if (cdr(path).isEmpty()) {
        tree.root = up;
    } else {
        RBNode p = (RBNode) car1(path);
        if (isLeftOf(x, p)) {
            p.left = up;
        } else {
            p.right = up;
        }
    }
}

private static void rightRotate(RBTree tree, Lot path) {
    RBNode x = (RBNode) car(path);
    RBNode up = x.left;
    x.left = up.right;
    up.right = x;

    if (cdr(path).isEmpty()) {
        tree.root = up;
    } else {
        RBNode p = (RBNode) car1(path);
        if (isLeftOf(x, p)) {
            p.left = up;
        } else {
            p.right = up;
        }
    }
}


static Lot minimum(@NotNull RBNode node, Lot path) {
    while (!node.isNil()) {
        path = cons(node, path);
        node = node.left;
    }
    return path;
}

static Lot maximum(@NotNull RBNode node, Lot path) {
    while (!node.isNil()) {
        path = cons(node, path);
        node = node.right;
    }
    return path;
}


record InsertFixing(RBTree tree, Lot path) {

    void process() {
        job(path);
        tree.root.color = false;
    }

    private void job(@NotNull Lot path) {
        if (2 < path.length() && ((RBNode) car1(path)).isRed()) {
            RBNode p = (RBNode) car1(path);
            RBNode pp = (RBNode) car2(path);
            if (isLeftOf(p, pp)) {
                RBNode u = pp.right;
                if (u.isRed()) {
                    p.color = false;
                    u.color = false;
                    pp.color = true;
                    job(cddr(path));
                } else {
                    if (isRightOf(car(path), p)) {
                        leftRotate(tree, cdr(path));
                        p = (RBNode) car(path);
                    }
                    rightRotate(tree, cddr(path));
                    p.color = false;
                    pp.color = true;
                }
            } else {
                RBNode u = pp.left;
                if (u.isRed()) {
                    p.color = false;
                    u.color = false;
                    pp.color = true;
                    job(cddr(path));
                } else {
                    if (isLeftOf(car(path), p)) {
                        rightRotate(tree, cdr(path));
                        p = (RBNode) car(path);
                    }
                    leftRotate(tree, cddr(path));
                    p.color = false;
                    pp.color = true;
                }
            }
        }
    }
}


private static void transplant(RBTree tree, @NotNull Lot path, RBNode node) {
    if (1 == path.length()) {
        tree.root = node;
    } else {
        RBNode p = (RBNode) car1(path);
        if (isLeftOf(car(path), p)) {
            p.left = node;
        } else {
            p.right = node;
        }
    }
}

static boolean delete(@NotNull RBTree tree, Object key) {
    Lot path = pathOf(tree, key);
    RBNode deleted = (RBNode) car(path);
    if (deleted.isNil()) {
        return false;
    } else {
        boolean color = deleted.color;
        RBNode x;
        if (deleted.left.isNil()) {
            x = deleted.right;
            transplant(tree, path, x);
            path = cons(x, cdr(path));
        } else if (deleted.right.isNil()) {
            x = deleted.left;
            transplant(tree, path, x);
            path = cons(x, cdr(path));
        } else {
            Lot min_path = minimum(deleted.right, lot());
            RBNode replace = (RBNode) car(min_path);
            color = replace.color;
            x = replace.right;
            if (!isRightOf(replace, deleted)) {
                transplant(tree, min_path, x);
                replace.right = deleted.right;
            }
            transplant(tree, path, replace);
            replace.left = deleted.left;
            replace.color = deleted.color;
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
    RBNode x;

    DeleteFixing(RBTree tree, Lot path) {
        this.tree = tree;
        this.path = path;
    }

    void process() {
        x = (RBNode) car(path);
        job(cdr(path));
        x.color = false;
    }

    void job(@NotNull Lot path) {
        if (!path.isEmpty() && x.isBlack()) {
            RBNode p = (RBNode) car(path);
            if (isLeftOf(x, p)) {
                RBNode s = p.right;
                if (s.isRed()) {
                    leftRotate(tree, path);
                    s.color = false;
                    p.color = true;
                    path = cons(p, cons(s, cdr(path)));
                    s = p.right;
                }
                if (s.left.isBlack() && s.right.isBlack()) {
                    s.color = true;
                    x = p;
                    job(cdr(path));
                } else {
                    if (s.right.isBlack()) {
                        rightRotate(tree, cons(s, path));
                        s.color = true;
                        p.right.color = false;
                        s = p.right;
                    }
                    leftRotate(tree, path);
                    s.color = p.color;
                    p.color = false;
                    s.right.color = false;
                }
            } else {
                RBNode s = p.left;
                if (s.isRed()) {
                    rightRotate(tree, path);
                    s.color = false;
                    p.color = true;
                    path = cons(p, cons(s, cdr(path)));
                    s = p.left;
                }
                if (s.left.isBlack() && s.right.isBlack()) {
                    s.color = true;
                    x = p;
                    job(cdr(path));
                } else {
                    if (s.left.isBlack()) {
                        leftRotate(tree, cons(s, path));
                        s.color = true;
                        p.left.color = false;
                        s = p.left;
                    }
                    rightRotate(tree, path);
                    s.color = p.color;
                    p.color = false;
                    s.left.color = false;
                }
            }
        }
    }
}


static class Traveling {

    private Lot col;

    Traveling() {
        col = lot();
    }

    Lot process(RBNode node) {
        job(node);
        return col;
    }

    private void job(@NotNull RBNode node) {
        Lot acc = lot(node);
        node = node.right;
        while (!acc.isEmpty()) {
            while (!node.isNil()) {
                acc = cons(node, acc);
                node = node.right;
            }
            node = (RBNode) car(acc);
            col = cons(lot(node.key, node.value), col);
            acc = cdr(acc);
            node = node.left;
            while (!node.isNil()) {
                acc = cons(node, acc);
                node = node.right;
            }
        }
    }
}

static class Filtering {

    private Lot col;
    private final IsWithOne fn;

    Filtering(IsWithOne fn) {
        col = lot();
        this.fn = fn;
    }

    Lot process(RBNode node) {
        job(node);
        return col;
    }

    private void job(@NotNull RBNode node) {
        Lot acc = lot(node);
        node = node.right;
        while (!acc.isEmpty()) {
            while (!node.isNil()) {
                acc = cons(node, acc);
                node = node.right;
            }
            node = (RBNode) car(acc);
            if (fn.apply(node.value)) {
                col = cons(lot(node.key, node.value), col);
            }
            acc = cdr(acc);
            node = node.left;
            while (!node.isNil()) {
                acc = cons(node, acc);
                node = node.right;
            }
        }
    }
}
}
