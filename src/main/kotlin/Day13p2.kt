package day13p2

import kotlin.math.min

fun List<Pattern>.sumOfScoredAndSmudgeFixedMirrorPositions(): Long = sumOf {
  it.fixSmudgeAndFindScoredMirrorPosition()
}

fun List<Pattern>.sumOfScoredMirrorPositions(): Long = sumOf { it.findScoredMirrorPosition() }

fun String.parsePatterns(): List<Pattern> = split("\n\n").map { it.toPattern() }

fun String.toPattern(): Pattern = Pattern(lines().map { it.toList() })

data class Pattern(val lines: List<List<Char>>) {

  fun findVerticalMirrorPosition(forcedPosition: Int = -1, smudgeColRow: Pair<Int, Int> = -1 to -1): Int =
    findVerticalMirrorPositions(forcedPosition, smudgeColRow).firstOrNull() ?: -1

  fun findHorizontalMirrorPosition(): Int = transpose().findVerticalMirrorPosition()

  fun findVerticalMirrorPositions(forcedPosition: Int = -1, smudgeColRow: Pair<Int, Int> = -1 to -1): Set<Int> {
    val length = lines.first().size
    var candidates = (if (forcedPosition == -1) (1..<length) else (forcedPosition..forcedPosition))
      .map { it to calcMirrorSize(it, length) }.toList()

    for (row in lines.indices) {
      val line = lines[row]
      candidates = candidates.filter { (position, mirrorSize) ->

        (position - mirrorSize..<position).zip(position + mirrorSize - 1 downTo position)
          .all { (a, b) -> line[a] == line[b] || (a == smudgeColRow.first && row == smudgeColRow.second) }
      }

      if (candidates.isEmpty()) return emptySet()
    }

    return candidates.map { it.first }.toSet()
  }

  private fun calcMirrorSize(position: Int, fullWidth: Int) = min(position, fullWidth - position)

  private fun transpose(): Pattern = Pattern(lines.transpose())

  fun toPatternString(): String = lines.joinToString("\n") { it.joinToString("") }
  fun findScoredMirrorPosition(): Long {
    var position = findVerticalMirrorPosition()
    if (position != -1) return position.toLong()

    position = findHorizontalMirrorPosition()

    require(position > 0) { "No mirror position found: ${lines.joinToString("\n") { it.joinToString("") }}" }

    return position * 100L
  }

  fun fixSmudgeAndFindScoredMirrorPosition(): Long {
    val position = fixSmudgeAndFindScoredMirrorPositionVertically()
    if (position != -1L) return position
    println("no smudge vertically, trying horizontally")

    return fixSmudgeAndFindScoredMirrorPositionHorizontally() * 100
  }

  private fun fixSmudgeAndFindScoredMirrorPositionVertically(): Long {
    val position = findVerticalMirrorPosition()
    for (i in 0..<lines.first().size - 1) {
      for (j in i + 1..<lines.first().size) {
        val diffRow = diff(i, j)
        if (diffRow == -1) continue
        val mirrorPosition = (i + j + 1) / 2
        if (mirrorPosition == position) continue
        val mirrorSize = calcMirrorSize(mirrorPosition, lines.first().size)
        if (mirrorSize == 0) continue
        if (findVerticalMirrorPosition(mirrorPosition, i to diffRow) == mirrorPosition) {
          println("smudge at $i, $diffRow vs $j with $mirrorPosition")
          return mirrorPosition.toLong()
        }
      }
    }

    return -1
  }

  private fun diff(i: Int, j: Int): Int {
    val diffs = lines.mapIndexedNotNull { index, chars ->
      if (chars[i] != chars[j]) index else null
    }.toList()
    return if (diffs.size == 1) diffs.single() else -1
  }

  private fun fixSmudgeAndFindScoredMirrorPositionHorizontally(): Long =
    transpose().fixSmudgeAndFindScoredMirrorPositionVertically()
}

fun List<String>.transposeStrings(): List<String> = asSequence()
  .map { it.toList() }
  .transpose()
  .map { it.joinToString("") }
  .toList()

fun List<List<Char>>.transpose(): List<List<Char>> = asSequence().transpose().toList()

fun Sequence<List<Char>>.transpose(): Sequence<List<Char>> =
  (0 until first().size).asSequence().map { col -> map { it[col] }.toList() }
