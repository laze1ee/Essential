# Essential
Essential is a core component of the Aplan interpreter, designed to implement two Lisp containers:
list and vector. It also includes functions that operate on these containers. To avoid naming
conflicts, the term "lot" is used for list, and "few" is used for vector.

## Features
* Detects and prints circular lists and vectors.
* Compares whether two cycles are equivalent.
* regardless of circularity in breadth or depth.