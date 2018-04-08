package spire.blas

import blasOps._
import spire.algebra.{VectorSpace,Field}
import com.github.fommil.netlib.BLAS

import scala.reflect.ClassTag

/** Vector space implemented BLAS Level 1 operations.
  *
  * @tparam T Element type of the matrix, which must also be the same as the type of the field.
  *           This is a limitation of the library; but remember you can always explicitly upcast an e.g. `Int` to a `Double`.
  */
abstract class VectorSpaceDenseVectorInstance[M <: Int,T : Field]
    (implicit val blas : BLAS,  val M : ValueOf[M], val ct : ClassTag[T])
    extends L1GeneralDenseOps[M, T] with VectorSpace[DenseVector[M,T],T]  {

  /** Returns a matrix with all elements negated. */
  def negate(x: Vector): Vector = SCAL(scalar.negate(scalar.one), x)

  /** Returns an m x n matrix with elements set to zero.
    * This implementation is optimized to allow fast return form certain BLAS routines, by setting stride = 0
    */
  val zero: Vector = new DenseVector[M, T] {
    def size : M = valueOf[M]
    def stride = 1
    def avalues = Array.fill(size)(scalar.zero)
    override def apply(i : Int) = scalar.zero
  }

  /** Vector addition */
  override def plus(x: Vector, y: Vector): Vector = (x,y) match {
    case (SCAL(α,x),y) => AXPY(α,x,y)
    case (x,SCAL(α,y)) => AXPY(α,y,x)
    case _ => AXPY(scalar.one, x, y)
  }

  /** Element-wise multiplication */
  def timesl(α: T, x: Vector): Vector = SCAL(α, x)

  /** Note that for the BLAS implementation of `Vector`, vector spaces only exist over fields of equivalent element type, `T` */
  implicit def scalar: Field[T] = evidence$1
}
