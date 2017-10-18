package nl.rabobank.oss.rules.finance.nl

import java.text.NumberFormat
import java.util.Locale

import scala.math.BigDecimal.RoundingMode._

// scalastyle:off method.name

/**
 * Representeert een bedrag in euro's.
 */
case class Bedrag private[finance] (waarde: BigDecimal) {
  /** Returnt de som van dit bedrag en n. */
  def + (n: Bedrag): Bedrag = Bedrag(waarde + n.waarde)

  /** Returnt het verschil tussen dit bedrag en n. */
  def - (n: Bedrag): Bedrag = Bedrag(waarde - n.waarde)

  /** Returnt het product van dit bedrag en n. */
  def * (n: BigDecimal): Bedrag = Bedrag(waarde * n)

  /** Returnt het quotiënt van dit bedrag en n. */
  def / (n: BigDecimal): Bedrag = Bedrag(waarde / n)

  /** Returnt het quotiënt van dit bedrag en n. */
  def / (n: Bedrag): BigDecimal = waarde / n.waarde

  /** Kapt dit bedrag af (naar beneden) op een rond honderdtal euro's. */
  def afgekaptOp100Euro: Bedrag = afgekaptOp(-2) // scalastyle:ignore magic.number

  /** Kapt dit bedrag af (naar beneden) op ronde euro's. */
  def afgekaptOpEuros: Bedrag = afgekaptOp(0)

  /** Kapt dit bedrag af (naar beneden) op hele centen. */
  def afgekaptOpCenten: Bedrag = afgekaptOp(2)

  /** Rondt dit bedrag af op hele centen, volgens BigDecimal.RoundingMode.HALF_EVEN. */
  def afgerondOpCenten: Bedrag = afgerondOp(2, BigDecimal.RoundingMode.HALF_EVEN)

  def afgerondOp(aantalDecimalen: Integer, afrondingsWijze: RoundingMode): Bedrag =
    Bedrag(waarde.setScale(aantalDecimalen, afrondingsWijze))

  /** Kapt dit bedrag af (naar beneden) op het gegeven aantal decimalen. */
  private def afgekaptOp(decimalen: Int): Bedrag = afgerondOp(decimalen, BigDecimal.RoundingMode.FLOOR)

  override def toString = NumberFormat.getCurrencyInstance(Bedrag.nederland).format(waarde)
}

object Bedrag {
  private val nederland = new Locale("nl", "NL")
  private[nl] val centNaarEuroFactor = BigDecimal(0.01)
}

trait BedragImplicits {
  abstract class ToBedrag(value: BigDecimal) {
    /** Maakt een Bedrag. */
    def euro: Bedrag = Bedrag(value)

    /** Maakt een Bedrag. */
    def cent: Bedrag = Bedrag(value * Bedrag.centNaarEuroFactor)

    /** Returnt het product van deze BigDecimal en Bedrag b. */
    def *(b: Bedrag): Bedrag = b * value
  }
  implicit class BigDecimalToBedrag(value: BigDecimal) extends ToBedrag(value)
  implicit class IntToBedrag(value: Int) extends ToBedrag(value)
  implicit class LongToBedrag(value: Long) extends ToBedrag(value)

  /** Het is niet mogelijk om een String te vermenigvuldigen met een Bedrag
    * Dit conflicteert met String's eigen * functie en is dus niet geimplementeerd*/
  implicit class StringToBedrag(value: String){
    /** Maakt een Bedrag. */
    def euro: Bedrag = Bedrag(BigDecimal(value))

    def cent: Bedrag = Bedrag(BigDecimal(value) * Bedrag.centNaarEuroFactor)
  }

  /** Zorgt ervoor dat zaken als "sum" gemakkelijk kunnen worden berekend op verzamelingen van Bedrag. */
  implicit object NumericBedrag extends Numeric[Bedrag] {
    override def plus(x: Bedrag, y: Bedrag): Bedrag = x + y
    override def minus(x: Bedrag, y: Bedrag): Bedrag = x - y
    override def times(x: Bedrag, y: Bedrag): Bedrag =
      throw new UnsupportedOperationException("Vermenigvuldiging van bedrag*bedrag zou een bedrag^2 geven, wat niets betekent.")
    override def negate(x: Bedrag): Bedrag = Bedrag(-x.waarde)
    override def fromInt(x: Int): Bedrag = x.euro
    override def toInt(x: Bedrag): Int = throw new UnsupportedOperationException("toInt zou leiden tot een verlies van precisie.")
    override def toLong(x: Bedrag): Long = throw new UnsupportedOperationException("toLong zou leiden tot een verlies van precisie.")
    override def toFloat(x: Bedrag): Float = throw new UnsupportedOperationException("toFloat zou leiden tot een verlies van precisie.")
    override def toDouble(x: Bedrag): Double = throw new UnsupportedOperationException("toDouble zou leiden tot een verlies van precisie.")
    override def compare(x: Bedrag, y: Bedrag): Int = x.waarde compare y.waarde
  }
}
