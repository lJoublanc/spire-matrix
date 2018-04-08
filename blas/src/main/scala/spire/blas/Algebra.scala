package spire.blas

import spire.algebra.VectorSpace
import com.github.fommil.netlib.BLAS

trait Algebra {
  import spire.std.double._
  /** Vector space instance for BLAS dense matrices */
  implicit def denseVectorOfDoubleVectorSpace[M <: Int : ValueOf](implicit blas : BLAS) :
    VectorSpace[DenseVector[M,Double],Double] = 
    new VectorSpaceDenseVectorInstance[M,Double] { self =>
    import blas._
  
    lazy val scal = dscal
    lazy val axpy = daxpy
    lazy val copy = dcopy
  }
}
