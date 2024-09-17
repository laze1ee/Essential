package test;

import essential.progresive.Few;
import essential.progresive.Lot;
import essential.progresive.Pr;
import org.junit.jupiter.api.Test;

import static essential.progresive.Pr.*;
import static org.junit.jupiter.api.Assertions.*;


class PrTest {

final Lot l1;
final Lot l2;
final Few f1;

PrTest() {
    l1 = lot(1, 2, 3);
    l2 = copyLot(l1);
    setCdr(cddr(l2), l2);
    f1 = few(1, 2, 3);
}

@Test
void refLot() {
    RuntimeException e = assertThrows(RuntimeException.class, () -> Pr.lotRef(l1, -5));
    System.out.println(e.getMessage());
}

@Test
void reverse() {
    Lot m = Pr.reverse(Pr.reverse(l1));
    assertEquals(l1, m);
}

@Test
void append() {
    Lot m = Pr.append(l1, l2);
    assertEquals("(1 2 3 . #0=(1 2 3 . #0#))", m.toString());
}

@Test
void lotHead() {
    Lot m1 = Pr.cons(true, Pr.cons(false, l1));
    Lot m2 = Pr.lotHead(m1, 3);
    Lot m3 = Pr.lot(true, false, 1);
    assertEquals(m2, m3);
}

@Test
void lotTail() {
    Lot m1 = Pr.cons(true, Pr.cons(false, l1));
    Lot m2 = Pr.lotTail(m1, 3);
    Lot m3 = Pr.lot(2, 3);
    assertEquals(m2, m3);
}

@Test
void lotToFew() {
    Few fw = Pr.lotToFew(l1);
    assertEquals(fw, f1);
}

@Test
void filterLot() {
    Lot lt = Pr.filterLot((a) -> ((int) a) % 2 == 0, l1);
    assertEquals(lt, Pr.lot(2));
}

@Test
void mapLot() {
    Lot m1 = Pr.mapLot((a) -> (int) a * (int) a, l1);
    Lot m2 = Pr.lot(1, 4, 9);
    assertEquals(m1, m2);
}

@Test
void makeFew() {
    Few fw1 = Pr.makeFew(10, Pr.lot("bad"));
    Few fw2 = Pr.makeFew(10);
    Pr.fillFew(fw2, Pr.lot("bad"));
    assertEquals(fw1, fw2);
}

@Test
void fewToLot() {
    Lot lt = Pr.fewToLot(f1);
    assertEquals(lt, l1);
}

@Test
void mapFew() {
    Few f2 = Pr.mapFew((a) -> (int) a * 10, f1);
    Few f3 = few(10, 20, 30);
    assertEquals(f2, f3);
}

@Test
void eq() {
    assertTrue(Pr.eq(true, true));
    assertTrue(Pr.eq(false, false));
    assertTrue(Pr.eq((byte) 0x80, (byte) 0x80));
    assertTrue(Pr.eq((short) -1, (short) -1));
    assertTrue(Pr.eq(531, 531));
    assertTrue(Pr.eq(-5L, -5L));
    assertTrue(Pr.eq(5.5319, 5.5319));
    assertFalse(Pr.eq(1, 1.0));
    assertTrue(Pr.eq('\\', '\\'));
    assertTrue(Pr.eq(Pr.symbol("abc"), Pr.symbol("abc")));
    assertTrue(Pr.eq(l2, l2));
    assertTrue(Pr.eq(f1, f1));
}
}