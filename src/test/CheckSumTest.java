/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package test;

import essential.utilities.CheckSum;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;


class CheckSumTest {

final String str = "Affection is desirable.";

@Test
void fletcher32() {
    int checksum = CheckSum.fletcher32(str.getBytes(StandardCharsets.UTF_8));
    assertEquals(0x66d30884, checksum);
}

@Test
void adler32() {
    int checksum = CheckSum.adler32(str.getBytes(StandardCharsets.UTF_8));
    assertEquals(0x66ea0885, checksum);
}
}