package essential.progresive;

import essential.utility.RBTree;


public class Symbol {

static final RBTree tree = new RBTree((m, n) -> (int) m < (int) n,
                                      (m, n) -> (int) m > (int) n);

final int checksum;

Symbol(int checksum) {
    this.checksum = checksum;
}

@Override
public String toString() {
    String str = (String) RBTree.ref(tree, checksum);
    if (str.isEmpty()) {
        return "||";
    } else {
        StringBuilder builder = new StringBuilder();
        int bound = str.length();
        char c = str.charAt(0);
        if (Character.isDigit(c) || Mate.isScalar(c)) {
            builder.append(String.format("\\u%X;", (int) c));
        } else {
            builder.append(c);
        }
        for (int i = 1; i < bound; i = i + 1) {
            c = str.charAt(i);
            if (Mate.isScalar(c)) {
                builder.append(String.format("\\u%X;", (int) c));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof Symbol sym) {
        return this.checksum == sym.checksum;
    } else {
        return false;
    }
}

@Override
public int hashCode() {
    return checksum;
}

public String toRaw() {
    return (String) RBTree.ref(tree, checksum);
}
}
