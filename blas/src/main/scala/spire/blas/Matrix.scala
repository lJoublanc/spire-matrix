package spire.blas

import spire.math.matrix
import scala.reflect.ClassTag

/** Extension methods for [[spire.math.matrix.Matrix]] companion. */
trait Matrix {
  implicit class MatrixConsOps(protected val companion : matrix.Matrix.type) {
    def apply[M <: Int, N <: Int] = new PartiallyAppliedMatrix[M,N]
  }
}

/** Helper to allow nice syntax `Matrix[3,3]` allowing compiler to infer data-type `T`. */
final class PartiallyAppliedMatrix[M <: Int , N <: Int] extends AnyRef {

  /** Creates a matrix of known dimensions, backed by an existing column-major array. */
  def fromDenseArray[T : ClassTag](array : Array[T])(implicit M : ValueOf[M], N : ValueOf[N]) : DenseMatrix[M, N, T] =
    new DenseMatrix[M, N, T] {
      val values : Array[T] = {
        assert(array.length == size, s"Array size (${array.length}) must match declared matrix size ($size).")
        array
      }
    }

  /** @constructor Canonical constructor. */
  def apply[T : ClassTag](xs : T*)(implicit M : ValueOf[M], N : ValueOf[N]): DenseMatrix[M, N, T] =
      fromDenseArray[T](xs.toArray)

}
