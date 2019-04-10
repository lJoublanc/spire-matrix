package spire.blas

import java.nio

/** A Generic interface to [[BlasLib]]. */
trait CBlas[T] extends Buffer[T] {

  type Buffer <: nio.Buffer 

  def copy(n: Int, x: Buffer, incx: Int, y: Buffer, incy: Int): Unit

  def scal(n: Int, α: T, x: Buffer, incx: Int): Unit

  def axpy(n: Int, α: T, x: Buffer, incx: Int, y: Buffer, incy: Int): Unit
}

object CBlas {

  type Aux[T, B <: nio.Buffer] = CBlas[T] { type Buffer = B }

  implicit def denseDouble: CBlas.Aux[Double, nio.DoubleBuffer] =
    new CBlas[Double] {

      type Buffer = nio.DoubleBuffer

      protected def elemSizeBytes = 8
      
      protected def fromBytes(b: nio.ByteBuffer) = b.asDoubleBuffer

      def wrap(array: Array[Double])(implicit byteOrder: nio.ByteOrder): Buffer = {
        val x = nio.DoubleBuffer.wrap(array)
        assert(x.order() == byteOrder)
        x
      }

      def put(b: nio.DoubleBuffer)(i: Int, d: Double) = b.put(i,d)

      def get(b: nio.DoubleBuffer)(i: Int) = b.get(i)

      def copy(n: Int, x: Buffer, incx: Int, y: Buffer, incy: Int): Unit = 
        BlasLib.cblas_dcopy(n, x, incx, y, incy)

      def scal(n: Int, α: Double, x: Buffer, incx: Int): Unit =
        BlasLib.cblas_dscal(n, α, x, incx)

      def axpy(n: Int,
               α: Double,
               x: Buffer,
               incx: Int,
               y: Buffer,
               incy: Int): Unit =
        BlasLib.cblas_daxpy(n, α, x, incx, y, incy)
    }
}
