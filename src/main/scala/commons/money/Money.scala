package commons.money

case class Money(cents: Long) {

  def value: Float = cents.toFloat / 100
  override def toString: String = f"$value%.2f"

  def +(x: Long) = Money(cents + x)
  def +(x: Float) = Money(cents + math.round(x * 100))
  def +(x: Double) = Money(cents + math.round(x * 100))

  def -(x: Long) = Money(cents - x)
  def -(x: Float) = Money(cents - math.round(x * 100))
  def -(x: Double) = Money(cents - math.round(x * 100))

  def *(x: Long) = Money(cents * x)
  def *(x: Float) = Money(cents * math.round(x * 100))
  def *(x: Double) = Money(cents * math.round(x * 100))

  def /(x: Long) = Money(cents / x)
  def /(x: Float) = Money(cents / math.round(x * 100))
  def /(x: Double) = Money(cents / math.round(x * 100))

  def +(m: Money) = Money(cents + m.cents)
  def -(m: Money) = Money(cents - m.cents)
}
