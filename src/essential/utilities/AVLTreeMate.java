package essential.utilities;

import essential.functional.IsWithOne;
import essential.progresive.Lot;
import org.jetbrains.annotations.NotNull;

import static essential.progresive.Pr.*;


@SuppressWarnings("DuplicatedCode")
class AVLTreeMate {

static @NotNull Lot pathOf(@NotNull AVLTree tree, Object key) {
    AVLNode node = tree.root;
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

private static void leftRotate(AVLTree tree, Lot path) {
    AVLNode x = (AVLNode) car(path);
    AVLNode up = x.right;
    if (up.balance == -1) {
        rightRotate(tree, cons(up, path));
    } else {
        x.right = up.left;
        up.left = x;
        reconnect(tree, path, x, up);
    }
}

private static void rightRotate(AVLTree tree, Lot path) {
    AVLNode x = (AVLNode) car(path);
    AVLNode up = x.left;
    if (up.balance == 1) {
        leftRotate(tree, cons(up, path));
    } else {
        x.left = up.right;
        up.right = x;
        reconnect(tree, path, x, up);
    }
}

private static void reconnect(AVLTree tree, Lot path, AVLNode x, AVLNode up) {
    if (cdr(path).isEmpty()) {
        tree.root = up;
    } else {
        AVLNode p = (AVLNode) car1(path);
        if (x.isLeftOf(p)) {
            p.left = up;
        } else {
            p.right = up;
        }
    }
    path = cons(x, cons(up, cdr(path)));
    update(tree, path);
}

static void update(AVLTree tree, @NotNull Lot path) {
    while (!path.isEmpty()) {
        AVLNode node = (AVLNode) car(path);
        int b = node.right.height - node.left.height;
        if (b == 2) {
            leftRotate(tree, path);
            return;
        } else if (b == -2) {
            rightRotate(tree, path);
            return;
        } else {
            node.height = 1 + Math.max(node.left.height, node.right.height);
            node.balance = b;
            path = cdr(path);
        }
    }
}


static Lot minimum(@NotNull AVLNode node, Lot path) {
    while (!node.isNil()) {
        path = cons(node, path);
        node = node.left;
    }
    return path;
}

static Lot maximum(@NotNull AVLNode node, Lot path) {
    while (!node.isNil()) {
        path = cons(node, path);
        node = node.right;
    }
    return path;
}

private static void transplant(AVLTree tree, @NotNull Lot path, AVLNode node) {
    if (cdr(path).isEmpty()) {
        tree.root = node;
    } else {
        AVLNode p = (AVLNode) car1(path);
        if (((AVLNode) car(path)).isLeftOf(p)) {
            p.left = node;
        } else {
            p.right = node;
        }
    }
}

static boolean delete(@NotNull AVLTree tree, Object key) {
    Lot path = pathOf(tree, key);
    AVLNode deleted = (AVLNode) car(path);
    if (deleted.isNil()) {
        return false;
    } else {
        AVLNode x;
        if (deleted.left.isNil()) {
            x = deleted.right;
            transplant(tree, path, x);
            path = cdr(path);
        } else if (deleted.right.isNil()) {
            x = deleted.left;
            transplant(tree, path, x);
            path = cdr(path);
        } else {
            Lot min_path = minimum(deleted.right, lot());
            AVLNode replace = (AVLNode) car(min_path);
            x = replace.right;
            if (!replace.isRightOf(deleted)) {
                transplant(tree, min_path, x);
                replace.right = deleted.right;
            }
            transplant(tree, path, replace);
            replace.left = deleted.left;
            path = append(cdr(min_path), cons(replace, cdr(path)));
        }
        if (x.isNil()) {
            update(tree, path);
        } else {
            update(tree, cons(x, path));
        }
        return true;
    }
}

static class Traveling {

    private Lot col;

    Traveling() {
        col = lot();
    }

    Lot process(AVLNode node) {
        job(node);
        return col;
    }

    private void job(AVLNode node) {
        Lot acc = lot(node);
        node = node.right;
        while (!acc.isEmpty()) {
            while (!node.isNil()) {
                acc = cons(node, acc);
                node = node.right;
            }
            node = (AVLNode) car(acc);
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

@SuppressWarnings("DuplicatedCode")
static class Filtering {

    private Lot col;
    private final IsWithOne fn;

    Filtering(IsWithOne fn) {
        col = lot();
        this.fn = fn;
    }

    Lot process(AVLNode node) {
        job(node);
        return col;
    }

    private void job(@NotNull AVLNode node) {
        Lot acc = lot(node);
        node = node.right;
        while (!acc.isEmpty()) {
            while (!node.isNil()) {
                acc = cons(node, acc);
                node = node.right;
            }
            node = (AVLNode) car(acc);
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
