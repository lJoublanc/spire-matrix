package spire.std

import spire.algebra.Eq
import spire.math.FiniteMatrix

import spire.implicits._

/** Implementing modules can use this for a naive equality implementation in the spire.math.matrix module */
trait MatrixInstance {

  /** Naive implementation of matrix equality. Checks element-by-element equivalence. */
  class FiniteMatrixEq[Mat[t,m <: Int, n <: Int] <: FiniteMatrix[t,m,n], T : Eq, M <: Int, N <: Int] extends Eq[Mat[T, M, N]] {
    def eqv(x : Mat[T,M,N], y : Mat[T,M,N]) : Boolean = 
      ( for ( i <- 0 until x.rows; j <- 0 until x.cols) yield x(i,j) === y(i,j) ) reduce (_ && _)
  }

  implicit def finiteMatrixEqInstance[Mat[t,m <: Int, n <: Int] <: FiniteMatrix[t,m,n], T : Eq, M <: Int, N <: Int] : Eq[Mat[T, M, N]] =
    new FiniteMatrixEq[Mat,T, M, N]
}
