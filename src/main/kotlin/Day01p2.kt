package day01p2


data class CalibrationValue(val value: Int)

private val digits = (
        generateSequence(1) { it + 1 }.takeWhile { it < 10 }.map { it.toString() to it } +
                sequenceOf(
                    "one" to 1,
                    "two" to 2,
                    "three" to 3,
                    "four" to 4,
                    "five" to 5,
                    "six" to 6,
                    "seven" to 7,
                    "eight" to 8,
                    "nine" to 9
                )
        ).toMap()

data class CalibrationDocumentLine(val value: String) {
    fun toCalibrationValue(): CalibrationValue = CalibrationValue(
        10 * digits.getValue(value.findAnyOf(digits.keys)!!.second)
                + digits.getValue(value.findLastAnyOf(digits.keys)!!.second)
    )
}

data class CalibrationDocument(val values: List<CalibrationDocumentLine>) {
    fun sumOfCalibrationValues(): Int = values.sumOf { it.toCalibrationValue().value }

    companion object {
        fun from(raw: String): CalibrationDocument =
            CalibrationDocument(raw.lines().map { CalibrationDocumentLine(it) })
    }
}
