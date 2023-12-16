package day16p2

import day16p2.Direction.*
import day16p2.TileType.Companion.toTileType
import day16p2.TileType.EmptySpace

fun String.findMaxCountEnergizedTiles(): Int {
  val contraption = toContraption()
  val numOfRows = contraption.rows.size
  val numOfCols = contraption.rows.first().size
  val allStartingPositions = (0 until numOfCols).asSequence().map { col -> Triple(Down, 0, col) } +
    (0 until numOfCols).asSequence().map { col -> Triple(Up, numOfRows - 1, col) } +
    (0 until numOfRows).asSequence().map { row -> Triple(Right, row, 0) } +
    (0 until numOfRows).asSequence().map { row -> Triple(Left, numOfCols - 1, row) }

  return allStartingPositions.maxOf { (direction, startRow, startCol) ->
    contraption.reset()
    contraption.simulateLightBeam(direction, startRow, startCol)
    contraption.countEnergizedTiles()
  }
}

fun String.countEnergizedTiles(): Int {
  val contraption = toContraption()
  contraption.simulateLightBeam()
  return contraption.countEnergizedTiles()
}

fun String.toContraption(): Contraption = Contraption(
  lines().mapIndexed { row, line -> line.mapIndexed { col, ch -> Tile(ch.toTileType(), row, col) } }
)

class Contraption(val rows: List<List<Tile>>) {
  fun row(row: Int): List<Tile> = rows[row]

  fun reset() {
    rows.forEach { row ->
      row.forEach { it.reset() }
    }
  }

  fun toText(): String = rows.joinToString("\n") { row ->
    row.joinToString("") { it.toSign().toString() }
  }

  fun toTextWithLightBeams(): String = rows.joinToString("\n") { row ->
    row.joinToString("") { it.toSignWithLightBeam().toString() }
  }

  fun toTextWithEnergizedTiles(): String = rows.joinToString("\n") { row ->
    row.joinToString("") { if (it.energized) "#" else "." }
  }

  fun simulateLightBeam(startDirection: Direction = Right, startRow: Int = 0, startCol: Int = 0) {
    val beamsToFollow = mutableListOf(startDirection to rows[startRow][startCol])

    while (beamsToFollow.isNotEmpty()) {
      val (direction, tile) = beamsToFollow.removeFirst()

      tile.simulateLightBeam(direction).forEach { newDirection ->
        val (newRow, newCol) = newDirection.position(tile.row, tile.col)
        if (newRow < 0 || newRow >= rows.size || newCol < 0 || newCol >= rows.first().size) return@forEach
        beamsToFollow.add(newDirection to rows[newRow][newCol])
      }
    }
  }

  fun countEnergizedTiles(): Int = rows.asSequence().flatten().count { it.energized }
}

class Tile(val type: TileType, val row: Int, val col: Int) {
  val lightBeams: MutableSet<Direction> = mutableSetOf()
  var energized: Boolean = false

  fun reset() {
    lightBeams.clear()
    energized = false
  }

  fun toSign(): Char = type.sign

  fun toSignWithLightBeam(): Char = if (type != EmptySpace) toSign() else when (lightBeams.size) {
    0 -> toSign()
    1 -> lightBeams.first().sign
    else -> lightBeams.size.toString()[0]
  }

  fun simulateLightBeam(direction: Direction): Set<Direction> {
    if (lightBeams.contains(direction)) return emptySet()

    energized = true
    lightBeams.add(direction)

    return type.directLightGoing(direction)
  }
}

enum class Direction(val sign: Char) {
  Right('>') {
    override fun position(row: Int, col: Int) = row to col + 1
  },
  Left('<') {
    override fun position(row: Int, col: Int) = row to col - 1
  },
  Up('^') {
    override fun position(row: Int, col: Int) = row - 1 to col
  },
  Down('v') {
    override fun position(row: Int, col: Int) = row + 1 to col
  };

  abstract fun position(row: Int, col: Int): Pair<Int, Int>
}

enum class TileType(val sign: Char) {
  EmptySpace('.') {
    override fun directLightGoing(direction: Direction): Set<Direction> = setOf(direction)
  },
  SlashMirror('/') {
    override fun directLightGoing(direction: Direction): Set<Direction> = when (direction) {
      Right -> setOf(Up)
      Left -> setOf(Down)
      Up -> setOf(Right)
      Down -> setOf(Left)
    }
  },
  BackslashMirror('\\') {
    override fun directLightGoing(direction: Direction): Set<Direction> = when (direction) {
      Right -> setOf(Down)
      Left -> setOf(Up)
      Up -> setOf(Left)
      Down -> setOf(Right)
    }
  },
  VerticalSplitter('|') {
    override fun directLightGoing(direction: Direction): Set<Direction> = when (direction) {
      Right -> setOf(Up, Down)
      Left -> setOf(Up, Down)
      else -> setOf(direction)
    }
  },
  HorizontalSplitter('-') {
    override fun directLightGoing(direction: Direction): Set<Direction> = when (direction) {
      Up -> setOf(Right, Left)
      Down -> setOf(Right, Left)
      else -> setOf(direction)
    }
  };

  abstract fun directLightGoing(direction: Direction): Set<Direction>

  companion object {
    private val typesBySign = entries.associateBy { it.sign }
    fun Char.toTileType(): TileType = typesBySign.getValue(this)
  }
}

