package day13p2

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.readInput

class Day13p2Tests : FunSpec({
  val examplePattern1 = """
#.##..##.
..#.##.#.
##......#
##......#
..#.##.#.
..##..##.
#.#.##.#.
  """.trimIndent()

  val examplePattern2 = """
#...##..#
#....#..#
..##..###
#####.##.
#####.##.
..##..###
#....#..#
  """.trimIndent()

  val examplePatterns = """
$examplePattern1

$examplePattern2
  """.trimIndent()

  test("Given the example patterns then it can be parsed correctly as 2 patterns") {
    val patterns = examplePatterns.parsePatterns()
    patterns.size shouldBe 2
    patterns[0].lines.size shouldBe 7
    patterns[0].toPatternString() shouldBe examplePattern1
    patterns[1].toPatternString() shouldBe examplePattern2
  }

  context("test transpose") {
    test(""" Given listOf("ab") then transposed it is expected to be listOf("a", "b") """) {
      listOf("ab").transposeStrings() shouldBe listOf("a", "b")
    }

    table(
      headers("Pattern", "Transposed"),
      row(listOf(listOf('a')), listOf(listOf('a'))),
      row(listOf(listOf('a', 'b')), listOf(listOf('a'), listOf('b'))),
      row(listOf(listOf('a', 'b', 'c')), listOf(listOf('a'), listOf('b'), listOf('c'))),
    ).forAll { pattern, expected ->
      test(""" Given $pattern then transposed it is expected to be $expected """) {
        pattern.transpose() shouldBe expected
      }
    }
  }

  context("Finding mirror position in a pattern") {
    table(
      headers("Pattern", "Position"),
      rows = examplePattern1.lines().map { row -> row(row, 5) }
    ).forAll { row, expectedPosition ->
      test(""" Given a pattern of "$row" then the expected vertical mirror position is $expectedPosition """) {
        val pattern = row.toPattern()
        pattern.findVerticalMirrorPositions() shouldContain expectedPosition
      }
    }

    test(""" Given example pattern 1 then the expected (vertical) mirror position is 5 """) {
      val pattern = examplePattern1.toPattern()
      pattern.findVerticalMirrorPosition() shouldBe 5
      pattern.findHorizontalMirrorPosition() shouldBe -1
      pattern.findScoredMirrorPosition() shouldBe 5
    }

    test(""" Given example pattern 2 then the expected (horizontal) mirror position is 4 """) {
      val pattern = examplePattern2.toPattern()
      pattern.findHorizontalMirrorPosition() shouldBe 4
      pattern.findVerticalMirrorPosition() shouldBe -1
      pattern.findScoredMirrorPosition() shouldBe 400
    }

    test(""" Given example pattern 3 then the expected (vertical) mirror position is 16""") {
      val pattern = """
        ..#...####...#...
        ....#.#..#.#.....
        ..##.######.##...
        ...#.######.#....
        #.###..##...##.##
        ##..##.##.##..###
        ......####.......
        ...###.##.###....
        ....###..###.....
        .##.#......#.##..
        .#.#........#.#..
        .##...#..#...##..
        #..#.#....#.#..##
      """.trimIndent().toPattern()

      pattern.findVerticalMirrorPosition() shouldBe 16
      pattern.findHorizontalMirrorPosition() shouldBe -1
      pattern.findScoredMirrorPosition() shouldBe 16
    }

    test(""" Given example pattern 3 then the expected (vertical) mirror position is """) {
      val pattern = """
        ..#...####...#...
        ....#.#..#.#.....
        ..##.######.##...
        ...#.######.#....
        #.##...##...##.##
        ##..##.##.##..###
        ......####.......
        ...###.##.###....
        ....###..###.....
        .##.#......#.##..
        .#.#........#.#..
        .##...#..#...##..
        #..#.#....#.#..##
      """.trimIndent().toPattern()

      pattern.findVerticalMirrorPosition() shouldBe 8
      pattern.findHorizontalMirrorPosition() shouldBe -1
      pattern.findScoredMirrorPosition() shouldBe 8
    }

    table(
      headers("Title", "Patterns", "Number of patterns parsed", "Expected Score"),
      row("Example Patterns", examplePatterns, 2, 405),
      row("Custom Patterns", readInput("day13p1"), 100, 33780),
    ).forAll { title, patterns, numberOfParts, score ->
      test(""" Given $title then the expected score is $score """) {
        val parsedPatterns = patterns.parsePatterns()
        parsedPatterns.size shouldBe numberOfParts
        parsedPatterns.sumOfScoredMirrorPositions() shouldBe score
      }
    }
  }

  context("Find smudge") {
    test(""" Given example pattern 1 then after the smudge is fixed the mirror position score should be 300""") {
      val pattern = examplePattern1.toPattern()
      pattern.fixSmudgeAndFindScoredMirrorPosition() shouldBe 300
    }

    test(""" Given example pattern 2 then after the smudge is fixed the mirror position score should be 100""") {
      val pattern = examplePattern2.toPattern()
      pattern.fixSmudgeAndFindScoredMirrorPosition() shouldBe 100
    }

    table(
      headers("Title", "Patterns", "Expected Score"),
      row("Example Patterns", examplePatterns, 400),
      row("Custom Patterns", readInput("day13p1"), 23479),
    ).forAll { title, patterns, score ->
      test(""" Given $title when finding and fixing smudge then the expected score is $score """) {
        val parsedPatterns = patterns.parsePatterns()
        parsedPatterns.sumOfScoredAndSmudgeFixedMirrorPositions() shouldBe score
      }
    }
  }
})
