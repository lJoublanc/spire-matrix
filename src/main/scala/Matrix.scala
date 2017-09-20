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
    def apply[T : ClassTag, M <: Int : ValueOf](xs : T*)(implicit one : ValueOf[1]) : ColumnVector[T,M] = 
      blas.DenseMatrix.fromArray[T,M,1](xs.toArray)
  }
}




