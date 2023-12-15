package day13p1

import kotlin.math.min

fun List<Pattern>.sumOfScoredMirrorPositions(): Long = sumOf { it.findScoredMirrorPosition() }

fun String.parsePatterns(): List<Pattern> = split("\n\n").map { it.toPattern() }

fun String.toPattern(): Pattern = Pattern(lines().map { it.toList() })

data class Pattern(val lines: List<List<Char>>) {

  fun findVerticalMirrorPosition(): Int = findVerticalMirrorPositions().firstOrNull() ?: -1

  fun findHorizontalMirrorPosition(): Int = transpose().findVerticalMirrorPosition()

  fun findVerticalMirrorPositions(): Set<Int> {
    val length = lines.first().size
    var candidates = (1..<length).map { it to min(it, length - it) }.toList()

    for (line in lines) {
      candidates = candidates.filter { (position, mirrorSize) ->

        (position - mirrorSize..<position).zip(position + mirrorSize - 1 downTo position)
          .all { (a, b) -> line[a] == line[b] }
      }

      if (candidates.isEmpty()) return emptySet()
    }

    return candidates.map { it.first }.toSet()
  }

  private fun transpose(): Pattern = Pattern(lines.transpose())

  fun toPatternString(): String = lines.joinToString("\n") { it.joinToString("") }
  fun findScoredMirrorPosition(): Long {
    var position = findVerticalMirrorPosition()
    if (position != -1) return position.toLong()

    position = findHorizontalMirrorPosition()

    require(position > 0)

    return position * 100L
  }
}

fun List<String>.transposeStrings(): List<String> = asSequence()
  .map { it.toList() }
  .transpose()
  .map { it.joinToString("") }
  .toList()

fun List<List<Char>>.transpose(): List<List<Char>> = asSequence().transpose().toList()

fun Sequence<List<Char>>.transpose(): Sequence<List<Char>> =
  (0 until first().size).asSequence().map { col -> map { it[col] }.toList() }

