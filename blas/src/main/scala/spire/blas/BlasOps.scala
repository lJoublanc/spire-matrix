package spire.blas

import com.github.fommil.netlib.BLAS

import scala.reflect.ClassTag
import scala.annotation.implicitNotFound

// This is not a package object because it can't be defined protected.
protected[blas] object blasOps {

  /** Level 1 BLAS routines.
    *
    * Note that, while these are toted as "Vector" operations, any method that operates on an `m × n` matrix can be
    * treated as an `m × 1` vector with `stride = 1`, and passed to L1 routines.
    *
    * Implementation note: Derived typeclasses implement operations lazily, in order to allow operator fusion: 
    * pattern matching over expressions to find the 'widest' possible BLAS subroutine to apply. For example, 
    * The signature of the expression `X + α Y` does not match the blas subroutine `_AXPY` (the scalar should
    * pre-multiply `X`), but we can pattern match and use properties of addition (commutativity) to re-arrange the 
    * expression to match it's signature. The actual matching step is implemented in the aglebra type-classes.
    *
    * @tparam T The element type of the matrix
    * @tparam M Number of rows.
    * @see BLAST § 2.5.3 Array Arguments
    */
  @implicitNotFound("Most common cause is dimension mismatch or missing implicit BLAS library instance.")
  protected[blas] trait L1GeneralDenseOps[M <: Int, T] {
    type Vector = DenseVector[M, T]
    implicit val ct : ClassTag[T]
    implicit val M : ValueOf[M]

    /** The external BLAS library implementation */
    def blas : BLAS

    /* § 2.8.4 : Vector Operations. */
    val scal : (Int, T, Array[T], Int) => Unit

    val axpy : (Int, T, Array[T], Int, Array[T], Int) => Unit

    /* § 2.8.5 : Data Movement with Vecors. */
    val copy : (Int, Array[T], Int, Array[T], Int) => Unit

    /** Reification of Level 1 BLAS subroutine.
      * This trait is used to build up a description of the expression, to allow optimisation of compound expressions.
      * All matrices are treated as 1-D, regardless of dim, as L1 subroutines take vectors as arguments. //TODO: investigate whether this affects performance?
      * @see <href a=https://stackoverflow.com/questions/15498187/incx-in-blas-routines>stackoverflow question</href>
      */
    sealed trait L1Vector extends DenseVector[M, T] { self : L1Vector =>
      def size : M = valueOf[M]
      lazy val avalues : Array[T] = self match {
        case SCAL(α,x) => withCopyOf(x)(scal(x.size, α, _, x.stride))
        case AXPY(α,x,y) => withCopyOf(y)(axpy(y.size, α, x.avalues, x.stride, _, y.stride))
      }
    }

    /* Adding `final` modifier results in [[https://issues.scala-lang.org/browse/SI-4440]] */
    case class SCAL(α : T, x : Vector) extends L1Vector { def stride = x.stride }

    case class AXPY(α : T, x : Vector, y : Vector) extends L1Vector { def stride = y.stride }

    /** Make a copy of the output argument so that `f` becomes referentially transparent.
      * Many BLAS subroutines accumulate the result into one of the input parameters. This convenience function
      * can be used to create a copy of that parameter and pass it to the function, so it is not overwritten in-place.
      * @param x the mutable 'output' parameter, a copy of which will be passed to f, to avoid mutation.
      * @param f the function literal `output => f(output)`
      * @return the value of the output parameter.
      */
    protected[blasOps] def withCopyOf(x : Vector)(f : Array[T] => Unit) : Array[T] = {
      val outBuff : Array[T] = Array ofDim x.size
      copy(x.size, x.avalues, x.stride, outBuff, x.stride)
      f(outBuff)
      outBuff
    }
  }

  /** Level 2 BLAS routines. */
  /* TODO: `copy` needs to be parametrized on the vector type.
  protected[blas] trait L2GeneralDenseOps[M <: Int, N <: Int, T] extends L1GeneralDenseOps[M, T] {
    import TransX._
    type Matrix = DenseMatrix[M,N,T]

    implicit val N : ValueOf[N]

    /* § 2.8.7 : Matrix Operations */
    val gemm : (String, Int, Int, Int, T, Array[T], Int, Array[T], Int, T, Array[T], Int) => Unit

    /** Reification of Level 2 BLAS subroutine.
      * This trait is used to build up a description of the expression, to allow optimisation of compound expressions.
      */
    sealed trait L2Matrix extends DenseMatrix[M, N, T] with L1Vector { self : L2Matrix =>
      def cols : N = valueOf[N]
      override lazy val values : Array[T] = self match {
        case GEMM(α,a,b,β,c) => withCopyOf(c){ gemm(NoTrans.toString, valueOf[M], valueOf[N], a.cols, α, a.values, a.rows, b.values, b.rows, β, _, a.rows) }
      }
    }

    case class GEMM(α : T, A : Matrix, B : Matrix, β : T, C : Matrix) extends L2Matrix
  }
  */

  protected[blasOps] object TransX extends Enumeration {
    val NoTrans = Value("N")
    val Trans = Value("T")
    val ConjTrans = Value("C")
  }
}
