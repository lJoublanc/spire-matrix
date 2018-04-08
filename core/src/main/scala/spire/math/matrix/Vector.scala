package spire.math.matrix

trait Vector[M , +T] {
  /** Number of elements in the vector */
  def size : Int

  /** Lift a vector into a matrix. Only available for `Vector[M,Vector[N,T]]` with `N` finite.  */
  //def toMatrix[N <: Int : ValueOf](implicit isNested : T <:< Vector[N, T]) : Matrix[M, N, T]
}

/** Implementations should use implicit extension methods e.g. `implicit class VectorConstructor(companion : Vector.type)` to provide constructors. */
object Vector {
  
}

trait FiniteVector[M <: Int, +T] extends Vector[M, T] with (Int => T) {
  /** The number of elements in the vector. */
  def size : Int

  /** The element at position `i` */
  def apply(i : Int) : T

  /** Refer to [[math.show]] for a more flexible display type-classes */
  override def toString : String =
    if (size > 30) s"<vector ($size)>"
    else " [ " + Seq.tabulate(size)(apply).mkString(" , ") + " ] "
}
