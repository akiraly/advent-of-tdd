package day09p1

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.readInput

class Day09p1Tests : FunSpec({

  val exampleOASISReport = """
    0 3 6 9 12 15
    1 3 6 10 15 21
    10 13 16 21 30 45
  """.trimIndent()

  context("parsing OASIS reports") {
    table(
      headers("Input", "History"),
      row("0 3 6 9 12 15", History(listOf(0L, 3L, 6L, 9L, 12L, 15L))),
      row("1 3 6 10 15 21", History(listOf(1L, 3L, 6L, 10L, 15L, 21L))),
      row("10 13 16 21 30 45", History(listOf(10L, 13L, 16L, 21L, 30L, 45L))),
      row(
        "10 15 15 10 0 -15 -35 -60 -90 -125 -165 -210 -260 -315 -375 -440 -510 -585 -665 -750 -840",
        History(
          listOf(
            10,
            15,
            15,
            10,
            0,
            -15,
            -35,
            -60,
            -90,
            -125,
            -165,
            -210,
            -260,
            -315,
            -375,
            -440,
            -510,
            -585,
            -665,
            -750,
            -840
          )
        )
      )
    ).forAll { line, history ->
      test(""" Given "$line" then we can parse it as $history""") {
        line.toHistory() shouldBe history
      }
    }

    test(""" Given "$exampleOASISReport" then we can parse it as OASIS Report""") {
      val report = exampleOASISReport.toOASISReport()
      report.entries shouldHaveSize 3
      report shouldBe OASISReport(
        listOf(
          History(listOf(0L, 3L, 6L, 9L, 12L, 15L)),
          History(listOf(1L, 3L, 6L, 10L, 15L, 21L)),
          History(listOf(10L, 13L, 16L, 21L, 30L, 45L))
        )
      )
    }
  }

  context("Calculating diff series") {
    table(
      headers("Series"),
      row(
        listOf(
          listOf(0L, 3L, 6L, 9L, 12L, 15L),
          listOf(3L, 3L, 3L, 3L, 3L),
          listOf(0L, 0L, 0L, 0L)
        )
      ),
      row(
        listOf(
          listOf(1L, 3L, 6L, 10L, 15L, 21L),
          listOf(2L, 3L, 4L, 5L, 6L),
          listOf(1L, 1L, 1L, 1L),
          listOf(0L, 0L, 0L)
        )
      ),
      row(
        listOf(
          listOf(10L, 13L, 16L, 21L, 30L, 45L),
          listOf(3L, 3L, 5L, 9L, 15L),
          listOf(0L, 2L, 4L, 6L),
          listOf(2L, 2L, 2L),
          listOf(0L, 0L)
        )
      )
    ).forAll { series ->
      series.asSequence().windowed(2).forEach { (a, b) ->
        test(""" Given "$a" then the diff series should be "$b" """) {
          a.diffSeries() shouldBe b
        }
      }
    }
  }

  context("Figuring out if it a series is all 0-s") {
    table(
      headers("Series", "Is all 0"),
      row(listOf(0L, 3L, 6L, 9L, 12L, 15L), false),
      row(listOf(3L, 3L, 3L, 3L, 3L), false),
      row(listOf(0L, 0L, 0L, 0L), true),

      row(listOf(1L, 3L, 6L, 10L, 15L, 21L), false),
      row(listOf(2L, 3L, 4L, 5L, 6L), false),
      row(listOf(1L, 1L, 1L, 1L), false),
      row(listOf(0L, 0L, 0L), true),

      row(listOf(10L, 13L, 16L, 21L, 30L, 45L), false),
      row(listOf(3L, 3L, 5L, 9L, 15L), false),
      row(listOf(0L, 2L, 4L, 6L), false),
      row(listOf(2L, 2L, 2L), false),
      row(listOf(0L, 0L), true)
    ).forAll { series, isAllZero ->
      test(""" Given "$series" then it should be ${if (isAllZero) "all 0" else "not all 0"} """) {
        series.isAllZero() shouldBe isAllZero
      }
    }
  }

  context("Predicting next value") {
    table(
      headers("Series", "Next value"),
      row(listOf(0L, 3L, 6L, 9L, 12L, 15L), 18L),
      row(listOf(1L, 3L, 6L, 10L, 15L, 21L), 28L),
      row(listOf(10L, 13L, 16L, 21L, 30L, 45L), 68L)
    ).forAll { series, prediction ->
      test(""" Given "$series" then the next value should be "$prediction" """) {
        series.predictNextValue() shouldBe prediction
      }
    }
  }

  test("Given $exampleOASISReport then the sum of all predicted next values should be 114") {
    exampleOASISReport.toOASISReport().sumOfPredictedNextValues() shouldBe 114
  }

  test("Given our custom OASIS report then the sum of all predicted next values should be 1834108701") {
    readInput("day09p1").toOASISReport().sumOfPredictedNextValues() shouldBe 1834108701
  }
})
