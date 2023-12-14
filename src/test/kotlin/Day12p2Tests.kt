package day12p2

import day12p2.HotSpring.*
import day12p2.HotSpring.Companion.toHotSpring
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.readInput
import java.util.*

class Day12p2Tests : FunSpec({
  context("Condition record converting between text input and model") {
    table(
      headers("CR character Input", "Expected"),
      row('.', operational),
      row('#', broken),
      row('?', unknown)
    ).forAll { input, expected ->
      val hotSpring = input.toHotSpring()
      test(""" Given a condition record character of "$input" then the expected parsed value should be "$expected" """) {
        hotSpring shouldBe expected
      }

      test(""" Given a condition record character of "$input" when parsed and then converted back to text then we should get the original input """) {
        hotSpring.crChar shouldBe input
      }
    }

    table(
      headers("CR row 1 Input", "Expected"),
      row(".", listOf(operational)),
      row("#", listOf(broken)),
      row("?", listOf(unknown)),
      row("..", listOf(operational, operational)),
      row("#.#.###", listOf(broken, operational, broken, operational, broken, broken, broken)),
      row("???.###", listOf(unknown, unknown, unknown, operational, broken, broken, broken)),
    ).forAll { input, expected ->
      val hotSprings = input.toHotSpringList()

      test(""" Given a condition record row 1 of "$input" then the expected parsed value should be "$expected" """) {
        hotSprings shouldBe expected
      }

      test(""" Given a condition record row 1 of "$input" when parsed and then converted back to text then we should get the original input """) {
        hotSprings.toConditionRecordRow1Text() shouldBe input
      }
    }

    table(
      headers("Integer list Input", "Expected"),
      row("1,1,3", listOf(1, 1, 3)),
      row("1,3,1,6", listOf(1, 3, 1, 6)),
    ).forAll { input, expected ->
      test(""" Given an integer list of "$input" then the expected parsed value should be "$expected" """) {
        input.toIntList() shouldBe expected
      }
    }

    table(
      headers("Condition Record Row Input", "Expected Row"),
      row(
        "#.#.### 1,1,3", ConditionRecordRow(
          listOf(broken, operational, broken, operational, broken, broken, broken),
          listOf(1, 1, 3)
        )
      ),
      row(
        "???.### 1,1,3", ConditionRecordRow(
          listOf(unknown, unknown, unknown, operational, broken, broken, broken),
          listOf(1, 1, 3)
        )
      )
    ).forAll { input, expectedRow ->
      val row = input.toConditionRecordRow()
      test(""" Given a condition record row of "$input" then the expected parsed value should be "$expectedRow" """) {
        row shouldBe expectedRow
      }

      test(""" Given a condition record row of "$input" when parsed and then the hotsprings converted back to text and parsed again then we should get the same hotsprings """) {
        row.hotSprings.toConditionRecordRow1Text().toHotSpringList() shouldBe row.hotSprings
      }

    }
  }

  context("placing map") {
    table(
      headers("Condition Record Row Input", "Expected Placing Map"),
      row(
        "#.#.### 1,1,3", listOf(1 to listOf(0), 1 to listOf(2), 3 to listOf(4)).map { (k, v) -> k to TreeSet(v) }
      ),
      row(
        "???.### 1,1,3", listOf(1 to listOf(0), 1 to listOf(2), 3 to listOf(4)).map { (k, v) -> k to TreeSet(v) }
      )
    ).forAll { input, expected ->
      val row = input.toConditionRecordRow()
      test(""" Given a condition record row of "$input" then the expected parsed value should have the following placing map "$expected" """) {
        row.placingMap shouldBe expected
      }
    }
  }

  context("Generate folded (part 1) solutions") {
    table(
      headers("CR row Input", "Expected solutions"),
      row("#.#.### 1,1,3", listOf("#.#.###")),
      row("???.### 1,1,3", listOf("#.#.###")),
      row(
        ".??..??...?##. 1,1,3", listOf(
          ".#...#....###.",
          "..#..#....###.",
          "..#...#...###.",
          ".#....#...###.",
        )
      ),
      row(
        "?###???????? 3,2,1", """
          .###.##.#...
          .###.##..#..
          .###.##...#.
          .###.##....#
          .###..##.#..
          .###..##..#.
          .###..##...#
          .###...##.#.
          .###...##..#
          .###....##.#
        """.trimIndent().lines()
      )
    ).forAll { input, solutions ->
      test(""" Given a condition record row of "$input" then the expected solutions should be "$solutions" """) {
        val row = input.toConditionRecordRow()
        row.solutionsAsText().toList() shouldContainExactlyInAnyOrder solutions
      }
    }

    table(
      headers("CR row Input", "Expected number of solutions"),
      row("#.#.### 1,1,3", 1),
      row("???.### 1,1,3", 1),
      row(".??..??...?##. 1,1,3", 4),
      row("?#?#?#?#?#?#?#? 1,3,1,6", 1),
      row("????.#...#... 4,1,1", 1),
      row("????.######..#####. 1,6,5", 4),
      row("?###???????? 3,2,1", 10)
    ).forAll { input, numOfSolutions ->
      test(""" Given a condition record row of "$input" then the expected number of solutions is $numOfSolutions """) {
        val row = input.toConditionRecordRow()
        row.countSolutions() shouldBe numOfSolutions
      }
    }

    table(
      headers("Title", "CR Input", "Expected sum of the number of solutions"),

      row(
        "example input",
        """
???.### 1,1,3
.??..??...?##. 1,1,3
?#?#?#?#?#?#?#? 1,3,1,6
????.#...#... 4,1,1
????.######..#####. 1,6,5
?###???????? 3,2,1
      """.trimIndent(),
        21
      ),

      row(
        "custom input",
        readInput("day12p1"),
        6827
      ),
    ).forAll { title, crinput, sum ->
      test(""" Given a condition record titled $title then the expected total number of solutions is $sum """) {
        crinput.sumOfSolutions() shouldBe sum
      }
    }
  }

  context("Unfold condition record rows") {
    table(
      headers("CR Input", "Expected"),
      row(".# 1", ".#?.#?.#?.#?.# 1,1,1,1,1"),
      row("???.### 1,1,3", "???.###????.###????.###????.###????.### 1,1,3,1,1,3,1,1,3,1,1,3,1,1,3"),
    ).forAll { inputCRRow, expectedUnfoldedCRRow ->
      test(""" Given a condition record row of "$inputCRRow" then the expected unfolded condition record row should be "$expectedUnfoldedCRRow" """) {
        inputCRRow.unfold() shouldBe expectedUnfoldedCRRow
      }
    }
  }

  context("Generate unfolded (part 2) solutions") {
    table(
      headers("CR row Input", "Expected number of solutions"),
      row("#.#.### 1,1,3", 1),
      row("???.### 1,1,3", 1),
      row(".??..??...?##. 1,1,3", 16384),
      row("?#?#?#?#?#?#?#? 1,3,1,6", 1),
      row("????.#...#... 4,1,1", 16),
      row("????.######..#####. 1,6,5", 2500),
      row("?###???????? 3,2,1", 506250)
    ).forAll { input, numOfSolutions ->
      test(""" Given a condition record row of "$input" when unfolded then the expected number of solutions is $numOfSolutions """) {
        val row = input.unfold().toConditionRecordRow()
        row.countSolutions() shouldBe numOfSolutions
      }
    }

    table(
      headers("Title", "CR Input", "Expected sum of the number of solutions"),

      row(
        "example input",
        """
???.### 1,1,3
.??..??...?##. 1,1,3
?#?#?#?#?#?#?#? 1,3,1,6
????.#...#... 4,1,1
????.######..#####. 1,6,5
?###???????? 3,2,1
      """.trimIndent(),
        525152
      ),

      row(
        "custom input",
        readInput("day12p1"),
        1537505634471
      ),
    ).forAll { title, crinput, sum ->
      test(""" Given a condition record titled $title when unfolded then the expected total number of solutions is $sum """) {
        crinput.lineSequence().sortedBy { line -> line.count { it == '?' } }.joinToString("\n") { it.unfold() }
          .sumOfSolutions() shouldBe sum
      }
    }
  }
})
