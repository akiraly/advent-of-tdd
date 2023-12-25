package day23p1

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import io.readInput

class Day23p1Tests : FunSpec({
  val exampleMap = """
#.#####################
#.......#########...###
#######.#########.#.###
###.....#.>.>.###.#.###
###v#####.#v#.###.#.###
###.>...#.#.#.....#...#
###v###.#.#.#########.#
###...#.#.#.......#...#
#####.#.#.#######.#.###
#.....#.#.#.......#...#
#.#####.#.#.#########v#
#.#...#...#...###...>.#
#.#.#v#######v###.###v#
#...#.>.#...>.>.#.###.#
#####v#.#.###v#.#.###.#
#.....#...#...#.#.#...#
#.#########.###.#.#.###
#...###...#...#...#.###
###.###.#.###v#####v###
#...#...#.#.>.>.#.>.###
#.###.###.#.###.#.#v###
#.....###...###...#...#
#####################.#
  """.trimIndent()

  val customMap = readInput("day23p1")

  context("Accessible map init") {
    table(
      headers("Map name", "Map", "Start", "End", "Number of accessible tiles", "Extra checks"),
      row("example map", exampleMap, CoordinateXY(1, 0), CoordinateXY(21, 22), 213) { accessibleMap: AccessibleMap ->
        accessibleMap.map.getValue(CoordinateXY(1, 1)).type shouldBe TileType.Path
        accessibleMap.map.getValue(CoordinateXY(10, 3)).type shouldBe TileType.SlopeRight
      },
      row("custom map", customMap, CoordinateXY(1, 0), CoordinateXY(139, 140), 9356) {}
    ).forAll { mapName, map, start, end, accessibleTiles, extraChecks ->

      test(""" Given the $mapName then the path, slope and start and end tiles can be extracted correctly""") {
        val accessibleMap = map.toAccessibleMap()

        accessibleMap.start shouldBe start
        accessibleMap.end shouldBe end

        accessibleMap.map.size shouldBe accessibleTiles

        extraChecks(accessibleMap)
      }
    }
  }

  context("Accessible map keep important only") {
    table(
      headers("Map name", "Map", "Number of accessible tiles", "Extra checks"),
      row("example map", exampleMap, 9) { accessibleMap: AccessibleMap ->
        val start = accessibleMap.map.getValue(accessibleMap.start)

        start.outgoing.single() shouldBe Path(start.coordinate, CoordinateXY(3, 5), 15)
      },
      row("custom map", customMap, 36) {}
    ).forAll { mapName, map, accessibleTiles, extraChecks ->

      test(""" Given the accessible map created from $mapName then it can be simplified to keep the important parts only""") {
        val accessibleMap = map.toAccessibleMap()

        val updated = accessibleMap.keepImportantOnly()

        updated.map.size shouldBe accessibleTiles

        val start = updated.map.getValue(updated.start)
        start.incoming shouldBe emptySet()
        start.outgoing.size shouldBe 1

        val end = updated.map.getValue(updated.end)
        end.outgoing shouldBe emptySet()
        end.incoming.size shouldBe 1

        updated.map.values.forEach { tile ->
          updated.isImportantTile(tile) shouldBe true
        }

        extraChecks(updated)
      }
    }
  }

  context("Accessible map hike finding") {
    table(
      headers("Map name", "Map", "Length of longest hike"),
      row("example map", exampleMap, 9),
      row("custom map", customMap, 36)
    ).forAll { mapName, map, lengthOfLongestHike ->
      test(""" Given the accessible map created from $mapName then the length of the longest hike is $lengthOfLongestHike""") {
        val accessibleMap = map.toAccessibleMap().keepImportantOnly()
        accessibleMap.findLengthOfLongestHike() shouldBe lengthOfLongestHike
      }
    }
  }
})

data class CoordinateXY(val x: Int, val y: Int) {
  init {
    require(x >= 0 && y >= 0)
  }
}

fun String.toAccessibleMap(): AccessibleMap {
  val lines = lines()
  val maxY = lines.size - 1
  val maxX = lines.first().length - 1

  fun Set<Pair<Int, Int>>.toNeighbours(coordinate: CoordinateXY): Sequence<CoordinateXY> =
    this.asSequence().map { (dx, dy) -> coordinate.x + dx to coordinate.y + dy }
      .filter { (x, y) -> x >= 0 && y >= 0 && x <= maxX && y <= maxY && lines[y][x] != '#' }
      .map { (x, y) -> CoordinateXY(x, y) }


  val tiles = lines.flatMapIndexed { y, line ->
    line.mapIndexedNotNull { x, tile ->
      val type = when (tile) {
        '.' -> TileType.Path
        '^' -> TileType.SlopeUp
        'v' -> TileType.SlopeDown
        '>' -> TileType.SlopeRight
        '<' -> TileType.SlopeLeft
        else -> return@mapIndexedNotNull null
      }
      val coordinate = CoordinateXY(x, y)
      if (type != TileType.Path) {
        val neighbours = setOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1).toNeighbours(coordinate)
        require(neighbours.count() == 2)
      }
      val incoming = type.incomingDeltas.toNeighbours(coordinate).map { Path(it, coordinate) }.toSet()
      val outgoing = type.outgoingDeltas.toNeighbours(coordinate).map { Path(coordinate, it) }.toSet()
      Tile(coordinate, type, incoming, outgoing)
    }
  }.associateBy { it.coordinate }

  return AccessibleMap(
    tiles.mapValues { (_, tile) ->
      tile.copy(
        incoming = tile.incoming.filter { path -> tiles.getValue(path.from).outgoing.contains(path) }.toSet(),
        outgoing = tile.outgoing.filter { path -> tiles.getValue(path.to).incoming.contains(path) }.toSet()
      )
    },
    lines.first().withIndex().single { it.value == '.' }.let { CoordinateXY(it.index, 0) },
    lines.withIndex().last().let { (i, line) ->
      CoordinateXY(line.withIndex().single { it.value == '.' }.index, i)
    }
  )
}

