package spire.blas

import java.nio

/** Typeclass allowing access to the elements of [[Vector]] */
trait DenseVector[M <: Int, T]
extends spire.math.matrix.Vector[M, T] {

  /** Tagless/final representation of computation context. Alias for:
    * {{{ GeneralVector[M,T,B] => GeneralVector[M,T,B] }}}
    * This corresponds to `x + y` with the first term equal to `y`.
    */
  type Repr[m <: Int,t]

  type Buffer <: nio.Buffer
  
  /** Allows vectors to contain non-contiguous elements.  This allows
    * operations to only affect the <em>n</em>th element in-memory, useful for
    * domains that are vectors themselves, e.g.  dual numbers, polynomials,
    * complex numbers (although the later has a dedicated type in BLAS), and
    * allows row-major operations.
    *
    * @see BLAST § 2.6.6 
    */
  protected[blas] def stride(f: Repr[M,T]): Int

  trait Builder {
    def zero: Repr[M,T]
    def apply(x: T, xs: T*): Repr[M,T]
    def fromBuffer(buff: Buffer, stride: Int = 1): Repr[M,T]
  }
}

object DenseVector {
  type Aux[M <: Int, T, B <: nio.Buffer] = DenseVector[M,T] { type Buffer = B ; type Repr[m <: Int,t] = spire.blas.Vector[m,t,B] }
}
