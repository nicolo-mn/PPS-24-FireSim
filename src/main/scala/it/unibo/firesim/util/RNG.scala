package it.unibo.firesim.util

/** An interface for a random number generator.
  */
trait RNG:
  /** Generates a random Double and the next state of the RNG, without modifying
    * the current instance.
    * @return
    *   tuple containing the number generated and the next instance
    */
  def nextDouble: (Double, RNG)

/** Implementation of RNG using a linear congruential generator (LCG).
  *
  * @param seed
  *   the initial seed for the RNG
  */
case class SimpleRNG(seed: Long) extends RNG:

  def nextDouble: (Double, RNG) =
    val newSeed = (seed * 0x5deece66dL + 0xbL) & 0xffffffffffffL
    val nextRNG = SimpleRNG(newSeed)
    val n =
      (newSeed << 5 | (newSeed >>> 43)).toDouble / (1L << 53).toDouble // usa 53 bit
    (n, nextRNG)
