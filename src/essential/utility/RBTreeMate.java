package essential.utility;

import org.jetbrains.annotations.NotNull;
import essential.functional.Has1;
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
    RBNode b = up.left;

    up.left = x;
    x.right = b;

    if (cdr(path).isEmpty()) {
        tree.root = up;
    } else {
        RBNode p = (RBNode) cadr(path);
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
    RBNode b = up.right;

    up.right = x;
    x.left = b;

    if (cdr(path).isEmpty()) {
        tree.root = up;
    } else {
        RBNode p = (RBNode) cadr(path);
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
        _job(path);
        tree.root.color = false;
    }

    private void _job(@NotNull Lot path) {
        if (2 < path.length() && ((RBNode) cadr(path)).isRed()) {
            RBNode p = (RBNode) cadr(path);
            RBNode pp = (RBNode) caddr(path);
            if (isLeftOf(p, pp)) {
                RBNode u = pp.right;
                if (u.isRed()) {
                    p.color = false;
                    u.color = false;
                    pp.color = true;
                    _job(cddr(path));
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
                    _job(cddr(path));
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
        RBNode p = (RBNode) cadr(path);
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
            RBNode alternate = (RBNode) car(min_path);
            color = alternate.color;
            x = alternate.right;
            if (!isRightOf(alternate, deleted)) {
                transplant(tree, min_path, x);
                alternate.right = deleted.right;
            }
            transplant(tree, path, alternate);
            alternate.left = deleted.left;
            alternate.color = deleted.color;
            path = append(cons(x, cdr(min_path)), cons(alternate, cdr(path)));
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
        _job(cdr(path));
        x.color = false;
    }

    private void _job(@NotNull Lot path) {
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
                    _job(cdr(path));
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
                    _job(cdr(path));
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

    Lot col;

    Traveling() {
        col = lot();
    }

    Lot process(RBNode node) {
        _job(node);
        return col;
    }

    private void _job(@NotNull RBNode node) {
        if (!node.isNil()) {
            _job(node.right);
            col = cons(lot(node.key, node.value), col);
            _job(node.left);
        }
    }
}

static class Filter {

    Lot col;
    final Has1 fn;

    Filter(Has1 fn) {
        col = lot();
        this.fn = fn;
    }

    Lot process(RBNode node) {
        _job(node);
        return col;
    }

    private void _job(@NotNull RBNode node) {
        if (!node.isNil()) {
            _job(node.right);
            if (fn.apply(node.value)) {
                col = cons(lot(node.key, node.value), col);
            }
            _job(node.left);
        }
    }
}
}

