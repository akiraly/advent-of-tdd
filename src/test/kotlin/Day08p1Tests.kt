package day08p1

import day08p1.Instruction.Left
import day08p1.Instruction.Right
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import io.readInput

class Day08p1Tests : FunSpec({
  test("""Given a reference as "ABC" then it should be possible to represent as a NodeId""") {
    val nodeId = NodeId("ABC")
    nodeId.value shouldBe "ABC"
  }

  test("Creating a Node should be possible with plain strings as well") {
    Node(id = "AAA", left = "BBB", right = "CCC") shouldBe Node(
      id = NodeId("AAA"),
      left = NodeId("BBB"),
      right = NodeId("CCC")
    )
  }

  test("""Given a "AAA = (BBB, CCC)" then it should be possible to parse it as Node""") {
    "AAA = (BBB, CCC)".toNode() shouldBe Node(id = "AAA", left = "BBB", right = "CCC")
  }

  test(""" Given a text with a series of nodes then it should be possible to parse it as a Network""") {
    val input = """
      AAA = (BBB, CCC)
      BBB = (DDD, EEE)
      CCC = (ZZZ, GGG)
      DDD = (DDD, DDD)
      EEE = (EEE, EEE)
      GGG = (GGG, GGG)
      ZZZ = (ZZZ, ZZZ)
    """.trimIndent()
    val network = input.toNetwork()
    network shouldBe Network(nodes = mapOf(
      "AAA" to Node(id = "AAA", left = "BBB", right = "CCC"),
      "BBB" to Node(id = "BBB", left = "DDD", right = "EEE"),
      "CCC" to Node(id = "CCC", left = "ZZZ", right = "GGG"),
      "DDD" to Node(id = "DDD", left = "DDD", right = "DDD"),
      "EEE" to Node(id = "EEE", left = "EEE", right = "EEE"),
      "GGG" to Node(id = "GGG", left = "GGG", right = "GGG"),
      "ZZZ" to Node(id = "ZZZ", left = "ZZZ", right = "ZZZ")
    ).mapKeys { (key, _) -> NodeId(key) })
  }

  test(""" Given a sequence of Node lines then it should be possible to parse it as a Network""") {
    val input = """
      AAA = (BBB, CCC)
      BBB = (DDD, EEE)
      CCC = (ZZZ, GGG)
      DDD = (DDD, DDD)
      EEE = (EEE, EEE)
      GGG = (GGG, GGG)
      ZZZ = (ZZZ, ZZZ)
    """.trimIndent().lineSequence()
    val network = input.toNetwork()
    network shouldBe Network(nodes = mapOf(
      "AAA" to Node(id = "AAA", left = "BBB", right = "CCC"),
      "BBB" to Node(id = "BBB", left = "DDD", right = "EEE"),
      "CCC" to Node(id = "CCC", left = "ZZZ", right = "GGG"),
      "DDD" to Node(id = "DDD", left = "DDD", right = "DDD"),
      "EEE" to Node(id = "EEE", left = "EEE", right = "EEE"),
      "GGG" to Node(id = "GGG", left = "GGG", right = "GGG"),
      "ZZZ" to Node(id = "ZZZ", left = "ZZZ", right = "ZZZ")
    ).mapKeys { (key, _) -> NodeId(key) })
  }

  context("parsing instructions") {
    table(
      headers("Input", "Instructions"),
      row("RL", listOf(Right, Left)),
      row("LR", listOf(Left, Right)),
      row("LL", listOf(Left, Left)),
      row("RR", listOf(Right, Right)),
      row("LLR", listOf(Left, Left, Right)),
      row("RRL", listOf(Right, Right, Left))
    ).forAll { input, expectedInstructions ->
      test("""Given an instruction list of "$input" then it should be possible to parse it as $expectedInstructions""") {
        input.toInstructionList() shouldBe expectedInstructions
      }
    }
  }

  val exampleDocuments1 = """
    RL

    AAA = (BBB, CCC)
    BBB = (DDD, EEE)
    CCC = (ZZZ, GGG)
    DDD = (DDD, DDD)
    EEE = (EEE, EEE)
    GGG = (GGG, GGG)
    ZZZ = (ZZZ, ZZZ)
  """.trimIndent()

  val exampleDocuments2 = """
    LLR

    AAA = (BBB, BBB)
    BBB = (AAA, ZZZ)
    ZZZ = (ZZZ, ZZZ)
  """.trimIndent()

  test("""Given Example documents 1 then it should be parse it as an Instruction List and a Network""") {
    exampleDocuments1.toInstructionListAndNetwork() shouldBe (listOf(Right, Left) to Network(nodes = mapOf(
      "AAA" to Node(id = "AAA", left = "BBB", right = "CCC"),
      "BBB" to Node(id = "BBB", left = "DDD", right = "EEE"),
      "CCC" to Node(id = "CCC", left = "ZZZ", right = "GGG"),
      "DDD" to Node(id = "DDD", left = "DDD", right = "DDD"),
      "EEE" to Node(id = "EEE", left = "EEE", right = "EEE"),
      "GGG" to Node(id = "GGG", left = "GGG", right = "GGG"),
      "ZZZ" to Node(id = "ZZZ", left = "ZZZ", right = "ZZZ")
    ).mapKeys { (key, _) -> NodeId(key) }))
  }

  test("""Given Example documents 2 then it should be parse it as an Instruction List and a Network""") {
    exampleDocuments2.toInstructionListAndNetwork() shouldBe (listOf(Left, Left, Right) to Network(nodes = mapOf(
      "AAA" to Node(id = "AAA", left = "BBB", right = "BBB"),
      "BBB" to Node(id = "BBB", left = "AAA", right = "ZZZ"),
      "ZZZ" to Node(id = "ZZZ", left = "ZZZ", right = "ZZZ")
    ).mapKeys { (key, _) -> NodeId(key) }))
  }

  test("""Given Example documents 1 then it should be possible to calculated the number of steps from start to finish as 2""") {
    exampleDocuments1.toInstructionListAndNetwork().let { (instructions, network) ->
      network.calculateNumberOfSteps(instructions) shouldBe 2
    }
  }

  test("""Given Example documents 2 then it should be possible to calculated the number of steps from start to finish as 6""") {
    exampleDocuments2.toInstructionListAndNetwork().let { (instructions, network) ->
      network.calculateNumberOfSteps(instructions) shouldBe 6
    }
  }

  test("""Given our custom documents then it should be possible to calculated the number of steps from start to finish as 13939""") {
    readInput("day08p1").toInstructionListAndNetwork().let { (instructions, network) ->
      network.calculateNumberOfSteps(instructions) shouldBe 13939
    }
  }
})
