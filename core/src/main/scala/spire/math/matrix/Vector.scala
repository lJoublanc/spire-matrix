package spire.math.matrix

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
