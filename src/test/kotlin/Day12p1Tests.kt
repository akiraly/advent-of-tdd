package day12p1

import day12p1.HotSpring.*
import day12p1.HotSpring.Companion.toHotSpring
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.apache.commons.math3.util.ArithmeticUtils

class Day12p1Tests : FunSpec({
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
      headers("Condition Record Row Input", "Number of unknowns", "Expected"),
      row(
        "#.#.### 1,1,3", 0, ConditionRecordRow(
          listOf(broken, operational, broken, operational, broken, broken, broken),
          listOf(1, 1, 3)
        )
      ),
      row(
        "???.### 1,1,3", 3, ConditionRecordRow(
          listOf(unknown, unknown, unknown, operational, broken, broken, broken),
          listOf(1, 1, 3)
        )
      )
    ).forAll { input, numOfUnknowns, expected ->
      val row = input.toConditionRecordRow()
      test(""" Given a condition record row of "$input" then the expected parsed value should be "$expected" """) {
        row shouldBe expected
        row.numberOfUnknowns shouldBe numOfUnknowns
        expected.numberOfUnknowns shouldBe numOfUnknowns
      }

      test(""" Given a condition record row of "$input" when parsed and then the hotsprings converted back to text and parsed again then we should get the same hotsprings """) {
        row.hotSprings.toConditionRecordRow1Text().toHotSpringList() shouldBe row.hotSprings
      }

    }
  }

  val exampleInput1 = """
    #.#.### 1,1,3
    .#...#....###. 1,1,3
    .#.###.#.###### 1,3,1,6
    ####.#...#... 4,1,1
    #....######..#####. 1,6,5
    .###.##....# 3,2,1
  """.trimIndent()

  context("Contiguous group of damaged springs") {
    exampleInput1.lineSequence().forEach { input ->
      test(""" Given a condition record row of "$input" then the calculated contiguous group of damaged springs (from the hot springs) should be the same as the list provided""") {
        val row = input.toConditionRecordRow()
        row.hotSprings.calcContiguousGroupOfDamagedSprings() shouldBe row.contGroupOfDamagedSprings
      }
    }
  }

  context("Generate variants for number of unknowns") {
    table(
      headers("Number of unknowns", "Expected variants"),
      row(0, emptyList()),
      row(
        1,
        listOf(
          listOf(operational),
          listOf(broken)
        )
      ),
      row(
        2,
        listOf(
          listOf(operational, operational),
          listOf(operational, broken),
          listOf(broken, operational),
          listOf(broken, broken)
        )
      ),
    ).forAll { n, expectedVariants ->
      test(""" Given $n unknown condition(s) then the variants should be as expected """) {
        val variants = generateVariantsForUnknowns(n).toList()
        variants.forEach { it shouldHaveSize n }
        variants shouldContainExactlyInAnyOrder expectedVariants
      }
    }

    (1..16).forEach { n ->
      val expectedCount = ArithmeticUtils.pow(2, n)
      test(""" Given $n unknown condition(s) then the number of variants should be $expectedCount and the variants should each have the size of $n """) {
        val variants = generateVariantsForUnknowns(n).toList()
        variants.size shouldBe expectedCount
        variants.forEach { it shouldHaveSize n }

        variants.distinct() shouldHaveSize expectedCount
      }
    }
  }

  context("Generate solution candidates") {
    table(
      headers("CR row Input", "Expected solution candidates"),
      row("#.#.### 1,1,3", emptyList()),
      row(
        "#.?.### 1,1,3", """
        #...###
        #.#.###
      """.trimIndent().lines()
      ),
      row(
        "???.### 1,1,3", """
        ....###
        ..#.###
        .#..###
        .##.###
        #...###
        #.#.###
        ##..###
        ###.###
      """.trimIndent().lines()
      ),
    ).forAll { input, candidates ->
      test(""" Given a condition record row of "$input" then the expected solution candidates should be "$candidates" """) {
        val row = input.toConditionRecordRow()
        row.solutionCandidatesAsText().toList() shouldContainExactlyInAnyOrder candidates
      }
    }
  }

  context("Generate solutions") {
    table(
      headers("CR row Input", "Expected solutions"),
      row("#.#.### 1,1,3", emptyList()),
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
      row("#.#.### 1,1,3", 0),
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

//      row(
//        "custom input",
//        readInput("day12p1"),
//        6827
//      ),
    ).forAll { title, crinput, sum ->
      test(""" Given a condition record titled $title then the expected total number of solutions is $sum """) {
        crinput.sumOfSolutions2() shouldBe sum
      }
    }
  }
})

