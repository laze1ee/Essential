/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import essential.datetime.Date;
import essential.datetime.Time;
import essential.progresive.Few;
import essential.progresive.Lot;
import org.junit.jupiter.api.Test;

import static essential.progresive.Pr.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class BinaryTest {

@Test
void testVarI32() {
    Few ins = few(0x35, -0x0F, 0x1FF, -0x1FFF, 0xF2345, -0xABCDE,
                  0x1234567, -0x7654321, 0x12345678, -0x12345678);
    Few bins = makeFew(10, false);
    for (int i = 0; i < 10; i += 1) {
        bins.set(i, Binary.encodeVarI32((int) ins.ref(i)));
    }
    Few out_ins = makeFew(10, false);
    for (int i = 0; i < 10; i += 1) {
        byte[] bs = (byte[]) bins.ref(i);
        int sz = Binary.sizeofVarI32(bs, 0);
        out_ins.set(i, Binary.decodeVarI32(bs, 0, sz));
    }
    assertTrue(equal(ins, out_ins));
}

@Test
void testBoolean() {
    Few bs = few(true, false, new boolean[]{true, false, true, false, false, true, true});

    Few bins = makeFew(3, 0);
    for (int i = 0; i < 3; i += 1) {
        bins.set(i, Binary.encode(bs.ref(i)));
    }
    Few out_bs = makeFew(3, 0);
    for (int i = 0; i < 3; i += 1) {
        out_bs.set(i, Binary.decode((byte[]) bins.ref(i)));
    }
    assertEquals(bs, out_bs);

    Few share_bins = makeFew(3, 0);
    for (int i = 0; i < 3; i += 1) {
        share_bins.set(i, Binary.encodeWithSharing(bs.ref(i)));
    }
    Few share_bs = makeFew(3, 0);
    for (int i = 0; i < 3; i += 1) {
        share_bs.set(i, Binary.decodeWithSharing((byte[]) share_bins.ref(i)));
    }
    assertEquals(bs, share_bs);
}

@Test
void testShort() {
    Few ss = few((short) 0x53AA, new short[]{0x2A, (short) 0xDEDE, -1});

    Few bins = makeFew(2, false);
    for (int i = 0; i < 2; i += 1) {
        bins.set(i, Binary.encode(ss.ref(i)));
    }
    Few out_ss = makeFew(2, false);
    for (int i = 0; i < 2; i += 1) {
        out_ss.set(i, Binary.decode((byte[]) bins.ref(i)));
    }
    assertEquals(ss, out_ss);

    Few share_bins = makeFew(2, false);
    for (int i = 0; i < 2; i += 1) {
        share_bins.set(i, Binary.encodeWithSharing(ss.ref(i)));
    }
    Few share_ss = makeFew(2, false);
    for (int i = 0; i < 2; i += 1) {
        share_ss.set(i, Binary.decodeWithSharing((byte[]) share_bins.ref(i)));
    }
    assertEquals(ss, share_ss);
}

@Test
void testInt() {
    Few ins = few(0xAABBCCDD, 53, new int[]{0, 1, -1, 0xFFFF});

    Few bins = makeFew(3, false);
    for (int i = 0; i < 3; i += 1) {
        bins.set(i, Binary.encode(ins.ref(i)));
    }
    Few out_ins = makeFew(3, false);
    for (int i = 0; i < 3; i += 1) {
        out_ins.set(i, Binary.decode((byte[]) bins.ref(i)));
    }
    assertEquals(ins, out_ins);

    Few share_bins = makeFew(3, false);
    for (int i = 0; i < 3; i += 1) {
        share_bins.set(i, Binary.encodeWithSharing(ins.ref(i)));
    }
    Few share_ins = makeFew(3, false);
    for (int i = 0; i < 3; i += 1) {
        share_ins.set(i, Binary.decodeWithSharing((byte[]) share_bins.ref(i)));
    }
    assertEquals(ins, share_ins);
}

@Test
void testLong() {
    Few ins = few(0xAABBCCDDL, 53, 0x12345678L, new long[]{1, -1, 0x123456789ABCDEFL});

    Few bins = makeFew(4, false);
    for (int i = 0; i < 4; i += 1) {
        bins.set(i, Binary.encode(ins.ref(i)));
    }
    Few out_ins = makeFew(4, false);
    for (int i = 0; i < 4; i += 1) {
        out_ins.set(i, Binary.decode((byte[]) bins.ref(i)));
    }
    assertEquals(ins, out_ins);

    Few share_bins = makeFew(4, false);
    for (int i = 0; i < 4; i += 1) {
        share_bins.set(i, Binary.encodeWithSharing(ins.ref(i)));
    }
    Few share_ins = makeFew(4, false);
    for (int i = 0; i < 4; i += 1) {
        share_ins.set(i, Binary.decodeWithSharing((byte[]) share_bins.ref(i)));
    }
    assertEquals(ins, share_ins);
}

@Test
void testFloat() {
    Few ins = few(2.531f, new float[]{0.123f, -1.123f, 11.23f});

    Few bins = makeFew(2, false);
    for (int i = 0; i < 2; i += 1) {
        bins.set(i, Binary.encode(ins.ref(i)));
    }
    Few out_ins = makeFew(2, false);
    for (int i = 0; i < 2; i += 1) {
        out_ins.set(i, Binary.decode((byte[]) bins.ref(i)));
    }
    assertEquals(ins, out_ins);

    Few share_bins = makeFew(2, false);
    for (int i = 0; i < 2; i += 1) {
        share_bins.set(i, Binary.encodeWithSharing(ins.ref(i)));
    }
    Few share_ins = makeFew(2, false);
    for (int i = 0; i < 2; i += 1) {
        share_ins.set(i, Binary.decodeWithSharing((byte[]) share_bins.ref(i)));
    }
    assertEquals(ins, share_ins);
}

@Test
void testDouble() {
    Few ins = few(2.531, 753.20466, new double[]{0.123, -1.123, 11.23});

    Few bins = makeFew(3, false);
    for (int i = 0; i < 3; i += 1) {
        bins.set(i, Binary.encode(ins.ref(i)));
    }
    Few out_ins = makeFew(3, false);
    for (int i = 0; i < 3; i += 1) {
        out_ins.set(i, Binary.decode((byte[]) bins.ref(i)));
    }
    assertEquals(ins, out_ins);

    Few share_bins = makeFew(3, false);
    for (int i = 0; i < 3; i += 1) {
        share_bins.set(i, Binary.encodeWithSharing(ins.ref(i)));
    }
    Few share_ins = makeFew(3, false);
    for (int i = 0; i < 3; i += 1) {
        share_ins.set(i, Binary.decodeWithSharing((byte[]) share_bins.ref(i)));
    }
    assertEquals(ins, share_ins);
}

@Test
void testCharAndString() {
    Few cs = few('$', '听',
                 "A journey of a thousand miles begins with a single step.",
                 "千里之行，始于足下。");

    Few bins = makeFew(4, false);
    for (int i = 0; i < 4; i += 1) {
        bins.set(i, Binary.encode(cs.ref(i)));
    }
    Few out_cs = makeFew(4, false);
    for (int i = 0; i < 4; i += 1) {
        out_cs.set(i, Binary.decode((byte[]) bins.ref(i)));
    }
    assertEquals(cs, out_cs);

    Few share_bins = makeFew(4, false);
    for (int i = 0; i < 4; i += 1) {
        share_bins.set(i, Binary.encodeWithSharing(cs.ref(i)));
    }
    Few share_cs = makeFew(4, false);
    for (int i = 0; i < 4; i += 1) {
        share_cs.set(i, Binary.decodeWithSharing((byte[]) share_bins.ref(i)));
    }
    assertEquals(cs, share_cs);
}

@Test
void testTimeAndDate() {
    Few misc = few(new Time(5313131, 2094),
                   new Date(2077, 4, 15, 17, 54, 16, 11235813, 8 * 3600));

    Few bins = makeFew(2, false);
    for (int i = 0; i < 2; i += 1) {
        bins.set(i, Binary.encode(misc.ref(i)));
    }
    Few out_misc = makeFew(2, false);
    for (int i = 0; i < 2; i += 1) {
        out_misc.set(i, Binary.decode((byte[]) bins.ref(i)));
    }
    assertEquals(misc, out_misc);

    Few share_bins = makeFew(2, false);
    for (int i = 0; i < 2; i += 1) {
        share_bins.set(i, Binary.encodeWithSharing(misc.ref(i)));
    }
    Few share_misc = makeFew(2, false);
    for (int i = 0; i < 2; i += 1) {
        share_misc.set(i, Binary.decodeWithSharing((byte[]) share_bins.ref(i)));
    }
    assertEquals(misc, share_misc);
}

@Test
void testFewAndLot() {
    Lot lt = lot(1, 2, 3, few(), few(lot()));
    Few fw = few(0, lt, -2, -3, -4);

    byte[] bin_lt = Binary.encode(lt);
    Lot out_lt = (Lot) Binary.decode(bin_lt);
    assertEquals(lt, out_lt);

    byte[] bin_fw = Binary.encode(fw);
    Few out_fw = (Few) Binary.decode(bin_fw);
    assertEquals(fw, out_fw);
}

@Test
void testFewAndLotWithSharing() {
    Lot lt = lot(1, 2, 3);
    Few fw = few(0, lt, -2, -3, -4);

    // Non-Identical Test
    byte[] bin_lt = Binary.encodeWithSharing(lt);
    Lot out_lt = (Lot) Binary.decodeWithSharing(bin_lt);
    assertEquals(lt, out_lt);

    byte[] bin_fw = Binary.encodeWithSharing(fw);
    Few out_fw = (Few) Binary.decodeWithSharing(bin_fw);
    assertEquals(fw, out_fw);

    // Identical Test
    setCdr(lt.cddr(), lt);
    bin_lt = Binary.encodeWithSharing(lt);
    out_lt = (Lot) Binary.decodeWithSharing(bin_lt);
    assertEquals(lt, out_lt);

    Lot ls = cons('a', cons(false, cons("abc", lt)));
    setCar(ls.cdr(), ls);
    byte[] bin_ls = Binary.encodeWithSharing(ls);
    Lot out_ls = (Lot) Binary.decodeWithSharing(bin_ls);
    assertEquals(ls, out_ls);

    fw.set(3, fw);
    bin_fw = Binary.encodeWithSharing(fw);
    out_fw = (Few) Binary.decodeWithSharing(bin_fw);
    assertEquals(fw, out_fw);
}
}