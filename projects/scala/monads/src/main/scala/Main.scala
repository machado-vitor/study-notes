sealed trait Box[+A]: // sealed so the implementation is on the same file
  def flatMap[B](f: A => Box[B]): Box[B] // the monad operation
  def map[B](f: A => B): Box[B] = flatMap(a => Box(f(a)))  // derived from flatMap

case class Full[A](value: A) extends Box[A]: // class create multiple instances with different data.
  def flatMap[B](f: A => Box[B]): Box[B] = f(value)  // apply function to value

case object Empty extends Box[Nothing]: // Only one instance lives in the entire program/ singleton
  def flatMap[B](f: Nothing => Box[B]): Box[B] = Empty  // short-circuit on empty
// Nothing is a subtype of everything, Empty can be returned from any Box operation
object Box:
  def apply[A](value: A): Box[A] = Full(value) // constructor

@main def monadDemo(): Unit =
  val result = for
    x <- Box(10)
    y <- Box(20)
    z <- Box(x + y) // flatmap
  yield z * 2 // yield uses map
  println(result)

  val empty = for
    x <- Box(5)
    y <- Empty //  When Empty is encountered, flatMap returns Empty immediately.
    z <- Box(x + 1)
  yield z
  println(empty)
