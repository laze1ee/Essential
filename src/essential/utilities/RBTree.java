package essential.utilities;

import essential.functional.Do1;
import org.jetbrains.annotations.NotNull;
import essential.functional.Predicate1;
import essential.functional.Predicate2;
import essential.progresive.Lot;

import static essential.progresive.Pr.*;


public class RBTree {

final Predicate2 less;
final Predicate2 greater;
RBNode root;

public RBTree(Predicate2 less, Predicate2 greater) {
    this.less = less;
    this.greater = greater;
    root = new RBNode();
}

@Override
public String toString() {
    return String.format("(Red-Black-Tree %s)", root);
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof RBTree tree) {
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
public static boolean insert(@NotNull RBTree tree, @NotNull Object key, Object value) {
    Lot path = RBTreeMate.pathOf(tree, key);
    RBNode node = (RBNode) car(path);
    if (node.isNil()) {
        node.key = key;
        node.value = value;
        node.color = true;
        node.left = new RBNode();
        node.right = new RBNode();
        RBTreeMate.InsertFixing fixer = new RBTreeMate.InsertFixing(tree, path);
        fixer.process();
        return true;
    } else {
        return false;
    }
}

public static boolean isPresent(@NotNull RBTree tree, @NotNull Object key) {
    Lot path = RBTreeMate.pathOf(tree, key);
    RBNode node = (RBNode) car(path);
    return !node.isNil();
}

public static Object ref(@NotNull RBTree tree, @NotNull Object key) {
    Lot path = RBTreeMate.pathOf(tree, key);
    RBNode node = (RBNode) car(path);
    if (node.isNil()) {
        throw new RuntimeException(String.format(Msg.NOT_PRESENT, key, tree));
    } else {
        return node.value;
    }
}

public static void set(@NotNull RBTree tree, @NotNull Object key, Object new_value) {
    Lot path = RBTreeMate.pathOf(tree, key);
    RBNode node = (RBNode) car(path);
    if (node.isNil()) {
        throw new RuntimeException(String.format(Msg.NOT_PRESENT, key, tree));
    } else {
        node.value = new_value;
    }
}

/***
 * @return if deleting succeeded, return true else false.
 */
public static boolean delete(@NotNull RBTree tree, @NotNull Object key) {
    if (tree.isEmpty()) {
        return false;
    } else {
        return RBTreeMate.delete(tree, key);
    }
}

public Object minimum() {
    Lot path = RBTreeMate.minimum(root, lot());
    if (path.isEmpty()) {
        throw new RuntimeException(Msg.EMPTY_TREE);
    } else {
        RBNode node = (RBNode) car(path);
        return node.key;
    }
}

public Object maximum() {
    Lot path = RBTreeMate.maximum(root, lot());
    if (path.isEmpty()) {
        throw new RuntimeException(Msg.EMPTY_TREE);
    } else {
        RBNode node = (RBNode) car(path);
        return node.key;
    }
}

public static Lot travel(@NotNull RBTree tree) {
    RBTreeMate.Traveling inst = new RBTreeMate.Traveling();
    return inst.process(tree.root);
}

public static Lot filter(Predicate1 fn, @NotNull RBTree tree) {
    RBTreeMate.Filtering inst = new RBTreeMate.Filtering(fn);
    return inst.process(tree.root);
}

public static void mapSet(Do1 fn, @NotNull RBTree tree) {
    RBTreeMate.MapSetting inst = new RBTreeMate.MapSetting(fn);
    inst.process(tree.root);
}
}

