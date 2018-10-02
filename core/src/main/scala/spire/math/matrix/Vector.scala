package spire.math.matrix

import scala.reflect.ClassTag

trait Vector[M <: Int, +T] {
  /** The number of elements in the vector. */
  def size: Int

  /** The element at position `i` */
  def apply(i : Int): T

  /** Refer to [[math.show]] for a more flexible display type-classes */
  override def toString : String =
    if (size > 30) s"<vector ($size)>"
    else " [ " + Seq.tabulate(size)(apply).mkString(" , ") + " ] "
}

object Vector {

  def apply[M <: Int] = new {
    def apply[T: ClassTag](xs: T*)(implicit builder: Builder[M]): Vector[M,T] =
      builder.apply[T](xs : _*)
  }

  trait Builder[M <: Int] {
    def apply[T: ClassTag](xs: T*): Vector[M,T]
  }
}
