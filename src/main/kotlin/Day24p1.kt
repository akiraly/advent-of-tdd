package day24p1

import org.apache.commons.geometry.euclidean.twod.Vector2D

fun List<Hailstone>.findNumberOfInteractions(testRange: ClosedFloatingPointRange<Double>): Long {
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
