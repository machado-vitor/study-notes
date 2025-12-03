# Monads in Scala

## What is a Monad?

A Monad is a design pattern that wraps values in a computational context and provides a way to chain operations on those wrapped values while maintaining the context.

A monad must implement two fundamental operations:
- `flatMap`: chains operations that return wrapped values
- `unit`: wraps a value into the monadic context

Monads must satisfy three laws:
1. Left identity: `unit(x).flatMap(f) == f(x)`
2. Right identity: `m.flatMap(unit) == m`
3. Associativity: `m.flatMap(f).flatMap(g) == m.flatMap(x => f(x).flatMap(g))`

### Option Monad

```scala
val some: Option[Int] = Some(5)
val none: Option[Int] = None

val result = some.flatMap(x => Some(x * 2))
```

### Either Monad
```scala
val right: Either[String, Int] = Right(42)
val left: Either[String, Int] = Left("Error")

val result = right.flatMap(x => Right(x + 1))
```

### Try Monad
```scala
import scala.util.{Try, Success, Failure}

val success: Try[Int] = Success(10)
val failure: Try[Int] = Failure(new Exception("Failed"))

val result = success.flatMap(x => Try(x / 2))
```

### Future Monad
```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

val future: Future[Int] = Future { 42 }
val result = future.flatMap(x => Future { x * 2 })
```

## For Comprehensions
Scala provides syntactic sugar for monadic operations using for comprehensions:
```scala
val result = for {
  x <- Some(5)
  y <- Some(10)
  z <- Some(15)
} yield x + y + z
```

This is equivalent to:
```scala
val result =
  Some(5).flatMap(x =>
    Some(10).flatMap(y =>
      Some(15).map(z =>
        x + y + z
      )
    )
  )
```