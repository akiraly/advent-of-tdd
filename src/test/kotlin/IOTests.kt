package io

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class IOTests : FunSpec({
    test("given a file, when calling readInput() it should return the content of the file") {
        readInput("io_example") shouldBe """
            1abc2
            pqr3stu8vwx
            a1b2c3d4e5f
            treb7uchet
        """.trimIndent()
    }
})
