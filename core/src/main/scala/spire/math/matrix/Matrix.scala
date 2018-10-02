package spire.math.matrix

import spire.algebra.Field

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

  def isUpperTriangular: Boolean

  def isLowerTriangular: Boolean

  def isTriangular: Boolean = isUpperTriangular || isLowerTriangular

  def isHermitian: Boolean

  def isIdentity: Boolean

  def isSquare: Boolean = rows == cols

  def isSymmetric: Boolean

  //def transpose: Matrix[N,M,T]
}

object Matrix {

  /** A `ColumnVector` is just a type alias for a `M` × `1` matrix. */
  type ColumnVector[M <: Int, T] = Matrix[M, 1, T]

  /** A `Square` is just a type alias for an `M` × `M` matrix. */
  type Square[M <: Int, T] = Matrix[M, M, T]

  def apply[M <: Int, N <: Int](implicit builder: Builder[M,N]): Builder[M,N] = builder

  /** Concrete implementations of `Matrix` extend this class to create
    * matrix instances.
    */
  trait Builder[M <: Int, N <: Int] {
    /** Constructor for interactive computing, e.g. using a console as input.
      * It takes it's arguments row-wise, to allow input over multiple lines.
      *
      * @param xs Matrix elements in <b>row-major</b> order, allowing 
      *           multi-line layout through a console.  
      * @example 
      * {{{
      * Matrix[2,2](1.0,0.0,
      *             2.0,0.0)
      * }}}
      */
    def apply[T](xs: T*): Matrix[M,N,T]

    def banded[T](xs: T*): Matrix[M,N,T]

    /** @example {{{
      * Matrix[2,2](1,2)
      * res0: Matrix(1, 0
      *              0  2)
      * }}}
      */
    def diagonal[T](xs: T*): Square[M,T]

    /** @example {{{
      * Matrix[3,3].symmetric(1, 2, 3,
      *                          1, 2,
      *                             1)
      * }}}
      */
    def symmetric[T](xs: T*): Square[M,T]

    /** @example {{{
      * Matrix[3,3].hermitian(2, 2 + i, 4,
      *                              3, i)
      * }}}
      */
    def hermitian[T](xs: T*): Square[M,T]

    /** The identity matrix.
      * @example {{{
      * Matrix[3,3].unit[Int]
      * res0: Matrix(1, 0, 0,
      *              0, 1, 0,
      *              0, 0, 1)
      * }}}
      */
    def unit[T: Field]: Square[M,T]

    def upperTriangular[T](xs: T*): Square[M,T]

    def lowerTriangular[T](xs: T*): Square[M,T]

  }
}
