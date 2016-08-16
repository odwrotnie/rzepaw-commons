package commons.random

import commons.date.DateUtil
import org.joda.time.{DateTime, Seconds}

import scala.collection.immutable.TreeMap
import scala.util.Random

object Rand {

  def int(max: Int) = Random.nextInt(max)

  def oneOptional[T](s: Seq[T]): Option[T] = {
    if (s.isEmpty) None
    else Some(s(Random.nextInt(s.size)))
  }

  def one[T](s: Iterable[T]): T = {
    require(s.nonEmpty, "The iterable is empty")
    s.toSeq(Random.nextInt(s.size))
  }

  def list[T](s: Seq[T], n: Int): Seq[T] =
    Random.shuffle(s).take(Random.nextInt(n + 1))

  def maybe(probability: Float = 0.5f, repeats: Int = 1)(f: => Any) {
    (1 to repeats) foreach { r =>
      if (Random.nextFloat() < probability)
        f
    }
  }

  def random[T](probabilityResult: (Float, T)*): T = {
    val random: Float = Random.nextFloat * probabilityResult.map(_._1).sum
    var cumulativeProbability: Float = 0
    val pr = probabilityResult find {
      case (probability, result) =>
        cumulativeProbability += probability
        random < cumulativeProbability
    }
    pr.get._2
  }

  // BOOLEAN

  def trueFalse(trues: Float = 1, falses: Float = 1) = {
    val trueProbability: Float = trues.toFloat / (trues + falses)
    Random.nextFloat() < trueProbability
  }

  // DATE

  def dateBetween(start: DateTime, end: DateTime) =
    start.plusSeconds(Random.nextInt(Seconds.secondsBetween(start, end).getSeconds))

  def dateInTheFuture(days: Int = 1): DateTime = {
    val before = DateUtil.now.plusDays(days)
    dateInTheFuture(before)
  }
  def dateInTheFuture(before: DateTime): DateTime =
    dateBetween(DateTime.now(), before)

  def dateInThePast(days: Int = 1): DateTime = {
    val after = DateUtil.now.minusDays(days)
    dateInThePast(after)
  }
  def dateInThePast(after: DateTime): DateTime =
    dateBetween(after, DateTime.now())
}
