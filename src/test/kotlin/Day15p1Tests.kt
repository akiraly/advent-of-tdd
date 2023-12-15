package day15p1

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.readInput

class Day15p1Tests : FunSpec({
  context(""" Tests with HASH("HASH") """) {
    val input = "HASH"

    val individualASCIICodesAndIntermediateResults = listOf(
      Triple('H', 72, 200),
      Triple('A', 65, 153),
      Triple('S', 83, 172),
      Triple('H', 72, 52)
    )

    for (i in input.indices) {
      val partialInput = input.substring(0, i + 1)
      val expectedResult = individualASCIICodesAndIntermediateResults[i]
      test(""" Given the example input of "$partialInput" when running HASH("$partialInput") then the result is ${expectedResult.third} """) {
        partialInput[i] shouldBe expectedResult.first
        partialInput[i].code shouldBe expectedResult.second

        val actualResult = HASH(partialInput)
        actualResult shouldBe expectedResult.third
      }
    }
  }

  val exampleInitSeqInput = "rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7"
  val exampleInitSeq = listOf("rn=1", "cm-", "qp=3", "cm=2", "qp-", "pc=4", "ot=9", "ab=5", "pc-", "pc=6", "ot=7")
  val exampleInitSeqHashes = mapOf(
    "rn=1" to 30,
    "cm-" to 253,
    "qp=3" to 97,
    "cm=2" to 47,
    "qp-" to 14,
    "pc=4" to 180,
    "ot=9" to 9,
    "ab=5" to 197,
    "pc-" to 48,
    "pc=6" to 214,
    "ot=7" to 231
  )

  context(""" Tests with exampleInitSeqInput """) {
    val initSeq = exampleInitSeqInput.parseInitSeq()

    test(""" Given the example input of "$exampleInitSeqInput" when parsing the init sequence then the result is $initSeq """) {
      initSeq shouldBe exampleInitSeq
    }

    initSeq.forEach { input ->
      val expectedHash = exampleInitSeqHashes[input]
      test(""" Given the example input of "$input" when running HASH("$input") then the result is $expectedHash """) {
        HASH(input) shouldBe expectedHash
      }
    }

  }

  test(""" Given the example init sequence when running HASH for all parts the sum of the hashes is 1320 """) {
    exampleInitSeqInput.sumOfInitSeqHashes() shouldBe 1320
  }

  test(""" Given the custom init sequence when running HASH for all parts the sum of the hashes is 516804 """) {
    readInput("day15p1").sumOfInitSeqHashes() shouldBe 516804
  }
})
