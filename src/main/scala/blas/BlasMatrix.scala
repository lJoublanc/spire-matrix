package blas

import math.{FiniteMatrix,ColumnVector}

import scala.reflect.ClassTag

trait blasMatrix {
  /** A dense matrix, backed by a column-major array with BLAS layout to support fast vectorised calculation.  */
  abstract class DenseMatrix[T : ClassTag, M <: Int : ValueOf, N <: Int : ValueOf] extends FiniteMatrix[T,M,N] {
    def rows : M = valueOf[M]

    def cols : N = valueOf[N]

    /** An immutable, 1-D array containing the matrix elements, the columns of wich are assumed to be contiguous 
        in memory, and rows separated by [[stride]] elements. i.e. column-major (fortran) order.*/
    protected[blas] def values : Array[T]

    /** Number of array elements between consecutive rows, or equivalently, the size of the leading dimension (column).
      * This value has special significance for some BLAS functions, when set to zero. 
      */
    protected[blas] def stride : Int = cols

    /** The element at position(i,j), whose array index is calculated at `i + j × stride`. */
    def apply(row : Int, col : Int) : T = values(row + col * stride)
  }

  object Matrix {
    /** Creates a matrix of known dimensions, backed by an existing column-major array.  */
    def fromDenseArray[T : ClassTag, M <: Int : ValueOf, N <: Int : ValueOf](array : Array[T]) : DenseMatrix[T,M,N] =
      new DenseMatrix[T,M,N] {
        val values : Array[T] = {
          assert(array.length == size, "Array size must match declared matrix size.")
          array
        }
      }

    /** Convenience constructor */
    def apply[T : ClassTag, M <: Int : ValueOf, N <: Int : ValueOf](xs : T*) : DenseMatrix[T,M,N] =
      fromDenseArray[T,M,N](xs.toArray)
  }

  object ColumnVector {
    /** Convenience constructor */
    def apply[T : ClassTag, M <: Int : ValueOf](xs : T*) : ColumnVector[T,M] = {
      implicit val n = valueOf[1]
      Matrix.fromDenseArray[T,M,1](xs.toArray)
    }
  }

  /** As per BLAS spec for packing efficiently */
  /* trait ULMatrix[T,R,C] extends Matrix */
}
