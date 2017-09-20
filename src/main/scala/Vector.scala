package math 

import scala.reflect.ClassTag

trait vector { 
  type Vector[T,M] = DenseMatrix[T,M,1]
  /** A `Vector` is a type alias for a one-column matrix. */
  object Vector {
    def fromArray[T : ClassTag,M <: Int](array : Array[T])(m : M) : Vector[T,M] = DenseMatrix.fromArray(array)(m,1)
    def apply[T : ClassTag, M <: Int](xs : T*)(m : M) : Vector[T,M] = DenseMatrix.fromArray(xs.toArray)(m,1)
  }
}
