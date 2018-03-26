package spire.blas

import spire.math.matrix.FiniteMatrix
import spire.math.matrix.{Vector,Matrix}

import scala.reflect.ClassTag

/** A dense matrix, backed by a column-major array with BLAS layout to support fast vectorised calculation.  */
abstract class DenseMatrix[M <: Int, N <: Int, T : ClassTag] extends DenseVector[M,Vector[N,T]] with FiniteMatrix[M, N, T] {
  final override def stride  = rows

  protected[blas] def avalues = ( for (j <- 0 until cols) yield apply(j) ).toArray

  /** An immutable, 1-D array containing the matrix elements, the columns of wich are assumed to be contiguous 
      in memory, and cols separated by [[rows]] elements. i.e. column-major (fortran) order.*/
  protected[blas] def values : Array[T]

  /** The column at position `i`, returned as a vector.
    * @return To avoid copies, a vector backed by a <em>mutable</em> array using [[java.nio.ByteBuffer#wrap]]. Make sure your code is pure or weird thinks will happen ...*/
  override def apply(col : Int) = new DenseVector[N,T] {
    import java.nio._
    def size = rows

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

/** Extension methods for [[spire.math.matrix.Matrix]] companion, available by `import implicits._` */
trait DenseMatrixConstructors {
  implicit class MatrixConstructorOps(companion : Matrix.type) extends AnyRef {
    def apply[M <: Int, N <: Int] = new PartiallyAppliedMatrix[M,N]
    /** Use this only when the matrix dimensions are unknown at compile-time.
      * @see Prefer `Matrix[M,N].fromDenseArray(array : Array[T])` for a safe version of this constructor. */
    def fromDenseArrayUnsafe[T : ClassTag](array : Array[T], columns : Int) = new DenseMatrix[Int,Int,T] {
      def rows : Int = array.length / columns
      def cols : Int = columns
      val values = array
    }
  }
  
  /** Helper to allow nice syntax `Matrix[3,3]` allowing compiler to infer data-type `T`. */
  final protected class PartiallyAppliedMatrix[M <: Int , N <: Int] extends AnyRef {

    /** Creates a matrix of known dimensions, backed by an existing column-major array. */
    def fromDenseArray[T : ClassTag](array : Array[T])(implicit M : ValueOf[M], N : ValueOf[N]) : DenseMatrix[M, N, T] =
      new DenseMatrix[M, N, T] {
        def rows : M = valueOf[M]

        def cols : N = valueOf[N]

        val values : Array[T] = {
          assert(array.length == size, s"Array size (${array.length}) must match declared matrix size ($size).")
          array
        }
      }

    /** 
      * This is a convenience constructor meant to be used for interactive coding, e.g. using a console as input.
      * It takes it's arguments row-wise, to allow input over multiple lines.
      * TODO : If passed a single scalar argument `x`, returns I * x.
      * @param xs : Matrix elements in <b>row-major</b> form, allowing multi-line layout through a console.  
      * @example 
      * {{{
      * Matrix[2,2](1.0,0.0,
      *             2.0,0.0)
      * }}}
      */
    def apply[T : ClassTag](xs : T*)(implicit M : ValueOf[M], N : ValueOf[N]): DenseMatrix[M, N, T] =
      fromDenseArray[T]({
        val m : Int = valueOf[M]
        val n : Int = valueOf[N]
        for { j <- 0 until n ; i <- 0 until m } yield xs(j + i * n)
      }.toArray)

  }
}
