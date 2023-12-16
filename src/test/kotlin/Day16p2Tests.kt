package day16p2

import day16p2.Direction.*
import day16p2.TileType.*
import day16p2.TileType.Companion.toTileType
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import io.readInput

class Day16p2Tests : FunSpec({

  val exampleContraption = """
.|...\....
|.-.\.....
.....|-...
........|.
..........
.........\
..../.\\..
.-.-/..|..
.|....-|.\
..//.|....
  """.trimIndent()

  val exampleContraptionWithLightBeams = """
>|<<<\....
|v-.\^....
.v...|->>>
.v...v^.|.
.v...v^...
.v...v^..\
.v../2\\..
<->-/vv|..
.|<<<2-|.\
.v//.|.v..
  """.trimIndent()

  val exampleEnergizedTiles = """
######....
.#...#....
.#...#####
.#...##...
.#...##...
.#...##...
.#..####..
########..
.#######..
.#...#.#..
  """.trimIndent()

  val exampleMaxContraptionWithLightBeams = """
.|<2<\....
|v-v\^....
.v.v.|->>>
.v.v.v^.|.
.v.v.v^...
.v.v.v^..\
.v.v/2\\..
<-2-/vv|..
.|<<<2-|.\
.v//.|.v..
  """.trimIndent()

  val exampleMaxEnergizedTiles = """
.#####....
.#.#.#....
.#.#.#####
.#.#.##...
.#.#.##...
.#.#.##...
.#.#####..
########..
.#######..
.#...#.#..
  """.trimIndent()


  context("testing light directions and mirrors/splitters") {
    table(
      headers("Light Goes...", ". Empty Space", "/ Mirror", "\\ Mirror", "| Splitter", "- Splitter"),
      row(Right, setOf(Right), setOf(Up), setOf(Down), setOf(Up, Down), setOf(Right)),
      row(Left, setOf(Left), setOf(Down), setOf(Up), setOf(Up, Down), setOf(Left)),
      row(Up, setOf(Up), setOf(Right), setOf(Left), setOf(Up), setOf(Left, Right)),
      row(Down, setOf(Down), setOf(Left), setOf(Right), setOf(Down), setOf(Left, Right))
    ).forAll { direction, onEmptySpace, onSlashMirror, onBackslashMirror, onVerticalSpltiter, onHorizontalSplitter ->
      test(""" Given light going $direction then directions should be $onEmptySpace (on empty space), $onSlashMirror (on slash mirror), $onBackslashMirror (on backslash mirror), $onVerticalSpltiter (on vertical splitter), $onHorizontalSplitter (on horizontal splitter)""") {
        EmptySpace.directLightGoing(direction) shouldBe onEmptySpace
        SlashMirror.directLightGoing(direction) shouldBe onSlashMirror
        BackslashMirror.directLightGoing(direction) shouldBe onBackslashMirror
        VerticalSplitter.directLightGoing(direction) shouldBe onVerticalSpltiter
        HorizontalSplitter.directLightGoing(direction) shouldBe onHorizontalSplitter
      }
    }
  }

  context("parsing tile types") {
    table(
      headers("Tile Sign", "Tile Type"),
      row('.', EmptySpace),
      row('/', SlashMirror),
      row('\\', BackslashMirror),
      row('|', VerticalSplitter),
      row('-', HorizontalSplitter),
    ).forAll { sign, tileType ->
      test(""" Given tile sign $sign then tile type should be $tileType""") {
        sign.toTileType() shouldBe tileType
      }
    }
  }

  test(""" Given the example contraction when parsed then it looks as expected """) {
    val contraption = exampleContraption.toContraption()

    contraption.row(0).map { it.type } shouldBe listOf(
      EmptySpace,
      VerticalSplitter,
      EmptySpace,
      EmptySpace,
      EmptySpace,
      BackslashMirror,
      EmptySpace,
      EmptySpace,
      EmptySpace,
      EmptySpace
    )

    contraption.toText() shouldBe exampleContraption
  }

  test(""" Given the example contraption when parsed and the light beam simulated then the light beams and energized tiles should look as expected """) {
    val contraption = exampleContraption.toContraption()
    contraption.simulateLightBeam()

    contraption.toTextWithLightBeams() shouldBe exampleContraptionWithLightBeams

    contraption.toTextWithEnergizedTiles() shouldBe exampleEnergizedTiles
  }

  test(""" Given the example contraption when parsed and light beams simulated then the number of energized tiles should be 46 """) {
    exampleContraption.countEnergizedTiles() shouldBe 46
  }

  test(""" Given the custom contraption when parsed and light beams simulated then the number of energized tiles should be 8539 """) {
    readInput("day16p1").countEnergizedTiles() shouldBe 8539
  }

  test(""" Given the example contraption when parsed and the light beam simulated from (Down, 0, 3) then the light beams and energized tiles should look as expected """) {
    val contraption = exampleContraption.toContraption()
    contraption.simulateLightBeam(Down, 0, 3)

    contraption.toTextWithLightBeams() shouldBe exampleMaxContraptionWithLightBeams

    contraption.toTextWithEnergizedTiles() shouldBe exampleMaxEnergizedTiles

    contraption.countEnergizedTiles() shouldBe 51
  }

  test(""" Given the example contraption when parsed and light beams simulated then the max number of energized tiles should be 51 """) {
    exampleContraption.findMaxCountEnergizedTiles() shouldBe 51
  }

  test(""" Given the custom contraption when parsed and light beams simulated then the max number of energized tiles should be 8674 """) {
    readInput("day16p1").findMaxCountEnergizedTiles() shouldBe 8674
  }
})
