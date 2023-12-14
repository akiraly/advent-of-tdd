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

private fun String.unfold(): String = split(" ").let { (first, second) ->
  generateSequence { first }.take(5).joinToString("?") +
    " " +
    generateSequence { second }.take(5).joinToString(",")
}

fun String.sumOfSolutions(): Long = lineSequence().sumOf { it.toConditionRecordRow().countSolutions() }

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
  val placingMap = calcPlacingMap()
  private val knownBrokenIndexes = hotSprings.asSequence().mapIndexedNotNull { index, hotSpring ->
    if (hotSpring == broken) index else null
  }.toCollection(TreeSet())

  private fun calcPlacingMap(): List<Pair<Int, NavigableSet<Int>>> {
    var minStartIndex = hotSprings.indexOfFirst { it != operational }.let { if (it == -1) 0 else it }
    var maxStartIndex =
      hotSprings.indexOfLast { it != operational }.let { if (it == -1) hotSprings.size else (it + 1) } -
        contGroupOfDamagedSprings.sum() - contGroupOfDamagedSprings.size + 1
    val result = mutableListOf<Pair<Int, TreeSet<Int>>>()

    contGroupOfDamagedSprings.forEach { groupSize ->
      result.add(groupSize to (minStartIndex..maxStartIndex).asSequence().mapNotNull { i ->
        if (
          (0..<groupSize).all { hotSprings[i + it] != operational }
          && (hotSprings.size == i + groupSize || hotSprings[i + groupSize] != broken)
          && if (i > 0) hotSprings[i - 1] != broken else true
        ) {
          i
        } else null
      }.toCollection(TreeSet()))
      minStartIndex += groupSize + 1
      maxStartIndex += groupSize + 1
    }

    return result
  }

  private fun solutions(): Sequence<List<HotSpring>> {
    val solutions = mutableListOf<List<HotSpring>>()
    placeGroup {
      val solution = it.toList()
      require(solution.size == hotSprings.size)
      require(solution.zip(hotSprings).all { (a, b) -> a == b || b == unknown }) { "$solution vs $hotSprings" }
      solutions.add(solution)
    }
    return solutions.asSequence()
  }

  fun countSolutions(): Long {
    val cache = mutableMapOf<Pair<Int, Int>, Long>()
    var count = 0L
    placeGroup(
      recursionFn = r@{ context ->
        val key = context.index to context.groupSizes.size
        cache[key]?.let {
          count += it
          return@r
        }
        val currentCount = count
        placeGroup(context)
        cache[key] = count - currentCount
      }
    ) {
      count++
    }
    return count
  }

  fun solutionsAsText(): Sequence<String> = solutions().map { it.toConditionRecordRow1Text() }

  private fun placeGroup(
    solution: Sequence<HotSpring> = emptySequence(),
    hotSprings: List<HotSpring> = this.hotSprings,
    index: Int = 0,
    groupSizes: List<Int> = contGroupOfDamagedSprings,
    allPlacings: List<Pair<Int, NavigableSet<Int>>> = placingMap,
    recursionFn: (PlaceGroupContext) -> Unit = this::placeGroup,
    solutionConsumer: (Sequence<HotSpring>) -> Unit
  ) = placeGroup(
    PlaceGroupContext(
      solution = solution,
      hotSprings = hotSprings,
      index = index,
      groupSizes = groupSizes,
      allPlacings = allPlacings,
      recursionFn = recursionFn,
      solutionConsumer = solutionConsumer
    )
  )

  private data class PlaceGroupContext(
    val solution: Sequence<HotSpring>,
    val hotSprings: List<HotSpring>,
    val index: Int,
    val groupSizes: List<Int>,
    val allPlacings: List<Pair<Int, NavigableSet<Int>>>,
    val recursionFn: (PlaceGroupContext) -> Unit,
    val solutionConsumer: (Sequence<HotSpring>) -> Unit
  )

  private fun placeGroup(context: PlaceGroupContext) {
    with(context) {
      require(index <= hotSprings.size)
      if (groupSizes.isEmpty()) {
        if (knownBrokenIndexes.ceiling(index) == null) {
          solutionConsumer(solution + (index..<hotSprings.size).asSequence().map { operational })
        }
        return
      }

      val (groupSize, groupPlacings) = allPlacings.first()
      val maxStart = knownBrokenIndexes.ceiling(index) ?: (hotSprings.size - 1)
      if (maxStart < index) return
      val placings = groupPlacings.subSet(index, true, maxStart, true)

      placings.forEach { i ->

        val endsWithOperational = i + groupSize < hotSprings.size

        recursionFn(context.copy(
          solution = solution +
            (0..<i - index).asSequence()
              .map { hotSprings[index + it].let { hs -> if (hs == broken) broken else operational } } +
            (0..<groupSize).asSequence().map { broken } +
            if (endsWithOperational) sequenceOf(operational) else emptySequence(),
          index = i + groupSize + if (endsWithOperational) 1 else 0,
          groupSizes = groupSizes.subList(1, groupSizes.size),
          allPlacings = allPlacings.subList(1, allPlacings.size),
        ))
      }
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
