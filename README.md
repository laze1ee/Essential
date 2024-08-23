# Essential

Essential is a core component of the Bach interpreter, mainly designed to
implement two Lisp containers: list and vector. To avoid naming conflicts, the
term "Lot" is used for list, and "Few" is used for vector.

## Features

* Detects and prints circular lists and vectors regardless of breadth or depth.
* Compares whether two cycles are equivalent in breadth or depth.
* Data structure AVL Tree.
* Data structure Red Black Tree.
* Data structure Queue based on Lot.
* Unified hashCode method based on Binary utility.
* More concise datetime utility.