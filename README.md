# Spire Matrix

An add-on to [spire](https://github.com/non/spire) for Matrices, with a default BLAS-backed implemenation.

## Features

### Algebra

An add-on to Spire that provides various Matrix data types (up to two dimensions) such as:

* Finite Matrix - A matrix with known dimemnsions m x n
* Square Matrix - A specialization of finite matrix, with dims m x m. 
* Upper/LowerTriangularMatrix - A matrix with zeros in the diagonals below/above the leading diagonal.
* Transpose - Symbolic representation of a transposed matrix ?

These have accompanying type-classes, such as MatrixMultiplication.

Data Type | Metric Space | 
==========================
FiniteMatrix ✓
SquareMatrix
UpperTriangualMatrix
BandedMatrix

### Design Goals

* Referential transparency and immutability. The idea is that this will help us to write safer code, and to make code as close as possible to mathematical expressions. The challenge is not to make a performance trade-off. In this sense this library is not trying to be another matlab/numpy/R.
* Type-safety through [singleton types](http://docs.scala-lang.org/sips/pending/42.type.html) as dimensional parameters, with various benefits e.g.
  * Catches dimension-mismatch in multiplication *at compile-time* (How often have you had a "dimension mismatch" runtime exception? )
  * IDE code-completion restricts only allowed operations, so you can't for example calculate the inverse of a rectangular matrix.
  * Support for infinite matrices and matrices with unknown dimensions, for example, to allow streaming calculations.
* Provide a default implementation with operations backed by calls to BLAS.
* Expressions are interpreted at runtime using lazy evaluation, allowing the most specialised subroutine to be picked dynamically. (It remains to be seen if this provides performance benefits). E.g. $ax + y$ matches the signature of `xAXPY` but $x + ay$ doesn't. The interpreter matches patterns like these to cover the widest possible use-cases, and should provide consistent performance regardless of how you write an expression.
* Minimal dependencies. With the exception of BLAS, this should be an extension of Spire.

## QuickStart


```
val A : Matrix[Double,2,2] = ???
val B : Matrix[Double,2,3] = ???

val C = A * B //doesn't compile

```
  

## Dependencies

    libraryDependencies ++= ( "org.typelevel" %% "spire-matrix-all" % 0.1 )

### Modules
`spire-matrix-all` pulls in all the dependencies. You can instead pick and choose modules:

* `spire-matrix-all`
  * `spire-matrix-core` imports live under `spire.std`. Note currently there are no default JVM implementations so this is just a set of traits.
    * `spire-matrix-blas` import live under `spire.std.matrix.blas`
  * `spire-pageant` Natural mathematical input (i.e. syntax) through UTF-16 operators and output via MathML, for use with notebook systems. TBC: probably move to it's own repo. This allows writing expressions such as $ val f = α X + x′ Y $.

* Typelevel 2.12.3 scala compiler. [type-literal support is required](https://github.com/typelevel/scala/blob/typelevel-readme/notes/typelevel-4.md#literal-types-pull5310-milesabin) for specifying matrix dimensions.
* netlib-java bindings.
* native BLAS/LAPACK (optional) libraries for performance.


## Related Work

Scala Alternatives:
* [Scala NLP Breeze](https://github.com/scalanlp/breeze/) Used by many machine learning libraries.
* [Luc J Bourhis' Spire Fork](https://github.com/luc-j-bourhis/spire/tree/topic/matrix-wip) An alternative implementation also backed by BLAS. Last updated 2013.

Netlib:
Most of the numerical routines are implemented via calls to Netlib's soubroutines in 
* [LAPACK](http://www.netlib.org/lapack/)
* [BLAS](http://www.netlib.org/blas/) which is distributed with LAPACK.
* [netlib-java](https://webcache.googleusercontent.com/search?q=cache:1OzhoqU_3uYJ:https://github.com/fommil/netlib-java/tree/master/netlib+&cd=1&hl=en&ct=clnk&gl=uk) Java bindings to netlib, which appears to have been discontinued, which unfortunately appears to have been [discontinued](https://stackoverflow.com/questions/46267411/has-netlib-java-been-discontinued)

