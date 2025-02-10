/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.progresive;

import org.junit.jupiter.api.Test;

import static essential.progresive.Pr.*;
import static org.junit.jupiter.api.Assertions.*;

class LotTest {

final Lot l1;
final Lot l2;
final Lot l3;

LotTest() {
    l1 = lot(1, 2, 3);
    l2 = l1.copy();
    l2.cddr().setCdr(l2);
    l3 = l1.copy();
    l3.cddr().setCdr(l3);
    l3.cdr().setCar(l2);
}

@Test
void testToString() {
    assertTrue(equal("(1 2 3)", l1.toString()));
    assertTrue(equal("#0=(1 2 3 . #0#)", l2.toString()));
    assertTrue(equal("#0=(#1=1 #2=(#1# 2 #3=3 . #2#) #3# . #0#)", l3.toString()));
}

@Test
void testEquals() {
    Lot m1 = l1.copy();
    assertTrue(equal(l1, m1));
    Lot m2 = l1.copy();
    m2.cddr().setCdr(m2);
    assertTrue(equal(l2, m2));
    Lot m3 = l1.copy();
    m3.cddr().setCdr(m3);
    m3.cdr().setCdr(l2);
    assertTrue(equal(l3, m3));
}

@Test
void isEmpty() {
    assertFalse(l1.isEmpty());
    assertTrue(new Lot().isEmpty());
}

@Test
void length() {
    assertEquals(3, l1.length());
    RuntimeException e = assertThrows(RuntimeException.class, l2::length);
    System.out.println(e.getMessage());
}

@Test
void isBreadthCircle() {
    assertFalse(l1.isBreadthCircle());
    assertTrue(l2.isBreadthCircle());
    assertTrue(l3.isBreadthCircle());
}

@Test
void car() {
    assertEquals(1, l1.car());
    assertEquals(1, l2.car());
    assertEquals(1, l3.car());
}

@Test
void cdr() {
    assertTrue(l1.cdr().cdr().cdr().isEmpty());
}

@Test
void caar() {
    assertEquals(1, lot(l1).caar());
}

@Test
void cddr() {
    assertEquals(lot(3), l1.cddr());
}

@Test
void cdar() {
    assertEquals(lot(2, 3), lot(l1).cdar());
}

@Test
void ref() {
    assertEquals(1, l1.ref(0));
    assertEquals(2, l1.ref(1));
    assertEquals(3, l1.ref(2));
    assertThrows(RuntimeException.class, () -> l1.ref(3));
    assertThrows(RuntimeException.class, () -> l1.ref(-1));
}

@Test
void reverse() {
    Lot l1_r = l1.reverse();
    assertTrue(equal(lot(3, 2, 1), l1_r));
}

@Test
void head() {
    Lot l1_head = l1.head(2);
    assertEquals(2, l1_head.length());
    assertThrows(RuntimeException.class, () -> l1.head(4));
    assertThrows(RuntimeException.class, () -> l1.head(-1));

    Lot l2_head = l2.head(10);
    assertEquals(10, l2_head.length());
}

@Test
void tail() {
    Lot l1_tail = l1.tail(1);
    assertEquals(lot(2, 3), l1_tail);

    assertThrows(RuntimeException.class, () -> l1.tail(4));
    assertThrows(RuntimeException.class, () -> l1.tail(-1));

    Lot l2_tail = l2.tail(10);
    assertEquals("#0=(2 3 1 . #0#)", l2_tail.toString());
}

@Test
void copy() {
    Lot l1_cpy = l1.copy();
    assertTrue(equal(l1, l1_cpy));
    assertNotSame(l1, l1_cpy);
}

@Test
void toFew() {
    Few l1_few = l1.toFew();
    assertEquals(few(1, 2, 3), l1_few);
}

@Test
void filter() {
    Lot l1_filtered = l1.filter(x -> (int)x % 2 == 0);
    assertEquals(lot(2), l1_filtered);
}

@Test
void map() {
    Lot l1_mapped = l1.map(x -> (int)x * 2);
    assertEquals(lot(2, 4, 6), l1_mapped);
}
}