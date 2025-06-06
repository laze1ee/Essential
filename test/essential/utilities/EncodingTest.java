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


class EncodingTest {

@Test
void process() {
  Few fw = few(1, 2, 3, false);
  Lot lt = lot('a', lot('b'), 'c', true);

  fw.set(2, fw);
  setCdr(lt.cddr().cdr(), lt);
  lt = cons(fw, lt);
  Encoding inst = new Encoding(lt);
  byte[] bin = inst.process();
  System.out.println(stringOf(bin));
}
}