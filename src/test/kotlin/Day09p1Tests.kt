package day09p1

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class Day09p1Tests: FunSpec({

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
      row("10 13 16 21 30 45", History(listOf(10L, 13L, 16L, 21L, 30L, 45L)))
    ).forAll { line, history ->
      test(""" Given "$line" then we can parse it as $history""") {
        line.toHistory() shouldBe history
      }
    }
  }
})

fun String.toHistory(): History = History(
  """(\d+)""".toRegex().findAll(this).map { it.value.toLong() }.toList()
)

data class History(val entries: List<Long>)
