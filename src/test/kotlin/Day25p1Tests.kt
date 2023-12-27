package day25p1

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import io.readInput

class Day25p1Tests : FunSpec({
  val exampleInput = """
    jqt: rhn xhk nvd
    rsh: frs pzl lsr
    xhk: hfx
    cmg: qnr nvd lhk bvb
    rhn: xhk bvb hfx
    bvb: xhk hfx
    pzl: lsr hfx nvd
    qnr: nvd
    ntq: jqt hfx bvb xhk
    nvd: lhk
    lsr: lhk
    rzs: qnr cmg lsr rsh
    frs: qnr lhk lsr
  """.trimIndent()

  val customInput = readInput("day25p1")

  table(
    headers("Name", "Input", "Expected result"),
    row("example wiring diagram", exampleInput, 54),
    row("custom wiring diagram", customInput, 619225),
  ).forAll { name, diagramInput, expectedResult ->
    test(""" Given the $name then the sizes of the 2 groups multiplied together is $expectedResult """) {
      val result = solve(diagramInput)
      result shouldBe expectedResult
    }
  }
})
