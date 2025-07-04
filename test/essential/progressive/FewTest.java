/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.progressive;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

import static essential.progressive.Pr.equal;
import static org.junit.jupiter.api.Assertions.*;

class FewTest {

final Few f1;
final Few f2;

FewTest() {
  f1 = Few.of(true, false, 1, 3);
  f2 = f1.copy();
  f2.set(1, f2);
}

@Test
void testToString() {
  assertEquals("#(#t #f 1 3)", f1.toString());
  assertEquals("#0=#(#t #0# 1 3)", f2.toString());
}

@Test
void testEquals() {
  Few fw = f1.copy();
  assertEquals(f1, fw);
  fw.set(1, fw);
  assertEquals(fw, f2);
}

@Test
void length() {
  assertEquals(4, f1.length());
  assertEquals(4, f2.length());
}

@Test
void ref() {
  assertEquals(true, f1.ref(0));
  assertEquals(false, f1.ref(1));
  assertEquals(1, f1.ref(2));
  assertEquals(3, f1.ref(3));

  assertThrows(RuntimeException.class, () -> f1.ref(4));
  assertThrows(RuntimeException.class, () -> f1.ref(-1));
}

@Test
void set() {
  Few fs = Few.of(1, 2, 3);
  fs.set(1, 4);
  assertEquals(4, fs.ref(1));
  assertThrows(RuntimeException.class, () -> fs.set(3, 5));
  assertThrows(RuntimeException.class, () -> fs.set(-1, 5));
}

@Test
void fill() {
  Few fs = Few.of(1, 2, 3, 4);
  fs.fill(5);
  assertEquals(5, fs.ref(0));
  assertEquals(5, fs.ref(1));
  assertEquals(5, fs.ref(2));
  assertEquals(5, fs.ref(3));
}

@Test
void copy() {
  Few fs = Few.of(1, 2, 3);
  Few cp = fs.copy();
  assertTrue(equal(fs, cp));
}

@Test
void map() {
  Few f  = Few.of(1, 2, 3);
  Few fs = f.map(o -> (int) o * 2);
  assertEquals(2, fs.ref(0));
  assertEquals(4, fs.ref(1));
  assertEquals(6, fs.ref(2));
}

@Test
void sort() {
  int length = 153;
  Few fw     = Few.make(length, 0);
  for (int i = 0; i < length; i += 1) {
    fw.set(i, ThreadLocalRandom.current().nextInt(1000) - 500);
  }

  fw.sort((o1, o2) -> (int) o1 < (int) o2);
  System.out.println(fw);
  fw.sort((o1, o2) -> (int) o1 > (int) o2);
  System.out.println(fw);
}
}