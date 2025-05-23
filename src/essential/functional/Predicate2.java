/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.functional;

/**
 * Predicate functional interface with 2 arguments.
 */
@FunctionalInterface
public interface Predicate2 {
boolean apply(Object o1, Object o2);
}
