package blas

import math.{FiniteMatrix,ColumnVector}

import scala.reflect.ClassTag

trait blasMatrix {
  /** 
    * A dense matrix, backed by a column-major array with BLAS layout to support fast vectorised calculation.
    */
  abstract class DenseMatrix[T : ClassTag, M <: Int : ValueOf, N <: Int : ValueOf] extends FiniteMatrix[T,M,N] {
    def rows : M = valueOf[M]
    def cols : N = valueOf[N]
    protected[blas] def values : Array[T]
    protected[blas] def stride : M = rows
    def apply(row : Int, col : Int) : T = values(row * stride + col)
  }

  object Matrix {
    /** Creates a matrix of known dimensions, backed by an existing column-major array.
      */
    def fromDenseArray[T : ClassTag, M <: Int : ValueOf, N <: Int : ValueOf](array : Array[T]) : DenseMatrix[T,M,N] =
      new DenseMatrix[T,M,N] {
        val values = {
          assert(rows*cols == array.length)
          array
        }
      }

   /** Convenience constructor */
    def apply[T : ClassTag, M <: Int : ValueOf, N <: Int : ValueOf](xs : T*) : FiniteMatrix[T,M,N] =
      fromDenseArray(xs.toArray)
  }

  object ColumnVector {
    /* Convenience constructor */
    def apply[T : ClassTag, M <: Int : ValueOf](xs : T*) : ColumnVector[T,M] = {
      implicit val one = valueOf[1]
      Matrix.fromDenseArray[T,M,1](xs.toArray)
    }
  }

  /** As per BLAS spec for packing efficiently */
  /* trait ULMatrix[T,R,C] extends Matrix */
}
