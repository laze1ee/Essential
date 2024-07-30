package test;

import essential.datetime.Time;
import essential.progresive.Lot;
import essential.progresive.Pr;
import essential.utility.RBTree;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static essential.progresive.Pr.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


class RBTreeTest {

@Test
void overall() {
    RBTree tree = new RBTree(Pr::less, Pr::greater);
    Random rd = new Random(Time.current().nanosecond());
    int bound = 1000;

    Lot keys = lot();
    for (int i = 0; i < bound; i += 1) {
        int len = rd.nextInt(1, 19);
        keys = cons(randomString(len), keys);
        RBTree.insert(tree, car(keys), false);
    }
    System.out.println(RBTree.travel(tree));

    Lot bk = reverse(keys);
    for (int i = 0; i < bound; i += 1) {
        String key = (String) car(bk);
        if (RBTree.isPresent(tree, key)) {
            RBTree.set(tree, key, rd.nextInt(100));
        }
        bk = cdr(bk);
    }
    System.out.println(RBTree.travel(tree));

    for (int i = 0; i < bound; i += 1) {
        RBTree.delete(tree, car(keys));
        keys = cdr(keys);
    }
    assertTrue(tree.isEmpty());
}
}