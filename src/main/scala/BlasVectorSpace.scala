package math

import spire.algebra.{VectorSpace,Field}
import com.github.fommil.netlib.BLAS
import blas.DenseMatrix
import cats.Applicative

import scala.reflect.ClassTag

trait blasVectorSpace {

  /** A Typeclass that uses BLAS to perform calculations.
    * The number of columns and rows must be known.
    * The type T must belong to the set of Reals, Doubles or Complex.
    * This implementation allocates an intermediate object on each operation, and only evaluates the
    * results once `Matrix.values` is materialized.
    * @tparm The element type of the matrix
    * @tparm M Number of rows.
    * @tparm N Number of columns.
    */
  trait BLASMatrixOps[T,M <: Int, N <: Int] {
    /** The BLAS library implementation used to perform optimized calculations where possible  */
    def blas : BLAS
    protected val xcopy : (Int, Array[T], Int, Array[T], Int) => Unit
  }

  /** A Type Class for Vector Spaces of `m` x `n` matrices, using BLAS for all operations.
    * @tparm T Element type of the matrix, which must also be the same as the type of the field.
    *          This is a limitation of the library; but remember you can always implicitly upcast an e.g. `Int` to a `Double`.
    */
  abstract class VectorSpaceMatrixInstance[T : Field : ClassTag, M <: Int : ValueOf, N <: Int : ValueOf]
      (implicit override val blas : BLAS) extends VectorSpace[DenseMatrix[T,M,N],T] with BLASMatrixOps[T,M,N] {
    type Matrix = DenseMatrix[T,M,N]
    lazy val size = valueOf[M]*valueOf[N]

    /** BLAS subroutines.
      * Implementing classes need to define these to resolve to the correct data type.
      */
    protected val xscal : (Int, T, Array[T], Int) => Unit
    protected val xaxpy : (Int, T, Array[T], Int, Array[T], Int) => Unit

    /** Reify Level 1 BLAS subroutine.
      * This trait is used to build up a description of the expression, to allow optimisation where possible.
      */
    protected sealed trait L1BLAS extends DenseMatrix[T,M,N] { self : L1BLAS =>
      val x : Matrix 
      lazy val values : Array[T] = self match {
        case SCAL(alpha,x) => withCopyOf(x)(xscal(x.values.length, alpha, _, x.stride))
        case AXPY(alpha,x,y) => withCopyOf(y)(xaxpy(y.values.length, alpha, x.values, x.stride, _, y.stride))
      }
    }
    protected case class SCAL(alpha : T, x : Matrix) extends L1BLAS
    protected case class AXPY(alpha : T, x : Matrix, y : Matrix) extends L1BLAS

    /* Vector Space Implementation */

    /** Returns a matrix with all elements negated. */
    def negate(x: Matrix): Matrix = SCAL(scalar.negate(scalar.one), x)

    /** Returns an m x n matrix with elements set to zero.*/
    lazy val zero: Matrix = DenseMatrix.fromArray[T,M,N](Array.fill(size)(scalar.zero))

    /** Matrix addition */  
    def plus(x: Matrix,y: Matrix): Matrix = (x,y) match {
      case (SCAL(a,x),y) => AXPY(a, x, y)
      case (x,SCAL(a,y)) => AXPY(a, y, x)
      case _ => AXPY(scalar.one, x, y)
    }

    /** Element-wise multiplication */
    def timesl(r: T,v: Matrix): Matrix = SCAL(r, v)

    implicit def scalar: Field[T] = evidence$1

    /** Make a copy of the output argument so that `f` becomes immutable.
      * Many BLAS subroutines accumulate the result to one of the input parameters. This convenience function
      * can be used to create a copy of that parameter and passing it to the function, so it is not over-written.
      */
    private def withCopyOf(x : Matrix)(f : Array[T] => Unit) : Array[T] = {
      val outBuff : Array[T] = Array ofDim x.values.length
      xcopy(x.values.length, x.values, x.stride, outBuff, x.stride)
      f(outBuff)
      outBuff
    }
  }

  import spire.std.double._
  import blas.DenseMatrix
  implicit def denseMatrixOfDoubleVectorSpace[M <: Int : ValueOf, N <: Int : ValueOf]
      (implicit blas : BLAS, A : Applicative[({ type m[x] = DenseMatrix[x,M,N] })#m]) :
      VectorSpaceMatrixInstance[Double,M,N] = 
    new VectorSpaceMatrixInstance[Double,M,N] {
    import blas._

    lazy val xcopy = dcopy
    lazy val xscal = dscal
    lazy val xaxpy = daxpy
  }
}
