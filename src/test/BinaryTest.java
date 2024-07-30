package test;

import essential.datetime.Date;
import essential.datetime.Time;
import essential.progresive.Lot;
import essential.utility.Binary;
import org.junit.jupiter.api.Test;

import static essential.progresive.Pr.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


class BinaryTest {

@Test
void testEncodeAndDecode() {
    Lot lt1 = lot(false, true, 73, 9531531L, 1.53902, '卡', "", "Stringing",
                  symbol("aa-si"), symbol("best"),
                  new Time(5313131, 2094),
                  new Date(2077, 4, 15, 17, 54, 16, 11235813, 8 * 3600));

    Lot lt2 = lot(new int[]{-1, -2, -3, -4, -5},
                  new int[0],
                  new long[]{6, 7, 8},
                  new long[0],
                  new double[]{13.17, 19.23, 29.31},
                  new double[0]);

    test(lt1);
    Lot lt3 = append(lt1, lt2);
    test(lotToFew(lt3));

    Lot lt4 = lot("six", lot(3, true, few(1, 1.13, lt1, lot()), few()), lt2);
    test(lt4);
}

void test(Object datum) {
    byte[] bin = Binary.encode(datum);
    Object item = Binary.decode(bin);
    assertEquals(datum, item);
}
}