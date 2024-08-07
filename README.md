# Essential

Essential is a core component of the Aplan interpreter, designed to implement two Lisp containers:
list and vector. It also includes functions that operate on these containers. To avoid naming
conflicts, the term "lot" is used for list, and "few" is used for vector.

## Features

* Detects and prints circular lists and vectors in breadth or depth.
* Compares whether two cycles are equivalent in breadth or depth.