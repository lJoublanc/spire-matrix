package spire.blas.vector

import spire.math.Vector
import spire.algebra.VectorSpace
import simulacrum.typeclass

import java.nio

@typeclass trait DenseVector[F[_ <: Int]] extends Vector[F] {

  type T

  type B <: nio.Buffer

  def toBuffer[M <: Int: ValueOf](f: F[M])(b: B, stride: Int = 1)(
    implicit V: VectorSpace[F[M],T]): B

  def fromBuffer[M <: Int](x: B): F[M]
}
