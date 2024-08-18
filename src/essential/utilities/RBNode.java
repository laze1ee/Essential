package essential.utilities;

import static essential.progresive.Pr.*;


class RBNode {

Object key;
Object value;
boolean color;
RBNode left;
RBNode right;

RBNode() {
    key = false;
    value = false;
    color = false;
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
    if (datum instanceof RBNode node) {
        return equal(key, node.key) &&
               equal(value, node.value) &&
               eq(color, node.color) &&
               left.equals(node.left) &&
               right.equals(node.right);
    } else {
        return false;
    }
}


boolean isNil() {
    return key instanceof Boolean &&
           left == null &&
           right == null;
}

boolean isRed() {
    return color;
}

boolean isBlack() {
    return !color;
}
}
