package spire

import java.nio

package object blas {
  type Vector[m <: Int,t,b <: nio.Buffer] = t => GeneralVector[m,t,b] => GeneralVector[m,t,b]
}
