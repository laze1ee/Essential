/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.progressive;

import org.junit.jupiter.api.Test;

import static essential.progressive.Pr.*;
import static org.junit.jupiter.api.Assertions.*;


class PrTest {

@Test
void testCons() {
  Lot lt = cons(1, cons(2, cons(3, Lot.of())));
  assertEquals(Lot.of(1, 2, 3), lt);
}

@Test
void testAppend() {
  Lot lt1    = Lot.of(1, 2, 3);
  Lot lt2    = Lot.of(4, 5, 6);
  Lot result = append(lt1, lt2);
  assertEquals(Lot.of(1, 2, 3, 4, 5, 6), result);

  // Test appending with an empty list
  Lot emptyLot = Lot.of();
  result = append(emptyLot, lt1);
  assertEquals(lt1, result);

  result = append(lt1, emptyLot);
  assertEquals(lt1, result);
}

@Test
void testIsBelong() {
  Lot lt = Lot.of(1, 2, 3);
  assertTrue(isBelong(3, lt));
  assertFalse(isBelong(4, lt));

  assertTrue(isBelong((o1, o2) -> (int) o1 == ((int) o2) * 3, 9, lt));
}
}