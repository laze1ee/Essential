/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

import essential.progresive.Few;
import org.junit.jupiter.api.Test;

import static essential.progresive.Pr.*;
import static org.junit.jupiter.api.Assertions.*;


class FewTest {

final Few f1;
final Few f2;

FewTest() {
    f1 =  few(true, false, 1 , 3);
    f2 = fewCopy(f1);
    fewSet(f2, 1, f2);
}


@Test
void testToString() {
    assertEquals("#(#t #f 1 3)", f1.toString());
    assertEquals("#0=#(#t #0# 1 3)", f2.toString());
}

@Test
void testEquals() {
    Few fw = fewCopy(f1);
    assertEquals(f1, fw);
    fewSet(fw, 1, fw);
    assertEquals(fw, f2);
}

}