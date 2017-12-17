package spire.blas

import spire.math.matrix._
import scala.reflect.ClassTag

trait MatrixConstructors {
  implicit class MatrixConsOps(protected val companion : Matrix.type) {
    /** Creates a matrix of known dimensions, backed by an existing column-major array. */
    def fromDenseArray[T : ClassTag, M <: Int : ValueOf, N <: Int : ValueOf](array : Array[T]) : DenseMatrix[T,M,N] =
      new DenseMatrix[T,M,N] {
        val values : Array[T] = {
          assert(array.length == size, "Array size must match declared matrix size.")
          array
        }
      }

    /** @constructor Canonical constructor. */
    def apply[T : ClassTag, M <: Int : ValueOf, N <: Int : ValueOf](xs : T*) : DenseMatrix[T,M,N] =
      fromDenseArray[T,M,N](xs.toArray)

    /** Assumes 1-D column vector. */
    def vector[T : ClassTag, M <: Int : ValueOf](xs : T*) : companion.ColumnVector[T,M] = apply[T,M,1](xs : _*)
  }
}
