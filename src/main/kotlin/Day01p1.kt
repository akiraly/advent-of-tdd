package day01p1


data class CalibrationValue(val value: Int)

data class CalibrationDocumentLine(val value: String) {
  fun toCalibrationValue(): CalibrationValue = CalibrationValue(
    10 * value.first { it.isDigit() }.digitToInt()
      + value.last { it.isDigit() }.digitToInt()
  )
}

data class CalibrationDocument(val values: List<CalibrationDocumentLine>) {
  fun sumOfCalibrationValues(): Int = values.sumOf { it.toCalibrationValue().value }

  companion object {
    fun from(raw: String): CalibrationDocument =
      CalibrationDocument(raw.lines().map { CalibrationDocumentLine(it) })
  }
}
