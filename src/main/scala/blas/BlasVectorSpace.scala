package blas

import spire.algebra.{VectorSpace,Field}
import com.github.fommil.netlib.BLAS

import scala.reflect.ClassTag

trait blasVectorSpace {

  /** A Type Class for Vector Spaces of `m × n` dense matrices, using BLAS L1 for operations.
    * As vector space methods operate on elements independently, an `m × n` array can simply be treated as 
    * an `m × 1` vector with the appropriate stride, and passed to L1 routines.
    * @tparm T Element type of the matrix, which must also be the same as the type of the field.
    *          This is a limitation of the library; but remember you can always implicitly upcast an e.g. `Int` to a `Double`.
    */
  abstract class VectorSpaceDenseMatrixInstance[T : Field, M <: Int, N <: Int]
      (implicit val blas : BLAS, val ct : ClassTag[T], val m : ValueOf[M], val n : ValueOf[N])
      extends L1GeneralDenseOps[T,M,N] with VectorSpace[DenseMatrix[T,M,N],T]  {
    lazy val size = valueOf[M]*valueOf[N]

    /** Returns a matrix with all elements negated. */
    def negate(x: Matrix): Matrix = SCAL(scalar.negate(scalar.one), x)

    /** Returns an m x n matrix with elements set to zero.
      * This implementation is optimized to allow fast return form certain BLAS routines, by setting stride = 0
      */
    val zero: Matrix = new DenseMatrix[T,M,N] {
      override def stride = 0
      def values = Array.fill(size)(scalar.zero)
      override def apply(i : Int, j : Int) = scalar.zero
    }

    /** Matrix addition */  
    override def plus(x: Matrix, y: Matrix): Matrix = (x,y) match {
      case (SCAL(α,x),y) => AXPY(α, x, y)
      case (x,SCAL(α,y)) => AXPY(α, y, x)
      case _ => AXPY(scalar.one, x, y)
    }

    /** Element-wise multiplication */
    def timesl(α: T, x: Matrix): Matrix = SCAL(α, x)

    /** Note that for the BLAS implementation of `Matrix`, vector spaces only exist over fields of equivalent element type, `T` */
    implicit def scalar: Field[T] = evidence$1
  }

  import spire.std.double._
  import blas.DenseMatrix
  implicit def denseMatrixOfDoubleVectorSpace[M <: Int : ValueOf, N <: Int : ValueOf](implicit blas : BLAS) : VectorSpaceDenseMatrixInstance[Double,M,N] = 
    new VectorSpaceDenseMatrixInstance[Double,M,N] {
    import blas._
  
    lazy val scal = dscal
    lazy val axpy = daxpy
    lazy val copy = dcopy
  }
}
