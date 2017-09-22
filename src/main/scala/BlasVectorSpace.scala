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
    * Traits derived from this implementation allocate an intermediate object on each operation, and only evaluates the
    * results once `Matrix.values` is materialized.
    * @tparm The element type of the matrix
    * @tparm M Number of rows.
    * @tparm N Number of columns.
    */
  protected abstract class BLASL1Ops[T : ClassTag,M <: Int : ValueOf, N <: Int : ValueOf] {
    type Matrix = DenseMatrix[T,M,N]

    /** The external BLAS library implementation */
    def blas : BLAS

    /* Table 1.3 : Vector Operations. */
    val scal : (Int, T, Array[T], Int) => Unit
    val axpy : (Int, T, Array[T], Int, Array[T], Int) => Unit

    /* Table 1.4 : Data Movement with Vecors. */
    val copy : (Int, Array[T], Int, Array[T], Int) => Unit

    /** Reification of Level 1 BLAS subroutine.
      * This trait is used to build up a description of the expression, to allow optimisation of compound expressions.
      */
    sealed trait L1 extends DenseMatrix[T,M,N] { self : L1 =>
      val x : Matrix 
      lazy val values : Array[T] = self match {
        case SCAL(alpha,x) => withCopyOf(x)(scal(x.size, alpha, _, x.stride))
        case AXPY(alpha,x,y) => withCopyOf(y)(axpy(y.size, alpha, x.values, x.stride, _, y.stride))
      }
    }

    // adding final results in https://issues.scala-lang.org/browse/SI-4440
    case class SCAL(alpha : T, x : Matrix) extends L1
    case class AXPY(alpha : T, x : Matrix, y : Matrix) extends L1

    /** Make a copy of the output argument so that `f` becomes immutable.
      * Many BLAS subroutines accumulate the result to one of the input parameters. This convenience function
      * can be used to create a copy of that parameter and pass it to the function, so it is not overwritten in-lace.
      */
    private def withCopyOf(x : Matrix)(f : Array[T] => Unit) : Array[T] = {
      val outBuff : Array[T] = Array ofDim x.size
      copy(x.size, x.values, x.stride, outBuff, x.stride)
      f(outBuff)
      outBuff
    }
  }

  /** A Type Class for Vector Spaces of `m` x `n` matrices, using BLAS for all operations.
    * @tparm T Element type of the matrix, which must also be the same as the type of the field.
    *          This is a limitation of the library; but remember you can always implicitly upcast an e.g. `Int` to a `Double`.
    */
  abstract class VectorSpaceMatrixInstance[T : Field : ClassTag, M <: Int : ValueOf, N <: Int : ValueOf]
      (implicit override val blas : BLAS) extends BLASL1Ops[T,M,N] with VectorSpace[DenseMatrix[T,M,N],T]  {
    lazy val size = valueOf[M]*valueOf[N]

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

    /** Note that for the BLAS implementation of `Matrix`, vector spaces only exist over fields of equivalent element type, `T` */
    implicit def scalar: Field[T] = evidence$4
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
