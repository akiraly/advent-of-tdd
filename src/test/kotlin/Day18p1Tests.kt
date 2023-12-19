package day18p1

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.readInput

class Day18p1Tests : FunSpec({
  val exampleDigPlan = """
    R 6 (#70c710)
    D 5 (#0dc571)
    L 2 (#5713f0)
    D 2 (#d2c081)
    R 2 (#59c680)
    D 2 (#411b91)
    L 5 (#8ceee2)
    U 2 (#caa173)
    L 1 (#1b58a2)
    U 2 (#caa171)
    R 2 (#7807d2)
    U 3 (#a77fa3)
    L 2 (#015232)
    U 2 (#7a21e3)
  """.trimIndent()

  test(""" Given the example dig plan then it can be parsed""") {
    val digPlan = exampleDigPlan.toDigPlan()

    digPlan.steps shouldHaveSize 14

    digPlan.toText() shouldBe exampleDigPlan
  }

  test(""" Given the example dig plan then it should form a loop with the length of 38 and size of 10 x 7""") {
    val digPlan = exampleDigPlan.toDigPlan()

    digPlan.length() shouldBe 38

    digPlan.isLoop() shouldBe true

    val rows = digPlan.rows()
    rows shouldBe DigPlan.Indexes(9, 0)
    rows.total shouldBe 10
    val columns = digPlan.columns()
    columns shouldBe DigPlan.Indexes(6, 0)
    columns.total shouldBe 7
  }

  test(""" Given the example dig plan when we dig it out then it should look as expected """) {
    val digPlan = exampleDigPlan.toDigPlan()
    val digGrid = digPlan.dig()
    digGrid.toText() shouldBe """
#######
#.....#
###...#
..#...#
..#...#
###.###
#...#..
##..###
.#....#
.######
    """.trimIndent()

    digGrid.capacity() shouldBe 38

    digGrid.digInterior()

    digGrid.toText() shouldBe """
#######
#######
#######
..#####
..#####
#######
#####..
#######
.######
.######
    """.trimIndent()

    digGrid.capacity() shouldBe 62
  }

  test(""" Given the example dig plan then the lava capacity is 62 """) {
    exampleDigPlan.lavaCapacity() shouldBe 62
  }

  test(""" Given the custom dig plan then the lava capacity is 49578 """) {
    readInput("day18p1").lavaCapacity() shouldBe 49578
  }
})
