package essential.utilities;

import essential.functional.Do1;
import essential.functional.Predicate1;
import essential.functional.Predicate2;
import essential.progresive.Lot;
import org.jetbrains.annotations.NotNull;

import static essential.progresive.Pr.*;


public class AVLTree {

final Predicate2 less;
final Predicate2 greater;
AVLNode root;

public AVLTree(Predicate2 less, Predicate2 greater) {
    this.less = less;
    this.greater = greater;
    root = new AVLNode();
}

@Override
public String toString() {
    return String.format("(AVL-Tree %s)", root);
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof AVLTree tree) {
        return root.equals(tree.root);
    } else {
        return false;
    }
}

public boolean isEmpty() {
    return root.isNil();
}

/***
 * @return if inserting succeeded, return true else false.
 */
public static boolean insert(@NotNull AVLTree tree, @NotNull Object key, Object value) {
    Lot path = AVLTreeMate.pathOf(tree, key);
    AVLNode node = (AVLNode) car(path);
    if (node.isNil()) {
        node.key = key;
        node.value = value;
        node.height = 1;
        node.left = new AVLNode();
        node.right = new AVLNode();
        AVLTreeMate.update(tree, path);
        return true;
    } else {
        return false;
    }
}

public static boolean isPresent(@NotNull AVLTree tree, @NotNull Object key) {
    Lot path = AVLTreeMate.pathOf(tree, key);
    AVLNode node = (AVLNode) car(path);
    return !node.isNil();
}

public static Object ref(@NotNull AVLTree tree, @NotNull Object key) {
    Lot path = AVLTreeMate.pathOf(tree, key);
    AVLNode node = (AVLNode) car(path);
    if (node.isNil()) {
        throw new RuntimeException(String.format(Msg.NOT_PRESENT, key, tree));
    } else {
        return node.value;
    }
}

public static void set(@NotNull AVLTree tree, @NotNull Object key, Object new_value) {
    Lot path = AVLTreeMate.pathOf(tree, key);
    AVLNode node = (AVLNode) car(path);
    if (node.isNil()) {
        throw new RuntimeException(String.format(Msg.NOT_PRESENT, key, tree));
    } else {
        node.value = new_value;
    }
}

/***
 * @return if deleting succeeded, return true else false.
 */
public static boolean delete(@NotNull AVLTree tree, @NotNull Object key) {
    if (tree.isEmpty()) {
        return false;
    } else {
        return AVLTreeMate.delete(tree, key);
    }
}

public Object minimum() {
    Lot path = AVLTreeMate.minimum(root, lot());
    if (path.isEmpty()) {
        throw new RuntimeException(Msg.EMPTY_TREE);
    } else {
        AVLNode node = (AVLNode) car(path);
        return node.key;
    }
}

public Object maximum() {
    Lot path = AVLTreeMate.maximum(root, lot());
    if (path.isEmpty()) {
        throw new RuntimeException(Msg.EMPTY_TREE);
    } else {
        AVLNode node = (AVLNode) car(path);
        return node.key;
    }
}

public static Lot travel(@NotNull AVLTree tree) {
    AVLTreeMate.Traveling inst = new AVLTreeMate.Traveling();
    return inst.process(tree.root);
}

public static Lot filter(Predicate1 fn, @NotNull AVLTree tree) {
    AVLTreeMate.Filtering inst = new AVLTreeMate.Filtering(fn);
    return inst.process(tree.root);
}

public static void mapSet(Do1 fn, @NotNull AVLTree tree) {
    AVLTreeMate.MapSetting inst = new AVLTreeMate.MapSetting(fn);
    inst.process(tree.root);
}
}
