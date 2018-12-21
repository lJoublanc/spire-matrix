package spire.blas

import java.nio

/** Data type representing a strided vector.
  * @tparam M The dimension of the vector, at the type level.
  * @tparam T The type of the scalar in the vector.
  * @tparam B The type of buffer used to store the vector.
  */
case class GeneralVector[M <: Int: ValueOf, T, B <: nio.Buffer](buffer: B, stride: Int = 1) {
  def size: Int = if (stride == 0) (valueOf[M]: Int) else valueOf[M] / stride
}
