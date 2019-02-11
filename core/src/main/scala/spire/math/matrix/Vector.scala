package spire.math.matrix

import cats.{Applicative,Foldable}

/** Typeclass for vector operations.
  * These operations can not return vectors of differnt sizes, as `M`
  * must be preserved.
  */
trait Vector[F[_,_], M] 
extends Foldable[F[M,?]] {

  /** The element at position `i`.*/
  def apply[T](fmt: F[M,T])(i : Int): T =
    get(fmt)(i) getOrElse { throw new IndexOutOfBoundsException(i.toString) }
}

object Vector {

  /*
  def apply[M] = new Constructor[M] {}

  trait Constructor[M] {
    def apply[T](x: T, xs: T*)(implicit tc: Vector[M,T]): tc.Repr[M,T] =
      tc.apply(x, xs: _*)
  }
  */
}
