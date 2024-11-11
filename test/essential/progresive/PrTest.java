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

class PrTest {

@Test
void testCons() {
    Lot lt = cons(1, cons(2, cons(3, lot())));
    assertEquals(lot(1, 2, 3), lt);
}

@Test
void testAppend() {
    Lot lt1 = lot(1, 2, 3);
    Lot lt2 = lot(4, 5, 6);
    Lot result = append(lt1, lt2);
    assertEquals(lot(1, 2, 3, 4, 5, 6), result);

    // Test appending with an empty list
    Lot emptyLot = lot();
    result = append(emptyLot, lt1);
    assertEquals(lt1, result);

    result = append(lt1, emptyLot);
    assertEquals(lt1, result);
}

@Test
void testIsBelong() {
    Lot lt = lot(1, 2, 3);
    assertTrue(isBelong(3, lt));
    assertFalse(isBelong(4, lt));

    assertTrue(isBelong((o1, o2) -> (int) o1  == ((int) o2) * 3, 9, lt));
}
}