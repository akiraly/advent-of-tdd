package day01p1

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.readInput

class Day01p1Tests : FunSpec({
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

    test("given a calibration document line 1abc2, when calling toCalibrationValue() it should return 12") {
        CalibrationDocumentLine("1abc2").toCalibrationValue() shouldBe CalibrationValue(12)
    }

    test("given a calibration document line pqr3stu8vwx, when calling toCalibrationValue() it should return 38") {
        CalibrationDocumentLine("pqr3stu8vwx").toCalibrationValue() shouldBe CalibrationValue(38)
    }

    test("given a calibration document line a1b2c3d4e5f, when calling toCalibrationValue() it should return 15") {
        CalibrationDocumentLine("a1b2c3d4e5f").toCalibrationValue() shouldBe CalibrationValue(15)
    }

    test("given a calibration document line treb7uchet, when calling toCalibrationValue() it should return 77") {
        CalibrationDocumentLine("treb7uchet").toCalibrationValue() shouldBe CalibrationValue(77)
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

    test("given the custom calibration document, when calling the sumOfCalibrationValues() it should return 55621") {
        CalibrationDocument.from(
            readInput("day01p1")
        ).sumOfCalibrationValues() shouldBe 55621
    }
})
