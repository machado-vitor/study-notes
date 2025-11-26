sealed trait Box[+A]:
  def flatMap[B](f: A => Box[B]): Box[B] // the monad operation
  def map[B](f: A => B): Box[B] = flatMap(a => Box(f(a)))  // derived from flatMap

case class Full[A](value: A) extends Box[A]:
  def flatMap[B](f: A => Box[B]): Box[B] = f(value)  // apply function to value

case object Empty extends Box[Nothing]:
  def flatMap[B](f: Nothing => Box[B]): Box[B] = Empty  // short-circuit on empty

object Box:
  def apply[A](value: A): Box[A] = Full(value) // constructor

@main def monadDemo(): Unit =
  val result = for
    x <- Box(10)
    y <- Box(20)
    z <- Box(x + y)
  yield z * 2
  println(result)

  val empty = for
    x <- Box(5)
    y <- Empty
    z <- Box(x + 1)
  yield z
  println(empty)
