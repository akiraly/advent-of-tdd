package day14p2

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.readInput

class Day14p2Tests : FunSpec({
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

    examplePlatform.toPlatform() shouldBe examplePlatform.toPlatform()
    examplePlatform.toPlatform() shouldBe examplePlatform.toPlatform().deepCopy()
  }

  test("Given the example platform when tilted north then the total load is 136") {
    examplePlatform.toPlatform().tiltNorth().calcTotalLoad() shouldBe 136
  }

  test("Given the custom platform when tilted north then the total load is 106990") {
    readInput("day14p1").toPlatform().tiltNorth().calcTotalLoad() shouldBe 106990
  }

  context("test spinning") {
    val platform = examplePlatform.toPlatform()

    test("Given the example platform when spun for 1 cycle then it looks as expected") {
      platform.deepCopy().spin(1).look() shouldBe """
.....#....
....#...O#
...OO##...
.OO#......
.....OOO#.
.O#...O#.#
....O#....
......OOOO
#...O###..
#..OO#....
    """.trimIndent()
    }

    test("Given the example platform when spun for 2 cycles then it looks as expected") {
      platform.deepCopy().spin(2).look() shouldBe """
.....#....
....#...O#
.....##...
..O#......
.....OOO#.
.O#...O#.#
....O#...O
.......OOO
#..OO###..
#.OOO#...O
    """.trimIndent()
    }

    test("Given the example platform when spun for 1 + 1 cycles then it looks as expected") {
      platform.deepCopy().spin(1).spin(1).look() shouldBe """
.....#....
....#...O#
.....##...
..O#......
.....OOO#.
.O#...O#.#
....O#...O
.......OOO
#..OO###..
#.OOO#...O
    """.trimIndent()
    }

    test("Given the example platform when spun for 3 cycles then it looks as expected") {
      platform.deepCopy().spin(3).look() shouldBe """
.....#....
....#...O#
.....##...
..O#......
.....OOO#.
.O#...O#.#
....O#...O
.......OOO
#...O###.O
#.OOO#...O
    """.trimIndent()
    }

    test("Given the example platform when spun for 1 + 1 + 1 cycles then it looks as expected") {
      platform.deepCopy().spin(1).spin(1).spin(1).look() shouldBe """
.....#....
....#...O#
.....##...
..O#......
.....OOO#.
.O#...O#.#
....O#...O
.......OOO
#...O###.O
#.OOO#...O
    """.trimIndent()
    }
  }

  test("Given the example platform when spun for 1 000 000 000 cycles then the total load is 64") {
    examplePlatform.toPlatform().spin(1_000_000_000).calcTotalLoad() shouldBe 64
  }

  test("Given the custom platform when spun for 1 000 000 000 cycles then the total load is 100531") {
    readInput("day14p1").toPlatform().spin(1_000_000_000).calcTotalLoad() shouldBe 100531
  }
})
