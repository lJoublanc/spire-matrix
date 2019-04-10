package spire.blas

import java.nio
import spire.algebra.{Field,VectorSpace,Eq}

import scala.reflect.ClassTag

package object vector {

  /**  BLAS strided Vector
    *  Parameters correspond to:
    *    1. Dimension of vector
    *    2. Scalar mulitplier `α` as per BLAS cheat-sheet AXPY.
    *    3. The stride of (this) vector `x` as per BLAS cheat-sheet AXPY.
    *    4. Another vector `y` as per BLAS cheat-sheet AXPY (null means zero vector)
    *    5. The stride of vector `y`.
    *    6. The buffer to output the result to.
    *
    *  @tparam M Number of elements in the vector $x$
    *  @tparam T The scalar element type inside the vector $x$
    *  @tparam B The nio.Buffer type that encodes `T`s.
    *
    *  @todo Once opaque types are in, hide this.
    */
  type Dense[M <: Int, T, B <: nio.Buffer] = 
    M => T => Int => B => Int => B => B

  type DenseDouble[M <: Int] = Dense[M,scala.Double,nio.DoubleBuffer]

  type DenseReal[M <: Int] = Dense[M, scala.Float, nio.FloatBuffer]

  implicit def denseEq[M <: Int: ValueOf, S: ClassTag, B <: nio.Buffer](implicit
    vs: VectorSpace[Dense[M,S,B],S],
    vb: DenseVector[({ type F[m <: Int] = Dense[m,S,B]})#F] { type T = S },
    equ: Eq[S]): Eq[Dense[M,S,B]] = 
    (x: Dense[M,S,B], y: Dense[M,S,B]) => {
      import spire.syntax.eq._
      import DenseVector.ops._
      val xb = toAllDenseVectorOps[({ type F[m <: Int] = Dense[m,S,B]})#F,M](x).toArray
      val yb = toAllDenseVectorOps[({ type F[m <: Int] = Dense[m,S,B]})#F,M](y).toArray
      var i = 0
      var ok = true
      while (i < xb.length && ok)
        if (xb(i) === yb(i)) i += 1
        else ok = false
      ok
    }

  implicit def denseVector[TT, BB <: nio.Buffer](implicit cblas: CBlas.Aux[TT,BB]):
    DenseVector[({ type F[m <: Int] = Dense[m,TT,BB]})#F] { type T = TT ; type B = BB} = 
      new DenseVector[({ type F[m <: Int] = Dense[m,TT,BB]})#F] { self =>

        type T = TT

        type B = BB

        def apply[M <: Int: ValueOf](f: Dense[M,T,B], i: Int)(
          implicit space: VectorSpace[Dense[M,T,B],T]): T = {
            val compileTo = f(valueOf[M])(space.scalar.one)(1)(null.asInstanceOf[B])(0)
            val buff = compileTo(cblas.allocateDirect(valueOf[M]))
            cblas.get(buff)(i)
        }

        def toBuffer[M <: Int: ValueOf](f: Dense[M,T,B])(b: B, stride: Int = 1)(
          implicit V: VectorSpace[Dense[M,T,B],T]): B =
            f(valueOf[M])(V.scalar.one)(stride)(null.asInstanceOf[B])(0)(b)


        def toArray[M <: Int: ValueOf](f: Dense[M,T,B], output: Array[T])(
          implicit ct: ClassTag[T], space: VectorSpace[Dense[M,T,B],T]): Array[T] = {
            val compileTo = f(valueOf[M])(space.scalar.one)(1)(null.asInstanceOf[B])(0)
            compileTo(cblas wrap output).array().asInstanceOf[Array[T]]
           }

        /** Strides are inferred from the size of the buffer */
        def fromBuffer[M <: Int](x: B): Dense[M,T,B] =
          m => α => incrx => y => incry => buff => {
            import cblas._
            assert(buff.capacity == m*incrx)
            if (incry == 0) { //αx + 0 = αX
              copy(m,x,incrx,buff,incrx) // x is overwritten
              scal(m,α,buff,incrx)
            }
            else {
              copy(m,y,incry,buff,incry) 
              axpy(m,α,x,incrx,buff,incry) //y is overwritten
            }
            buff
          }

        /** Returns a stride-1 vector */
        def apply[M <: Int : ValueOf](xs: T*): Dense[M,T,B] = {
          val m = valueOf[M]
          assert(xs.length == m)
          assert(xs.nonEmpty)
          val b = cblas.allocateDirect(m)
          for (j <- 0 until m) cblas.put(b)(j,xs(j))
          self.fromBuffer[M](b)
        }
      }

  implicit def denseVectorSpace[M <: Int: ValueOf, T, B <: nio.Buffer](
    implicit cblas: CBlas.Aux[T,B], field: Field[T]): VectorSpace[Dense[M,T,B],T] =
  new VectorSpace[Dense[M,T,B],T] {
    import spire.syntax.field._

    def negate(f: Dense[M,T,B]): Dense[M,T,B] = 
      timesl(scalar.negate(scalar.one),f)

    /** @todo BLAS is optimized to short-circuit certain routines, by setting stride
      * = 0.
      */
    def zero: Dense[M,T,B] =
      m => _ => incrx => y => incry => buff => {
        if (incry == 0) // α0 + 0 = 0
          cblas.scal(m, scalar.zero, buff, incrx) //this should short-circuit and fill buff with zeros
        else // α0 + y = y
          cblas.copy(m, y, incry, buff, incry)
        buff
      }

    /** Vector addition */
    def plus(f: Dense[M,T,B], g: Dense[M,T,B]): Dense[M,T,B] =
      m => α => incrfg => y => incry => buff => // α(f + g) + y = αf + (αg + y)
        f(m)(α)(incrfg)(g(m)(α)(incrfg)(y)(incry)(buff))(incry)(buff)

    /** Element-wise multiplication */
    def timesl(α: T, f: Dense[M,T,B]): Dense[M,T,B] =
      m => β => incrf => y => incry => buff => // β(αf) + y = αβf + y
        f(m)(β*α)(incrf)(y)(incry)(buff)

    def scalar = field
  }
}
