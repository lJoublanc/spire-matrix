package spire.blas

import blasOps._
import spire.algebra.{VectorSpace,Field}
import com.github.fommil.netlib.BLAS

import scala.reflect.ClassTag

/** A Type Class for Vector Spaces of `m × n` dense matrices, using BLAS L1 for operations.
  *
  * As vector space methods operate on elements independently, an `m × n` array can simply be treated as
  * an `m × 1` vector with the appropriate stride, and passed to L1 routines.
  *
  * @tparam T Element type of the matrix, which must also be the same as the type of the field.
  *           This is a limitation of the library; but remember you can always implicitly upcast an e.g. `Int` to a `Double`.
  */
abstract class VectorSpaceDenseMatrixInstance[M <: Int, N <: Int, T : Field]
    (implicit val blas : BLAS,  val M : ValueOf[M], val N : ValueOf[N], val ct : ClassTag[T])
    extends L1GeneralDenseOps[M, N, T] with VectorSpace[DenseMatrix[M, N, T],T]  {

  /** Returns a matrix with all elements negated. */
  def negate(x: Matrix): Matrix = SCAL(scalar.negate(scalar.one), x)

  /** Returns an m x n matrix with elements set to zero.
    * This implementation is optimized to allow fast return form certain BLAS routines, by setting stride = 0
    */
  val zero: Matrix = new DenseMatrix[M, N, T] {
    def rows : M = valueOf[M]
    def cols : N = valueOf[N]
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
