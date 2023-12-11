package day10p2


import day10p2.Direction.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import io.readInput

class Day10p2Tests : FunSpec({
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

  context(""" Enlarging map """) {
    table(
      headers("Map", "Enlarged map"),
      row(
        exampleMap1,
        """
          ..........
          ..........
          ..S---7...
          ..|...|...
          ..|...|...
          ..|...|...
          ..L---J...
          ..........
          ..........
          ..........
        """.trimIndent()
      ),
      row(
        exampleMap2,
        """
          ....F-7...
          ....|.|...
          ..F-J.|...
          ..|...|...
          S-J...L-7.
          |.......|.
          |.F-----J.
          |.|.......
          L-J.......
          ..........
        """.trimIndent()
      ),
      row(
        exampleMap3,
        """
          ....F-7...
          ....|.|...
          ..F-J.|...
          ..|...|...
          S-J...L-7.
          |.......|.
          |.F-----J.
          |.|.......
          L-J.......
          ..........
        """.trimIndent()
      )
    ).forAll { originalRawMap, enlargedRawMap ->
      test(""" Given $originalRawMap, then the enlarged map should be $enlargedRawMap""") {
        val originalMap = originalRawMap.toPipeMap()
        originalMap.enlargeMap() shouldBe enlargedRawMap
      }
    }
  }

  context("Calculating the number of steps to farthest loop point on enlarged maps") {
    table(
      headers("Title", "Map", "Number of steps"),
      row("example map 1", exampleMap1, 8),
      row("example map 2", exampleMap2, 16),
      row("example map 3", exampleMap3, 16),
      row("custom input map", readInput("day10p1"), 13198)
    ).forAll { title, rawMap, numberOfSteps ->
      test(""" Given enlarged $title, then the number of steps to get to the farthest point of the loop should be $numberOfSteps""") {
        rawMap.toPipeMap().enlargeMap().toPipeMap().findNumberOfStepsToFarthestPointInLoop() shouldBe numberOfSteps
      }
    }
  }

  context(" Mark inside/outside regions in the enlarged map ") {
    table(
      headers("Map", "Enlarged map with region marks"),
      row(
        exampleMap1,
        """
OOOOOOOOOO
OOOOOOOOOO
OOS---7OOO
OO|III|OOO
OO|III|OOO
OO|III|OOO
OOL---JOOO
OOOOOOOOOO
OOOOOOOOOO
OOOOOOOOOO
        """.trimIndent()
      ),
      row(
        exampleMap2,
        """
OOOOF-7OOO
OOOO|I|OOO
OOF-JI|OOO
OO|III|OOO
S-JIIIL-7O
|IIIIIII|O
|IF-----JO
|I|OOOOOOO
L-JOOOOOOO
OOOOOOOOOO
        """.trimIndent()
      ),
      row(
        exampleMap3,
        """
OOOOF-7OOO
OOOO|I|OOO
OOF-JI|OOO
OO|III|OOO
S-JIIIL-7O
|IIIIIII|O
|IF-----JO
|I|OOOOOOO
L-JOOOOOOO
OOOOOOOOOO
        """.trimIndent()
      ),
      row(
        """
          ...........
          .S-------7.
          .|F-----7|.
          .||.....||.
          .||.....||.
          .|L-7.F-J|.
          .|..|.|..|.
          .L--J.L--J.
          ...........
        """.trimIndent(),
        """
OOOOOOOOOOOOOOOOOOOOOO
OOOOOOOOOOOOOOOOOOOOOO
OOS---------------7OOO
OO|IIIIIIIIIIIIIII|OOO
OO|IF-----------7I|OOO
OO|I|OOOOOOOOOOO|I|OOO
OO|I|OOOOOOOOOOO|I|OOO
OO|I|OOOOOOOOOOO|I|OOO
OO|I|OOOOOOOOOOO|I|OOO
OO|I|OOOOOOOOOOO|I|OOO
OO|IL---7OOOF---JI|OOO
OO|IIIII|OOO|IIIII|OOO
OO|IIIII|OOO|IIIII|OOO
OO|IIIII|OOO|IIIII|OOO
OOL-----JOOOL-----JOOO
OOOOOOOOOOOOOOOOOOOOOO
OOOOOOOOOOOOOOOOOOOOOO
OOOOOOOOOOOOOOOOOOOOOO
        """.trimIndent()
      ),
      row(
        """
          ..........
          .S------7.
          .|F----7|.
          .||....||.
          .||....||.
          .|L-7F-J|.
          .|..||..|.
          .L--JL--J.
          ..........
        """.trimIndent(),
        """
OOOOOOOOOOOOOOOOOOOO
OOOOOOOOOOOOOOOOOOOO
OOS-------------7OOO
OO|IIIIIIIIIIIII|OOO
OO|IF---------7I|OOO
OO|I|OOOOOOOOO|I|OOO
OO|I|OOOOOOOOO|I|OOO
OO|I|OOOOOOOOO|I|OOO
OO|I|OOOOOOOOO|I|OOO
OO|I|OOOOOOOOO|I|OOO
OO|IL---7OF---JI|OOO
OO|IIIII|O|IIIII|OOO
OO|IIIII|O|IIIII|OOO
OO|IIIII|O|IIIII|OOO
OOL-----JOL-----JOOO
OOOOOOOOOOOOOOOOOOOO
OOOOOOOOOOOOOOOOOOOO
OOOOOOOOOOOOOOOOOOOO
        """.trimIndent()
      ),
      row(
        """
          .F----7F7F7F7F-7....
          .|F--7||||||||FJ....
          .||.FJ||||||||L7....
          FJL7L7LJLJ||LJ.L-7..
          L--J.L7...LJS7F-7L7.
          ....F-J..F7FJ|L7L7L7
          ....L7.F7||L7|.L7L7|
          .....|FJLJ|FJ|F7|.LJ
          ....FJL-7.||.||||...
          ....L---J.LJ.LJLJ...
        """.trimIndent(),
        """
OOF---------7OF-7OF-7OF-7OF---7OOOOOOOOO
OO|IIIIIIIII|O|I|O|I|O|I|O|III|OOOOOOOOO
OO|IF-----7I|O|I|O|I|O|I|O|IF-JOOOOOOOOO
OO|I|OOOOO|I|O|I|O|I|O|I|O|I|OOOOOOOOOOO
OO|I|OOOF-JI|O|I|O|I|O|I|O|IL-7OOOOOOOOO
OO|I|OOO|III|O|I|O|I|O|I|O|III|OOOOOOOOO
F-JIL-7OL-7IL-JIL-JI|O|IL-JIIIL---7OOOOO
|IIIII|OOO|IIIIIIIII|O|IIIIIIIIIII|OOOOO
L-----JOOOL-7IIIIIIIL-JIS-7IF---7IL-7OOO
OOOOOOOOOOOO|IIIIIIIIIII|O|I|OOO|III|OOO
OOOOOOOOF---JIIIIIF-7IF-JO|IL-7OL-7IL-7O
OOOOOOOO|IIIIIIIII|O|I|OOO|III|OOO|III|O
OOOOOOOOL-7IIIF-7I|O|IL-7O|IIIL-7OL-7I|O
OOOOOOOOOO|III|O|I|O|III|O|IIIII|OOO|I|O
OOOOOOOOOO|IF-JOL-JO|IF-JO|IF-7I|OOOL-JO
OOOOOOOOOO|I|OOOOOOO|I|OOO|I|O|I|OOOOOOO
OOOOOOOOF-JIL---7OOO|I|OOO|I|O|I|OOOOOOO
OOOOOOOO|IIIIIII|OOO|I|OOO|I|O|I|OOOOOOO
OOOOOOOOL-------JOOOL-JOOOL-JOL-JOOOOOOO
OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
        """.trimIndent()
      ),
      row(
        """
          FF7FSF7F7F7F7F7F---7
          L|LJ||||||||||||F--J
          FL-7LJLJ||||||LJL-77
          F--JF--7||LJLJ7F7FJ-
          L---JF-JLJ.||-FJLJJ7
          |F|F-JF---7F7-L7L|7|
          |FFJF7L7F-JF7|JL---7
          7-L-JL7||F7|L7F-7F7|
          L.L7LFJ|||||FJL7||LJ
          L7JLJL-JLJLJL--JLJ.L
        """.trimIndent(),
        """
OOF-7OF-SOF-7OF-7OF-7OF-7OF-7OF-------7O
OO|I|O|I|O|I|O|I|O|I|O|I|O|I|O|IIIIIII|O
OO|IL-JI|O|I|O|I|O|I|O|I|O|I|O|IF-----JO
OO|IIIII|O|I|O|I|O|I|O|I|O|I|O|I|OOOOOOO
OOL---7IL-JIL-JI|O|I|O|I|O|IL-JIL---7OOO
OOOOOO|IIIIIIIII|O|I|O|I|O|IIIIIIIII|OOO
F-----JIF-----7I|O|IL-JIL-JIIIF-7IF-JOOO
|IIIIIII|OOOOO|I|O|IIIIIIIIIII|O|I|OOOOO
L-------JOF---JIL-JIIIIIIIIIF-JOL-JOOOOO
OOOOOOOOOO|IIIIIIIIIIIIIIIII|OOOOOOOOOOO
OOOOOOF---JIF-------7IIIIIIIL-7OOOOOOOOO
OOOOOO|IIIII|OOOOOOO|IIIIIIIII|OOOOOOOOO
OOOOF-JIF-7IL-7OF---JIF-7IIIIIL-------7O
OOOO|III|O|III|O|IIIII|O|IIIIIIIIIIIII|O
OOOOL---JOL-7I|O|IF-7I|OL-7IF---7IF-7I|O
OOOOOOOOOOOO|I|O|I|O|I|OOO|I|OOO|I|O|I|O
OOOOOOOOOOF-JI|O|I|O|I|OF-JIL-7O|I|OL-JO
OOOOOOOOOO|III|O|I|O|I|O|IIIII|O|I|OOOOO
OOOOOOOOOOL---JOL-JOL-JOL-----JOL-JOOOOO
OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
        """.trimIndent()
      )
    ).forAll { originalRawMap, enlargedAndMarkedMap ->
      test(""" Given $originalRawMap, then the enlarged and marked map should be $enlargedAndMarkedMap""") {
        val originalMap = originalRawMap.toPipeMap()
        originalMap.enlargeMap().toPipeMap().markRegionsOnly() shouldBe enlargedAndMarkedMap
      }
    }
  }


  context(" Mark inside/outside regions in the map ") {
    table(
      headers("Map", "Map with region marks"),
      row(
        exampleMap1,
        """
OOOOO
OS-7O
O|I|O
OL-JO
OOOOO
        """.trimIndent()
      ),
      row(
        exampleMap2,
        """
OOF7O
OFJ|O
SJIL7
|F--J
LJOOO
        """.trimIndent()
      ),
      row(
        exampleMap3,
        """
OOF7O
OFJ|O
SJIL7
|F--J
LJOOO
        """.trimIndent()
      ),
      row(
        """
          ...........
          .S-------7.
          .|F-----7|.
          .||.....||.
          .||.....||.
          .|L-7.F-J|.
          .|..|.|..|.
          .L--J.L--J.
          ...........
        """.trimIndent(),
        """
OOOOOOOOOOO
OS-------7O
O|F-----7|O
O||OOOOO||O
O||OOOOO||O
O|L-7OF-J|O
O|II|O|II|O
OL--JOL--JO
OOOOOOOOOOO
        """.trimIndent()
      ),
      row(
        """
          ..........
          .S------7.
          .|F----7|.
          .||....||.
          .||....||.
          .|L-7F-J|.
          .|..||..|.
          .L--JL--J.
          ..........
        """.trimIndent(),
        """
OOOOOOOOOO
OS------7O
O|F----7|O
O||OOOO||O
O||OOOO||O
O|L-7F-J|O
O|II||II|O
OL--JL--JO
OOOOOOOOOO
        """.trimIndent()
      ),
      row(
        """
          .F----7F7F7F7F-7....
          .|F--7||||||||FJ....
          .||.FJ||||||||L7....
          FJL7L7LJLJ||LJ.L-7..
          L--J.L7...LJS7F-7L7.
          ....F-J..F7FJ|L7L7L7
          ....L7.F7||L7|.L7L7|
          .....|FJLJ|FJ|F7|.LJ
          ....FJL-7.||.||||...
          ....L---J.LJ.LJLJ...
        """.trimIndent(),
        """
OF----7F7F7F7F-7OOOO
O|F--7||||||||FJOOOO
O||OFJ||||||||L7OOOO
FJL7L7LJLJ||LJIL-7OO
L--JOL7IIILJS7F-7L7O
OOOOF-JIIF7FJ|L7L7L7
OOOOL7IF7||L7|IL7L7|
OOOOO|FJLJ|FJ|F7|OLJ
OOOOFJL-7O||O||||OOO
OOOOL---JOLJOLJLJOOO
        """.trimIndent()
      ),
      row(
        """
          FF7FSF7F7F7F7F7F---7
          L|LJ||||||||||||F--J
          FL-7LJLJ||||||LJL-77
          F--JF--7||LJLJ7F7FJ-
          L---JF-JLJ.||-FJLJJ7
          |F|F-JF---7F7-L7L|7|
          |FFJF7L7F-JF7|JL---7
          7-L-JL7||F7|L7F-7F7|
          L.L7LFJ|||||FJL7||LJ
          L7JLJL-JLJLJL--JLJ.L
        """.trimIndent(),
        """
OF7FSF7F7F7F7F7F---7
O|LJ||||||||||||F--J
OL-7LJLJ||||||LJL-7O
F--JF--7||LJLJIF7FJO
L---JF-JLJIIIIFJLJOO
OOOF-JF---7IIIL7OOOO
OOFJF7L7F-JF7IIL---7
OOL-JL7||F7|L7F-7F7|
OOOOOFJ|||||FJL7||LJ
OOOOOL-JLJLJL--JLJOO
        """.trimIndent()
      )
    ).forAll { rawMap, markedMap ->
      test(""" Given $rawMap, then the enlarged and marked map should be $markedMap""") {
        val map = rawMap.toPipeMap()
        map.markRegions() shouldBe markedMap
      }
    }
  }


  context(" Tiles enclosed by the loop ") {
    table(
      headers("Title", "Map", "Number of enclosed tiles"),
      row("example map 1", exampleMap1, 1),
      row("example map 2", exampleMap2, 1),
      row("example map 3", exampleMap3, 1),
      row(
        "example map 4",
        """
          ...........
          .S-------7.
          .|F-----7|.
          .||.....||.
          .||.....||.
          .|L-7.F-J|.
          .|..|.|..|.
          .L--J.L--J.
          ...........
        """.trimIndent(),
        4
      ),
      row(
        "example map 5",
        """
          ..........
          .S------7.
          .|F----7|.
          .||....||.
          .||....||.
          .|L-7F-J|.
          .|..||..|.
          .L--JL--J.
          ..........
        """.trimIndent(),
        4
      ),
      row(
        "example map 6",
        """
          .F----7F7F7F7F-7....
          .|F--7||||||||FJ....
          .||.FJ||||||||L7....
          FJL7L7LJLJ||LJ.L-7..
          L--J.L7...LJS7F-7L7.
          ....F-J..F7FJ|L7L7L7
          ....L7.F7||L7|.L7L7|
          .....|FJLJ|FJ|F7|.LJ
          ....FJL-7.||.||||...
          ....L---J.LJ.LJLJ...
        """.trimIndent(),
        8
      ),
      row(
        "example map 7",
        """
          FF7FSF7F7F7F7F7F---7
          L|LJ||||||||||||F--J
          FL-7LJLJ||||||LJL-77
          F--JF--7||LJLJ7F7FJ-
          L---JF-JLJ.||-FJLJJ7
          |F|F-JF---7F7-L7L|7|
          |FFJF7L7F-JF7|JL---7
          7-L-JL7||F7|L7F-7F7|
          L.L7LFJ|||||FJL7||LJ
          L7JLJL-JLJLJL--JLJ.L
        """.trimIndent(),
        10
      ),
      row(
        "custom input map",
        readInput("day10p1"),
        477
      )
    ).forAll { title, rawMap, numberOfTiles ->
      test(""" Given $title, then the number of enclosed tiles should be $numberOfTiles""") {
        val map = rawMap.toPipeMap()
        map.countNumberOfEnclosedTiles() shouldBe numberOfTiles
      }
    }
  }
})
