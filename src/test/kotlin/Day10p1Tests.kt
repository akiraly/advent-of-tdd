package day10p1


import day10p1.Direction.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import io.readInput

class Day10p1Tests : FunSpec({
  context(""" Coordinates and directions""") {
    val coordinate = Coordinate(row = 0, column = 0)

    table(
      headers("Direction", "Result Coordinate"),
      row(north, Coordinate(row = -1, column = 0)),
      row(south, Coordinate(row = 1, column = 0)),
      row(east, Coordinate(row = 0, column = 1)),
      row(west, Coordinate(row = 0, column = -1)),
    ).forAll { direction, resultCoordinate ->
      test("""Given $coordinate and $direction then the result should be $resultCoordinate""") {
        val result = direction(coordinate)
        result shouldBe resultCoordinate
      }
    }

    table(
      headers("Direction", "Reverse"),
      row(north, south),
      row(south, north)
    ).forAll { direction, reverse ->
      test("""Given $direction then the reverse should be $reverse""") {
        direction.reverse shouldBe reverse
        reverse.reverse shouldBe direction
      }
    }
  }

  context(""" Pipe types and directions """) {
    table(
      headers("Pipe type", "Direction(s)"),
      row('|', setOf(north, south)),
      row('-', setOf(east, west)),
      row('L', setOf(north, east)),
      row('J', setOf(north, west)),
      row('7', setOf(south, west)),
      row('F', setOf(south, east)),
      row('.', emptySet())
    ).forAll { rawPipeType, directions ->
      val pipeType = rawPipeType.toPipeType()
      test("""Given $pipeType then the possible directions should be $directions""") {
        pipeType.directions shouldBe directions
      }
    }
  }

  val exampleMap1 = """
    .....
    .S-7.
    .|.|.
    .L-J.
    .....
  """.trimIndent()

  val exampleMap2 = """
    ..F7.
    .FJ|.
    SJ.L7
    |F--J
    LJ...
  """.trimIndent()

  val exampleMap3 = """
    7-F7-
    .FJ|7
    SJLL7
    |F--J
    LJ.LJ
  """.trimIndent()

  context(""" Map parsing """) {
    table(
      headers("Map", "Start Coordinates"),
      row(exampleMap1, Coordinate(row = 1, column = 1)),
      row(exampleMap2, Coordinate(row = 2, column = 0)),
      row(exampleMap3, Coordinate(row = 2, column = 0)),
    ).forAll { mapString, startCoordinate ->
      test("""Given $mapString then the start coordinate should be $startCoordinate""") {
        mapString.findStartCoordinate() shouldBe startCoordinate
      }
    }

    table(
      headers("Map", "Coordinate", "Pipe type"),
      row(exampleMap1, Coordinate(row = 1, column = 1), PipeType.F),
      row(exampleMap2, Coordinate(row = 2, column = 0), PipeType.F),
      row(exampleMap3, Coordinate(row = 2, column = 0), PipeType.F),
    ).forAll { mapString, coordinate, pipeType ->
      test("""Given $mapString then the pipe type at $coordinate, based on neighbors, should be $pipeType""") {
        mapString.lines().calcPipeType(coordinate) shouldBe pipeType
      }

      test("""Given $mapString then we should be able to parse the Map and find the start at $coordinate and the start pipe type as $pipeType""") {
        val map = mapString.toPipeMap()
        map.startCoordinate shouldBe coordinate
        map[map.startCoordinate] shouldBe pipeType
      }
    }
  }

  context("Calculating the number of steps to farthest loop point") {
    table(
      headers("Title", "Map", "Number of steps"),
      row("example map 1", exampleMap1, 4),
      row("example map 2", exampleMap2, 8),
      row("example map 3", exampleMap3, 8),
      row("custom input map", readInput("day10p1"), 6599)
    ).forAll { title, rawMap, numberOfSteps ->
      test(""" Given $title, then the number of steps to get to the farthest point of the loop should be $numberOfSteps""") {
        rawMap.toPipeMap().findNumberOfStepsToFarthestPointInLoop() shouldBe numberOfSteps
      }
    }
  }
})
