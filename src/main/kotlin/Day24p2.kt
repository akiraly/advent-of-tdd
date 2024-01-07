package day24p2

import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.math.MathContext.DECIMAL128
import java.math.RoundingMode.HALF_UP

fun calcCollisionStone(hailstones: List<Hailstone>): Hailstone {
  for (vx in -400..400) {
    val (vy, px, py) = findCollisionPointXY { vy ->
      hailstones.map { it.copy(vx = it.vx - vx, vy = it.vy - vy) }
    }
      ?.let { Triple(it.first, it.second.x, it.second.y) }
      ?: continue

    val (vz, px2, pz) = findCollisionPointXY { vz ->
      hailstones.map { it.copy(py = it.pz, vx = it.vx - vx, vy = it.vz - vz) }
    }
      ?.let { Triple(it.first, it.second.x, it.second.y) }
      ?: error("no collision point found")

    require(px == px2)

    return Hailstone(px.toLong(), py.toLong(), pz.toLong(), vx.toLong(), vy.toLong(), vz.toLong())
  }
  error("no collision point found")
}

private fun findCollisionPointXY(hailstonesFn: (Int) -> List<Hailstone>): Pair<Int, Vector2D>? {
  vy@ for (vy in -400..400) {
    var collisionPoint: Vector2D? = null
    val hailstones = hailstonesFn(vy)
    for (i in 0 until hailstones.size - 1) {
      for (j in i + 1 until hailstones.size) {
        val hi = hailstones[i]
        val hj = hailstones[j]
        val intersection = hi.intersection(hj)
        if (collisionPoint == null) {
          collisionPoint = intersection
          continue
        }

        if (intersection == null) {
          if (hi.isParallelTo(hj)) continue
          continue@vy
        }

        if (collisionPoint != intersection) {
          continue@vy
        }
      }
    }
    requireNotNull(collisionPoint)
    return vy to collisionPoint
  }
  return null
}

fun List<Hailstone>.findNumberOfInteractions(testRange: ClosedRange<BigDecimal>): Long {
  var count = 0L
  for (i in 0 until size - 1) {
    var localCount = 0L
    for (j in i + 1 until size) {
      val intersection = this[i].intersection(this[j], testRange)
      if (intersection != null) {
        count++
        localCount++
      }
    }
    if (localCount > 200) println("localCount = $localCount for i = $i")
  }
  return count
}

fun String.toHailstones(): List<Hailstone> = lineSequence().map { it.toHailstone() }.toList()

fun String.toHailstone(): Hailstone {
  val nums = split(',', '@').map { it.trim().toLong() }
  return Hailstone(nums[0], nums[1], nums[2], nums[3], nums[4], nums[5])
}

data class Hailstone(val px: Long, val py: Long, val pz: Long, val vx: Long, val vy: Long, val vz: Long) {

  fun isParallelTo(o: Hailstone): Boolean = area(
    vx.toBD(),
    vy.toBD(),
    o.vx.toBD(),
    o.vy.toBD()
  ).compareTo(ZERO) == 0

  fun intersection(o: Hailstone): Vector2D? = intersection(px, py, vx, vy, o.px, o.py, o.vx, o.vy)

  fun intersection(other: Hailstone, testRange: ClosedRange<BigDecimal>): Vector2D? =
    intersection(other)?.let {
      if (it.x in testRange && it.y in testRange) it
      else null
    }

  companion object {
    fun intersection(
      p1A: Long,
      p1B: Long,
      v1A: Long,
      v1B: Long,
      p2A: Long,
      p2B: Long,
      v2A: Long,
      v2B: Long
    ): Vector2D? = intersection(
      p1A.toBD(),
      p1B.toBD(),
      v1A.toBD(),
      v1B.toBD(),
      p2A.toBD(),
      p2B.toBD(),
      v2A.toBD(),
      v2B.toBD()
    )

    private fun intersection(
      p1A: BigDecimal,
      p1B: BigDecimal,
      v1A: BigDecimal,
      v1B: BigDecimal,
      p2A: BigDecimal,
      p2B: BigDecimal,
      v2A: BigDecimal,
      v2B: BigDecimal
    ): Vector2D? {
      if (v2A.compareTo(ZERO) == 0) return null

      val area = area(v1A, v1B, v2A, v2B)
      if (area.compareTo(ZERO) == 0) return null

      val t1 = ((p1B - p2B) * v2A - (p1A - p2A) * v2B).divide(area, DECIMAL128)
      if (t1 < ZERO) return null

      val t2 = (p1A - p2A + t1 * v1A).divide(v2A, DECIMAL128)
      if (t2 < ZERO) return null

      return Vector2D(p1A + t1 * v1A, p1B + t1 * v1B)
    }

    fun area(v1A: BigDecimal, v1B: BigDecimal, v2A: BigDecimal, v2B: BigDecimal): BigDecimal = v1A * v2B - v1B * v2A
  }
}

fun Int.toBD() = toBigDecimal(DECIMAL128)
fun Long.toBD() = toBigDecimal(DECIMAL128)
fun String.toBD() = toBigDecimal(DECIMAL128)

data class Vector2D(val x: BigDecimal, val y: BigDecimal) {

  fun eq(that: Vector2D): Boolean = this == that || this.round() == that.round()

  private fun round(): Vector2D = copy(x = x.setScale(3, HALF_UP), y = y.setScale(3, HALF_UP))

  companion object {
    fun parse(expectedIntersection: String): Vector2D {
      val (x, y) = expectedIntersection.trim('(', ')').split(',').map { it.trim().toBD() }
      return Vector2D(x, y)
    }
  }
}

