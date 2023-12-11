package day11p2

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import io.readInput


class Day11p2Tests : FunSpec({
  val exampleImage = """
    ...#......
    .......#..
    #.........
    ..........
    ......#...
    .#........
    .........#
    ..........
    .......#..
    #...#.....
  """.trimIndent()

  test("Given the example image then it should be parsed as an expected Universe") {
    exampleImage.toUniverse() shouldBe Universe(
      numOfRows = 10,
      numOfColumns = 10,
      galaxies = setOf(
        0 to 3,
        1 to 7,
        2 to 0,
        4 to 6,
        5 to 1,
        6 to 9,
        8 to 7,
        9 to 0,
        9 to 4,
      ).asSequence().map { Galaxy(Coordinate(it.first, it.second)) }.toSet()
    )
  }

  test("Given the example image when parsed as an Universe and converted back to an image then we should get back the original image") {
    exampleImage.toUniverse().toImage() shouldBe exampleImage
  }

  test("Given the example image the expanded universe looks as expected") {
    exampleImage.toUniverse().toExpandedUniverse().toImage() shouldBe """
....#........
.........#...
#............
.............
.............
........#....
.#...........
............#
.............
.............
.........#...
#....#.......
    """.trimIndent()
  }

  context("Distance between galaxies") {
    table(
      headers("Coordinate 1", "Coordinate 2", "Distance"),
      row(Coordinate(0, 0), Coordinate(0, 0), 0),
      row(Coordinate(0, 0), Coordinate(0, 1), 1),
      row(Coordinate(0, 0), Coordinate(1, 0), 1),
      row(Coordinate(6, 1), Coordinate(11, 5), 9),
      row(Coordinate(0, 4), Coordinate(10, 9), 15),
      row(Coordinate(2, 0), Coordinate(7, 12), 17),
      row(Coordinate(11, 0), Coordinate(11, 5), 5),
    ).forAll { c1, c2, expectedDistance ->
      val galaxy1 = Galaxy(c1)
      val galaxy2 = Galaxy(c2)
      test("Given $galaxy1 and $galaxy2 then they should have a distance of $expectedDistance") {
        galaxy2.distanceTo(galaxy1) shouldBe expectedDistance
        galaxy1.distanceTo(galaxy2) shouldBe expectedDistance
      }
    }
  }

  context("Sum of all shortest paths between galaxies") {
    table(
      headers("Title", "Universe image", "Expansion rate", "Sum of shortest paths"),
      row("Example image", exampleImage, 2, 374),
      row("Example image", exampleImage, 10, 1030),
      row("Example image", exampleImage, 100, 8410),
      row("Custom image", readInput("day11p1"), 2, 9521776),
      row("Custom image", readInput("day11p1"), 1_000_000, 553224415344),
    ).forAll { title, image, expansionRate, expectedSumOfShortestPaths ->
      test("Given $title and expansion rate = $expansionRate, then the sum of all shortest paths between galaxies should be $expectedSumOfShortestPaths") {
        image.toUniverse()
          .toExpandedUniverse(expansionRate.toLong())
          .sumOfShortestPaths() shouldBe expectedSumOfShortestPaths
      }
    }
  }
})
