package it.unibo.firesim.model.monads

import it.unibo.firesim.model.monads.Monads.*

/** The ReaderState monad encapsulates a computation that depends on an
  * environment of type E and evolves a state of type S.
  */
object ReaderStates:

  /** ReaderState case class
    *
    * @param run
    *   a function that takes an environment E and a state S and produces a new
    *   state and a result of type A.
    */
  case class ReaderState[E, S, A](run: (E, S) => (S, A))

  /** Minimal set of algorithms for the ReaderState monad. */
  object ReaderState:

    extension [E, S, A](m: ReaderState[E, S, A])

      /** Executes the ReaderState computation with a given environment and
        * state.
        *
        * @param e
        *   the environment.
        * @param s
        *   the initial state.
        * @return
        *   a tuple containing the new state and the computed result.
        */
      def apply(e: E, s: S): (S, A) = m match
        case ReaderState(run) => run(e, s)

  /** Defines a given instance that works on all types E, S */
  given readerStateMonad[E, S]: Monad[[A] =>> ReaderState[E, S, A]] with
    def unit[A](a: A): ReaderState[E, S, A] = ReaderState((e, s) => (s, a))

    extension [A](m: ReaderState[E, S, A])

      /** Runs the state, use result to create a new state. Chains computations
        * passing down the environment.
        *
        * @param f
        *   a function that transforms the extracted result into a new
        *   ReaderState.
        * @return
        *   a new ReaderState representing the composed computation.
        */
      override def flatMap[B](f: A => ReaderState[E, S, B])
          : ReaderState[E, S, B] =
        ReaderState((e, s) =>
          m.apply(e, s) match
            case (s2, a) => f(a).apply(e, s2)
        )
