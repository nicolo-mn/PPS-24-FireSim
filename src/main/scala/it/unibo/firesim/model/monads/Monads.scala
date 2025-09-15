package it.unibo.firesim.model.monads

/** A computational package for elements of type A. */
object Monads:

  /** Monad interface.
   *
   * Defines unit and composition operations.
   */
  trait Monad[M[_]]:
    /** Wraps a value. */
    def unit[A](a: A): M[A]

    extension [A](m: M[A])
      /** Chains computations. */
      def flatMap[B](f: A => M[B]): M[B]
      /** Map derived operation. */
      def map[B](f: A => B): M[B] = m.flatMap(a => unit(f(a)))
