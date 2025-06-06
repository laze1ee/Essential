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

class EqualityTest {

@Test
void process() {
  Few f1 = few(1, 2, 3, false);
  Few f2 = few(1, 2, 3, false);
  Lot l1 = f1.toLot();
  Lot l2 = f2.toLot();

  // non-sharing
  assertTrue(Equality.process(f1, f2));
  assertTrue(Equality.process(l1, l2));

  // container sharing item
  String share = "share";
  f1.set(1, share);
  f2.set(1, share);
  assertTrue(Equality.process(f1, f2));
  l1 = cons(share, l1);
  l2 = cons(share, l2);
  assertTrue(Equality.process(l1, l2));

  // recursive sharing item
  f1.set(3, f1);
  f2.set(3, f2);
  assertTrue(Equality.process(f1, f2));
  setCar(l1.cdr(), l1);
  setCdr(l1.cddr().cddr(), l1);
  setCar(l2.cdr(), l2);
  setCdr(l2.cddr().cddr(), l2);
  assertTrue(Equality.process(l1, l2));

  // make it more complex
  f1.set(0, l1);
  l1 = cons(f1, l1);
  f2.set(0, l2);
  l2 = cons(f2, l2);
  assertTrue(Equality.process(f1, f2));
  assertTrue(Equality.process(l1, l2));
}
}