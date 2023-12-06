package day06p1

private val intRegex = """(\d+)""".toRegex()
fun String.toIntList(): List<Int> =
  intRegex.findAll(this).map { it.value.toInt() }.toList()

fun String.toRecords(): Records {
  val lines = lines()
  require(lines.size == 2) { lines.size }
  val raceDurations = lines[0].toIntList()
  val recordDistances = lines[1].toIntList()
  require(raceDurations.size == recordDistances.size)
  return Records(
    raceDurations.zip(recordDistances)
      .map { (raceDuration, recordDistance) -> Record(raceDuration, recordDistance) }
      .toSet()
  )
}

data class Records(val records: Set<Record>) {
  fun productOfNumberOfWaysToBeatRecord(): Int =
    records.asSequence().map { it.calcNumberOfWaysToBeatRecord() }.reduce { acc, i -> acc * i }
}

data class Record(val raceDuration: Int, val recordDistance: Int) {
  fun calcDistance(buttonHoldTime: Int): Int = buttonHoldTime * (raceDuration - buttonHoldTime)
  fun calcNumberOfWaysToBeatRecord(): Int = (0..raceDuration).count { calcDistance(it) > recordDistance }
}
