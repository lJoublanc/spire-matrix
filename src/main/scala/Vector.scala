package math 

import scala.reflect.ClassTag

trait vector { 
  type Vector[T,C] = DenseMatrix[T,1,C]
  /** A `Vector` is just a type alias for a one-row matrix. */
  object Vector {
    def fromArray[T : ClassTag,C <: Int](array : Array[T])(c : C) : Vector[T,C] = DenseMatrix.fromArray(array)(1,c)
    def apply[T : ClassTag, C<: Int](xs : T*)(c : C) : Vector[T,C] = DenseMatrix.fromArray(xs.toArray)(1,c)
  }
}
