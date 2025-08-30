package it.unibo.firesim.model.monads

import it.unibo.firesim.model.monads.Monads.*

object ReaderStates:

  case class ReaderState[E, S, A](run: (E, S) => (S, A))

  object ReaderState:

    extension [E, S, A](m: ReaderState[E, S, A])

      def apply(e: E, s: S): (S, A) = m match
        case ReaderState(run) => run(e, s)

  given readerStateMonad[E, S]: Monad[[A] =>> ReaderState[E, S, A]] with
    def unit[A](a: A): ReaderState[E, S, A] = ReaderState((e, s) => (s, a))

    extension [A](m: ReaderState[E, S, A])

      override def flatMap[B](f: A => ReaderState[E, S, B])
          : ReaderState[E, S, B] =
        ReaderState((e, s) =>
          m.apply(e, s) match
            case (s2, a) => f(a).apply(e, s2)
        )