data class AccessibleMap(val map: Map<CoordinateXY, Tile>, val start: CoordinateXY, val end: CoordinateXY) {

  init {
    map.values.forEach { tile ->
      require(tile.isStart() || tile.incoming.isNotEmpty())
      require(tile.isEnd() || tile.outgoing.isNotEmpty())

      tile.incoming.forEach { path ->
        require(map.getValue(path.from).outgoing.contains(path))
      }
      tile.outgoing.forEach { path ->
        require(map.getValue(path.to).incoming.contains(path))
      }
    }
  }

  private fun Tile.isStart() = coordinate == start
  private fun Tile.isEnd() = coordinate == end

  private fun Tile.isImportant() = isStart() || isEnd() || isCrossroads()

  fun isImportantTile(tile: Tile) = tile.isImportant()

  fun keepImportantOnly(): AccessibleMap {
    val important = map.values.asSequence()
      .filter { it.isImportant() }
      .toSet()
    val importantCoordinates = important.map { it.coordinate }.toSet()

    val incoming: Map<CoordinateXY, MutableSet<Path>> =
      important.asSequence().map { tile ->
        tile.coordinate to tile.incoming.asSequence().filter { importantCoordinates.contains(it.from) }.toMutableSet()
      }.toMap()
    val outgoing: Map<CoordinateXY, Set<Path>> = important.asSequence()
      .map { tile ->
        val coordinate = tile.coordinate
        val outgoingPaths = tile.outgoing
        coordinate to outgoingPaths.asSequence().mapNotNull path@{ p ->
          var current = map.getValue(p.to)
          var oldPath = p
          var length = 1
          while (!current.isImportant()) {
            oldPath = current.outgoing.asSequence().filter { it.to != oldPath.from }.singleOrNull() ?: return@path null
            current = map.getValue(oldPath.to)
            length++
          }
          if (length == 1) return@path p

          val newPath = Path(coordinate, current.coordinate, length)

          val endTile = incoming.getValue(current.coordinate)
          endTile.remove(oldPath)
          endTile.add(newPath)

          newPath
        }.toSet()
      }.toMap()

    return AccessibleMap(
      important.asSequence().map { tile ->
        tile.coordinate to tile.copy(
          incoming = incoming.getValue(tile.coordinate),
          outgoing = outgoing.getValue(tile.coordinate)
        )
      }.toMap(),
      start,
      end
    )

  }

  fun findLengthOfLongestHike(): Int {
    val start = map.getValue(start)
    val end = map.getValue(end)
    var max = 0
    hike(setOf(start), end) { _, length -> max = maxOf(max, length) }
    return max
  }

  private fun hike(hike: Set<Tile>, end: Tile, length: Int = 0, onSuccess: (Set<Tile>, Int) -> Unit) {
    val previous = hike.last()
    if (previous == end) onSuccess(hike, length)

    previous.outgoing.forEach { p ->
      val next = map.getValue(p.to)
      if (!hike.any { it.coordinate == next.coordinate }) hike(hike + next, end, length + p.length, onSuccess)
    }
  }
}

data class Tile(val coordinate: CoordinateXY, val type: TileType, val incoming: Set<Path>, val outgoing: Set<Path>) {
  fun isCrossroads() =
    (incoming.asSequence().map { it.from } + outgoing.asSequence().map { it.to }).distinct().count() > 2
}

data class Path(val from: CoordinateXY, val to: CoordinateXY, val length: Int = 1)

enum class TileType(val incomingDeltas: Set<Pair<Int, Int>>, val outgoingDeltas: Set<Pair<Int, Int>> = incomingDeltas) {
  Path(setOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)),
  SlopeLeft(setOf(1 to 0), setOf(-1 to 0)),
  SlopeRight(setOf(-1 to 0), setOf(1 to 0)),
  SlopeUp(setOf(0 to 1), setOf(0 to -1)),
  SlopeDown(setOf(0 to -1), setOf(0 to 1))
}
