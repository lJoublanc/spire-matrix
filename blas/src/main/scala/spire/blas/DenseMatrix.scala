package spire.blas

import spire.math.matrix.FiniteMatrix
import spire.math.matrix.Vector

import scala.reflect.ClassTag

/** A dense matrix, backed by a column-major array with BLAS layout to support fast vectorised calculation.  */
abstract class DenseMatrix[M <: Int : ValueOf, N <: Int : ValueOf, T : ClassTag] extends DenseVector[M,Vector[N,T]] with FiniteMatrix[M, N, T] {
  def rows : M = valueOf[M]

  def cols : N = valueOf[N]

  final override def stride  = rows

  protected[blas] def avalues = ( for (j <- 0 until cols) yield apply(j) ).toArray

  /** An immutable, 1-D array containing the matrix elements, the columns of wich are assumed to be contiguous 
      in memory, and cols separated by [[rows]] elements. i.e. column-major (fortran) order.*/
  protected[blas] def values : Array[T]

  /** The column at position `i`, returned as a vector.
    * @return To avoid copies, a vector backed by a <em>mutable</em> array using [[java.nio.ByteBuffer#wrap]]. Make sure your code is pure or weird thinks will happen ...*/
  override def apply(col : Int) = new DenseVector[N,T] {
    import java.nio._

    def avalues = {
      (values : Array[_]) match { case a : Array[Double] => //TODO : implement using `cats.Const` and structural reflection implicit of a `Buffer[T] {def get ; def put }` typeclass.
        DoubleBuffer.wrap(a, stride * col, rows).array
      }
    }.asInstanceOf[Array[T]]

    def stride = 1
  }

  /** The element at position `(i,j)`, whose array index is calculated at `i + j × stride`. */
  def apply(row : Int, col : Int) : T = values(row + col * rows)

  /*
  def toVector : Vector[M,Vector[N,T]] = new DenseVector[M,Vector[N,T]] {
    ???
  }*/
}
