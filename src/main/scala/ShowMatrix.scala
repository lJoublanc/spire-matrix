package math 

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.{UTF_16,UTF_8}

trait showMatrix {
  /** Modeled after cats.show. It should be possible to directly replace it. */
  trait Show[-T] extends AnyRef { def show(t : T) : String }

  implicit class ShowOpsSyntax[-T](t : T)(implicit S: Show[T]) extends AnyRef { def show : String = S.show(t) }

  /** Given a formatting string (without the `f` interpolator), generate a `Show` */
  protected[showMatrix] def from[T](f: T => String) = new Show[T]{ override def show(t : T) : String = f(t) }

  /** Override this e.g. to change display precision of matrix elements */
  implicit val doubleShow : Show[Double] = from[Double]( d => f"$d%.2f")
  
  /** Override this e.g. to change display precision of matrix elements */
  implicit val realShow : Show[Float] = from[Float]( d => f"$d%.2f")

  /** Display on a console. Supports matrices of dimensions up to 80x80 and supports unicode */
  implicit def consoleShowFiniteMatrix[T : Show, M <: Int : ValueOf, N <: Int : ValueOf]
      (implicit charset : Charset = Charset.defaultCharset()) =
  new Show[FiniteMatrix[T,M,N]] {
    val (maxWidth,maxHeight) = (80/6,80)
    val comma = ", "

    protected def rowToStr(s : Seq[String]) : String = s.mkString(comma)

    def show(x : FiniteMatrix[T,M,N]) : String = {
      val showT = implicitly[Show[T]]
     
      if (x.size == 0) "[ Null Matrix (m x n = 0) ]"
      else if (x.cols > maxWidth || x.rows > maxHeight) s"[ Matrix (${x.rows} x ${x.cols} - too large to display) ]"
      else if (x.rows == 1) "[" ++ rowToStr(Seq.tabulate(x.size)(i => showT.show(x(0,i)))) ++ "]"
      else {
        val rows : Seq[Seq[String]] = Seq.tabulate(valueOf[M],valueOf[N])((i,j) => showT.show(x(i,j)))
        val maxWidth = rows.map(_.length).max
        lazy val blankRow = Seq.fill(maxWidth + rows.length - 1)(' ').toString
        if (Set(UTF_8,UTF_16) contains charset) 
           s"┌ $blankRow ┐" +: rows.map("│" ++ rowToStr(_) ++ "│" ) :+ s"└ $blankRow ┘"
        else 
           rows.map("|" ++ rowToStr(_) ++ "|")
      }.mkString("\n")
    }
  }
}
