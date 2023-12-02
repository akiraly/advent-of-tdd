package day02p1

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class Day02p1Tests : BehaviorSpec({
    Given("a number of red, green and blue cubes") {
        Then("we can represent them properly") {
            val cubes = Cubes(red = 3, green = 5, blue = 7)
            cubes.red shouldBe 3
            cubes.green shouldBe 5
            cubes.blue shouldBe 7
        }
    }

    Given("a series of text representations of handful of cubes") {
        listOf(
            "3 red, 5 green, 4 blue" to Cubes(red = 3, green = 5, blue = 4),
            "3 red" to Cubes(red = 3),
            "3 blue, 4 red" to Cubes(blue = 3, red = 4),
            "12 red, 13 green, 14 blue" to Cubes(red = 12, green = 13, blue = 14)
        ).forEach { (text, expectedCubes) ->
            And("the text is `$text`") {
                When("we parse it") {
                    val cubes = Cubes.from(text)
                    Then("it should parse as $expectedCubes") {
                        cubes shouldBe expectedCubes
                    }
                }
            }
        }
    }

    Given("A handful and a bag of cubes") {
        val handful = Cubes(red = 3, green = 5, blue = 4)
        val bag = Cubes(red = 12, green = 13, blue = 14)
        When("we test if the bag can contain the handful") {
            val canContain = bag.canContain(handful)
            Then("it should return true") {
                canContain shouldBe true
            }
        }
    }

    Given("a game with an id and a series of handful of cubes") {
        Then("we can represent them properly") {
            val game = Game(
                1,
                listOf(
                    Cubes(blue = 3, red = 4),
                    Cubes(red = 1, green = 2, blue = 6),
                    Cubes(green = 2)
                )
            )
            game.id shouldBe 1
            game.handfuls shouldBe listOf(
                Cubes(blue = 3, red = 4),
                Cubes(red = 1, green = 2, blue = 6),
                Cubes(green = 2)
            )
        }
    }

    Given("a series of text representations of games") {
        listOf(
            "Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green" to Game(
                1,
                listOf(
                    Cubes(blue = 3, red = 4),
                    Cubes(red = 1, green = 2, blue = 6),
                    Cubes(green = 2)
                )
            ),

            "Game 14: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red" to Game(
                14,
                listOf(
                    Cubes(green = 1, red = 3, blue = 6),
                    Cubes(red = 1, green = 2, blue = 6),
                    Cubes(green = 2)
                )
            )
        ).forEach { (text, expectedGame) ->
            And("the text is `$text`") {
                When("we parse it") {
                    val game = Game.from(text)
                    Then("it should parse as $expectedGame") {
                        game shouldBe expectedGame
                    }
                }
            }
        }
    }
})

data class Game(val id: Int, val handfuls: List<Cubes>) {
    companion object {
        fun from(raw: String): Game {
            val (gameText, handfulTexts) = raw.split(": ")
            val id = gameText.split(" ")[1].toInt()
            val handfuls = handfulTexts.split("; ").map { Cubes.from(it) }
            return Game(id, handfuls)
        }
    }
}

data class Cubes(val red: Int = 0, val green: Int = 0, val blue: Int = 0) {
    fun canContain(other: Cubes): Boolean =
        red >= other.red && green >= other.green && blue >= other.blue

    companion object {
        private val reds = """(\d+) red""".toRegex()

        private val greens = """(\d+) green""".toRegex()

        private val blues = """(\d+) blue""".toRegex()

        private fun Regex.parseNumOfCubes(raw: String) = find(raw)?.let {
            it.groupValues[1].toInt()
        } ?: 0

        fun from(raw: String): Cubes = Cubes(
            red = reds.parseNumOfCubes(raw),
            green = greens.parseNumOfCubes(raw),
            blue = blues.parseNumOfCubes(raw)
        )
    }
}
