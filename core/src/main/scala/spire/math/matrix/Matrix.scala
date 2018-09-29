package spire.math.matrix

/** An immutable 2-D Matrix.
  *
  * A matrix with known dimensions, supporting safe indexing.  A matrix
  * enriches the category of 'vectors of vectors', hence the extensinon of
  * `FiniteVector[M, Vector[N,T]]`.
  * 
  * @tparam M Number of rows. Use `Int` if not known.
  * @tparam N Number of columns. Use `Int` if not known.
  * @tparam T The type of the matrix elements.
  */
trait Matrix[M <: Int, N <: Int, +T] extends Vector[M, Vector[N,T]] {

  /** Number of rows in the matrix */
  def rows: M

  /** Number of columns in the matrix */
  def cols: N

  /** The number of elements in the matrix, `M` x `N` */
  override def size: Int = rows * cols

  /** The element at position `i`,`j`. */
  def apply(row : Int, col : Int): T = apply(row)(col)

  /** Refer to [[math.show]] for a more flexible display type-classes */
  override def toString : String =
    if (size > 40) s"<matrix ($rows x $cols)>" 
    else "[" + Seq.tabulate(rows,cols)(apply).mkString(" , ") + " ] " //TODO : use `transpose.tabulate(cols)(apply)` once .T implemented.
}

/** Companion objects should use a factory (implicit) type-class to extend this
  * with constructor methods.
  */
object Matrix {
  /** A `ColumnVector` is just a type alias for a `M` × `1` matrix. */
  type ColumnVector[M <: Int, T] = Matrix[M, 1, T]

  /** A `Square` is just a type alias for an `M` × `M` matrix. */
  type Square[M <: Int, T] = Matrix[M, M, T]
}


