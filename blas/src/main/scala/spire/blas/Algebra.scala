package spire.blas

import spire.algebra.VectorSpace
import com.github.fommil.netlib.BLAS

trait Algebra {
  import spire.std.double._
  /** Vector space instance for BLAS dense matrices */
  implicit def denseMatrixOfDoubleVectorSpace[M <: Int : ValueOf, N <: Int : ValueOf](implicit blas : BLAS) :
    VectorSpace[DenseMatrix[M,N,Double],Double] = 
    new VectorSpaceDenseMatrixInstance[M,N,Double] { self =>
    import blas._
  
    lazy val scal = dscal
    lazy val axpy = daxpy
    lazy val copy = dcopy
  }
}
