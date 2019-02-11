package spire

import java.nio
import spire.math.matrix.Vector

package object blas {

  /**  BLAS strided Vector
    *
    *  @tparam M Number of elements in the vector.
    *  @tparam I The stride
    *  @tparam B The nio.Buffer type returned
    *  @tparam T The scalar element type inside the vector
    *
    *  The two scalar parameters correspond to $α$ and $β$ in $α + βx$.
    */
  type DenseVector[M <: Int, I <: Int, T] =
    M => T => T => I => nio.Buffer => nio.Buffer

  implicit class DenseVectorFactory(val companion: Vector.type) {
    /** @tparam M The number of elements in the vector
      * @tparam I The stride
      */
    def apply[M <: Int : ValueOf, I <: Int : ValueOf] : Vector[DenseVector[M,I,?],M] =  //TODO infer buffer type
      new Vector[DenseVector,M] {
        // Members declared in cats.Applicative
        def pure[T](x: T): DenseVector[M,T] = 
          valueOf[M] => valueOf[I] =>
        
        // Members declared in cats.Apply
        def ap[T, T2](ff: DenseVector[M,T => T2])(fa: DenseVector[M,T]): DenseVector[M,T2] = ???
        
        // Members declared in cats.Foldable
        def foldLeft[T, T2](fa: DenseVector[T],b: T2)(f: (T2, T) => T2): T2 = ???
        def foldRight[T, T2](fa: DenseVector[T],lb: cats.Eval[T2])(f: (T, cats.Eval[T2]) => cats.Eval[T2]): cats.Eval[T2] = ???
        
        // Members declared in spire.math.matrix.Vector
        def apply[T](fmt: DenseVector[T])(i: Int): T = ???
      }
  }
}
