package spire.blas

import spire.algebra.VectorSpace
import com.github.fommil.netlib.BLAS

trait Algebra {
  import spire.std.double._
  /** Vector space instance for BLAS dense matrices */
  implicit def denseMatrixOfDoubleVectorSpace[M <: Int : ValueOf, N <: Int : ValueOf](implicit blas : BLAS) :
    VectorSpace[DenseMatrix[Double,M,N],Double] = 
    new VectorSpaceDenseMatrixInstance[Double,M,N] { self =>
    import blas._
  
    lazy val scal = dscal
    lazy val axpy = daxpy
    lazy val copy = dcopy
  }
}
