/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import org.jetbrains.annotations.NotNull;


public class CheckSum {

private static final int FLETCHER_MOD = 65535;
private static final int ADLER_MOD = 65521;

public static int fletcher32(byte @NotNull [] bin) {
    int sum0 = 0;
    int sum1 = 0;
    int i = 0;
    int len = bin.length;
    while (len > 0) {
        int block = len;
        if (block > 4103) {
            block = 4103;
        }
        len -= block;
        for (; 0 < block; block -= 1, i += 1) {
            sum0 = (bin[i] & 0xFF) + sum0;
            sum1 = sum0 + sum1;
        }
        sum0 = sum0 % FLETCHER_MOD;
        sum1 = sum1 % FLETCHER_MOD;
    }
    return (sum1 << 16) | sum0;
}


public static int adler32(byte @NotNull [] bin) {
    int sum0 = 1;
    int sum1 = 0;
    int i = 0;
    int len = bin.length;
    while (len > 0) {
        int block = len;
        if (block > 4095) {
            block = 4095;
        }
        len -= block;
        for (; 0 < block; block -= 1, i += 1) {
            sum0 = sum0 + (bin[i] & 0xFF);
            sum1 = sum1 + sum0;
        }
        sum0 %= ADLER_MOD;
        sum1 %= ADLER_MOD;
    }
    return (sum1 << 16) | sum0;
}
}
