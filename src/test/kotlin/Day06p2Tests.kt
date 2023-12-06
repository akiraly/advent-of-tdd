package day06p2

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import io.readInput

class Day06p2Tests : FunSpec({
  test("A Record can be properly represented") {
    val record = Record(raceDuration = 7, recordDistance = 9)
    record.raceDuration shouldBe 7
    record.recordDistance shouldBe 9
  }

  context("Given a Record(raceDuration = 7, recordDistance = 9), the distance can be calculated based on the duration the button was held") {
    val record = Record(raceDuration = 7, recordDistance = 9)
    table(
      headers("button hold time", "distance"),
      row(0, 0),
      row(1, 6),
      row(2, 10),
      row(3, 12),
      row(4, 12),
      row(5, 10),
      row(6, 6),
      row(7, 0)
    ).forAll { buttonHoldTime, distance ->
      test("Given a button hold time of $buttonHoldTime, the distance travelled should be $distance") {
        record.calcDistance(buttonHoldTime.toLong()) shouldBe distance.toLong()
      }
    }
  }
  context("""Given a number with any number of spaced between the digits with a header as input (in the form of "Time:      7  15   30") then it should be properly parsed into a number""") {
    table(
      headers("input", "number"),
      row("Time:      7  15   30", 71530),
      row("Distance:  9  40  200", 940200),
    ).forAll { input, expectedNumber ->
      test("Given $input when we parse it then it should be $expectedNumber") {
        input.toSingleNumber() shouldBe expectedNumber
      }
    }
  }


  val exampleRecordDocument = """
Time:      7  15   30
Distance:  9  40  200
  """.trimIndent()

  test("Given a record document the Record can be properly parsed") {
    val record = exampleRecordDocument.toRecord()
    record shouldBe Record(71530, 940200)
  }

  context("Given a Record the number of ways to beat the record can be calculated") {
    table(
      headers("record", "number of ways to beat record"),
      row(Record(7, 9), 4),
      row(Record(15, 40), 8),
      row(Record(30, 200), 9)
    ).forAll { record, expectedNumberOfWays ->
      test("Given $record the number of ways to beat the record should be $expectedNumberOfWays") {
        record.calcNumberOfWaysToBeatRecord() shouldBe expectedNumberOfWays
      }
    }
  }

  test("Given the example record document then the number of ways to beat the record can be calculated") {
    val record = exampleRecordDocument.toRecord()
    record.calcNumberOfWaysToBeatRecord() shouldBe 71503
  }

  test("Given the custom record document then the number of ways to beat the record can be calculated") {
    val record = readInput("day06p1").toRecord()
    record.calcNumberOfWaysToBeatRecord() shouldBe 38220708
  }
})
