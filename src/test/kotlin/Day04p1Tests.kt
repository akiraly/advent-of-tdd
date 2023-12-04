package day04p1

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import io.readInput

class Day04p1Tests : FunSpec({
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
        card.playedNumbers shouldBe setOf(6, 9, 17, 31, 48, 53, 83, 86)
    }

    context("Given a series of scratchcards then the points should be calculated correctly") {
        table(
            headers("card", "points"),
            row("Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53", 8),
            row("Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19", 2),
            row("Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1", 2),
            row("Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83", 1),
            row("Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36", 0),
            row("Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11", 0)
        ).forAll { card, points ->
            test("Given a scratchcard of `$card` then the points should be $points") {
                Scratchcard.from(card).points() shouldBe points
            }
        }
    }

    test("Given an example pile of scratchcards then the total points should be 13") {
        calcTotalPoints4PileOfScratchcards(
            """
                Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
                Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
                Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
                Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
                Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
                Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
            """.trimIndent()
        ) shouldBe 13
    }
    test("Given our custom pile of scratchcards then the total points should be 28750") {
        calcTotalPoints4PileOfScratchcards(readInput("day04p1")) shouldBe 28750
    }

})
