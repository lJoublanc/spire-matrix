package spire.blas

import spire.math.matrix
import scala.reflect.ClassTag

/** Extension methods for [[spire.math.matrix.Matrix]] companion, available by `import implicits._` */
trait DenseMatrixConstructors {
  implicit class MatrixConstructorOps(companion : matrix.Matrix.type) extends AnyRef {
    def apply[M <: Int, N <: Int] = new PartiallyAppliedMatrix[M,N]
  }

  
  /** Helper to allow nice syntax `Matrix[3,3]` allowing compiler to infer data-type `T`. */
  final protected class PartiallyAppliedMatrix[M <: Int , N <: Int] extends AnyRef {

    /** Creates a matrix of known dimensions, backed by an existing column-major array. */
    def fromDenseArray[T : ClassTag](array : Array[T])(implicit M : ValueOf[M], N : ValueOf[N]) : DenseMatrix[M, N, T] =
      new DenseMatrix[M, N, T] {
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

