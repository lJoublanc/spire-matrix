package object math 
  extends matrix 
  with showMatrix {

  object blas
    extends math.blasMatrix
    with math.blasVectorSpace
}
