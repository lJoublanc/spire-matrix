package spire.blas

import com.github.fommil.netlib.BLAS

import scala.reflect.ClassTag
import scala.annotation.implicitNotFound

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
    * @tparam N Number of columns.
    * @see BLAST § 2.5.3 Array Arguments
    */
  @implicitNotFound("Most common cause is dimension mismatch or missing implicit BLAS library instance.")
  protected[blas] trait L1GeneralDenseOps[T, M <: Int, N <: Int] {
    type Matrix = DenseMatrix[T, M, N]
    implicit val ct : ClassTag[T]
    implicit val m : ValueOf[M]
    implicit val n : ValueOf[N]

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
    sealed trait L1Matrix extends DenseMatrix[T,M,N] { self : L1Matrix =>
      lazy val values : Array[T] = self match {
        case SCAL(α,x) => withCopyOf(x)(scal(x.size, α, _, 1))
        case AXPY(α,x,y) => withCopyOf(y)(axpy(y.size, α, x.values, 1, _, 1))
      }
    }

    // adding `final` modifier results in https://issues.scala-lang.org/browse/SI-4440
    case class SCAL(α : T, x : Matrix) extends L1Matrix
    case class AXPY(α : T, x : Matrix, y : Matrix) extends L1Matrix

    /** Make a copy of the output argument so that `f` becomes referentially transparent.
      * Many BLAS subroutines accumulate the result into one of the input parameters. This convenience function
      * can be used to create a copy of that parameter and pass it to the function, so it is not overwritten in-place.
      * @param x the mutable 'output' parameter, a copy of which will be passed to f, to avoid mutation.
      * @param f the function literal `output => f(output)`
      * @return the value of the output parameter.
      */
    protected[blasOps] def withCopyOf(x : Matrix)(f : Array[T] => Unit) : Array[T] = {
      val outBuff : Array[T] = Array ofDim x.size
      copy(x.size, x.values, 1, outBuff, 1)
      f(outBuff)
      outBuff
    }
  }

  /** Level 2 BLAS routines. */
  protected[blas] trait L2GeneralDenseOps[T, M <: Int, N <: Int] extends L1GeneralDenseOps[T, M, N] {
    import TransX._

    /* § 2.8.7 : Matrix Operations */
    val gemm : (String, Int, Int, Int, T, Array[T], Int, Array[T], Int, T, Array[T], Int) => Unit

    /** Reification of Level 2 BLAS subroutine.
      * This trait is used to build up a description of the expression, to allow optimisation of compound expressions.
      */
    sealed trait L2Matrix extends L1Matrix { self : L2Matrix =>
      override lazy val values : Array[T] = self match {
        case GEMM(α,a,b,β,c) => withCopyOf(c){ gemm(NoTrans.toString, valueOf[M], valueOf[N], a.cols, α, a.values, a.rows, b.values, b.rows, β, _, a.rows) }
      }
    }

    case class GEMM(α : T, A : Matrix, B : Matrix, β : T, C : Matrix) extends L2Matrix
  }

  protected[blasOps] object TransX extends Enumeration {
    val NoTrans = Value("N")
    val Trans = Value("T")
    val ConjTrans = Value("C")
  }
}
