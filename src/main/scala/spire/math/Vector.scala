package spire.math

import spire.algebra.VectorSpace
import simulacrum.typeclass

import scala.reflect.ClassTag

@typeclass trait Vector[F[_ <: Int]] {

  /** The field of the vector; the type of element it contains. */
  type T

  /** Return element at index `i` */
  def apply[M <: Int: ValueOf](f: F[M], i: Int)(implicit space: VectorSpace[F[M],T]): T

  /** Standard builder. */
  def apply[M <: Int: ValueOf](xs: T*): F[M]

  /** Allocates a new array and copies result to it. */
  def toArray[M <: Int: ValueOf](f: F[M])(
    implicit ct: ClassTag[T], space: VectorSpace[F[M],T]): Array[T] =
      toArray(f, Array ofDim valueOf[M])

  /** Returns the result as an array. */
  def toArray[M <: Int: ValueOf](f: F[M], output: Array[T])(
    implicit ct: ClassTag[T], space: VectorSpace[F[M],T]): Array[T]
}
