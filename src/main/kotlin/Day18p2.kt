package day18p2

import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

fun String.lavaCapacityOriginal(): Long {
  val digPlan = toDigPlanOriginal()
  val digGrid = digPlan.dig()
  return digGrid.capacity()
}

fun String.lavaCapacityUpdated(): Long {
  val digPlan = toDigPlanUpdated()
  val digGrid = digPlan.dig()
  return digGrid.capacity()
}

fun String.toDigPlanOriginal(): DigPlan = DigPlan(
  lines().map { it.toStepOriginal() }.toList()
)


private val stepRegexOriginal = """([RLUD]) (\d+) .*""".toRegex()

fun String.toStepOriginal(): Step =
  stepRegexOriginal.matchEntire(this)!!.destructured
    .let { (direction, distance) ->
      Step(
        direction = Direction.valueOf(direction),
        distance = distance.toInt()
      )
    }

fun String.toDigPlanUpdated(): DigPlan = DigPlan(
  lines().map { it.toStepUpdated() }.toList()
)


private val stepRegexUpdated = """.*\(#(\w+)([0-3])\)""".toRegex()

fun String.toStepUpdated(): Step =
  stepRegexUpdated.matchEntire(this)!!.destructured
    .let { (distanceHex, directionCode) ->
      Step(
        direction = when (directionCode) {
          "0" -> Direction.R
          "1" -> Direction.D
          "2" -> Direction.L
          else -> {
            require(directionCode == "3")
            Direction.U
          }
        },
        distance = distanceHex.toInt(16),
      )
    }

data class DigPlan(val steps: List<Step>) {
  val length: Long = steps.sumOf { it.distance.toLong() }
  val lengthByDirection = steps.groupBy { it.direction }.mapValues { (_, steps) -> steps.sumOf { it.distance } }
  fun toText(): String = steps.joinToString("\n")

  fun isLoop(): Boolean =
    lengthByDirection[Direction.R] == lengthByDirection[Direction.L] &&
      lengthByDirection[Direction.U] == lengthByDirection[Direction.D]

  fun rows(): Indexes {
    var max = 0
    var current = 0
    var min = 0
    for (step in steps) {
      when (step.direction) {
        Direction.D -> {
          current += step.distance
          max = max.coerceAtLeast(current)
        }

        Direction.U -> {
          current -= step.distance
          min = min.coerceAtMost(current)
        }

        else -> continue
      }
    }
    return Indexes(max, min)
  }

  fun columns(): Indexes {
    var max = 0
    var current = 0
    var min = 0
    for (step in steps) {
      when (step.direction) {
        Direction.R -> {
          current += step.distance
          max = max.coerceAtLeast(current)
        }

        Direction.L -> {
          current -= step.distance
          min = min.coerceAtMost(current)
        }

        else -> continue
      }
    }
    return Indexes(max, min)
  }

  data class Indexes(val max: Int, val min: Int) {
    val total: Int = max - min + 1
  }

  fun dig(): DigGrid {
    val columnIndexes = columns()
    val rowIndexes = rows()
    val columns = TreeSet<BorderColumn>()
    val rows = TreeSet<BorderRow>()
    var currentCol = columnIndexes.min.absoluteValue
    var currentRow = rowIndexes.min.absoluteValue
    for (step in steps) {
      when (step.direction) {
        Direction.D -> {
          val col = BorderColumn(currentCol, currentRow, currentRow + step.distance)
          columns.add(col)
          currentRow = col.rowEnd
        }

        Direction.U -> {
          val col = BorderColumn(currentCol, currentRow - step.distance, currentRow)
          columns.add(col)
          currentRow = col.rowStart
        }

        Direction.L -> {
          val row = BorderRow(currentRow, currentCol - step.distance, currentCol)
          rows.add(row)
          currentCol = row.colStart
        }

        Direction.R -> {
          val row = BorderRow(currentRow, currentCol, currentCol + step.distance)
          rows.add(row)
          currentCol = row.colEnd
        }
      }
    }

    val finalColumns = TreeSet<BorderColumn>()
    for (col in columns) {
      val splitters =
        rows.asSequence().map { it.row }.distinct().filter { row -> col.rowStart < row && row < col.rowEnd }
          .toMutableList()
      var remaining = col
      while (splitters.isNotEmpty()) {
        val splitter = splitters.removeFirst()!!
        val (firstPart, secondPart) = remaining.split(splitter)
        finalColumns.add(firstPart)
        remaining = secondPart
      }
      finalColumns.add(remaining)
    }

    return DigGrid(columnIndexes.total, rowIndexes.total, finalColumns, rows)
  }
}

data class DigGrid(
  val numOfColumns: Int,
  val numOfRows: Int,
  val columns: TreeSet<BorderColumn>,
  val rows: TreeSet<BorderRow>
) {
  val length: Long =
    columns.sumOf { it.rowEnd.toLong() - it.rowStart } + rows.sumOf { it.colEnd.toLong() - it.colStart }

  override fun toString(): String = columns.joinToString("\n") + "\n" + rows.joinToString("\n")
  fun capacity(): Long {

    val columns = TreeSet(columns)
    val rowsByRowNum = rows.groupBy { it.row }

    var total = length

    while (columns.isNotEmpty()) {
      val first = columns.removeFirst()!!
      val second = columns.removeFirst()!!
      require(first.col < second.col)
      require(first.rowStart == second.rowStart)
      val commonEnd = min(first.rowEnd, second.rowEnd)

      val rowsInSameRow = rowsByRowNum.getValue(commonEnd)
      val gapStart = rowsInSameRow.firstOrNull { first.col in it.colStart..it.colEnd }?.colEnd ?: first.col
      val gapEnd = rowsInSameRow.firstOrNull { second.col in it.colStart..it.colEnd }?.colStart ?: second.col
      val totalGap = max(0, gapEnd - gapStart - 1)
      val borderGap = rowsInSameRow.sumOf { it.between(gapStart, gapEnd) }
      val gap = totalGap - borderGap


      total += gap

      val colDiff: Long = second.col - first.col - 1L
      val rowDiff: Long = commonEnd - first.rowStart - 1L
      val area: Long = colDiff * rowDiff

      total += area
    }

    return total
  }
}

data class BorderColumn(val col: Int, val rowStart: Int, val rowEnd: Int) : Comparable<BorderColumn> {
  override fun toString(): String = "$col, ($rowStart, $rowEnd)"

  override fun compareTo(other: BorderColumn): Int = compareValuesBy(this, other,
    { it.rowStart },
    { it.col }
  )

  fun split(splitter: Int): Pair<BorderColumn, BorderColumn> = copy(rowEnd = splitter) to copy(rowStart = splitter)
}

data class BorderRow(val row: Int, val colStart: Int, val colEnd: Int) : Comparable<BorderRow> {
  override fun toString(): String = "$row, ($colStart, $colEnd)"

  override fun compareTo(other: BorderRow): Int = compareValuesBy(this, other,
    { it.row },
    { it.colStart }
  )

  fun between(start: Int, end: Int): Int {
    if (colEnd <= start) return 0
    if (end <= colStart) return 0
    return min(end - 1, colEnd) - max(start + 1, colStart) + 1
  }
}

data class Step(val direction: Direction, val distance: Int) {
  override fun toString(): String = "$direction $distance"
}

enum class Direction {
  R,
  L,
  U,
  D;
}
