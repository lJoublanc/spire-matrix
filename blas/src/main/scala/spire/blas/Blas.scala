package spire.blas

import java.nio

/** A Generic interface to [[BlasLib]]. */
protected[blas] trait Blas[T] {

  type Buffer <: nio.Buffer

  def scal(n: Int, α: T, x: Buffer, incx: Int): Unit

  def axpy(n: Int, α: T, x: Buffer, incx: Int, y: Buffer, incy: Int): Unit
}

object Blas {

  type Aux[T, B <: nio.Buffer] = Blas[T] { type Buffer = B }

  implicit def denseDouble: Blas.Aux[Double,nio.DoubleBuffer] = new Blas[Double] {

    type Buffer = nio.DoubleBuffer

    def scal(n: Int, α: Double, x: Buffer, incx: Int): Unit =
      BlasLib.cblas_dscal(n,α,x,incx)

    def axpy(n: Int, α: Double, x: Buffer, incx: Int, y: Buffer, incy: Int): Unit =
      BlasLib.cblas_daxpy(n,α,x,incx,y,incy)
  }
}
