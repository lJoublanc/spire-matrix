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
    /** Number of rows in the matrix */
    def rows : M

    /** Number of columns in the matrix */
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

  /** A `ColumnVector` is just a type alias for a `M` x 1 matrix. */
  type ColumnVector[T,M <: Int] = FiniteMatrix[T,M,1]

  /** A `ColumnVector` is just a type alias for a `M` x `M` matrix. */
  type SquareMatrix[T, M <: Int] = FiniteMatrix[T,M,M]

}




