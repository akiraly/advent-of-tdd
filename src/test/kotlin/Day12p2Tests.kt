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
      headers("Condition Record Row Input", "Number of unknowns", "Expected Row"),
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
    ).forAll { input, numOfUnknowns, expectedRow ->
      val row = input.toConditionRecordRow()
      test(""" Given a condition record row of "$input" then the expected parsed value should be "$expectedRow" """) {
        row shouldBe expectedRow
        row.numberOfUnknowns shouldBe numOfUnknowns
        expectedRow.numberOfUnknowns shouldBe numOfUnknowns
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
        "#.#.### 1,1,3", listOf(1 to listOf(0, 2), 3 to listOf(4))
      ),
      row(
        "???.### 1,1,3", listOf(1 to listOf(0, 1, 2), 1 to listOf(1, 2),  3 to listOf(4))
      )
    ).forAll { input, expected ->
      val row = input.toConditionRecordRow()
      test(""" Given a condition record row of "$input" then the expected parsed value should have the following placing map "$expected" """) {
        row.placingMap2 shouldBe expected
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
        crinput.lineSequence().forEach { line ->
          val row = line.toConditionRecordRow()
          val old = row.solutionsAsTextOld().sorted().toList()
          val new = row.solutionsAsText().sorted().toList()
          if (old != new) {
            println(line)
            new shouldBe old
          }
        }
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
        6827
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

fun String.sumOfSolutions(): Long = lineSequence().sumOf {
  println(it)
//  stats.clear()
//  globalCount = 0
  val count = it.toConditionRecordRow().countSolutions()
  println(count)
  count
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
  val numberOfKnownBrokens = hotSprings.count { it == broken }
  val totalNumberOfBrokens = contGroupOfDamagedSprings.sum()
  val placingMap = calcPlacingMap()
  val placingMap2 = calcPlacingMap2()
  val knownBrokenIndexes = hotSprings.asSequence().mapIndexedNotNull { index, hotSpring ->
    if (hotSpring == broken) index else null
  }.toCollection(TreeSet())

//  init {
//    val groupCount = hotSprings.asSequence().filter { it != unknown }
//      .windowed(2, partialWindows = true)
//      .sumOf { if (it.size == 1 && it[0] == broken) 1L else if (it[0] == broken && it[1] == operational) 1 else 0 }
//    println(groupCount)
//  }

  fun calcPlacingMap(): NavigableMap<Int, out NavigableSet<Int>> {
    return TreeMap(contGroupOfDamagedSprings.asSequence().distinct().associateWith { groupSize ->
      (0..hotSprings.size - groupSize).asSequence().mapNotNull { i ->
        if (
          (0..<groupSize).all { hotSprings[i + it] != operational }
          && (hotSprings.size == i + groupSize || hotSprings[i + groupSize] != broken)
          && if (i > 0) hotSprings[i - 1] != broken else true
        ) {
          i
        } else null
      }.toCollection(TreeSet())
    })
  }

  fun calcPlacingMap2(): List<Pair<Int, NavigableSet<Int>>> {
    var minStartIndex = 0
    var maxStartIndex = hotSprings.size - contGroupOfDamagedSprings.sum() - contGroupOfDamagedSprings.size + 1
    val result = mutableListOf<Pair<Int, TreeSet<Int>>>()

    contGroupOfDamagedSprings.forEach { groupSize ->
      result.add( groupSize to (minStartIndex..maxStartIndex).asSequence().mapNotNull { i ->
        if (
          (0..<groupSize).all { hotSprings[i + it] != operational }
          && (hotSprings.size == i + groupSize || hotSprings[i + groupSize] != broken)
          && if (i > 0) hotSprings[i - 1] != broken else true
        ) {
          i
        } else null
      }.toCollection(TreeSet()))
      if (minStartIndex > 0) {
        minStartIndex++
      }
      minStartIndex += groupSize
      maxStartIndex += groupSize
      if (maxStartIndex < hotSprings.size) {
        maxStartIndex++
      }
    }

    return result
  }

  private fun solutionsOld(): Sequence<List<HotSpring>> {
    println(placingMap)
    val solutions = mutableListOf<List<HotSpring>>()
    processNext(
      hotSpringsToProcess = hotSprings,
      brokenGroupsToProcess = contGroupOfDamagedSprings,
      remainingNumberOfUnknowns = numberOfUnknowns,
      remainingNumberOfKnownBrokens = numberOfKnownBrokens,
      remainingTotalNumberOfBrokens = totalNumberOfBrokens,
      numberOfGroupsNeeded = contGroupOfDamagedSprings.size
    ) {
      solutions.add(it.toList())
    }
    return solutions.asSequence()
  }

  private fun solutions(): Sequence<List<HotSpring>> {
    println(placingMap2)
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
    println(placingMap2)
    var count = 0L
    placeGroup {
      count++
      if (count % 5_000_000 == 0L) println(count)
    }
    return count
  }

  fun solutionsAsTextOld(): Sequence<String> = solutionsOld().map { it.toConditionRecordRow1Text() }
  fun solutionsAsText(): Sequence<String> = solutions().map { it.toConditionRecordRow1Text() }

  private fun placeGroup(
    solution: Sequence<HotSpring> = emptySequence(),
    hotSprings: List<HotSpring> = this.hotSprings,
    index: Int = 0,
    groupSizes: List<Int> = contGroupOfDamagedSprings,
    allPlacings: List<Pair<Int, NavigableSet<Int>>> = placingMap2,
    solutionConsumer: (Sequence<HotSpring>) -> Unit
  ) {
    require(index <= hotSprings.size)
    if (groupSizes.isEmpty()) {
      if (knownBrokenIndexes.ceiling(index) == null) {
        solutionConsumer(solution + (index..<hotSprings.size).asSequence().map { operational })
      }
      return
    }

    val (groupSize, groupPlacings) = allPlacings.first()
    //val groupSize = groupSizes.first()
    val maxStart = knownBrokenIndexes.ceiling(index) ?: (hotSprings.size - 1)
    if (maxStart < index) return
    val placings = groupPlacings.subSet(index, true, maxStart, true)

    placings.forEach { i ->

      //if ((index..<i).any { hotSprings[it] == broken }) return@forEach

      val endsWithOperational = i + groupSize < hotSprings.size

      placeGroup(
        solution +
          (0..<i - index).asSequence()
            .map { hotSprings[index + it].let { hs -> if (hs == broken) broken else operational } } +
          (0..<groupSize).asSequence().map { broken } +
          if (endsWithOperational) sequenceOf(operational) else emptySequence(),
        hotSprings,
        i + groupSize + if (endsWithOperational) 1 else 0,
        groupSizes.subList(1, groupSizes.size),
        allPlacings.subList(1, allPlacings.size),
        solutionConsumer
      )
    }
  }
}

private fun processNext(
  result: Sequence<HotSpring> = emptySequence(),
  hotSpringsToProcess: List<HotSpring>,
  remainingBrokensInCurrentGroup: Int? = null,
  brokenGroupsToProcess: List<Int>,
  remainingNumberOfUnknowns: Int,
  remainingNumberOfKnownBrokens: Int,
  remainingTotalNumberOfBrokens: Int,
  numberOfGroupsNeeded: Int,
  numberOfGroupsCreated: Int = 0,
  solutionConsumer: (Sequence<HotSpring>) -> Unit
) {
  if (hotSpringsToProcess.isEmpty()) {
    processEnd(
      result,
      hotSpringsToProcess,
      remainingBrokensInCurrentGroup,
      brokenGroupsToProcess,
      remainingNumberOfUnknowns,
      remainingNumberOfKnownBrokens,
      remainingTotalNumberOfBrokens,
      numberOfGroupsNeeded,
      numberOfGroupsCreated,
      solutionConsumer
    )
    return
  }

//  if (numberOfGroupsNeeded - numberOfGroupsCreated - 1 > hotSpringsToProcess.size - remainingTotalNumberOfBrokens) {
//    return
//  }

  val next = hotSpringsToProcess.first()
  val remaining = hotSpringsToProcess.subList(1, hotSpringsToProcess.size)
  when (next) {
    operational -> processOperational(
      result,
      remaining,
      remainingBrokensInCurrentGroup,
      brokenGroupsToProcess,
      remainingNumberOfUnknowns,
      remainingNumberOfKnownBrokens,
      remainingTotalNumberOfBrokens,
      numberOfGroupsNeeded,
      numberOfGroupsCreated,
      solutionConsumer
    )

    broken -> processKnownBroken(
      result,
      remaining,
      remainingBrokensInCurrentGroup,
      brokenGroupsToProcess,
      remainingNumberOfUnknowns,
      remainingNumberOfKnownBrokens,
      remainingTotalNumberOfBrokens,
      numberOfGroupsNeeded,
      numberOfGroupsCreated,
      solutionConsumer
    )

    unknown -> processUnknown(
      result,
      remaining,
      remainingBrokensInCurrentGroup,
      brokenGroupsToProcess,
      remainingNumberOfUnknowns,
      remainingNumberOfKnownBrokens,
      remainingTotalNumberOfBrokens,
      numberOfGroupsNeeded,
      numberOfGroupsCreated,
      solutionConsumer
    )
  }
}

private fun processOperational(
  result: Sequence<HotSpring>,
  hotSpringsToProcess: List<HotSpring>,
  remainingBrokensInGroup: Int?,
  brokenGroupsToProcess: List<Int>,
  remainingNumberOfUnknowns: Int,
  remainingNumberOfKnownBrokens: Int,
  remainingTotalNumberOfBrokens: Int,
  numberOfGroupsNeeded: Int,
  numberOfGroupsCreated: Int,
  solutionConsumer: (Sequence<HotSpring>) -> Unit
) {
  if (remainingBrokensInGroup.isCurrentGroupNotEmpty()) {
    markStats(hotSpringsToProcess.size)
    return
  }
//  if (numberOfGroupsNeeded - numberOfGroupsCreated - 1 > hotSpringsToProcess.size - remainingTotalNumberOfBrokens) {
//    return
//  }

  addCurrentAndProcessNext(
    operational,
    result,
    hotSpringsToProcess,
    null,
    brokenGroupsToProcess,
    remainingNumberOfUnknowns,
    remainingNumberOfKnownBrokens,
    remainingTotalNumberOfBrokens,
    numberOfGroupsNeeded,
    numberOfGroupsCreated,
    solutionConsumer
  )
}

fun processKnownBroken(
  result: Sequence<HotSpring>,
  hotSpringsToProcess: List<HotSpring>,
  remainingBrokensInGroup: Int?,
  brokenGroupsToProcess: List<Int>,
  remainingNumberOfUnknowns: Int,
  remainingNumberOfKnownBrokens: Int,
  remainingTotalNumberOfBrokens: Int,
  numberOfGroupsNeeded: Int,
  numberOfGroupsCreated: Int,
  solutionConsumer: (Sequence<HotSpring>) -> Unit
) = processBroken(
  result,
  hotSpringsToProcess,
  remainingBrokensInGroup,
  brokenGroupsToProcess,
  remainingNumberOfUnknowns,
  remainingNumberOfKnownBrokens - 1,
  remainingTotalNumberOfBrokens - 1,
  numberOfGroupsNeeded,
  numberOfGroupsCreated,
  solutionConsumer
)

fun processBroken(
  result: Sequence<HotSpring>,
  hotSpringsToProcess: List<HotSpring>,
  remainingBrokensInGroup: Int?,
  brokenGroupsToProcess: List<Int>,
  remainingNumberOfUnknowns: Int,
  remainingNumberOfKnownBrokens: Int,
  remainingTotalNumberOfBrokens: Int,
  numberOfGroupsNeeded: Int,
  numberOfGroupsCreated: Int,
  solutionConsumer: (Sequence<HotSpring>) -> Unit
) {
  val nextRemainingBrokensInGroup: Int
  val nextBrokenGroupsToProcess: List<Int>
  val nextNumberOfGroupsCreated: Int
  if (remainingBrokensInGroup != null) {
    if (remainingBrokensInGroup == 0) {
      markStats(hotSpringsToProcess.size)
      return
    }
    nextRemainingBrokensInGroup = remainingBrokensInGroup - 1
    nextBrokenGroupsToProcess = brokenGroupsToProcess
    nextNumberOfGroupsCreated = numberOfGroupsCreated
  } else if (brokenGroupsToProcess.isEmpty()) {
    markStats(hotSpringsToProcess.size)
    return
  } else {
    nextRemainingBrokensInGroup = brokenGroupsToProcess.first() - 1
    nextBrokenGroupsToProcess = brokenGroupsToProcess.subList(1, brokenGroupsToProcess.size)
    nextNumberOfGroupsCreated = numberOfGroupsCreated + 1
  }

  //if (nextRemainingBrokensInGroup < 0) return

  addCurrentAndProcessNext(
    broken,
    result,
    hotSpringsToProcess,
    nextRemainingBrokensInGroup,
    nextBrokenGroupsToProcess,
    remainingNumberOfUnknowns,
    remainingNumberOfKnownBrokens,
    remainingTotalNumberOfBrokens,
    numberOfGroupsNeeded,
    nextNumberOfGroupsCreated,
    solutionConsumer
  )
}

val stats = TreeMap<Int, Long>()
var globalCount = 0L

fun markStats(size: Int) {
  if (true) return
  stats[size] = stats.getOrDefault(size, 0) + 1
  globalCount++
  if (globalCount % 5_000_000 == 0L) {
    println("globalCount: $globalCount")
    println("stats: $stats")
  }
}

private fun addCurrentAndProcessNext(
  current: HotSpring,
  result: Sequence<HotSpring>,
  hotSpringsToProcess: List<HotSpring>,
  remainingBrokensInGroup: Int?,
  brokenGroupsToProcess: List<Int>,
  remainingNumberOfUnknowns: Int,
  remainingNumberOfKnownBrokens: Int,
  remainingTotalNumberOfBrokens: Int,
  numberOfGroupsNeeded: Int,
  numberOfGroupsCreated: Int,
  solutionConsumer: (Sequence<HotSpring>) -> Unit
) = processNext(
  result + current,
  hotSpringsToProcess,
  remainingBrokensInGroup,
  brokenGroupsToProcess,
  remainingNumberOfUnknowns,
  remainingNumberOfKnownBrokens,
  remainingTotalNumberOfBrokens,
  numberOfGroupsNeeded,
  numberOfGroupsCreated,
  solutionConsumer
)

fun processUnknown(
  result: Sequence<HotSpring>,
  hotSpringsToProcess: List<HotSpring>,
  remainingBrokensInCurrentGroup: Int?,
  brokenGroupsToProcess: List<Int>,
  remainingNumberOfUnknowns: Int,
  remainingNumberOfKnownBrokens: Int,
  remainingTotalNumberOfBrokens: Int,
  numberOfGroupsNeeded: Int,
  numberOfGroupsCreated: Int,
  solutionConsumer: (Sequence<HotSpring>) -> Unit
) {
  if (
    remainingBrokensInCurrentGroup.isCurrentGroupEmpty()
    && remainingTotalNumberOfBrokens < remainingNumberOfKnownBrokens + remainingNumberOfUnknowns
  ) {
    processOperational(
      result,
      hotSpringsToProcess,
      remainingBrokensInCurrentGroup,
      brokenGroupsToProcess,
      remainingNumberOfUnknowns - 1,
      remainingNumberOfKnownBrokens,
      remainingTotalNumberOfBrokens,
      numberOfGroupsNeeded,
      numberOfGroupsCreated,
      solutionConsumer
    )
  }

  if (
    remainingTotalNumberOfBrokens > 0
    && remainingTotalNumberOfBrokens > remainingNumberOfKnownBrokens
  ) {
    //if (!(hotSpringsToProcess.size == remainingTotalNumberOfBrokens && numberOfGroupsCreated < numberOfGroupsNeeded)) {
    processBroken(
      result,
      hotSpringsToProcess,
      remainingBrokensInCurrentGroup,
      brokenGroupsToProcess,
      remainingNumberOfUnknowns - 1,
      remainingNumberOfKnownBrokens,
      remainingTotalNumberOfBrokens - 1,
      numberOfGroupsNeeded,
      numberOfGroupsCreated,
      solutionConsumer
    )
    //}
  }
}

private fun processEnd(
  result: Sequence<HotSpring>,
  hotSpringsToProcess: List<HotSpring>,
  remainingBrokensInCurrentGroup: Int?,
  brokenGroupsToProcess: List<Int>,
  remainingNumberOfUnknowns: Int,
  remainingNumberOfKnownBrokens: Int,
  remainingTotalNumberOfBrokens: Int,
  numberOfGroupsNeeded: Int,
  numberOfGroupsCreated: Int,
  solutionConsumer: (Sequence<HotSpring>) -> Unit
) {
  require(hotSpringsToProcess.isEmpty())
  require(remainingNumberOfUnknowns == 0)
  require(remainingNumberOfKnownBrokens == 0)
  require(remainingTotalNumberOfBrokens == 0)
  require(remainingBrokensInCurrentGroup.isCurrentGroupEmpty())
  require(brokenGroupsToProcess.isEmpty())
  require(numberOfGroupsCreated == numberOfGroupsNeeded)

  solutionConsumer(result)
}

private fun Int?.isCurrentGroupEmpty(): Boolean = this == null || this == 0

private fun Int?.isCurrentGroupNotEmpty(): Boolean = !isCurrentGroupEmpty()

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
