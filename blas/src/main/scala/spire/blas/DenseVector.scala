package spire.blas

import spire.math.matrix.{Vector,FiniteVector}
import scala.reflect.ClassTag

abstract class DenseVector[M <: Int, T : ClassTag] extends FiniteVector[M, T] {

  /** Underlying array of values. Because `Matrix` inherits `Vector[Vector]` , this needs to have a different name from `Matrix.values` to avoid name-clash. */
  protected[blas] def avalues : Array[T]

  /** Leading dimension. Allows vectors to contain non-contiguous elements.
    * This allows operations to only affect the <em>n</em>th element in-memory, useful for domains that are vectors themselves, e.g.
    * dual numbers, polynomials, complex numbers (although the later has a dedicated type in BLAS), and allows row-major operations.
    * @see BLAST § 2.6.6 */
  protected[blas] def stride : Int

  def apply(i : Int) : T = avalues(i * stride)
}

/** Extension methods for [[spire.math.matrix.Vector]] companion, available by `import implicits._` */
trait DenseVectorConstructors {
  implicit class VectorConstructorOps(companion : Vector.type) extends AnyRef {
    def apply[M <: Int] = new PartiallyAppliedVector[M]

    /** Use this only when the vector dimensions are unknown at compile-time.
      * @see Prefer `Vector[N].fromArray(array : Array[T])` for a safe version of this constructor. */
    def fromDenseArrayUnsafe[T : ClassTag](array : Array[T], strides : Int = 1) : DenseVector[Int,T] = new DenseVector[Int,T] {
      def size = array.length / stride
      val avalues = array
      def stride = strides
    }
  }

  final protected class PartiallyAppliedVector[M <: Int] extends AnyRef {
    def fromDenseArray[T : ClassTag](array : Array[T], strides : Int = 1)(implicit M : ValueOf[M]) : DenseVector[M,T] = 
      new DenseVector[M,T] {
        def size = valueOf[M] / stride

        val avalues = {
          assert(array.length == size, s"Array size (${array.length}) must match declared vector size ($size).")
          array
        }
        def stride = strides
      }

    def apply[T : ClassTag](xs : T*)(implicit M : ValueOf[M]) : DenseVector[M,T] = fromDenseArray[T](xs.toArray)
  }
}
