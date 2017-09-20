package math

import spire.algebra.{VectorSpace,Field}
import com.github.fommil.netlib.BLAS
import blas.DenseMatrix
import cats.Applicative

import scala.reflect.ClassTag

trait blasVectorSpace {

  /** A Typeclass that uses BLAS to perform calculations.
    * The number of columns and rows must be known.
    * The type T is restricted to Real, Double or Complex
    * It allocates intermediate objects to represent native BLAS operations, and executes these lazily.
    * @tparm The element type of the matrix, which must also be the same as the type of the field.
    *        This is a limitation of the library; but remember you can always implicitly upcast an e.g. `Int` to a `Double`.
    * @tparm M Number of rows.
    * @tparm N Number of columns.
    */
  trait BLASMatrixOps[T,M <: Int, N <: Int] {
    /** The BLAS library implementation used to perform optimized calculations where possible */
    def blas : BLAS
  }

  trait BLASMatrixApplicative[T,M <: Int, N <: Int, Mat[_,_,_] <: DenseMatrix[_,_ <: Int,_ <: Int]] extends Applicative[({ type m[x] = Mat[x,M,N] })#m ] {
  }

  abstract class VectorSpaceMatrixInstance[Mat[_,_ <: Int,_ <: Int] <: DenseMatrix[_,_ <: Int,_ <: Int], T : Field : ClassTag, M <: Int : ValueOf, N <: Int : ValueOf]
    (implicit override val blas : BLAS, A : Applicative[({ type m[x] = Mat[x,M,N] })#m])
    extends VectorSpace[Mat[T,M,N],T] with BLASMatrixOps[T,M,N] {
    type Matrix = Mat[T,M,N]

    //Level 1 BLAS
    protected trait BLASOp extends DenseMatrix[T,M,N] { val n : Int; val x : Matrix; val incx : Int ; override lazy val values : Array[T] = ??? }
    protected case class SCAL(n : Int, alpha : T, x : Matrix, incx : Int) extends BLASOp
    protected case class AXPY(n : Int, alpha : T, x : Matrix, incx : Int, y : Matrix, incy : Int) extends BLASOp

    def negate(x: Matrix): Matrix = 
      SCAL(x.values.length, scalar.negate(scalar.one), x, x.stride).asInstanceOf[Matrix] //FIXME : these casts are no good.

    def zero: Matrix = A.pure(scalar.zero)

    def plus(x: Matrix,y: Matrix): Matrix = 
      AXPY(x.values.length, scalar.one, x, x.stride, y, y.stride).asInstanceOf[Matrix]

    def timesl(r: T,v: Matrix): Matrix =
      SCAL(v.values.length, r, v, v.stride).asInstanceOf[Matrix]

    implicit def scalar: Field[T] = evidence$1
  }

  import spire.std.double._
  import blas.DenseMatrix
  implicit def denseMatrixOfDoubleVectorSpace[M <: Int : ValueOf, N <: Int : ValueOf]
     (implicit blas : BLAS, A : Applicative[({ type m[x] = DenseMatrix[x,M,N] })#m]) = 
     new VectorSpaceMatrixInstance[DenseMatrix,Double,M,N] {

  }
}
