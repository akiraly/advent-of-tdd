package day12p2

import day12p2.HotSpring.*
import day12p2.HotSpring.Companion.toHotSpring
import java.util.*


fun String.unfold(): String = split(" ").let { (first, second) ->
  generateSequence { first }.take(5).joinToString("?") +
    " " +
    generateSequence { second }.take(5).joinToString(",")
}

fun String.sumOfSolutions(): Long = lineSequence().sumOf { it.toConditionRecordRow().countSolutions() }

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
