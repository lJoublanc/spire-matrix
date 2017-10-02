package spire.cats

import spire.math.FiniteMatrix

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.{UTF_16,UTF_8}

import cats.Show

trait showMatrix {
  implicit class ShowOpsSyntax[T](t : T)(implicit S: Show[T]) extends AnyRef { def show : String = S.show(t) }

  /** Given a function from T to String, generate a `Show` */
  protected[showMatrix] def from[T](f: T => String) = new Show[T]{ override def show(t : T) : String = f(t) }

  /** Override this e.g. to change display precision of matrix elements */
  implicit val doubleShow : Show[Double] = from[Double]( d => f"$d%.2f")
  
  /** Override this e.g. to change display precision of matrix elements */
  implicit val realShow : Show[Float] = from[Float]( d => f"$d%.2f")

  /** Display on a console. Supports matrices of dimensions up to 80x80 and supports unicode */
  implicit def consoleShowFiniteMatrix[T : Show, M <: Int : ValueOf, N <: Int : ValueOf, Mat[t,m <: Int,n <:Int] <: FiniteMatrix[t,m,n]]
      (implicit charset : Charset = Charset.defaultCharset()) : Show[Mat[T,M,N]] =
  new Show[Mat[T,M,N]] {
    type Matrix = Mat[T,M,N]
    val showT = implicitly[Show[T]]
    val (maxWidth,maxHeight) = (80/6,80)
    val comma = ", "

    /** Return a sequence of `comma` separated rows with aligned (equal width) elements */
    protected def matToStr(x : Matrix) : Seq[String] = {
      val ss : Seq[Seq[String]] = Seq.tabulate(valueOf[M],valueOf[N])( (i,j) => showT show x(i,j))
      val maxRowWidth = ss.foldLeft(0)(_ max _.length)
      val maxElemWidth = ss.view.flatten.foldLeft(0)(_ max _.length)
      ss map { row =>
         row map { col =>
           val prefix = Array.fill(maxElemWidth - col.length)(' ')
           prefix ++: col
         } mkString comma
      }
    }

    def show(x : Matrix) : String = {
      lazy val rows : Seq[String] = matToStr(x)
      if (x.size == 0) "[ Null Matrix (m x n = 0) ]"
      else if (x.cols > maxWidth || x.rows > maxHeight) s"[ Matrix (${x.rows} x ${x.cols} - too large to display) ]"
      else if (x.rows == 1) "[" ++ rows.head ++ "]"
      else {
        val maxWidth = rows.foldLeft(0)(_ max _.length)
        lazy val blankRow = Seq.fill(maxWidth)(' ').toString
        if (Set(UTF_8,UTF_16) contains charset) 
           s"┌$blankRow┐" +: rows.map("│" ++ _ ++ "│" ) :+ s"└$blankRow┘"
        else 
           rows.map("|" ++ _ ++ "|")
      }.mkString("\n")
    }
  }
}
