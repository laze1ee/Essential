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


class ToStringTest {

@Test
void process() {
  Few fw = Few.of(1, 2, 3, false);
  Lot lt = Lot.of('a', 'b', 'c');

  // non-sharing
  assertEquals("#(1 2 3 #f)", ToString.process(fw));
  assertEquals("(#\\a #\\b #\\c)", ToString.process(lt));

  // contains sharing item
  String share = "share";
  fw.set(1, share);
  fw.set(3, share);
  assertEquals("#(1 #0=\"share\" 3 #0#)", ToString.process(fw));
  setCar(lt.cddr(), share);
  lt = cons(share, lt);
  assertEquals("(#0=\"share\" #\\a #\\b #0#)", ToString.process(lt));

  // recursive sharing item
  fw.set(2, fw);
  assertEquals("#0=#(1 #1=\"share\" #0# #1#)", ToString.process(fw));
  setCar(lt.cdr(), lt);
  setCdr(lt.cdr().cddr(), lt);
  assertEquals("#0=(#1=\"share\" #0# #\\b #1# . #0#)", ToString.process(lt));

  // make it more complex
  lt = cons(fw, lt);
  fw.set(0, lt);
  assertEquals("#0=(#1=#(#0# #2=\"share\" #1# #2#) . #3=(#2# #3# #\\b #2# . #3#))",
               ToString.process(lt));

  // test empty Lot
  Lot l1 = Lot.of();
  Lot l2 = cons(1, cons(l1, cons(3, l1)));
  assertEquals("(1 #0=() 3 . #0#)", ToString.process(l2));
}
}