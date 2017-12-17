package spire.blas

import spire.math.matrix.FiniteMatrix

import scala.reflect.ClassTag

/** A dense matrix, backed by a column-major array with BLAS layout to support fast vectorised calculation.  */
abstract class DenseMatrix[T : ClassTag, M <: Int : ValueOf, N <: Int : ValueOf] extends FiniteMatrix[T,M,N] {
  def rows : M = valueOf[M]

  def cols : N = valueOf[N]

  /** An immutable, 1-D array containing the matrix elements, the columns of wich are assumed to be contiguous 
      in memory, and cols separated by [[rows]] elements. i.e. column-major (fortran) order.*/
  protected[blas] def values : Array[T]

  /** The element at position(i,j), whose array index is calculated at `i + j × stride`. */
  def apply(row : Int, col : Int) : T = values(row + col * rows)
}
