package essential.utilities;

import org.jetbrains.annotations.NotNull;

import static essential.progresive.Pr.*;


class AVLNode {

Object key;
Object value;
int height;
int balance;
AVLNode left;
AVLNode right;

AVLNode() {
    key = false;
    value = false;
    height = 0;
    balance = 0;
    left = null;
    right = null;
}

@Override
public String toString() {
    if (this.isNil()) {
        return "nil";
    } else {
        return String.format("(%s %s %s %s)", stringOf(key), stringOf(value), left, right);
    }
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof AVLNode node) {
        return equal(key, node.key) &&
               equal(value, node.value) &&
               height == node.height &&
               balance == node.balance &&
               left.equals(node.left) &&
               right.equals(node.right);
    } else {
        return false;
    }
}

public boolean isNil() {
    return key instanceof Boolean &&
           left == null &&
           right == null;
}

public boolean isLeftOf(@NotNull AVLNode node) {
    return eq(this, node.left);
}

public boolean isRightOf(@NotNull AVLNode node) {
    return eq(this, node.right);
}
}
