package day07p2

import day07p2.Card.Ace
import day07p2.Card.Joker
import day07p2.HandType.*

fun List<HandBid>.totalWinning(): Long {
  return this.asSequence().sortedBy { it.hand }.mapIndexed { index, handBid -> (index + 1L) * handBid.bid }.sum()
}

fun String.toHandBidList(): List<HandBid> = lines().map { it.toHandBid() }
fun String.toHandBid(): HandBid {
  val (hand, bid) = split(" ")
  return HandBid(hand.toHand(), bid.toInt())
}

data class HandBid(val hand: Hand, val bid: Int)
enum class HandType {
  HighCard,
  OnePair,
  TwoPair,
  ThreeOfAKind,
  FullHouse,
  FourOfAKind,
  FiveOfAKind;

  companion object {
    fun allHandTypes(): List<HandType> = entries
  }
}

fun String.toHand(): Hand = Hand(map { it.toCard() }.toList())
data class Hand(val cards: List<Card>) : Comparable<Hand> {
  val handType: HandType

  init {
    require(cards.size == 5) { cards }
    val cardsByType = cards.groupBy { it }.toMutableMap()
    val jokers = cardsByType.remove(Joker)
    if (jokers != null) {
      val (card, cards) = cardsByType.maxByOrNull { (_, cards) -> cards.size }?.let { it.key to it.value }
        ?: (Ace to emptyList())
      cardsByType[card] = cards + jokers.indices.map { card }
    }
    handType = if (cardsByType.size == 5) HighCard
    else if (cardsByType.size == 4) OnePair
    else if (cardsByType.size == 3) {
      if (cardsByType.values.any { it.size == 2 }) TwoPair else ThreeOfAKind
    } else if (cardsByType.size == 2) {
      if (cardsByType.values.any { it.size == 3 }) FullHouse else FourOfAKind
    } else FiveOfAKind
  }

  override fun compareTo(other: Hand): Int {
    val result = handType.compareTo(other.handType)
    if (result != 0) return result

    return cards.asSequence().zip(other.cards.asSequence())
      .filter { (a, b) -> a != b }
      .map { (a, b) -> a.compareTo(b) }
      .firstOrNull() ?: 0
  }
}

fun Char.toCard(): Card = Card[this]
enum class Card(val shortId: Char) {
  Joker('J'),
  Two('2'),
  Three('3'),
  Four('4'),
  Five('5'),
  Six('6'),
  Seven('7'),
  Eight('8'),
  Nine('9'),
  Ten('T'),
  Queen('Q'),
  King('K'),
  Ace('A');

  companion object {
    fun allCards(): List<Card> = entries

    private val byShortId = entries.associateBy { it.shortId }
    operator fun get(shortId: Char): Card = byShortId.getValue(shortId)
  }
}
