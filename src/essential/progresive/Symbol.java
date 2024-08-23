package essential.progresive;

import essential.utilities.AVLTree;


public class Symbol {

static final AVLTree tree = new AVLTree((m, n) -> (int) m < (int) n,
                                        (m, n) -> (int) m > (int) n);
final int checksum;

Symbol(int checksum) {
    this.checksum = checksum;
}

@Override
public String toString() {
    return (String) AVLTree.ref(tree, checksum);
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
}
