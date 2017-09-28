package math

import spire.algebra.{Ring,Order,Signed}
import scala.specialized

object ULPOrderSingle {
  /** Convenience functions for introspecting IEEE-754 numbers. */
  implicit class IEEE754Float(val f : Float) {
     def i : Int = java.lang.Float.floatToIntBits(f)
     def isNegative : Boolean = i < 0
     // def mantissa = i & ( (1 << 23) - 1 )
     // def exponent = (i >> 23) & 0xFF
  }
}

object ULPOrderDouble {
  /** Convenience functions for manipulating IEEE-754 numbers. */
  implicit class IEEE754Double(val f : Double) {
     def i : Long = java.lang.Double.doubleToLongBits(f)
     def isNegative : Boolean = i < 0
  }
}

/**
  * "Soft" equality for IEEE-754 double precision numbers using ULP.
  * This will allow an approximate comparison of floating-point numbers.
  * Mix this into an `xxxAlgebra` to override ordering.
  * Overrides `eqv`, `neqv` etc. with the defaults form [[cats.kernel.Order]], that just forward to `compare`.
  * Adapted from `randomascii`.
  * @see <href a=https://randomascii.wordpress.com/2012/02/25/comparing-floating-point-numbers-2012-edition/>section "ULP, he said nervously"</href>
  */
trait ULPOrderDouble extends Order[Double] {
  import ULPOrderDouble._
  import java.lang.Math.abs
  val δ : Long = 50

  override def eqv(x: Double, y: Double): Boolean = compare(x, y) == 0
  override def neqv(x: Double, y: Double): Boolean = compare(x, y) != 0
  override def lteqv(x: Double, y: Double): Boolean = compare(x, y) <= 0
  override def lt(x: Double, y: Double): Boolean = compare(x, y) < 0
  override def gteqv(x: Double, y: Double): Boolean = compare(x, y) >= 0
  override def gt(x: Double, y: Double): Boolean = compare(x, y) > 0
  override def compare(x: Double, y: Double): Int =
    if (x.isNegative != y.isNegative) {
      if (x == y) 0  // +0 == -0
      else if (x.isNegative) -1 else +1
    }
    else { 
      val diff : Long = x.i - y.i
      if ( abs(diff) <= δ ) 0
      else if (diff < 0) -1 else +1
    }
}
