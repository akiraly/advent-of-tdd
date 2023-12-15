package day15p2

import day15p2.Step.AddLens
import day15p2.Step.RemoveLens
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.readInput

class Day15p2Tests : FunSpec({
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

  val exampleSteps = mapOf(
    "rn=1" to AddLens(Lens("rn", 0), 1),
    "cm-" to RemoveLens(Lens("cm", 0)),
    "qp=3" to AddLens(Lens("qp", 1), 3),
    "cm=2" to AddLens(Lens("cm", 0), 2),
    "qp-" to RemoveLens(Lens("qp", 1)),
    "pc=4" to AddLens(Lens("pc", 3), 4),
    "ot=9" to AddLens(Lens("ot", 3), 9),
    "ab=5" to AddLens(Lens("ab", 3), 5),
    "pc-" to RemoveLens(Lens("pc", 3)),
    "pc=6" to AddLens(Lens("pc", 3), 6),
    "ot=7" to AddLens(Lens("ot", 3), 7)
  )

  context(""" Tests with exampleInitSeqInput """) {
    val steps = exampleInitSeqInput.parseSteps()

    test(""" Given the example input of "$exampleInitSeqInput" when parsing the init sequence the steps then the result is ${exampleSteps.values} """) {
      steps shouldBe exampleSteps.values
    }

    steps.forEach { step ->
      val stepAsText = step.toText()
      val expectedStep = exampleSteps.getValue(stepAsText)

      step shouldBe expectedStep

      test(""" Given the lens label of "$stepAsText" when running HASH("${step.lens.label}") then the result is ${expectedStep.lens.hash} """) {
        HASH(step.lens.label) shouldBe expectedStep.lens.hash
      }
    }

  }

  test(""" Given the example init sequence when processing the steps the total focusing power is 145 """) {
    Boxes().processInitSeq(exampleInitSeqInput) shouldBe 145
  }

  test(""" Given the custom init sequence when processing the steps the total focusing power is 231844 """) {
    Boxes().processInitSeq(readInput("day15p1")) shouldBe 231844
  }
})
