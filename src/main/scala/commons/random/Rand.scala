package commons.random

import org.joda.time.{DateTime, Seconds}

import scala.util.Random

object Rand {

  def int(max: Int) = Random.nextInt(max)

  def oneOptional[T](s: Seq[T]): Option[T] = {
    if (s.isEmpty) None
    else Some(s(Random.nextInt(s.size)))
  }

  def one[T](s: Seq[T]): T = {
    require(!s.isEmpty)
    s(Random.nextInt(s.size))
  }

  def list[T](s: Seq[T], n: Int): Seq[T] =
    Random.shuffle(s).take(Random.nextInt(n + 1))

  def maybe(probability: Float = 0.5f, repeats: Int = 1)(f: => Any) {
    (1 to repeats) foreach { r =>
      if (probability < Random.nextFloat())
        f
    }
  }

  def trueFalse(trues: Float = 1, falses: Float = 1) = {
    val trueProbability: Float = trues.toFloat / (trues + falses)
    Random.nextFloat() < trueProbability
  }

  def dateBetween(start: DateTime, end: DateTime) =
    start.plusSeconds(Random.nextInt(Seconds.secondsBetween(start, end).getSeconds))

  def dateInTheFuture(before: DateTime) =
    dateBetween(DateTime.now(), before)

  def dateInThePast(after: DateTime) =
    dateBetween(after, DateTime.now())
}
