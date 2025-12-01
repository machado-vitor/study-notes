class Lazy[+A](value: => A) { private lazy val internal: A = value }

class Animal
class Dog extends Animal

val lazyDog: Lazy[Dog] = new Lazy(new Dog)
val lazyAnimal: Lazy[Animal] = lazyDog // Lazy is covariant
