package math

import scala.reflect.ClassTag

trait matrix {

  /**
    * An immutable 2-D Matrix.
    * @tparm T The type of the matrix elements.
    * @tparm M Number of rows (may be abstract).
    * @tparm N Number of columns (may be abstract).
    */
  trait Matrix[T,M,N] {
    def rows : M
    def cols : N
  }

  /** A matrix with known dimensions, supporting safe indexing */
  trait FiniteMatrix[T,M <: Int, N <: Int] extends Matrix[T,M,N] with Function2[Int,Int,T] {
    def rows : M
    def cols : N
    /** The number of elements in the matrix, m x n */
    def size : Int /* M x N */ = rows * cols
    /** The element at position i,j. */
    def apply(row : Int, col : Int) : T
  }

  object Matrix {
    /* The default implementation is [[blasMatrix.DenseMatrix]] */
    def apply[T : ClassTag, M <: Int : ValueOf, N <: Int : ValueOf](xs : T*) : blas.DenseMatrix[T,M,N] =
      blas.DenseMatrix.fromArray(xs.toArray)
  }

  // trait MatrixInvariantOps[M <: Matrix[_,_,_]] { def copy : M ; def view : M; def uninitialized : M}

  /** A `ColumnVector` is just a type alias for a `M` x 1 matrix. */
  type ColumnVector[T,M <: Int] = blas.DenseMatrix[T,M,1]

  object ColumnVector {
    /* The default implementation is [[blasMatrix.DenseMatrix]] */
    def apply[T : ClassTag, M <: Int : ValueOf](xs : T*) : ColumnVector[T,M] = {
      implicit val one = valueOf[1]
      blas.DenseMatrix.fromArray[T,M,1](xs.toArray)
    }
  }
}




