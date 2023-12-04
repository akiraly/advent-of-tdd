package day04p2

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.readInput

class Day04p2Tests : FunSpec({
    test("""Given "41" as input when we parse it then it should be setOf(42)""") {
        "41".toIntSet() shouldBe setOf(41)
    }

    test("""Given "83 86  6 31 17  9 48 53" as input when we parse it then it should be setOf(6, 9, 17, 31, 48, 53, 83, 86)""") {
        "83 86  6 31 17  9 48 53".toIntSet() shouldBe setOf(6, 9, 17, 31, 48, 53, 83, 86)
    }

    test("""Given "Card 12: 41 48 83 86 17 | 83 86  6 31 17  9 48 53" as input then we should parse it as expected""") {
        val card = Scratchcard.from("Card 12: 41 48 83 86 17 | 83 86  6 31 17  9 48 53")
        card.id shouldBe 12
        card.winningNumbers shouldBe setOf(17, 41, 48, 83, 86)
        card.ownedNumbers shouldBe setOf(6, 9, 17, 31, 48, 53, 83, 86)
    }

    context("Given a series of scratchcards then the num of owned winning numbers should be calculated correctly") {
        table(
            headers("card", "num of owned winning numbers"),
            row("Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53", 4),
            row("Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19", 2),
            row("Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1", 2),
            row("Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83", 1),
            row("Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36", 0),
            row("Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11", 0)
        ).forAll { card, numOfOwnedWinningNumbers ->
            test("Given a scratchcard of `$card` then the numb of owned winning numbers should be $numOfOwnedWinningNumbers") {
                Scratchcard.from(card).numOfOwnedWinningNumbers shouldBe numOfOwnedWinningNumbers
            }
        }
    }

    test("Given an example pile of scratchcards then we can properly represent it") {
        val pile = PileOfScratchcards.from(
            """
                Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
                Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
                Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
                Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
                Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
                Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
            """.trimIndent()
        )
        pile.cards shouldHaveSize 6
        pile.cards.forEachIndexed { index, card -> card.id shouldBe index + 1 }
    }

    test("Given an example pile of scratchcards then the total number of owned scratchcards should be 30") {
        val pile = PileOfScratchcards.from(
            """
                Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
                Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
                Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
                Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
                Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
                Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
            """.trimIndent()
        )
        pile.countTotalNumberOfOwnedScratchcards() shouldBe 30
    }

    test("Given our custom pile of scratchcards then the total number of owned scratchcards should be 10212704") {
        PileOfScratchcards.from(readInput("day04p1"))
            .countTotalNumberOfOwnedScratchcards() shouldBe 10212704
    }
})
