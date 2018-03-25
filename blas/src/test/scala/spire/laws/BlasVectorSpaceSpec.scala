package spire.laws

import org.typelevel.discipline.scalatest.Discipline
import org.scalatest.FunSuite
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.util.Buildable

import scala.reflect.ClassTag
import com.github.fommil.netlib.{BLAS, F2jBLAS}
import spire.syntax.vectorSpace
import spire.math.optional.ULPOrderDouble
import spire.math.matrix._
import spire.blas._
import spire.blas.implicits._

class BlasVectorSpaceSpec extends FunSuite with Discipline {

  // Override [in]equality operators

  implicit val blasInstance : BLAS = new F2jBLAS()

  // These two allow us to do 'soft' comparison of floating point numbers.
  implicit object DoubleAlgebra extends spire.std.DoubleAlgebra with ULPOrderDouble { val δ = 1000 }
  implicit val arbDouble : Arbitrary[Double] = Arbitrary { Gen.choose(-1e6,1e6) }
    
  implicit def arbitraryDoubleMatrix[T : Arbitrary : ClassTag, M <: Int : ValueOf, N <: Int : ValueOf] : Arbitrary[DenseMatrix[T,M,N]] = {
    implicit val builder = implicitly[Buildable[T,Array[T]]]
    Arbitrary {
      Gen.containerOfN[Array,T](valueOf[M]*valueOf[N], arbitrary[T]) map { as =>
        Matrix[M,N].fromDenseArray(as)
      }
    }
  }

  checkAll("BLAS double precision dense matrix (column vector)", VectorSpaceLaws[DenseMatrix[Double,5,1],Double].vectorSpace)
  checkAll("BLAS double precision dense matrix (row vector)", VectorSpaceLaws[DenseMatrix[Double,1,5],Double].vectorSpace)
  checkAll("BLAS double precision dense matrix (square)", VectorSpaceLaws[DenseMatrix[Double,2,2],Double].vectorSpace)
  checkAll("BLAS double precision dense matrix (rectangular)", VectorSpaceLaws[DenseMatrix[Double,2,3],Double].vectorSpace)
  //checkAll("BLAS double precision dense matrix (null matrix)", VectorSpaceLaws[DenseMatrix[Double,2,2],Double].vectorSpace)

}
