/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package test;

import essential.datetime.Time;
import essential.progresive.Lot;
import essential.utilities.RBTree;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static essential.progresive.Pr.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


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
        boolean success = RBTree.insert(tree, key, false);
        if (success) {
            keys = cons(key, keys);
        } else {
            System.out.printf("key %s is presented\n", key);
        }
    }
    Lot all = RBTree.travel(tree);
    System.out.printf("tree length: %s\n", all.length());

    Lot item = keys;
    while (!item.isEmpty()) {
        RBTree.set(tree, car(item), rd.nextInt(1000));
        item = cdr(item);
    }
    System.out.println("set done");

    RBTree evens = RBTree.filter(o -> ((int) o % 2) == 0, tree);
    System.out.printf("tree even number length: %s\n", evens.size());

    while (!keys.isEmpty()) {
        int key = (int) car(keys);
        boolean success = RBTree.delete(tree, key);
        if (!success) {
            System.out.printf("key %s is not presented\n", key);
        }
        keys = cdr(keys);
    }
    assertTrue(tree.isEmpty());
}
}