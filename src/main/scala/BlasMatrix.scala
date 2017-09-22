package math

import scala.reflect.ClassTag

trait blasMatrix {
  /** 
    * A dense matrix, backed by a column-major array with BLAS layout to support fast calculations with that library.
    */
  abstract class DenseMatrix[T : ClassTag, M <: Int : ValueOf, N <: Int : ValueOf] extends FiniteMatrix[T,M,N] {
    def rows : M = valueOf[M]
    def cols : N = valueOf[N]
    protected[math] def values : Array[T]
    protected[math] def stride : M = rows
    def apply(row : Int, col : Int) : T = values(row * stride + col)
  }

  object DenseMatrix extends blasVectorSpace {
   /** 
     * Creates a matrix of known dimensions, backed by an existing array. 
     */
    def fromArray[T : ClassTag, M <: Int : ValueOf, N <: Int : ValueOf](array : Array[T]) : DenseMatrix[T,M,N] =
      new DenseMatrix[T,M,N] {
        val values = {
          assert(rows*cols == array.length)
          array
        }
      }
  }

  /** As per BLAS spec for packing efficiently */
  /* trait ULMatrix[T,R,C] extends Matrix */
}
