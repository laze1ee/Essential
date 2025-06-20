/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import essential.datetime.Time;
import essential.progressive.Lot;
import essential.progressive.Pr;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static essential.progressive.Pr.cons;
import static org.junit.jupiter.api.Assertions.*;


class RBTreeTest {

private static final int SIZE = 10000;

private final RBTree tree;
private Lot keys;

private RBTreeTest() {
  tree = new RBTree(Pr::less, Pr::greater);
  keys = Lot.of();
}

void insert() {
  Random random = new Random(Time.current().nanosecond());
  Lot repeat = Lot.of();
  for (int i = 0; i < SIZE; i += 1) {
    while (true) {
      String key = RandGenerator.ascii(random.nextInt(3, 8));
      boolean success = tree.insert(key, random.nextInt(SIZE));
      if (success) {
        keys = cons(key, keys);
        break;
      }
      repeat = cons(key, repeat);
    }
  }
  System.out.println("repeated keys: " + repeat);
}

void visit() {
  Lot all = tree.travel();
  assertEquals(all.length(), tree.size());

  RBTree new_tree = tree.map(o -> ((int) o) * 3);

  RBTree evens = new_tree.filter(o -> ((int) o % 2) == 0);
  int even_size = evens.size();
  System.out.println("even amount: " + even_size);
  RBTree odds = new_tree.filter(o -> ((int) o % 2) != 0);
  int odd_size = odds.size();
  System.out.println("odd amount: " + odd_size);
  assertEquals(even_size + odd_size, new_tree.size());

  Lot statistic = tree.depthStatistic();
  System.out.println("depth statistic: " + statistic);
}

void delete() {
  keys = RandGenerator.shuffle(keys);
  while (!keys.isEmpty()) {
    boolean success = tree.delete(keys.car());
    keys = keys.cdr();
  }
  assertTrue(tree.isEmpty());
}

@Test
void overall() {
  visit();

  insert();
  visit();
  delete();
}
}