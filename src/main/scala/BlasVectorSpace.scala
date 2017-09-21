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
    * @tparm The element type of the matrix, which must also be the same as the type of the field.
    *        This is a limitation of the library; but remember you can always implicitly upcast an e.g. `Int` to a `Double`.
    * @tparm M Number of rows.
    * @tparm N Number of columns.
    */
  trait BLASMatrixOps[T,M <: Int, N <: Int] {
    /** The BLAS library implementation used to perform optimized calculations where possible */
    def blas : BLAS
    val blasCopy : (Int, Array[T], Int, Array[T], Int) => Unit
  }

  trait BLASMatrixApplicative[M <: Int, N <: Int, Mat[_,_ <: Int,_ <: Int] <: DenseMatrix[_,_ <: Int,_ <: Int]] extends Applicative[({ type m[x] = Mat[x,M,N] })#m ] {
    /** Returns the canonical bases (unit square) scaled by `a`. */
    def pure[A](a : A) : Mat[A,M,N] = ???
    /** Applies the function `ff` independently to each matrix element. */
    def ap[A,B](ff : Mat[A => B,M,N])(fa : Mat[A,M,N]) : F[B,M,N] = ???
  }

  abstract class VectorSpaceMatrixInstance[Mat[_,_ <: Int,_ <: Int] <: DenseMatrix[_,_ <: Int,_ <: Int], T : Field : ClassTag, M <: Int : ValueOf, N <: Int : ValueOf]
      (implicit override val blas : BLAS, A : Applicative[({ type m[x] = Mat[x,M,N] })#m])
      extends VectorSpace[Mat[T,M,N],T] with BLASMatrixOps[T,M,N] {
    type Matrix = Mat[T,M,N]

    /** Level 1 BLAS subroutine.
      * Subclasses of this trait delegate BLAS calls to the implementing/specializing class, via 
      * the `values` member, which materialize the result.
      */
    protected sealed trait L1BLAS extends DenseMatrix[T,M,N] { val x : Matrix }
    protected case class SCAL(alpha : T, x : Matrix) extends L1BLAS
    protected case class AXPY(alpha : T, x : Matrix, y : Matrix) extends L1BLAS

    def negate(x: Matrix): Matrix = SCAL(scalar.negate(scalar.one), x)

    def zero: Matrix = A.pure(scalar.zero)

    def plus(x: Matrix,y: Matrix): Matrix = (x,y) match {
      case (SCAL(a,x),y) => AXPY(a, x, y)
      case (x,SCAL(a,y)) => AXPY(a, y, x)
      case _ => AXPY(scalar.one, x, y)
    }

    def timesl(r: T,v: Matrix): Matrix = SCAL(r, v)

    implicit def scalar: Field[T] = evidence$1

    /** Make a copy of the output argument so that `f` becomes immutable.
      * Many BLAS subroutines accumulate the result to one of the input parameters. This convenience function
      * can be used to create a copy of that parameter and passing it to the function, so it is not over-written.
      */
    protected def withCopyOf(x : Matrix)(f : Array[T] => Unit) : Array[T] = {
      val outBuff : Array[T] = Array ofDim x.values.length
      blasCopy(x.values.length, x.values, x.stride, outBuff, x.stride)
      f(outBuff)
      outBuff
    }
  }

  import spire.std.double._
  import blas.DenseMatrix
  implicit def denseMatrixOfDoubleVectorSpace[M <: Int : ValueOf, N <: Int : ValueOf]
      (implicit blas : BLAS, A : Applicative[({ type m[x] = DenseMatrix[x,M,N] })#m]) :
      VectorSpaceMatrixInstance[DenseMatrix,Double,M,N] = 
    new VectorSpaceMatrixInstance[DenseMatrix,Double,M,N] {
    import blas._

    protected case class SCAL(alpha : Double, x : Matrix) extends L1BLAS {
      override lazy val values = withCopyOf(x)(dscal(x.values.length, alpha, _, x.stride))
    }

    protected case class AXPY(alpha : Double, x : Matrix, y : Matrix) extends L1BLAS {
      override lazy val values = withCopyOf(y)(daxpy(y.values.length, alpha, x.values, x.stride, _, y.stride))
    }

    def blasCopy = dcopy
  }
}
