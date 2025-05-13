/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

class Msg {

// Binary
static final String INVALID_RANGE = "invalid byte array range [%s %s]";
static final String UNSUPPORTED = "unsupported type %s for encoding";
static final String UNMATCHED_BIN_LABEL = "unmatched binary label %s in index %s";

// Tree
static final String INVALID_RB_NODE = "invalid red-black-tree node:\n%s";
static final String INVALID_AVL_NODE = "invalid AVL-tree node:\n%s";
static final String EMPTY_TREE = "empty tree";
static final String NOT_PRESENT = "key %s is not present in tree %s";

// Queue
static final String EMPTY_QUEUE = "empty queue";
}
