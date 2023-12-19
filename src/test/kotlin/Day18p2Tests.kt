package day18p2

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.readInput

class Day18p2Tests : FunSpec({
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

  val customDigPlan = readInput("day18p1")

  test(""" (Original) Given the example dig plan then it can be parsed""") {
    val digPlan = exampleDigPlan.toDigPlanOriginal()

    digPlan.steps shouldHaveSize 14

    digPlan.toText() shouldBe """
R 6
D 5
L 2
D 2
R 2
D 2
L 5
U 2
L 1
U 2
R 2
U 3
L 2
U 2
    """.trimIndent()
  }

  test(""" Given the example dig plan then it can be parsed""") {
    val digPlan = exampleDigPlan.toDigPlanUpdated()

    digPlan.steps shouldHaveSize 14

    digPlan.toText() shouldBe """
R 461937
D 56407
R 356671
D 863240
R 367720
D 266681
L 577262
U 829975
L 112010
D 829975
L 491645
U 686074
L 5411
U 500254
    """.trimIndent()
  }

  test("""(Original) Given the example dig plan then it should form a loop with the length of 38 and size 10 x 7""") {
    val digPlan = exampleDigPlan.toDigPlanOriginal()

    digPlan.length shouldBe 38

    digPlan.isLoop() shouldBe true

    val rows = digPlan.rows()
    rows shouldBe DigPlan.Indexes(9, 0)
    rows.total shouldBe 10
    val columns = digPlan.columns()
    columns shouldBe DigPlan.Indexes(6, 0)
    columns.total shouldBe 7
  }

  test(""" Given the example dig plan then it should form a loop with the length of 6405262 and size 1186329 x 1186329""") {
    val digPlan = exampleDigPlan.toDigPlanUpdated()

    digPlan.length shouldBe 6405262

    digPlan.isLoop() shouldBe true

    val rows = digPlan.rows()
    rows shouldBe DigPlan.Indexes(1186328, 0)
    rows.total shouldBe 1186329
    val columns = digPlan.columns()
    columns shouldBe DigPlan.Indexes(1186328, 0)
    columns.total shouldBe 1186329
  }

  test("""(Original) Given the custom dig plan then it should form a loop with the length of 3466 and size 264 x 387""") {
    val digPlan = customDigPlan.toDigPlanOriginal()

    digPlan.length shouldBe 3466

    digPlan.isLoop() shouldBe true

    val rows = digPlan.rows()
    rows shouldBe DigPlan.Indexes(71, -192)
    rows.total shouldBe 264
    val columns = digPlan.columns()
    columns shouldBe DigPlan.Indexes(329, -57)
    columns.total shouldBe 387
  }

  test(""" Given the custom dig plan then it should form a loop with the length of 176096896 and size 12454594 x 20066754""") {
    val digPlan = customDigPlan.toDigPlanUpdated()

    digPlan.length shouldBe 176096896

    digPlan.isLoop() shouldBe true

    val rows = digPlan.rows()
    rows shouldBe DigPlan.Indexes(2775118, -9679475)
    rows.total shouldBe 12454594
    val columns = digPlan.columns()
    columns shouldBe DigPlan.Indexes(9490626, -10576127)
    columns.total shouldBe 20066754
  }

  test("""(Original) Given the example dig plan when we dig it out then it should look as expected """) {
    val digPlan = exampleDigPlan.toDigPlanOriginal()
    val digGrid = digPlan.dig()

    digGrid.toString() shouldBe """
0, (0, 2)
6, (0, 2)
2, (2, 5)
6, (2, 5)
0, (5, 7)
4, (5, 7)
1, (7, 9)
6, (7, 9)
0, (0, 6)
2, (0, 2)
5, (0, 2)
5, (4, 6)
7, (0, 1)
7, (4, 6)
9, (1, 6)
    """.trimIndent()

    digGrid.length shouldBe digPlan.length

    digGrid.capacity() shouldBe 62
  }

  test(""" Given the example dig plan when we dig it out then it should look as expected """) {
    val digPlan = exampleDigPlan.toDigPlanUpdated()
    val digGrid = digPlan.dig()

    digGrid.toString() shouldBe """
0, (0, 56407)
461937, (0, 56407)
0, (56407, 356353)
818608, (56407, 356353)
0, (356353, 500254)
497056, (356353, 500254)
609066, (356353, 500254)
818608, (356353, 500254)
5411, (500254, 919647)
497056, (500254, 919647)
609066, (500254, 919647)
818608, (500254, 919647)
5411, (919647, 1186328)
497056, (919647, 1186328)
609066, (919647, 1186328)
1186328, (919647, 1186328)
0, (0, 461937)
56407, (461937, 818608)
356353, (497056, 609066)
500254, (0, 5411)
919647, (818608, 1186328)
1186328, (5411, 497056)
1186328, (609066, 1186328)
    """.trimIndent()

    digGrid.length shouldBe digPlan.length

    digGrid.capacity() shouldBe 952408144115
  }

  test(""" (Original) Given the example dig plan then the lava capacity is 62 """) {
    exampleDigPlan.lavaCapacityOriginal() shouldBe 62
  }

  test(""" Given the example dig plan then the lava capacity is 952408144115 """) {
    exampleDigPlan.lavaCapacityUpdated() shouldBe 952408144115
  }

  test(""" (Original) Given the custom dig plan (with original parsing) then the lava capacity is 49578 """) {
    customDigPlan.lavaCapacityOriginal() shouldBe 49578
  }

  test(""" Given the custom dig plan (with original parsing) then the lava capacity is 52885384955882 """) {
    customDigPlan.lavaCapacityUpdated() shouldBe 52885384955882
  }
})
