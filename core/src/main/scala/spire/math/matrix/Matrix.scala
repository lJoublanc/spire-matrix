package spire.math.matrix

/**
  * An immutable 2-D Matrix.
  *
  * @tparam T The type of the matrix elements.
  * @tparam M Number of rows (may be abstract).
  * @tparam N Number of columns (may be abstract).
  */
trait Matrix[T, M, N] {
  /** Number of rows in the matrix */
  def rows : M

  /** Number of columns in the matrix */
  def cols : N
}

/** companion objects should use a factory (implicit) type-class to extend this with constructor methods */
object Matrix {
  /** A `ColumnVector` is just a type alias for a `M` × `1` matrix. */
  type ColumnVector[T, M <: Int] = FiniteMatrix[T, M, 1]

  /** A `Square` is just a type alias for an `M` × `M` matrix. */
  type Square[T, M <: Int] = FiniteMatrix[T, M, M]
}

/** A matrix with known dimensions, supporting safe indexing */
trait FiniteMatrix[T, M <: Int, N <: Int] extends Matrix[T, M, N] with ((Int,Int) => T) {
  def rows : M

  def cols : N

  /** The number of elements in the matrix, `M` x `N` */
  def size : Int /* M x N */ = rows * cols

  /** The element at position i,j. */
  def apply(row : Int, col : Int) : T

  /** Refer to [[math.show]] for a more flexible display type-classes */
  override def toString : String = {
    if (size > 40) s"<$rows × $cols matrix>" 
    else Seq.tabulate(rows,cols)(apply).toString
  }
}

