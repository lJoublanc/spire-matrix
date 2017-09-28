/**
 * This package implements matrix data types and algebras (as type-class instances) using external BLAS libraries.
 * We use the Fortran 77 language bindings translated to JVM, via netlib-java.
 * For detailed specs, see the BLAST technical specification 2001.
 *
 * The variable naming convention used throughout this module is that of the BLAST 2001 spec.
 * A,B,C - matrices
 * u,v,w,x,y,z - (column) vectors
 * greek alphabet - scalars
 * 
 * All matrices must be finite - the number of columns and rows must be known.
 * The matrix element type T must belong to the set of Reals, Doubles or Complex (TBC).
 *
 * @see <href a=http://www.netlib.org/blas/blast-forum>BLAS Technical Forum Standard</href>
 * @see <href a=http://math.nist.gov/javanumerics/blas.html>Java Numerics disussion on BLAS</href>
 */
package object blas
  extends blasMatrix
  with blasOps
  with blasVectorSpace
