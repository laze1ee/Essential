/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import essential.progressive.Lot;
import org.junit.jupiter.api.Test;


class RandGeneratorTest {

@Test
void string() {
  String text = "Unity is strength when there is teamwork.";
  String str2 = RandGenerator.string(text, 10);
  System.out.println(str2);
}

@Test
void shuffle() {
  Lot lt = Lot.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
  Lot ls = RandGenerator.shuffle(lt);
  System.out.println(ls);
}
}