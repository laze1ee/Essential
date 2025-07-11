/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import essential.datetime.Date;
import essential.datetime.Time;
import essential.progressive.Few;
import essential.progressive.Lot;
import org.junit.jupiter.api.Test;

import static essential.progressive.Pr.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class BinaryTest {

@Test
void testVarI32() {
  Few ins = Few.of(0x35, -0x0F, 0x1FF, -0x1FFF, 0xF2345, -0xABCDE,
                   0x1234567, -0x7654321, 0x12345678, -0x12345678);
  Few bins = Few.make(10, false);
  for (int i = 0; i < 10; i += 1) {
    bins.set(i, Binary.encodeVarI32((int) ins.ref(i)));
  }
  Few out_ins = Few.make(10, false);
  for (int i = 0; i < 10; i += 1) {
    byte[] bs = (byte[]) bins.ref(i);
    int    sz = Binary.sizeofVarI32(bs, 0);
    out_ins.set(i, Binary.decodeVarI32(bs, 0, sz));
  }
  assertTrue(equal(ins, out_ins));
}

@Test
void testBoolean() {
  Few bs = Few.of(true, false, new boolean[]{true, false, true, false, false, true, true});

  Few bins = Few.make(3, 0);
  for (int i = 0; i < 3; i += 1) {
    bins.set(i, Binary.encode(bs.ref(i)));
  }
  Few out_bs = Few.make(3, 0);
  for (int i = 0; i < 3; i += 1) {
    out_bs.set(i, Binary.decode((byte[]) bins.ref(i)));
  }
  assertEquals(bs, out_bs);

  Few share_bins = Few.make(3, 0);
  for (int i = 0; i < 3; i += 1) {
    share_bins.set(i, Binary.encode(bs.ref(i)));
  }
  Few share_bs = Few.make(3, 0);
  for (int i = 0; i < 3; i += 1) {
    share_bs.set(i, Binary.decode((byte[]) share_bins.ref(i)));
  }
  assertEquals(bs, share_bs);
}

@Test
void testShort() {
  Few ss = Few.of((short) 0x53AA, new short[]{0x2A, (short) 0xDEDE, -1});

  Few bins = Few.make(2, false);
  for (int i = 0; i < 2; i += 1) {
    bins.set(i, Binary.encode(ss.ref(i)));
  }
  Few out_ss = Few.make(2, false);
  for (int i = 0; i < 2; i += 1) {
    out_ss.set(i, Binary.decode((byte[]) bins.ref(i)));
  }
  assertEquals(ss, out_ss);

  Few share_bins = Few.make(2, false);
  for (int i = 0; i < 2; i += 1) {
    share_bins.set(i, Binary.encode(ss.ref(i)));
  }
  Few share_ss = Few.make(2, false);
  for (int i = 0; i < 2; i += 1) {
    share_ss.set(i, Binary.decode((byte[]) share_bins.ref(i)));
  }
  assertEquals(ss, share_ss);
}

@Test
void testInt() {
  Few ins = Few.of(0xAABBCCDD, 53, new int[]{0, 1, -1, 0xFFFF});

  Few bins = Few.make(3, false);
  for (int i = 0; i < 3; i += 1) {
    bins.set(i, Binary.encode(ins.ref(i)));
  }
  Few out_ins = Few.make(3, false);
  for (int i = 0; i < 3; i += 1) {
    out_ins.set(i, Binary.decode((byte[]) bins.ref(i)));
  }
  assertEquals(ins, out_ins);

  Few share_bins = Few.make(3, false);
  for (int i = 0; i < 3; i += 1) {
    share_bins.set(i, Binary.encode(ins.ref(i)));
  }
  Few share_ins = Few.make(3, false);
  for (int i = 0; i < 3; i += 1) {
    share_ins.set(i, Binary.decode((byte[]) share_bins.ref(i)));
  }
  assertEquals(ins, share_ins);
}

@Test
void testLong() {
  Few ins = Few.of(0xAABBCCDDL, 53, 0x12345678L, new long[]{1, -1, 0x123456789ABCDEFL});

  Few bins = Few.make(4, false);
  for (int i = 0; i < 4; i += 1) {
    bins.set(i, Binary.encode(ins.ref(i)));
  }
  Few out_ins = Few.make(4, false);
  for (int i = 0; i < 4; i += 1) {
    out_ins.set(i, Binary.decode((byte[]) bins.ref(i)));
  }
  assertEquals(ins, out_ins);

  Few share_bins = Few.make(4, false);
  for (int i = 0; i < 4; i += 1) {
    share_bins.set(i, Binary.encode(ins.ref(i)));
  }
  Few share_ins = Few.make(4, false);
  for (int i = 0; i < 4; i += 1) {
    share_ins.set(i, Binary.decode((byte[]) share_bins.ref(i)));
  }
  assertEquals(ins, share_ins);
}

@Test
void testFloat() {
  Few ins = Few.of(2.531f, new float[]{0.123f, -1.123f, 11.23f});

  Few bins = Few.make(2, false);
  for (int i = 0; i < 2; i += 1) {
    bins.set(i, Binary.encode(ins.ref(i)));
  }
  Few out_ins = Few.make(2, false);
  for (int i = 0; i < 2; i += 1) {
    out_ins.set(i, Binary.decode((byte[]) bins.ref(i)));
  }
  assertEquals(ins, out_ins);

  Few share_bins = Few.make(2, false);
  for (int i = 0; i < 2; i += 1) {
    share_bins.set(i, Binary.encode(ins.ref(i)));
  }
  Few share_ins = Few.make(2, false);
  for (int i = 0; i < 2; i += 1) {
    share_ins.set(i, Binary.decode((byte[]) share_bins.ref(i)));
  }
  assertEquals(ins, share_ins);
}

@Test
void testDouble() {
  Few ins = Few.of(2.531, 753.20466, new double[]{0.123, -1.123, 11.23});

  Few bins = Few.make(3, false);
  for (int i = 0; i < 3; i += 1) {
    bins.set(i, Binary.encode(ins.ref(i)));
  }
  Few out_ins = Few.make(3, false);
  for (int i = 0; i < 3; i += 1) {
    out_ins.set(i, Binary.decode((byte[]) bins.ref(i)));
  }
  assertEquals(ins, out_ins);

  Few share_bins = Few.make(3, false);
  for (int i = 0; i < 3; i += 1) {
    share_bins.set(i, Binary.encode(ins.ref(i)));
  }
  Few share_ins = Few.make(3, false);
  for (int i = 0; i < 3; i += 1) {
    share_ins.set(i, Binary.decode((byte[]) share_bins.ref(i)));
  }
  assertEquals(ins, share_ins);
}

@Test
void testCharAndString() {
  Few cs = Few.of('$', '听',
                  "A journey of a thousand miles begins with a single step.",
                  "千里之行，始于足下。");

  Few bins = Few.make(4, false);
  for (int i = 0; i < 4; i += 1) {
    bins.set(i, Binary.encode(cs.ref(i)));
  }
  Few out_cs = Few.make(4, false);
  for (int i = 0; i < 4; i += 1) {
    out_cs.set(i, Binary.decode((byte[]) bins.ref(i)));
  }
  assertEquals(cs, out_cs);

  Few share_bins = Few.make(4, false);
  for (int i = 0; i < 4; i += 1) {
    share_bins.set(i, Binary.encode(cs.ref(i)));
  }
  Few share_cs = Few.make(4, false);
  for (int i = 0; i < 4; i += 1) {
    share_cs.set(i, Binary.decode((byte[]) share_bins.ref(i)));
  }
  assertEquals(cs, share_cs);
}

@Test
void testTimeAndDate() {
  Few misc = Few.of(new Time(5313131, 2094),
                    new Date(2077, 4, 15, 17, 54, 16, 11235813, 8 * 3600));

  Few bins = Few.make(2, false);
  for (int i = 0; i < 2; i += 1) {
    bins.set(i, Binary.encode(misc.ref(i)));
  }
  Few out_misc = Few.make(2, false);
  for (int i = 0; i < 2; i += 1) {
    out_misc.set(i, Binary.decode((byte[]) bins.ref(i)));
  }
  assertEquals(misc, out_misc);

  Few share_bins = Few.make(2, false);
  for (int i = 0; i < 2; i += 1) {
    share_bins.set(i, Binary.encode(misc.ref(i)));
  }
  Few share_misc = Few.make(2, false);
  for (int i = 0; i < 2; i += 1) {
    share_misc.set(i, Binary.decode((byte[]) share_bins.ref(i)));
  }
  assertEquals(misc, share_misc);
}

@Test
void testFewAndLot() {
  Lot lt = Lot.of(1, 2, 3);
  Few fw = Few.of(0, lt, -2, -3, -4);

  // Non-Identical Test
  byte[] bin_lt = Binary.encode(lt);
  Lot    out_lt = (Lot) Binary.decode(bin_lt);
  assertEquals(lt, out_lt);

  byte[] bin_fw = Binary.encode(fw);
  Few    out_fw = (Few) Binary.decode(bin_fw);
  assertEquals(fw, out_fw);

  // Identical Test
  setCdr(lt.cddr(), lt);
  bin_lt = Binary.encode(lt);
  out_lt = (Lot) Binary.decode(bin_lt);
  assertEquals(lt, out_lt);

  Lot ls = cons('a', cons(false, cons("abc", lt)));
  setCar(ls.cdr(), ls);
  byte[] bin_ls = Binary.encode(ls);
  Lot    out_ls = (Lot) Binary.decode(bin_ls);
  assertEquals(ls, out_ls);

  fw.set(3, fw);
  bin_fw = Binary.encode(fw);
  out_fw = (Few) Binary.decode(bin_fw);
  assertEquals(fw, out_fw);
}
}