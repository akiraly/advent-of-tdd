package day01p2

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.readInput

class Day01p2Tests : FunSpec({
    test("should be able to represent a calibration value") {
        CalibrationValue(15).value shouldBe 15
    }

    test("should be able to represent a line from the calibration document") {
        CalibrationDocumentLine("1abc2").value shouldBe "1abc2"
    }

    test("should be able to represent a calibration document") {
        CalibrationDocument.from(
            """
            1abc2
            pqr3stu8vwx
            a1b2c3d4e5f
            treb7uchet
        """.trimIndent()
        ).values shouldBe listOf(
            "1abc2", "pqr3stu8vwx", "a1b2c3d4e5f", "treb7uchet"
        ).map { CalibrationDocumentLine(it) }
    }

    listOf(
        "1abc2" to 12,
        "pqr3stu8vwx" to 38,
        "a1b2c3d4e5f" to 15,
        "treb7uchet" to 77
    ).forEach { (line, value) ->
        test("given a calibration document line `$line`, when calling toCalibrationValue() it should return $value") {
            CalibrationDocumentLine(line).toCalibrationValue() shouldBe CalibrationValue(value)
        }
    }

    test("given the example calibration document, when calling the sumOfCalibrationValues() it should return 142") {
        CalibrationDocument.from(
            """
            1abc2
            pqr3stu8vwx
            a1b2c3d4e5f
            treb7uchet
        """.trimIndent()
        ).sumOfCalibrationValues() shouldBe 142
    }

    test("given the custom calibration document, when calling the sumOfCalibrationValues() it should return 53592") {
        CalibrationDocument.from(
            readInput("day01p1")
        ).sumOfCalibrationValues() shouldBe 53592
    }

    listOf(
        "one" to 11,
        "oneone" to 11,
        "two1nine" to 29,
        "eightwothree" to 83,
        "abcone2threexyz" to 13,
        "xtwone3four" to 24,
        "4nineeightseven2" to 42,
        "zoneight234" to 14,
        "7pqrstsixteen" to 76,
    ).forEach { (line, value) ->
        test("given a calibration document line `$line`, when calling toCalibrationValue() it should return $value") {
            CalibrationDocumentLine(line).toCalibrationValue() shouldBe CalibrationValue(value)
        }
    }
})
