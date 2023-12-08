package day08p2

import day08p2.Instruction.Left
import day08p2.Instruction.Right
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import io.readInput

class Day08p2Tests : FunSpec({
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

  context("node ids can tell about themselves if they are a starting or ending or neither") {
    table(
      headers("Node id", "Is Starting", "Is Ending"),
      row("11A", true, false),
      row("22A", true, false),
      row("11Z", false, true),
      row("22Z", false, true),
      row("11B", false, false),
      row("22B", false, false),
    ).forAll { nodeId, isStarting, isEnding ->
      test("Given a node id $nodeId then it should be possible to tell if it is starting or ending or neither") {
        with(NodeId(nodeId)) {
          starting shouldBe isStarting
          ending shouldBe isEnding
        }
      }
    }
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

  val exampleDocuments3 = """
    LR

    11A = (11B, XXX)
    11B = (XXX, 11Z)
    11Z = (11B, XXX)
    22A = (22B, XXX)
    22B = (22C, 22C)
    22C = (22Z, 22Z)
    22Z = (22B, 22B)
    XXX = (XXX, XXX)
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

  test("""Given Example documents 1 then it should be possible to calculate the number of steps from start to finish as 2""") {
    exampleDocuments1.toInstructionListAndNetwork().let { (instructions, network) ->
      network.calculateNumberOfSteps(instructions) shouldBe 2
    }
  }

  test("""Given Example documents 2 then it should be possible to calculate the number of steps from start to finish as 6""") {
    exampleDocuments2.toInstructionListAndNetwork().let { (instructions, network) ->
      network.calculateNumberOfSteps(instructions) shouldBe 6
    }
  }

  test("""Given Example documents 3 then it should be possible to calculate the number of steps from start to finish as 6""") {
    exampleDocuments3.toInstructionListAndNetwork().let { (instructions, network) ->
      network.calculateNumberOfSteps(instructions) shouldBe 6
    }
  }

  test("""Given our custom documents then it should be possible to calculate the number of steps from start to finish as 8906539031197""") {
    readInput("day08p1").toInstructionListAndNetwork().let { (instructions, network) ->
      network.calculateNumberOfSteps(instructions) shouldBe 8906539031197
    }
  }
})
