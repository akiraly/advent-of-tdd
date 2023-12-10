package day10p1

import day10p1.Direction.*

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
  operator fun get(coordinate: Coordinate): PipeType = pipes[coordinate.row][coordinate.column]
  fun findNumberOfStepsToFarthestPointInLoop(): Long {
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
        if (existingNumOfSteps != null) return if (existingNumOfSteps == numOfSteps) existingNumOfSteps else continue
        reachMap[newCoordinate.row][newCoordinate.column] = numOfSteps
        queue.add(newCoordinate)
      }
    }

    error("Didn't find farthest point")
  }
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
  '.' -> PipeType.dot
  else -> error("Unknown pipe type: $this")
}

enum class PipeType(val directions: Set<Direction>) {
  `|`(setOf(north, south)),
  `-`(setOf(east, west)),
  L(setOf(north, east)),
  J(setOf(north, west)),
  `7`(setOf(south, west)),
  F(setOf(south, east)),
  dot(emptySet());
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
