package day24p2

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.readInput
import org.apache.commons.geometry.euclidean.twod.Vector2D
import org.apache.commons.numbers.core.Precision


class Day24p2Tests : FunSpec({
  val exampleInput = """
    19, 13, 30 @ -2,  1, -2
    18, 19, 22 @ -1, -1, -2
    20, 25, 34 @ -2, -2, -4
    12, 31, 28 @ -1, -2, -1
    20, 19, 15 @  1, -5, -3
  """.trimIndent()
  val exampleTestRange = 7.0..27.0

  val customInput = readInput("day24p1")
  val customTestRange = 200_000_000_000_000.0..400_000_000_000_000.0

  context("parsing") {

    test(""" Given an example hailstone as 19, 13, 30 @ -2, 1, -2 then it should be parsed correctly """) {
      "19, 13, 30 @ -2, 1, -2".toHailstone() shouldBe Hailstone(19, 13, 30, -2, 1, -2)
    }

    test("""Given the example input then the list of hailstones should be parsed correctly""") {
      val hailstones = exampleInput.toHailstones()
      hailstones shouldBe listOf(
        Hailstone(19, 13, 30, -2, 1, -2),
        Hailstone(18, 19, 22, -1, -1, -2),
        Hailstone(20, 25, 34, -2, -2, -4),
        Hailstone(12, 31, 28, -1, -2, -1),
        Hailstone(20, 19, 15, 1, -5, -3)
      )
    }

    test("""Given the custom input then the list of hailstones should be parsed correctly""") {
      val hailstones = customInput.toHailstones()
      hailstones shouldHaveSize 300
      hailstones.first() shouldBe Hailstone(260346828765750, 357833641339849, 229809969824403, 64, -114, 106)
    }
  }

  context("test 2d intersection") {
    table(
      headers("Hailstone A", "Hailstone B", "Intersection", "Intersection in test range"),
      row("19, 13, 30 @ -2, 1, -2", "18, 19, 14 @ -1, -1, -1", "(14.333, 15.333)", "(14.333, 15.333)"),
      row("19, 13, 30 @ -2, 1, -2", "20, 25, 34 @ -2, -2, -4", "(11.667, 16.667)", "(11.667, 16.667)"),
      row("19, 13, 30 @ -2, 1, -2", "12, 31, 28 @ -1, -2, -1", "(6.2, 19.4)", null),
      row("19, 13, 30 @ -2, 1, -2", "20, 19, 15 @ 1, -5, -3", null, null),
      row("18, 19, 22 @ -1, -1, -2", "20, 25, 34 @ -2, -2, -4", null, null),
      row("18, 19, 22 @ -1, -1, -2", "12, 31, 28 @ -1, -2, -1", "(-6.0, -5.0)", null),
      row("18, 19, 22 @ -1, -1, -2", "20, 19, 15 @ 1, -5, -3", null, null),
      row("20, 25, 34 @ -2, -2, -4", "12, 31, 28 @ -1, -2, -1", "(-2.0, 3.0)", null),
      row("20, 25, 34 @ -2, -2, -4", "20, 19, 15 @ 1, -5, -3", null, null),
      row("12, 31, 28 @ -1, -2, -1", "20, 19, 15 @ 1, -5, -3", null, null),
    ).forAll { hailstoneA, hailstoneB, expectedIntersection, expectedIntersectionInTestRange ->
      val hailA = hailstoneA.toHailstone()
      val hailB = hailstoneB.toHailstone()
      val precision = Precision.doubleEquivalenceOfEpsilon(1e-3)
      test("""Given the hailstone A $hailstoneA and hailstone B $hailstoneB then the intersection should be $expectedIntersection""") {
        val intersection: Vector2D? = hailA.intersection(hailB)
        val reversed = hailB.intersection(hailA)
        if (expectedIntersection != null) {
          requireNotNull(intersection)
          intersection.eq(
            Vector2D.parse(expectedIntersection),
            precision
          ) shouldBe true
          intersection.eq(
            reversed,
            precision
          ) shouldBe true
        } else {
          intersection shouldBe null
          reversed shouldBe null
        }
      }

      test("""Given the hailstone A $hailstoneA and hailstone B $hailstoneB then the intersection in test range $exampleTestRange should be $expectedIntersectionInTestRange""") {
        val intersection: Vector2D? = hailA.intersection(hailB, exampleTestRange)
        val reversed = hailB.intersection(hailA, exampleTestRange)
        if (expectedIntersectionInTestRange != null) {
          requireNotNull(intersection)
          intersection.eq(
            Vector2D.parse(expectedIntersectionInTestRange),
            precision
          ) shouldBe true
          intersection.eq(
            reversed,
            precision
          ) shouldBe true
        } else {
          intersection shouldBe null
          reversed shouldBe null
        }
      }
    }

    test(""" Given the example hailstone list and test range then the number of intersections should be 2 """) {
      val hailstones = exampleInput.toHailstones()

      hailstones.findNumberOfInteractions(exampleTestRange) shouldBe 2
    }

    test(""" Given the custom hailstone list and test range then the number of intersections should be 20434 """) {
      val hailstones = customInput.toHailstones()

      hailstones.findNumberOfInteractions(customTestRange) shouldBe 20434
    }
  }
})

private fun List<Hailstone>.findNumberOfInteractions(testRange: ClosedFloatingPointRange<Double>): Long {
  var count = 0L
  for (i in 0 until size - 1) {
    for (j in i + 1 until size) {
      val intersection = this[i].intersection(this[j], testRange)
      if (intersection != null) {
        count++
      }
    }
  }
  return count
}

fun String.toHailstones(): List<Hailstone> = lineSequence().map { it.toHailstone() }.toList()

fun String.toHailstone(): Hailstone {
  val nums = split(',', '@').map { it.trim().toLong() }
  return Hailstone(nums[0], nums[1], nums[2], nums[3], nums[4], nums[5])
}

data class Hailstone(val px: Long, val py: Long, val pz: Long, val vx: Long, val vy: Long, val vz: Long) {

  fun intersection(o: Hailstone): Vector2D? {
    val area = vx.toDouble() * o.vy - vy * o.vx
    if (area == 0.0) return null

    val t1 = ((py - o.py) * o.vx - (px - o.px) * o.vy) / area

    val t2 = (px - o.px + t1 * vx) / o.vx

    if (t1 < 0 || t2 < 0) return null

    return Vector2D.of(px + t1 * vx, py + t1 * vy)
  }

  fun intersection(other: Hailstone, testRange: ClosedFloatingPointRange<Double>): Vector2D? =
    intersection(other)?.let {
      if (it.x in testRange && it.y in testRange) it
      else null
    }
}
