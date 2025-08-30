package it.unibo.firesim.model.monads

object Monads:

  trait Monad[M[_]]:
    def unit[A](a: A): M[A]

    extension [A](m: M[A])
      def flatMap[B](f: A => M[B]): M[B]
      def map[B](f: A => B): M[B] = m.flatMap(a => unit(f(a)))
