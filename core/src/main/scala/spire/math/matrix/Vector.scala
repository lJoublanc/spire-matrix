package spire.math.matrix

trait Vector[M, T] {

  type Repr[_ <: M,_]

  /** The number of elements in the vector. */
  def size(fmt: Repr[M,T]): Int

  /** The element at position `i` */
  def apply(fmt: Repr[M,T])(i : Int): T

  /** Refer to [[math.show]] for a more flexible display type-classes */
  def toString(fmt: Repr[M,T]): String = {
    val s = size(fmt)
    if (s > 30) s"<vector ($s)>"
    else " [ " + Seq.tabulate(s)(apply(fmt)).mkString(" , ") + " ] "
  }

  /** Return all the values in this vector in an array of length [[size]]. */
  def toArray(f: Repr[M,T]): Array[T]

  /** Vector of size `M` with all elements set to zero. */
  def zero: Repr[M,T]

  /** Vector of size `M` containing x's. */
  def apply(x: T, xs: T*): Repr[M,T]
}

object Vector {

  type Aux[M,T,V[_ <: M,_]] = Vector[M,T]{ type Repr[m <: M,t] = V[m,t] }

  def apply[M] = new Sizer[M] {}

  trait Sizer[M] {
    def apply[T](x: T, xs: T*)(implicit tc: Vector[M,T]): tc.Repr[M,T] =
      tc.apply(x, xs: _*)
  }
}
