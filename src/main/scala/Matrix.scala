package math

import scala.reflect.ClassTag

trait matrix {

  /**
    * An immutable 2-D Matrix.
    * @tparm T The type of the matrix elements.
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

  // trait MatrixInvariantOps[M <: Matrix[_,_,_]]

  /** 
    * A dense matrix, backed by an array with BLAS layout to support fast calculations with that library.
    */
  abstract class DenseMatrix[T : ClassTag,M,N] extends Matrix[T,M,N] {
    val values : Array[T]
    def rows : M
    def cols : N
    def stride : N = cols

  }

  object DenseMatrix extends DenseMatrixInstances{
   /** 
     * Creates a matrix of known dimensions, backed by an existing array. 
     */
    def fromArray[T : ClassTag, M <: Int, N <: Int](array : Array[T])(m : M, n : N) : DenseMatrix[T,M,N] =
      new DenseMatrix[T,M,N] {
        val (rows,cols) = (m,n)
        val values = {
          assert(m*n == array.length)
          array
        }
      }
  }

  trait DenseMatrixInstances {
    import spire.algebra.{VectorSpace,Field}
    import com.github.fommil.netlib._
    implicit def denseMatrixDoubleVectorSpace[M <: Int,N <: Int](implicit blas : BLAS, F : Field[Double]) : VectorSpace[DenseMatrix[Double,M,N],Double] = new VectorSpace[DenseMatrix[Double,M,N],Double] {
      import blas._
      // Members declared in algebra.ring.AdditiveGroup
      def negate(x: DenseMatrix[Double,M,N]): DenseMatrix[Double,M,N] = ???

      // Members declared in algebra.ring.AdditiveMonoid
      def zero: DenseMatrix[Double,M,N] = ???

      // Members declared in algebra.ring.AdditiveSemigroup
      def plus(x: DenseMatrix[Double,M,N],y: DenseMatrix[Double,M,N]): DenseMatrix[Double,M,N] = ???

      // Members declared in spire.algebra.Module
      def timesl(r: Double,v: DenseMatrix[Double,M,N]): DenseMatrix[Double,M,N] = {
        val a : Array[Double] = Array ofDim v.values.length
        dcopy(v.values.length, v.values, v.stride, a, v.stride)
        dscal(v.values.length, r, v.values, v.stride)
        DenseMatrix.fromArray[Double,M,N](a)(v.rows,v.cols)
      }

      // Members declared in spire.algebra.VectorSpace
      implicit def scalar: Field[Double] = F
    }
  } 
  /** As per BLAS spec for packing efficiently */
  /* trait ULMatrix[T,R,C] extends Matrix */
}




