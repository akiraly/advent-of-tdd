package day06p2

fun String.toRecord(): Record {
  val lines = lines()
  require(lines.size == 2) { lines.size }
  return Record(
    raceDuration = lines[0].toSingleNumber(),
    recordDistance = lines[1].toSingleNumber()
  )
}

fun String.toSingleNumber(): Long =
  this.asSequence().filter { it.isDigit() }.joinToString("").toLong()

data class Record(val raceDuration: Long, val recordDistance: Long) {
  fun calcDistance(buttonHoldTime: Long): Long = buttonHoldTime * (raceDuration - buttonHoldTime)
  fun calcNumberOfWaysToBeatRecord(): Int = (0..raceDuration).count { calcDistance(it) > recordDistance }
}
