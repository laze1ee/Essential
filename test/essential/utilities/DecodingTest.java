/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import essential.progressive.Few;
import essential.progressive.Lot;
import org.junit.jupiter.api.Test;

import static essential.progressive.Pr.*;
import static org.junit.jupiter.api.Assertions.*;

class DecodingTest {

@Test
void process() {
    Few fw = few(1, 2, 3, false);
    Lot lt = lot('a', lot('b'), 'c', true);

    fw.set(1, fw);
    lt.set(2, lt);
    setCdr(lt.cdr().cddr(), lt);
    lt = cons(fw, lt);

    Encoding en = new Encoding(lt);
    byte[] bin = en.process();
    Decoding de = new Decoding(bin, 0);
    Object datum = de.process();
    System.out.println(datum);
    assertEquals(lt, datum);
}
}