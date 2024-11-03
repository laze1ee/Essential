/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

import essential.datetime.Date;
import essential.datetime.Time;
import essential.progresive.Few;
import essential.progresive.Lot;
import essential.utilities.Binary;
import org.junit.jupiter.api.Test;

import static essential.progresive.Pr.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SuppressWarnings("DuplicatedCode")
class BinaryTest {

@Test
void testI32() {
    Few ins = few(0x35, -0x0F, 0x1FF, -0x1FFF, 0xF2345, -0xABCDE,
                  0x1234567, -0x7654321, 0x12345678, -0x12345678);
    Few bins = makeFew(10, false);
    for (int i = 0; i < 10; i += 1) {
        fewSet(bins, i, Binary.encodeI32((int) fewRef(ins, i)));
    }
    Few out_ins = makeFew(10, false);
    for (int i = 0; i < 10; i += 1) {
        byte[] bs = (byte[]) fewRef(bins, i);
        int sz = Binary.sizeOfI32(bs, 0);
        fewSet(out_ins, i, Binary.decodeI32(bs, 0, sz));
    }
    assertTrue(equal(ins, out_ins));
}

@Test
void testBoolean() {
    Few bs = few(true, false, new boolean[]{true, false, true, false, false, true, true});
    Few bins = makeFew(3, 0);
    for (int i = 0; i < 3; i += 1) {
        fewSet(bins, i, Binary.encode(fewRef(bs, i)));
    }
    Few out_bs = makeFew(3, 0);
    for (int i = 0; i < 3; i += 1) {
        fewSet(out_bs, i, Binary.decode((byte[]) fewRef(bins, i)));
    }
    assertTrue(equal(bs, out_bs));
}

@Test
void testShort() {
    Few ss = few((short) 0x53AA, new short[]{0x2A, (short) 0xDEDE, -1});
    Few bins = makeFew(2, false);
    for (int i = 0; i < 2; i += 1) {
        fewSet(bins, i, Binary.encode(fewRef(ss, i)));
    }
    Few out_ss = makeFew(2, false);
    for (int i = 0; i < 2; i += 1) {
        fewSet(out_ss, i, Binary.decode((byte[]) fewRef(bins, i)));
    }
    assertTrue(equal(ss, out_ss));
}

@Test
void testInt() {
    Few ins = few(0xAABBCCDD, 53, new int[]{0, 1, -1, 0xFFFF});
    Few bins = makeFew(3, false);
    for (int i = 0; i < 3; i += 1) {
        fewSet(bins, i, Binary.encode(fewRef(ins, i)));
    }
    Few out_ins = makeFew(3, false);
    for (int i = 0; i < 3; i += 1) {
        fewSet(out_ins, i, Binary.decode((byte[]) fewRef(bins, i)));
    }
    assertTrue(equal(ins, out_ins));
}

@Test
void testLong() {
    Few ins = few(0xAABBCCDDL, 53, 0x12345678L, new long[]{1, -1, 0x123456789ABCDEFL});
    Few bins = makeFew(4, false);
    for (int i = 0; i < 4; i += 1) {
        fewSet(bins, i, Binary.encode(fewRef(ins, i)));
    }
    Few out_ins = makeFew(4, false);
    for (int i = 0; i < 4; i += 1) {
        fewSet(out_ins, i, Binary.decode((byte[]) fewRef(bins, i)));
    }
    assertTrue(equal(ins, out_ins));
}

@Test
void testFloat() {
    Few ins = few(2.531f, new float[]{0.123f, -1.123f, 11.23f});
    Few bins = makeFew(2, false);
    for (int i = 0; i < 2; i += 1) {
        fewSet(bins, i, Binary.encode(fewRef(ins, i)));
    }
    Few out_ins = makeFew(2, false);
    for (int i = 0; i < 2; i += 1) {
        fewSet(out_ins, i, Binary.decode((byte[]) fewRef(bins, i)));
    }
    assertTrue(equal(ins, out_ins));
}

@Test
void testDouble() {
    Few ins = few(2.531, 753.20466, new double[]{0.123, -1.123, 11.23});
    Few bins = makeFew(3, false);
    for (int i = 0; i < 3; i += 1) {
        fewSet(bins, i, Binary.encode(fewRef(ins, i)));
    }
    Few out_ins = makeFew(3, false);
    for (int i = 0; i < 3; i += 1) {
        fewSet(out_ins, i, Binary.decode((byte[]) fewRef(bins, i)));
    }
    assertTrue(equal(ins, out_ins));
}

@Test
void testCharAndString() {
    Few cs = few('$', '听',
                 "A journey of a thousand miles begins with a single step.",
                 "千里之行，始于足下。");
    Few bins = makeFew(4, false);
    for (int i = 0; i < 4; i += 1) {
        fewSet(bins, i, Binary.encode(fewRef(cs, i)));
    }
    Few out_cs = makeFew(4, false);
    for (int i = 0; i < 4; i += 1) {
        fewSet(out_cs, i, Binary.decode((byte[]) fewRef(bins, i)));
    }
    assertTrue(equal(cs, out_cs));
}

@Test
void testTimeAndDate() {
    Few misc = few(new Time(5313131, 2094),
                   new Date(2077, 4, 15, 17, 54, 16, 11235813, 8 * 3600));
    Few bins = makeFew(2, false);
    for (int i = 0; i < 2; i += 1) {
        fewSet(bins, i, Binary.encode(fewRef(misc, i)));
    }
    Few out_misc = makeFew(2, false);
    for (int i = 0; i < 2; i += 1) {
        fewSet(out_misc, i, Binary.decode((byte[]) fewRef(bins, i)));
    }
    assertTrue(equal(misc, out_misc));
}

@Test
void testLotAndFew() {
    Lot lt = lot(1, 2, 3);
    Few fw = few(0, lt, -2, -3, -4);

    // Non-Identical Test
    byte[] bin_lt = Binary.encode(lt);
    Lot out_lt = (Lot) Binary.decode(bin_lt);
    assertTrue(equal(lt, out_lt));

    byte[] bin_fw = Binary.encode(fw);
    Few out_fw = (Few) Binary.decode(bin_fw);
    assertTrue(equal(fw, out_fw));

    // Identical Test
    setCdr(cddr(lt), lt);
    bin_lt = Binary.encode(lt);
    out_lt = (Lot) Binary.decode(bin_lt);
    assertTrue(equal(lt, out_lt));

    Lot ls = cons('a', cons(false, cons("abc", lt)));
    setCar(cdr(ls), ls);
    byte[] bin_ls = Binary.encode(ls);
    Lot out_ls = (Lot) Binary.decode(bin_ls);
    assertTrue(equal(ls, out_ls));

    fewSet(fw, 3, fw);
    bin_fw = Binary.encode(fw);
    out_fw = (Few) Binary.decode(bin_fw);
    assertTrue(equal(fw, out_fw));
}
}