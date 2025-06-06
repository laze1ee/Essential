/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;


class CheckSumTest {

private final byte[] bin;

CheckSumTest() {
  String current_dir = System.getProperty("user.dir");
  Path file = Path.of(current_dir, "test/essential/utilities/test-checksum.txt");
  String text;
  try {
    text = Files.readString(file);
  } catch (IOException e) {
    throw new RuntimeException(e);
  }
  bin = text.getBytes(StandardCharsets.UTF_8);
}

@Test
void adler32() {
  int checksum = CheckSum.adler32(bin);
  System.out.printf("adler32: %X\n", checksum);
  assertEquals(0x1AC2823D, checksum);
}

@Test
void fletcher32() {
  int checksum = CheckSum.fletcher32(bin);
  System.out.printf("fletcher32: %X\n", checksum);
  assertEquals(0xD09681B0, checksum);
}
}