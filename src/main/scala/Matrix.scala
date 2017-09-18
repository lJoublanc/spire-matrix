package math

import scala.reflect.ClassTag

trait matrix {

  /**
    * An immutable 2-D Matrix.
    * @tparm T The type of the primitive data.
    * @tparm M Number of rows (may be abstract).
    * @tparm N Number of columns (may be abstract).
    */
  sealed trait Matrix[T,M,N]

  object Matrix {
    /* By default, creates a dense matrix */
    def fromArray[T : ClassTag, M <: Int, N <: Int](array : Array[T])(m : M, n : N) : Matrix[T,M,N] = 
      DenseMatrix.fromArray(array)(m,n)

    /* Convenience function for creating dense matrix of known size */
    def apply[T : ClassTag,M <: Int,N <: Int](xs : T*)(m : M, n : N) : Matrix[T,M,N] = fromArray(xs.toArray)(m,n)
  }

  /** 
    * A dense matrix, backed by an array with BLAS layout to support fast calculations with that library.
    */
  abstract class DenseMatrix[T : ClassTag,R,C] extends Matrix[T,R,C] {
    val values : Array[T]
    val stride : Int
  }

  object DenseMatrix {
   /** 
     * Creates a matrix of known dimensions, backed by an existing array. 
     */
    def fromArray[T : ClassTag, M <: Int, N <: Int](array : Array[T])(m : M, n : N) : DenseMatrix[T,M,N] =
      new DenseMatrix[T,M,N] {
        val stride = n
        val values = {
          assert(m*n == array.length)
          array
        }
      }
  }
  /** As per BLAS spec for packing efficiently */
  /* trait ULMatrix[T,R,C] extends Matrix */
}
