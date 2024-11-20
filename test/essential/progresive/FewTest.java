/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.progresive;

import org.junit.jupiter.api.Test;

import static essential.progresive.Pr.equal;
import static essential.progresive.Pr.few;
import static org.junit.jupiter.api.Assertions.*;

class FewTest {

final Few f1;
final Few f2;

FewTest() {
    f1 =  few(true, false, 1 , 3);
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
    Few fs = few(1, 2, 3);
    fs.set(1, 4);
    assertEquals(4, fs.ref(1));
    assertThrows(RuntimeException.class, () -> fs.set(3, 5));
    assertThrows(RuntimeException.class, () -> fs.set(-1, 5));
}

@Test
void fill() {
    Few fs = few(1, 2, 3, 4);
    fs.fill(5);
    assertEquals(5, fs.ref(0));
    assertEquals(5, fs.ref(1));
    assertEquals(5, fs.ref(2));
    assertEquals(5, fs.ref(3));
}

@Test
void copy() {
    Few fs = few(1, 2, 3);
    Few cp = fs.copy();
    assertTrue(equal(fs, cp));
}

@Test
void map() {
    Few f = few(1, 2, 3);
    Few fs = f.map(o -> (int) o * 2);
    assertEquals(2, fs.ref(0));
    assertEquals(4, fs.ref(1));
    assertEquals(6, fs.ref(2));
}

@Test
void find() {
    Few fs = few(1, 2, 3);
    assertEquals(0, fs.find((o1, o2) -> (int) o1 == (int) o2, 1));
    assertEquals(1, fs.find((o1, o2) -> (int) o1 == (int) o2, 2));
    assertEquals(2, fs.find((o1, o2) -> (int) o1 == (int) o2, 3));
    assertEquals(-1, fs.find((o1, o2) -> (int) o1 == (int) o2, 4));
}
}