package blas

import spire.std.double._
import spire.algebra.VectorSpace
import spire.laws._ // required for implicit discipline.Predicate
import spire.laws.VectorSpaceLaws
import blas._

import org.typelevel.discipline.scalatest.Discipline
import org.typelevel.discipline.Predicate
import org.scalatest.FunSuite
import org.scalacheck.{Arbitrary,Gen}
import org.scalacheck.Arbitrary._
import org.scalacheck.util.Buildable

import scala.reflect.ClassTag

import com.github.fommil.netlib.{BLAS,F2jBLAS}

class BlasVectorSpaceSpec extends FunSuite with Discipline {

  import math.finiteMatrixEqInstance

  implicit val blasInstance : BLAS = new F2jBLAS()
  
  implicit def arbitraryDoubleMatrix[T : Arbitrary : ClassTag, M <: Int : ValueOf, N <: Int : ValueOf] : Arbitrary[DenseMatrix[T,M,N]] = {
    implicit val builder = implicitly[Buildable[T,Array[T]]]
    Arbitrary { 
      Gen.containerOfN[Array,T](valueOf[M]*valueOf[N], arbitrary[T]) map { as =>
        blas.Matrix.fromDenseArray[T,M,N](as)
      }
    }
  }
         

  checkAll("BLAS double precision dense matrix (square)", VectorSpaceLaws[DenseMatrix[Double,2,2],Double].vectorSpace)
  checkAll("BLAS double precision dense matrix (rectangular)", VectorSpaceLaws[DenseMatrix[Double,2,3],Double].vectorSpace)
  checkAll("BLAS double precision dense matrix (column vector)", VectorSpaceLaws[DenseMatrix[Double,5,1],Double].vectorSpace)
  checkAll("BLAS double precision dense matrix (row vector)", VectorSpaceLaws[DenseMatrix[Double,1,5],Double].vectorSpace)
  checkAll("BLAS double precision dense matrix (null matrix)", VectorSpaceLaws[DenseMatrix[Double,5,1],Double].vectorSpace)
  
}
