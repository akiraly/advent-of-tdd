package day10p2

import day10p2.Direction.*

fun String.toPipeMap(): PipeMap {
  val startCoordinate = findStartCoordinate()
  val lines = lines()
  val startPipeType = lines.calcPipeType(startCoordinate)
  return PipeMap(
    pipes = lines.mapIndexed { row, line ->
      line.mapIndexed { column, ch ->
        val coordinate = Coordinate(row, column)
        if (coordinate == startCoordinate) startPipeType else ch.toPipeType()
      }
    },
    startCoordinate = startCoordinate
  )
}

data class PipeMap(val pipes: List<List<PipeType>>, val startCoordinate: Coordinate) {
  val numberOfRows = pipes.size
  val numberOfColumns = pipes[0].size
  val reachMap: Array<Array<Long?>>
  val numOfStepsToFarthestPointInLoop: Long

  init {
    val (rm, numOfSteps) = calcReachMap()
    reachMap = rm
    numOfStepsToFarthestPointInLoop = numOfSteps
  }

  operator fun get(coordinate: Coordinate): PipeType = pipes[coordinate.row][coordinate.column]
  fun findNumberOfStepsToFarthestPointInLoop(): Long = numOfStepsToFarthestPointInLoop

  private fun calcReachMap(): Pair<Array<Array<Long?>>, Long> {
    val reachMap = Array(numberOfRows) { arrayOfNulls<Long>(numberOfColumns) }

    val queue = mutableListOf(startCoordinate)
    reachMap[startCoordinate.row][startCoordinate.column] = 0L

    while (queue.isNotEmpty()) {
      val coordinate = queue.removeFirst()!!
      val numOfSteps = reachMap[coordinate.row][coordinate.column]!! + 1
      for (direction in pipes[coordinate.row][coordinate.column].directions) {
        val newCoordinate = direction(coordinate)
        if (newCoordinate.row < 0 || newCoordinate.row >= this.numberOfRows) continue
        if (newCoordinate.column < 0 || newCoordinate.column >= this.numberOfColumns) continue
        val existingNumOfSteps = reachMap[newCoordinate.row][newCoordinate.column]
        if (existingNumOfSteps != null) {
          if (existingNumOfSteps != numOfSteps) continue

          return reachMap to existingNumOfSteps
        }
        reachMap[newCoordinate.row][newCoordinate.column] = numOfSteps
        queue.add(newCoordinate)
      }
    }

    error("Didn't find farthest point")
  }

  fun enlargeMap(): String {
    val result = Array(numberOfRows * 2) { Array(numberOfColumns * 2) { '.' } }
    for (row in 0 until numberOfRows) {
      for (column in 0 until numberOfColumns) {
        val reach = reachMap[row][column]
        val newRow = row * 2
        val newColumn = column * 2
        if (reach == null) {
          result[newRow][newColumn] = '.'
          result[newRow][newColumn + 1] = '.'
          result[newRow + 1][newColumn] = '.'
          continue
        }
        val pipeType = pipes[row][column]
        if (Coordinate(row, column) == startCoordinate) {
          result[newRow][newColumn] = 'S'
        } else {
          result[newRow][newColumn] = pipeType.ch
        }
        if (pipeType.directions.contains(east)) {
          result[newRow][newColumn + 1] = PipeType.`-`.ch
        }
        if (pipeType.directions.contains(south)) {
          result[newRow + 1][newColumn] = PipeType.`|`.ch
        }
      }
    }
    return result.joinToString("\n") { it.joinToString("") }
  }

