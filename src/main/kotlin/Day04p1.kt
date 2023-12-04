package day04p1

import org.apache.commons.math3.util.ArithmeticUtils

fun calcTotalPoints4PileOfScratchcards(raw: String): Long =
    raw.lineSequence().sumOf { Scratchcard.from(it).points() }

data class Scratchcard(val id: Int, val winningNumbers: Set<Int>, val playedNumbers: Set<Int>) {
    fun points(): Long {
        val numOfMatchedNumbers = winningNumbers.intersect(playedNumbers).size
        return if (numOfMatchedNumbers > 0) ArithmeticUtils.pow(2L, numOfMatchedNumbers - 1) else 0
    }

    companion object {
        private val cardRegex = """Card \s*(\d+): (.+) \| (.+)""".toRegex()

        fun from(raw: String): Scratchcard {
            val (id, winningNumbers, playedNumbers) = cardRegex
                .matchEntire(raw)?.destructured ?: error("no match for $raw")

            return Scratchcard(
                id.toInt(),
                winningNumbers.toIntSet(),
                playedNumbers.toIntSet()
            )
        }
    }
}

private val intRegex = """(\d+)""".toRegex()

internal fun String.toIntSet(): Set<Int> =
    intRegex.findAll(this).map { it.value.toInt() }.toSet()