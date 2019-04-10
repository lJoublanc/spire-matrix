package spire

import java.nio

package object blas {
  implicit val defaultByteOrder: nio.ByteOrder = nio.ByteOrder.LITTLE_ENDIAN
}
