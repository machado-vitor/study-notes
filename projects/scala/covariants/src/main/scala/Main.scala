class Lazy[+A](value: => A) { private lazy val internal: A = value }
// "=> A" is a By-name parameter.
// It means "Don't evaluate the expression, just remember it for later"

class Animal
class Dog extends Animal

val lazyDog: Lazy[Dog] = new Lazy(new Dog)
val lazyAnimal: Lazy[Animal] = lazyDog // Lazy is covariant

