package spire.blas

import scala.reflect.ClassTag

import spire.algebra.{VectorSpace,Field}
import java.nio

package object implicits {

  /*
  implicit def doubleVector[M <: Int : ValueOf](implicit B: Blas.Aux[Double,nio.DoubleBuffer]): 
    DenseVector.Aux[M,Double,nio.DoubleBuffer] =
  new DenseVector[M,Double] {
    import B._
    private final val y0 = GeneralVector[M,Double,nio.DoubleBuffer](null,0)

    type Repr[m <: Int, t] = Vector[m,t,nio.DoubleBuffer]

    type Buffer = nio.DoubleBuffer

    //TODO: cache results
    def apply(f: Vector[M,Double,java.nio.DoubleBuffer])(i: Int): Double = {
      val x = f(1)(y0)
      x.buffer.get(i * x.stride)
    }

    def size(f: Vector[M,Double,java.nio.DoubleBuffer]): Int = 
      f(1)(y0).size

    protected[blas] def stride(f: Vector[M,Double,nio.DoubleBuffer]): Int =
      f(1)(y0).stride

    def toArray(f: Vector[M,Double,nio.DoubleBuffer]): Array[Double] = {
      val x = f(1)(y0)
      x.stride match {
        case 0 => Array.fill(x.size)(0.0)
        case 1 if x.buffer.hasArray => x.buffer.array()
        case _ => { 
          for (i <- 0 until x.size) yield x.buffer.get(i * x.stride)
        }.toArray
      }
    }

    def zero: Vector[M,Double,nio.DoubleBuffer] = 
      _ => y => if (y.stride != 0) y else y0

    def fromBuffer(buff: nio.DoubleBuffer, stride: Int = 1):
          Vector[M,Double,nio.DoubleBuffer] = 
    {
      assert(buff.isDirect, "Buffer is not direct")
      val x = GeneralVector[M,Double,nio.DoubleBuffer](buff, stride)
      assert(valueOf[M] == x.size,
        s"Buffer limit/stride (${x.size}) must match declared vector size (${valueOf[M]}).")
      α => y => 
        if (y.stride == 0) {
          val out : GeneralVector[M,Double,nio.DoubleBuffer] = 
            x.copy(buffer = x.buffer.duplicate())
          scal(out.size, α, out.buffer, out.stride)
          out
        }
        else {
          val out : GeneralVector[M,Double,nio.DoubleBuffer] = 
            y.copy(buffer = y.buffer.duplicate())
          axpy(x.size, α, x.buffer, x.stride, out.buffer, out.stride)
          out
        }
    }

    def apply(x: Double, xs: Double*): Vector[M,Double,nio.DoubleBuffer] = {
      val arr = x +: xs
      val buff =
        nio.ByteBuffer
          .allocateDirect(arr.size*8)
          .order(nio.ByteOrder.LITTLE_ENDIAN)
          .asDoubleBuffer
      arr foreach buff.put
      buff.rewind() //TODO: flip?
      fromBuffer(buff)
    }
  }

  /** Note that in BLAS, vector spaces only exist over fields of the same type. */
  implicit def vectorSpaceInstance[M <: Int: ValueOf, T: ClassTag, B <: nio.Buffer](
      implicit V: DenseVector.Aux[M,T,B], field: Field[T]): 
    VectorSpace[Vector[M,T,B],T] =
  new VectorSpace[Vector[M,T,B],T] {
    import spire.syntax.field._

    def negate(f: Vector[M,T,B]): Vector[M,T,B] = 
      timesl(scalar.negate(scalar.one),f)

    /** BLAS is optimized to short-circuit certain routines, by setting stride
      * = 0.
      */
    def zero = V.zero

    /** Vector addition */
    override def plus(f: Vector[M,T,B], g: Vector[M,T,B]): Vector[M,T,B] = 
      α => y => f(α)(g(scalar.one)(y))

    /** Element-wise multiplication */
    def timesl(α: T, f: Vector[M,T,B]): Vector[M,T,B] =
      β => y => f(α*β)(y)

    def scalar = field
  }
  */
}
