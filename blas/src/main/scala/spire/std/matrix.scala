package spire.std

import spire.blas.{DenseMatrix,VectorSpaceDenseMatrixInstance}
import spire.algebra.{VectorSpace,Field}

import com.github.fommil.netlib.BLAS

/** This package implements matrix and vector data types and algebras using external BLAS libraries.
  *
  * We use the Fortran 77 language bindings translated to JVM, via netlib-java.
  * For detailed specs, see the BLAST technical specification 2001.
  *
  * All matrices must be finite - the number of columns and rows must be known.
  * The matrix element type `T` must belong to the set of `Float`, `Double`s or `Complex` (TBC).
  *
  * @see [[http://www.netlib.org/blas/blast-forum>BLAS Technical Forum Standard]]
  *      [[http://math.nist.gov/javanumerics/blas.html Java Numerics disussion on BLAS]]
  *
  * @note The variable naming convention used throughout this module is that of the BLAST 2001 spec.
  *       A,B,C - matrices
  *       u,v,w,x,y,z - (column) vectors
  *       greek letters) - scalars
  */
package object matrix extends MatrixInstance {

  import spire.std.double._
  /** Vector space instance for BLAS dense matrices */
  implicit def denseMatrixOfDoubleVectorSpace[M <: Int : ValueOf, N <: Int : ValueOf](implicit blas : BLAS) :
    VectorSpace[DenseMatrix[Double,M,N],Double] = 
    new VectorSpaceDenseMatrixInstance[Double,M,N] { self =>
    import self.blas._
  
    lazy val scal = dscal
    lazy val axpy = daxpy
    lazy val copy = dcopy
  }
}
