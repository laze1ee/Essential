/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import essential.datetime.Time;
import essential.progresive.Lot;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static essential.progresive.Pr.cons;
import static essential.progresive.Pr.lot;
import static org.junit.jupiter.api.Assertions.*;


class RBTreeTest {

@Test
void overall() {
    RBTree tree = new RBTree((o1, o2) -> (int) o1 < (int) o2,
                               (o1, o2) -> (int) o1 > (int) o2);
    Random rd = new Random(Time.current().nanosecond());
    int times = 1000000;

    Lot keys = lot();
    for (int i = 0; i < times; i += 1) {
        int key = rd.nextInt();
        boolean success = tree.insert(key, false);
        keys = cons(key, keys);
        if (!success) {
            System.out.printf("key %s is presented\n", key);
        }
    }

    Lot all = tree.travel();
    System.out.printf("tree length: %s\n", all.length());

    Lot item = keys;
    while (!item.isEmpty()) {
        tree.set(item.car(), rd.nextInt(1000000));
        item = item.cdr();
    }
    System.out.println("set done");

    RBTree evens = tree.filter(o -> ((int) o % 2) == 0);
    System.out.println("filter done");
    System.out.printf("tree even number length: %s\n", evens.size());

    Lot depth = RBTree.depthStatistic(tree);
    System.out.printf("tree depth: %s\n", depth);

    while (!keys.isEmpty()) {
        int key = (int) keys.car();
        boolean success = tree.delete(key);
        if (!success) {
            System.out.printf("key %s is not presented\n", key);
        }
        keys = keys.cdr();
    }
    assertTrue(tree.isEmpty());
}
}