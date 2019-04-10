package spire.blas

/** Typeclass to make `java.nio.Buffer generic. */
trait Buffer[T] {
  import java.nio

  final type Element = T

  type Buffer <: nio.Buffer

  protected def fromBytes(b: nio.ByteBuffer): Buffer

  protected def elemSizeBytes: Int

  def allocateDirect(i: Int)(implicit byteOrder: nio.ByteOrder): Buffer =
    fromBytes(nio.ByteBuffer.allocateDirect(i * elemSizeBytes).order(byteOrder))

  def wrap(array: Array[T])(implicit byteOrder: nio.ByteOrder): Buffer

  def put(b: Buffer)(i: Int, t: T): Buffer

  def get(b: Buffer)(i: Int): T
}
