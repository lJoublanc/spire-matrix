package math 

import cats.Show
import cats.syntax.show._

import blas.DenseMatrix
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_16

trait showMatrix {

  implicit def showFiniteMatrix[T : Show, M <: Int : ValueOf, N <: Int : ValueOf](implicit charset : Charset = Charset.defaultCharset()) = new Show[DenseMatrix[T,M,N]] {
    
    def show(x : DenseMatrix[T,M,N]) : String = {
      if (valueOf[M] == 0 || valueOf[N] == 0) "Null Matrix (m or n == 0)"
      else {
        val rows : Seq[Seq[String]] = Seq.tabulate(valueOf[M],valueOf[N])(x(_,_).show)
        val maxWidth = rows.map(_.length).max
        lazy val blankRow = Seq.fill(maxWidth + rows.length - 1)(' ').toString //include commas
        if (charset == UTF_16) 
           s"┌ $blankRow ┐" +: rows.map("│" ++ _.mkString(", ") ++ "│" ) :+ s"└ $blankRow ┘"
        else 
           rows.map("|" ++ _.mkString(", ") ++ "|")
      }.mkString("\n")
    }
  }
}
