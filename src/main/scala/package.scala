package object math extends matrix {
  object blas
    extends math.blasMatrix
    with math.blasVectorSpace
}
