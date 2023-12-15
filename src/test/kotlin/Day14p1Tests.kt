package day14p1

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.readInput

class Day14p1Tests : FunSpec({
  val examplePlatform = """
O....#....
O.OO#....#
.....##...
OO.#O....O
.O.....O#.
O.#..O.#.#
..O..#O..O
.......O..
#....###..
#OO..#....
  """.trimIndent()

  test("Given the example platform when tilted north then it looks as expected") {
    examplePlatform.toPlatform().tiltNorth().look() shouldBe """
OOOO.#.O..
OO..#....#
OO..O##..O
O..#.OO...
........#.
..#....#.#
..O..#.O.O
..O.......
#....###..
#....#....
    """.trimIndent()
  }

  test("Given the example platform when tilted north then the total load is 136") {
    examplePlatform.toPlatform().tiltNorth().calcTotalLoad() shouldBe 136
  }

  test("Given the custom platform when tilted north then the total load is 106990") {
    readInput("day14p1").toPlatform().tiltNorth().calcTotalLoad() shouldBe 106990
  }
})