private fun String.sumOfSolutions2(): Long = lineSequence().sumOf { it.toConditionRecordRow().countSolutions2() }

fun String.sumOfSolutions(): Int = lineSequence().sumOf { it.toConditionRecordRow().countSolutions() }

fun generateVariantsForUnknowns(n: Int): Sequence<List<HotSpring>> {
  if (n == 0) return emptySequence()
  var counter = ArithmeticUtils.pow(2, n)
  return generateSequence {
    if (counter == 0) return@generateSequence null
    counter--
    counter.toString(2).padStart(n, '0').map {
      when (it) {
        '0' -> broken
        else -> {
          require(it == '1')
          operational
        }
      }
    }
  }
}

fun List<HotSpring>.calcContiguousGroupOfDamagedSprings(): List<Int> {
  val contGroupOfDamagedSprings = mutableListOf<Int>()
  var currentSize = 0

  forEach {
    when (it) {
      broken -> currentSize++
      else -> {
        require(it == operational)
        if (currentSize > 0) {
          contGroupOfDamagedSprings.add(currentSize)
          currentSize = 0
        }
      }
    }
  }

  if (currentSize > 0) contGroupOfDamagedSprings.add(currentSize)

  return contGroupOfDamagedSprings
}

fun String.toConditionRecordRow(): ConditionRecordRow {
  val (crRow1, contGroupOfDamagedSprings) = split(" ")
  return ConditionRecordRow(
    crRow1.toHotSpringList(),
    contGroupOfDamagedSprings.toIntList()
  )
}

data class ConditionRecordRow(
  val hotSprings: List<HotSpring>,
  val contGroupOfDamagedSprings: List<Int>
) {
  val numberOfUnknowns = hotSprings.count { it == unknown }

  fun solutionCandidates(): Sequence<List<HotSpring>> {
    if (numberOfUnknowns == 0) return emptySequence()

    return generateVariantsForUnknowns(numberOfUnknowns).map { it.toMutableList() }.map { unknownMappings ->
      hotSprings.map {
        when (it) {
          unknown -> unknownMappings.removeFirst()!!
          else -> it
        }
      }
    }
  }

  fun solutionCandidatesAsText(): Sequence<String> = solutionCandidates().map { it.toConditionRecordRow1Text() }

  fun solutions(): Sequence<List<HotSpring>> = solutionCandidates()
    .filter { it.calcContiguousGroupOfDamagedSprings() == contGroupOfDamagedSprings }

  fun countSolutions(): Int = solutions().count()

  fun solutionsAsText(): Sequence<String> = solutions().map { it.toConditionRecordRow1Text() }

  fun countSolutions2(): Long {
    val hotSprings = this@ConditionRecordRow.hotSprings.toMutableList()
    val startState = StartState()
    var currentState: State = startState
    val states = mutableListOf(currentState)

    while (hotSprings.isNotEmpty()) {
      val newState = currentState.accept(hotSprings.removeFirst())
      if (newState != currentState) {
        currentState = newState
        states.add(currentState)
      }
    }

    return startState.count
  }

  interface State {
    fun accept(current: HotSpring): State
  }

  inner class StartState : State {
    var count: Long = 0

    override fun accept(current: HotSpring): State {
      TODO("Not yet implemented")
    }
  }

}

private val intRegex = """(\d+)""".toRegex()
fun String.toIntList(): List<Int> = intRegex.findAll(this).map { it.value.toInt() }.toList()

fun List<HotSpring>.toConditionRecordRow1Text(): String = joinToString("") { it.crChar.toString() }

fun String.toHotSpringList(): List<HotSpring> = map { it.toHotSpring() }

enum class HotSpring(val crChar: Char) {
  operational('.'),
  broken('#'),
  unknown('?');

  companion object {
    private val hotSpringsByCrChar = entries.associateBy { it.crChar }

    fun Char.toHotSpring() = hotSpringsByCrChar.getValue(this)
  }
}
