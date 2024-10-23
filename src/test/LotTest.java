package test;

import essential.progresive.Lot;
import org.junit.jupiter.api.Test;

import static essential.progresive.Pr.*;
import static org.junit.jupiter.api.Assertions.*;


class LotTest {


final Lot l1;
final Lot l2;
final Lot l3;

LotTest() {
    l1 = lot(1, 2, 3);
    l2 = lotCopy(l1);
    setCdr(cddr(l2), l2);
    l3 = lotCopy(l1);
    setCdr(cddr(l3), l3);
    setCar(cdr(l3), l2);
}

@Test
void testToString() {
    assertEquals("(1 2 3)", l1.toString());
    assertEquals("#0=(1 2 3 . #0#)", l2.toString());
    assertEquals("#0=(1 #1=(1 2 3 . #1#) 3 . #0#)", l3.toString());
}

@Test
void testEquals() {
    Lot m1 = lotCopy(l1);
    assertEquals(l1, m1);
    Lot m2 = lotCopy(l1);
    setCdr(cddr(m2), m2);
    assertEquals(l2, m2);
    Lot m3 = lotCopy(l1);
    setCdr(cddr(m3), m3);
    setCar(cdr(m3), l2);
    assertEquals(l3, m3);
}

@Test
void length() {
    assertEquals(3, l1.length());
    RuntimeException e = assertThrows(RuntimeException.class, l2::length);
    System.out.println(e.getMessage());
}
}