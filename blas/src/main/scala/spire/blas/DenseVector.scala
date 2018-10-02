package spire.blas

import spire.math.matrix.Vector
import spire.algebra.{VectorSpace,Field}
import scala.reflect.ClassTag
import java.nio

protected[blas] trait Blas[T] {

  type Buffer <: nio.Buffer

  def scal(n : Int, α: T, x: Buffer, incx: Int): Unit

  def axpy(n: Int, α: T, x: Buffer, incx: Int, y: Buffer, incy: Int): Unit
}

protected[blas] object Blas {

  type Aux[T, B <: nio.Buffer] = Blas[T] { type Buffer = B }

  implicit def denseDouble: Blas.Aux[Double,nio.DoubleBuffer] = new Blas[Double] {

    type Buffer = nio.DoubleBuffer

    def scal(n: Int, α: Double, x: Buffer, incx: Int): Unit =
      BlasLib.dscal_(n,α,x,incx)

    def axpy(n: Int, α: Double, x: Buffer, incx: Int, y: Buffer, incy: Int): Unit =
      BlasLib.daxpy_(n,α,x,incx,y,incy)
  }

}

trait DenseVector[M <: Int, T]
extends Vector[M, T] {

  type Buffer <: nio.Buffer
  
  /** Allows vectors to contain non-contiguous elements.  This allows
    * operations to only affect the <em>n</em>th element in-memory, useful for
    * domains that are vectors themselves, e.g.  dual numbers, polynomials,
    * complex numbers (although the later has a dedicated type in BLAS), and
    * allows row-major operations.
    *
    * @see BLAST § 2.6.6 
    */
  protected[blas] def stride: Int

  def toBuffer: Buffer

  final def toArray: Array[T] = toBuffer.array().asInstanceOf[Array[T]]
}

object DenseVector {

  type Aux[M <: Int, T, B <: nio.Buffer] = DenseVector[M,T] { type Buffer = B }

  /** Note that for the BLAS implementation of `Vector`, vector spaces only
    * exist over fields of the same type.
    */
  implicit def vectorSpaceInstance[M <: Int: ValueOf, T: ClassTag, B <: nio.Buffer](
      implicit builder: Vector.Builder[M], blas: Blas.Aux[T,B], field: Field[T]): 
    VectorSpace[DenseVector.Aux[M,T,B] => DenseVector.Aux[M,T,B],T] =
  new VectorSpace[DenseVector.Aux[M,T,B] => DenseVector.Aux[M,T,B],T] {
    import blas._

    type CtxVec = DenseVector.Aux[M,T,B] => DenseVector.Aux[M,T,B]

    def negate(f: CtxVec): CtxVec = timesl(scalar.negate(scalar.one),f)

    /** BLAS is optimized to short-circuit certain routines, by setting stride
      * = 0.
      */
    def zero = _ => {
      new DenseVector[M, T] {
        type Buffer = B
        def size: M = valueOf[M]
        def stride = 0
        def toBuffer: B = ???
        override def apply(i : Int) = scalar.zero
      } : DenseVector.Aux[M,T,B]
    }

    /** Vector addition */
    override def plus(f: CtxVec, g: CtxVec): CtxVec =
      f compose g

    /** Element-wise multiplication */
    def timesl(α: T, f: CtxVec): CtxVec = x => {
      val y = f(x)
      if (x.stride == 0) {
        scal(y.size, α, y.toBuffer, y.stride)
        y
      } else {
        axpy(x.size, α, x.toBuffer, x.stride, y.toBuffer, y.stride)
        y
      }
    }

    def scalar = field
  }
}

package object implicits {
  implicit def doubleBuilder[M <: Int : ValueOf]: Vector.Builder[M] = new Vector.Builder[M] {
    def fromBuffer(buff: nio.DoubleBuffer, strides: Int = 1):
        DenseVector.Aux[M,Double,nio.DoubleBuffer] = 
      new DenseVector[M,Double] {

        type Buffer = nio.DoubleBuffer

        assert(buff.limit == size, 
          s"Array size (${buff.limit}) must match declared vector size ($size).")

        override def apply(i : Int) = toBuffer.get(i * stride)

        protected[blas] def stride = strides

        def size = valueOf[M] / stride

        def toBuffer = buff
      }

    def apply[T: ClassTag](xs : T*): Vector[M,T] = xs.toArray match {
      case a: Array[Double] => fromBuffer(nio.DoubleBuffer.wrap(a))
    }
  }
}
