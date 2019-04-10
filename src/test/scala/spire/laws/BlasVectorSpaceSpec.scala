package spire.laws

import org.typelevel.discipline.scalatest.Discipline
import org.scalatest.FunSuite
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.util.Buildable

import scala.reflect.ClassTag
import spire.math.optional.ULPOrderDouble
import spire.blas.vector._

import java.nio

class BlasVectorSpaceSpec extends FunSuite with Discipline {

  // Override [in]equality operators
  // These two allow us to do 'soft' comparison of floating point numbers.
  implicit object DoubleAlgebra extends spire.std.DoubleAlgebra with ULPOrderDouble { val δ = 1000 }
  implicit val arbDouble : Arbitrary[Double] = Arbitrary { Gen.choose(-1e6,1e6) }
    
  implicit def arbitraryDenseVector[M <: Int : ValueOf, TT : Arbitrary : ClassTag, B <: nio.Buffer](
    implicit vector: spire.math.Vector[({ type F[m <: Int] = Dense[m,TT,B]})#F] { type T = TT }): Arbitrary[Dense[M,TT,B]] = {
    implicit val builder = implicitly[Buildable[TT,Array[TT]]]
    Arbitrary {
      Gen.containerOfN[Array,TT](valueOf[M], arbitrary[TT]) map { as =>
        vector[M](as : _*)
      }
    }
  }

  checkAll("BLAS double precision dense vector", VectorSpaceLaws[DenseDouble[5],Double].vectorSpace)
  /*
  checkAll("BLAS double precision dense matrix (row vector)", VectorSpaceLaws[DenseMatrix[1,5,Double],Double].vectorSpace)
  checkAll("BLAS double precision dense matrix (square)", VectorSpaceLaws[DenseMatrix[2,2,Double],Double].vectorSpace)
  checkAll("BLAS double precision dense matrix (rectangular)", VectorSpaceLaws[DenseMatrix[2,3,Double],Double].vectorSpace)
  checkAll("BLAS double precision dense matrix (null matrix)", VectorSpaceLaws[DenseMatrix[2,2,Double],Double].vectorSpace)
  */
}
