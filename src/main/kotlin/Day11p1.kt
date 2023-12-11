package day11p1

import java.util.*
import kotlin.math.absoluteValue

fun String.toUniverse(): Universe {
  val lines = lines()
  val numOfRows = lines.size
  val numOfColumns = lines.first().length

  val galaxies = lines.flatMapIndexed { row: Int, line: String ->
    line.mapIndexedNotNull { column, ch -> if (ch == '#') row to column else null }
  }
    .map { Galaxy(Coordinate(it.first, it.second)) }
    .toSet()

  return Universe(numOfRows.toLong(), numOfColumns.toLong(), galaxies)
}

data class Universe(val numOfRows: Long, val numOfColumns: Long, val galaxies: Set<Galaxy>) {
  fun toExpandedUniverse(): Universe {
    var rowOffset = 0L
    val rowOffsets = TreeMap<Long, Long>()
    rowOffsets[0] = rowOffset
    val galaxyRows = galaxies.asSequence().map { it.coordinate.row }.toSet()
    (0..<numOfRows).asSequence().filter { !galaxyRows.contains(it) }.forEach { row ->
      rowOffset++
      rowOffsets[row] = rowOffset
    }

    var columnOffset = 0L
    val columnOffsets = TreeMap<Long, Long>()
    columnOffsets[0] = columnOffset
    val galaxyColumns = galaxies.asSequence().map { it.coordinate.column }.toSet()
    (0..<numOfColumns).asSequence().filter { !galaxyColumns.contains(it) }.forEach { column ->
      columnOffset++
      columnOffsets[column] = columnOffset
    }

    return Universe(
      numOfRows + rowOffset,
      numOfColumns + columnOffset,
      galaxies.map { galaxy ->
        val row = galaxy.coordinate.row
        val column = galaxy.coordinate.column
        val ro = rowOffsets.floorEntry(row).value!!
        val co = columnOffsets.floorEntry(column).value!!

        Galaxy(Coordinate(row + ro, column + co))
      }
        .toSet()
    )
  }

  fun toImage(): String {
    val galaxyColumnsByRow =
      galaxies.groupBy({ it.coordinate.row }) { it.coordinate.column }.mapValues { it.value.toSet() }

    return (0..<numOfRows).asSequence().map { row ->
      val galaxiesInTheRow = galaxyColumnsByRow[row] ?: emptySet()
      (0..<numOfColumns).asSequence().map { column -> if (galaxiesInTheRow.contains(column)) '#' else '.' }.toList()
    }.joinToString("\n") { it.joinToString("") }
  }

  fun sumOfShortestPaths(): Long {
    val galaxyList = galaxies.toList()
    var result = 0L
    for (i in galaxyList.indices) {
      val galaxy1 = galaxyList[i]
      for (j in (i + 1)..<galaxyList.size) {
        val galaxy2 = galaxyList[j]
        result += galaxy1.distanceTo(galaxy2)
      }
    }

    return result
  }
}

data class Galaxy(val coordinate: Coordinate) {
  fun distanceTo(that: Galaxy): Long = coordinate.distanceTo(that.coordinate)
}

data class Coordinate(val row: Long, val column: Long) {
  constructor(row: Int, column: Int) : this(row.toLong(), column.toLong())

  fun distanceTo(that: Coordinate): Long = (row - that.row).absoluteValue + (column - that.column).absoluteValue
}
