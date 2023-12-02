package day02p2

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.readInput

class Day02p2Tests : BehaviorSpec({
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

        listOf(
            "4 red, 2 green, and 6 blue" to 48,
            "1 red, 3 green, and 4 blue" to 12,
            "20 red, 13 green, and 6 blue" to 1560,
            "14 red, 3 green, and 15 blue" to 630,
            "6 red, 3 green, and 2 blue" to 36
        ).forEach { (text, expectedPower) ->
            val cubes = Cubes.from(text)
            And("the cubes are $cubes") {
                When("we calculate the power") {
                    val power = cubes.power
                    Then("it should be $expectedPower") {
                        power shouldBe expectedPower
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

        When("we test if the handful can contain the bag") {
            val canContain = handful.canContain(bag)
            Then("it should return false") {
                canContain shouldBe false
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
                    Cubes(green = 3, red = 6),
                    Cubes(green = 3, blue = 15, red = 14)
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

    Given("A game and a bag of cubes") {
        val bag = Cubes(red = 12, green = 13, blue = 14)
        And("the bag is $bag") {
            listOf(
                "Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green" to true,
                "Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue" to true,
                "Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red" to false,
                "Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red" to false,
                "Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green" to true
            ).forEach { (gameText, expectedPossibility) ->
                And("the game is $gameText") {
                    val game = Game.from(gameText)
                    When("we test if the game is possible") {
                        val possible = game.isPossible(bag)
                        Then("it should return $expectedPossibility") {
                            possible shouldBe expectedPossibility
                        }
                    }
                }
            }
        }
    }

    Given("An example list of games and a bag of cubes") {
        val games = Game.gameListFrom(
            """
            Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
            Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
            Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
            Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
            Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
        """.trimIndent()
        )
        val bag = Cubes(red = 12, green = 13, blue = 14)
        When("we sum the ids of the games that are possible") {
            val sum = games.idSumOfPossibleGames(bag)
            Then("we should get the correct sum") {
                sum shouldBe 8
            }
        }
        When("we sum the power of the minimum bags of the games") {
            val sum = games.sumOfMinimumBagPowers()
            Then("we should get the correct sum") {
                sum shouldBe 2286
            }
        }
    }

    Given("Our custom input list of games and a bag of cubes") {
        val games = Game.gameListFrom(readInput("day02p1"))
        val bag = Cubes(red = 12, green = 13, blue = 14)
        When("we sum the ids of the games that are possible") {
            val sum = games.idSumOfPossibleGames(bag)
            Then("we should get the correct sum") {
                sum shouldBe 2505
            }
        }
        When("we sum the power of the minimum bags of the games") {
            val sum = games.sumOfMinimumBagPowers()
            Then("we should get the correct sum") {
                sum shouldBe 70265
            }
        }
    }

    Given("A game") {
        listOf(
            "Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green" to "4 red, 2 green, and 6 blue",
            "Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue" to "1 red, 3 green, and 4 blue",
            "Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red" to "20 red, 13 green, and 6 blue",
            "Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red" to "14 red, 3 green, and 15 blue",
            "Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green" to "6 red, 3 green, and 2 blue"
        ).asSequence()
            .map { (game, bag) -> Game.from(game) to Cubes.from(bag) }
            .forEach { (game, expectedBag) ->
                And("the game is $game and the bag is $expectedBag") {
                    When("we calculate the minimum sized bag of cubes") {
                        val bag = game.minimumBagOfCubes()
                        Then("it should be $expectedBag") {
                            bag shouldBe expectedBag
                        }
                    }
                }
            }
    }

})
