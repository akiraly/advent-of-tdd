package day09p2

fun List<Long>.predictPreviousValue(): Long {
  var result = first()
  var mult = -1
  var current = this
  while (!current.isAllZero()) {
    current = current.diffSeries()
    result += mult * current.first()
    mult *= -1
  }
  return result
}

fun List<Long>.predictNextValue(): Long {
  var result = last()
  var current = this
  while (!current.isAllZero()) {
    current = current.diffSeries()
    result += current.last()
  }
  return result
}

fun List<Long>.isAllZero(): Boolean = all { it == 0L }

fun List<Long>.diffSeries(): List<Long> = windowed(2).map { it[1] - it[0] }

fun String.toOASISReport(): OASISReport = OASISReport(lines().map { it.toHistory() })

data class OASISReport(val entries: List<History>) {
  fun sumOfPredictedNextValues(): Long = entries.sumOf { it.predictNextValue() }
  fun sumOfPredictedPreviousValues(): Long = entries.sumOf { it.predictPreviousValue() }
}

fun String.toHistory(): History = History(
  """(-?\d+)""".toRegex().findAll(this).map { it.value.toLong() }.toList()
)

data class History(val entries: List<Long>) {
  fun predictNextValue(): Long = entries.predictNextValue()
  fun predictPreviousValue(): Long = entries.predictPreviousValue()
}
