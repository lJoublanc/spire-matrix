package blas

import com.github.fommil.netlib.BLAS

import scala.reflect.ClassTag

protected[blas] trait blasOps {
  /** A Typeclass that uses BLAS to perform calculations.
    * The number of columns and rows must be known.
    * The type T must belong to the set of Reals, Doubles or Complex.
    * Traits derived from this implementation allocate an intermediate object on each operation, and only evaluates the
    * results once `Matrix.values` is materialized.
    * @tparm The element type of the matrix
    * @tparm M Number of rows.
    * @tparm N Number of columns.
    */
  abstract class L1Ops[T : ClassTag,M <: Int : ValueOf, N <: Int : ValueOf] {
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
    sealed trait L1Matrix extends DenseMatrix[T,M,N] { self : L1Matrix =>
      val x : Matrix 
      lazy val values : Array[T] = self match {
        case SCAL(alpha,x) => withCopyOf(x)(scal(x.size, alpha, _, x.stride))
        case AXPY(alpha,x,y) => withCopyOf(y)(axpy(y.size, alpha, x.values, x.stride, _, y.stride))
      }
    }

    // adding `final` modifier results in https://issues.scala-lang.org/browse/SI-4440
    case class SCAL(alpha : T, x : Matrix) extends L1Matrix
    case class AXPY(alpha : T, x : Matrix, y : Matrix) extends L1Matrix

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
}
