package test;

import essential.utility.CheckSum;
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