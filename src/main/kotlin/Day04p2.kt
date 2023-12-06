package day04p2


data class PileOfScratchcards(val cards: List<Scratchcard>) {
  fun countTotalNumberOfOwnedScratchcards(): Int {
    val numOfCardsOwnedByEachCard = Array(cards.size) { 1 }
    cards.forEachIndexed { i, card ->
      val num = numOfCardsOwnedByEachCard[i]
      for (j in i + 1 until i + 1 + card.numOfOwnedWinningNumbers) {
        numOfCardsOwnedByEachCard[j] += num
      }
    }
    return numOfCardsOwnedByEachCard.sum()
  }

  companion object {
    fun from(raw: String): PileOfScratchcards =
      PileOfScratchcards(raw.lineSequence().map { Scratchcard.from(it) }.toList())
  }

}

data class Scratchcard(val id: Int, val winningNumbers: Set<Int>, val ownedNumbers: Set<Int>) {
  val numOfOwnedWinningNumbers: Int = winningNumbers.intersect(ownedNumbers).size

  companion object {
    private val cardRegex = """Card \s*(\d+): (.+) \| (.+)""".toRegex()

    fun from(raw: String): Scratchcard {
      val (id, winningNumbers, ownedNumbers) = cardRegex
        .matchEntire(raw)?.destructured ?: error("no match for $raw")

      return Scratchcard(
        id.toInt(),
        winningNumbers.toIntSet(),
        ownedNumbers.toIntSet()
      )
    }
  }
}

private val intRegex = """(\d+)""".toRegex()

fun String.toIntSet(): Set<Int> =
  intRegex.findAll(this).map { it.value.toInt() }.toSet()
