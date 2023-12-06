package day06p1

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import io.readInput

class Day06p1Tests : FunSpec({
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
        record.calcDistance(buttonHoldTime) shouldBe distance
      }
    }
  }
  context("""Given a list of ints with header as input (in the form of "Time:      7  15   30")then it should be properly parsed into a list of Ints""") {
    table(
      headers("input", "list of Ints"),
      row("Time:      7  15   30", listOf(7, 15, 30)),
      row("Distance:  9  40  200", listOf(9, 40, 200)),
    ).forAll { input, expectedIntList ->
      test("Given $input when we parse it then it should be $expectedIntList") {
        input.toIntList() shouldBe expectedIntList
      }
    }
  }


  val exampleRecordsDocument = """
Time:      7  15   30
Distance:  9  40  200
  """.trimIndent()

  test("Given a records document the Record entries can be properly parsed") {
    val records = exampleRecordsDocument.toRecords()
    records.records shouldBe setOf(
      Record(7, 9),
      Record(15, 40),
      Record(30, 200)
    )
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

  test("Given the example records document the product of the number of ways to beat the record can be calculated") {
    val records = exampleRecordsDocument.toRecords()
    records.productOfNumberOfWaysToBeatRecord() shouldBe 288
  }

  test("Given the custom records document the product of the number of ways to beat the record can be calculated") {
    val records = readInput("day06p1").toRecords()
    records.productOfNumberOfWaysToBeatRecord() shouldBe 741000
  }
})
