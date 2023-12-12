package day12p1

import day12p1.HotSpring.*
import day12p1.HotSpring.Companion.toHotSpring

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
  val numberOfUnknowns = hotSprings.count { it == unknown }

  private fun solutions(): Sequence<List<HotSpring>> {
    val solutions = mutableListOf<List<HotSpring>>()
    processNext(hotSpringsToProcess = hotSprings, brokenGroupsToProcess = contGroupOfDamagedSprings) {
      solutions.add(it)
    }
    return solutions.asSequence()
  }

  fun countSolutions(): Long {
    var count = 0L
    processNext(hotSpringsToProcess = hotSprings, brokenGroupsToProcess = contGroupOfDamagedSprings) {
      count++
    }
    return count
  }

  fun solutionsAsText(): Sequence<String> = solutions().map { it.toConditionRecordRow1Text() }
}

private fun processNext(
  result: MutableList<HotSpring> = mutableListOf(),
  hotSpringsToProcess: List<HotSpring>,
  remainingBrokensInCurrentGroup: Int? = null,
  brokenGroupsToProcess: List<Int>,
  solutionConsumer: (List<HotSpring>) -> Unit
) {
  if (hotSpringsToProcess.isEmpty()) {
    processEnd(result, hotSpringsToProcess, remainingBrokensInCurrentGroup, brokenGroupsToProcess, solutionConsumer)
    return
  }

  val next = hotSpringsToProcess.first()
  val remaining = hotSpringsToProcess.subList(1, hotSpringsToProcess.size)
  when (next) {
    operational -> processOperational(
      result,
      remaining,
      remainingBrokensInCurrentGroup,
      brokenGroupsToProcess,
      solutionConsumer
    )

    broken -> processBroken(
      result,
      remaining,
      remainingBrokensInCurrentGroup,
      brokenGroupsToProcess,
      solutionConsumer
    )

    unknown -> processUnknown(
      result,
      remaining,
      remainingBrokensInCurrentGroup,
      brokenGroupsToProcess,
      solutionConsumer
    )
  }
}

private fun processOperational(
  result: MutableList<HotSpring>,
  hotSpringsToProcess: List<HotSpring>,
  remainingBrokensInGroup: Int?,
  brokenGroupsToProcess: List<Int>,
  solutionConsumer: (List<HotSpring>) -> Unit
) {
  if (remainingBrokensInGroup.isCurrentGroupNotEmpty()) {
    return
  }
  addCurrentAndProcessNext(
    operational,
    result,
    hotSpringsToProcess,
    null,
    brokenGroupsToProcess,
    solutionConsumer
  )
}

fun processBroken(
  result: MutableList<HotSpring>,
  hotSpringsToProcess: List<HotSpring>,
  remainingBrokensInGroup: Int?,
  brokenGroupsToProcess: List<Int>,
  solutionConsumer: (List<HotSpring>) -> Unit
) {
  val nextRemainingBrokensInGroup: Int
  val nextBrokenGroupsToProcess: List<Int>
  if (remainingBrokensInGroup != null) {
    nextRemainingBrokensInGroup = remainingBrokensInGroup - 1
    nextBrokenGroupsToProcess = brokenGroupsToProcess
  } else if (brokenGroupsToProcess.isEmpty()) {
    return
  } else {
    nextRemainingBrokensInGroup = brokenGroupsToProcess.first() - 1
    nextBrokenGroupsToProcess = brokenGroupsToProcess.subList(1, brokenGroupsToProcess.size)
  }

  if (nextRemainingBrokensInGroup < 0) return

  addCurrentAndProcessNext(
    broken,
    result,
    hotSpringsToProcess,
    nextRemainingBrokensInGroup,
    nextBrokenGroupsToProcess,
    solutionConsumer
  )
}

fun processUnknown(
  result: MutableList<HotSpring>,
  hotSpringsToProcess: List<HotSpring>,
  remainingBrokensInCurrentGroup: Int?,
  brokenGroupsToProcess: List<Int>,
  solutionConsumer: (List<HotSpring>) -> Unit
) {
  processOperational(
    result,
    hotSpringsToProcess,
    remainingBrokensInCurrentGroup,
    brokenGroupsToProcess,
    solutionConsumer
  )

  processBroken(
    result,
    hotSpringsToProcess,
    remainingBrokensInCurrentGroup,
    brokenGroupsToProcess,
    solutionConsumer
  )
}

private fun processEnd(
  result: MutableList<HotSpring>,
  hotSpringsToProcess: List<HotSpring>,
  remainingBrokensInCurrentGroup: Int?,
  brokenGroupsToProcess: List<Int>,
  solutionConsumer: (List<HotSpring>) -> Unit
) {
  require(hotSpringsToProcess.isEmpty())

  if (remainingBrokensInCurrentGroup.isCurrentGroupNotEmpty() || brokenGroupsToProcess.isNotEmpty()) return

  solutionConsumer(result.toList())
}

private fun Int?.isCurrentGroupNotEmpty(): Boolean = this != null && this > 0

private fun addCurrentAndProcessNext(
  current: HotSpring,
  result: MutableList<HotSpring>,
  hotSpringsToProcess: List<HotSpring>,
  remainingBrokensInGroup: Int?,
  brokenGroupsToProcess: List<Int>,
  solutionConsumer: (List<HotSpring>) -> Unit
) {
  result.add(current)

  processNext(result, hotSpringsToProcess, remainingBrokensInGroup, brokenGroupsToProcess, solutionConsumer)

  result.removeLast()
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
