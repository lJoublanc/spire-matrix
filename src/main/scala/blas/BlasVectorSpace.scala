package blas

import spire.algebra.{VectorSpace,Field}
import com.github.fommil.netlib.BLAS

import scala.reflect.ClassTag

trait blasVectorSpace {

  /** A Type Class for Vector Spaces of `m` x `n` matrices, using BLAS for all operations.
    * @tparm T Element type of the matrix, which must also be the same as the type of the field.
    *          This is a limitation of the library; but remember you can always implicitly upcast an e.g. `Int` to a `Double`.
    */
  abstract class VectorSpaceMatrixInstance[T : Field : ClassTag, M <: Int : ValueOf, N <: Int : ValueOf]
      (implicit val blas : BLAS) extends L1Ops[T,M,N] with VectorSpace[DenseMatrix[T,M,N],T]  {
    lazy val size = valueOf[M]*valueOf[N]

    /** Returns a matrix with all elements negated. */
    def negate(x: Matrix): Matrix = SCAL(scalar.negate(scalar.one), x)

    /** Returns an m x n matrix with elements set to zero.*/
    lazy val zero: Matrix = Matrix.fromDenseArray[T,M,N](Array.fill(size)(scalar.zero))

    /** Matrix addition */  
    def plus(x: Matrix,y: Matrix): Matrix = (x,y) match {
      case (SCAL(a,x),y) => AXPY(a, x, y)
      case (x,SCAL(a,y)) => AXPY(a, y, x)
      case _ => AXPY(scalar.one, x, y)
    }

    /** Element-wise multiplication */
    def timesl(r: T,v: Matrix): Matrix = SCAL(r, v)

    /** Note that for the BLAS implementation of `Matrix`, vector spaces only exist over fields of equivalent element type, `T` */
    implicit def scalar: Field[T] = evidence$1
  }

  import spire.std.double._
  import blas.DenseMatrix
  implicit def denseMatrixOfDoubleVectorSpace[M <: Int : ValueOf, N <: Int : ValueOf](implicit blas : BLAS) :
      VectorSpaceMatrixInstance[Double,M,N] = 
    new VectorSpaceMatrixInstance[Double,M,N] {
    import blas._

    lazy val copy = dcopy
    lazy val scal = dscal
    lazy val axpy = daxpy
  }
}
