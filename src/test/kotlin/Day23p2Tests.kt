package day23p2

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import io.readInput

class Day23p2Tests : FunSpec({
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
//        start.incoming shouldBe emptySet()
        start.outgoing.size shouldBe 1

        val end = updated.map.getValue(updated.end)
//        end.outgoing shouldBe emptySet()
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
      row("example map", exampleMap, 154),
      row("custom map", customMap, 6298)
    ).forAll { mapName, map, lengthOfLongestHike ->
      test(""" Given the accessible map created from $mapName then the length of the longest hike is $lengthOfLongestHike""") {
        val accessibleMap = map.toAccessibleMap().keepImportantOnly()
        accessibleMap.findLengthOfLongestHike() shouldBe lengthOfLongestHike
      }
    }
  }
})