  fun markRegionsOnly(): String {
    val result = Array(numberOfRows) { Array(numberOfColumns) { '.' } }
    val visitedCoordinates = mutableSetOf<Coordinate>()
    for (row in 0 until numberOfRows) {
      for (column in 0 until numberOfColumns) {
        val reach = reachMap[row][column]
        val currentCoordinate = Coordinate(row, column)
        if (reach != null) {
          if (currentCoordinate == startCoordinate) {
            result[row][column] = 'S'
          } else {
            result[row][column] = pipes[row][column].ch
          }
          continue
        }
        if (visitedCoordinates.contains(currentCoordinate)) continue
        val queue = mutableListOf(currentCoordinate)
        val regionCoordinates = mutableSetOf<Coordinate>()
        var endOfMapReached = false
        while (queue.isNotEmpty()) {
          val coordinate = queue.removeFirst()
          if (regionCoordinates.contains(coordinate)) continue
          if (coordinate.row < 0 || coordinate.row >= this.numberOfRows
            || coordinate.column < 0 || coordinate.column >= this.numberOfColumns
          ) {
            endOfMapReached = true
            continue
          }
          val r = reachMap[coordinate.row][coordinate.column]
          if (r != null) continue
          regionCoordinates.add(coordinate)
          Direction.entries.forEach { queue.add(it(coordinate)) }
        }
        regionCoordinates.forEach { result[it.row][it.column] = if (endOfMapReached) 'O' else 'I' }
        visitedCoordinates.addAll(regionCoordinates)
      }
    }
    return result.joinToString("\n") { it.joinToString("") }
  }

  fun markRegions(): String {
    val enlargedAndMarkedMap = enlargeMap().toPipeMap().markRegionsOnly().lines()
    val result = Array(numberOfRows) { Array(numberOfColumns) { '.' } }
    for (row in 0 until numberOfRows) {
      for (column in 0 until numberOfColumns) {
        val reach = reachMap[row][column]
        if (reach != null) {
          if (Coordinate(row, column) == startCoordinate) {
            result[row][column] = 'S'
          } else {
            result[row][column] = pipes[row][column].ch
          }
          continue
        }

        result[row][column] = enlargedAndMarkedMap[2 * row][2 * column]
      }
    }
    return result.joinToString("\n") { it.joinToString("") }
  }

  fun countNumberOfEnclosedTiles(): Int = markRegions().count { it == 'I' }
}

fun List<String>.calcPipeType(coordinate: Coordinate): PipeType {
  val directions = Direction.entries.asSequence().filter { direction ->
    val newCoordinate = direction(coordinate)
    if (newCoordinate.row < 0 || newCoordinate.row >= this.size) return@filter false

    val line = this[newCoordinate.row]
    if (newCoordinate.column < 0 || newCoordinate.column >= line.length) return@filter false

    val pipeType = line[newCoordinate.column].toPipeType()
    pipeType.directions.any { it.reverse == direction }
  }.toSet()

  val candidates = PipeType.entries.filter { it.directions == directions }

  require(candidates.size == 1)

  return candidates.first()
}

fun String.findStartCoordinate(): Coordinate = lineSequence().flatMapIndexed { row: Int, line: String ->
  line.mapIndexed { column: Int, char: Char -> Coordinate(row, column) to char }
}.first { (_, char) -> char == 'S' }.first

fun Char.toPipeType(): PipeType = when (this) {
  '|' -> PipeType.`|`
  '-' -> PipeType.`-`
  'L' -> PipeType.L
  'J' -> PipeType.J
  '7' -> PipeType.`7`
  'F' -> PipeType.F
  else -> PipeType.dot
}

enum class PipeType(val ch: Char, val directions: Set<Direction>) {
  `|`('|', setOf(north, south)),
  `-`('-', setOf(east, west)),
  L('L', setOf(north, east)),
  J('J', setOf(north, west)),
  `7`('7', setOf(south, west)),
  F('F', setOf(south, east)),
  dot('.', emptySet());
}

enum class Direction {
  north {
    override operator fun invoke(coordinate: Coordinate) = coordinate.copy(row = coordinate.row - 1)

    override val reverse: Direction get() = south
  },
  south {
    override operator fun invoke(coordinate: Coordinate) = coordinate.copy(row = coordinate.row + 1)

    override val reverse: Direction get() = north
  },
  east {
    override operator fun invoke(coordinate: Coordinate) = coordinate.copy(column = coordinate.column + 1)

    override val reverse: Direction get() = west
  },
  west {
    override operator fun invoke(coordinate: Coordinate) = coordinate.copy(column = coordinate.column - 1)

    override val reverse: Direction get() = east
  };

  abstract operator fun invoke(coordinate: Coordinate): Coordinate

  abstract val reverse: Direction
}

data class Coordinate(val row: Int, val column: Int)
