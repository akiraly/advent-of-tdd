package day08p1

import day08p1.Instruction.Left
import day08p1.Instruction.Right

fun String.toInstructionListAndNetwork(): Pair<List<Instruction>, Network> {
  val lines = lineSequence()
  return lines.first().toInstructionList() to lines.drop(2).toNetwork()
}

fun Sequence<String>.toNetwork(): Network = Network(
  map { it.toNode() }.associateBy { it.id }
)

fun String.toNetwork(): Network = lineSequence().toNetwork()

data class Network(val nodes: Map<NodeId, Node>) {
  fun calculateNumberOfSteps(instructions: List<Instruction>): Int {
    var currentNode = nodes.getValue(NodeId("AAA"))
    var steps = 0
    val endId = NodeId("ZZZ")
    while (true) {
      val nextId = currentNode.doStep(instructions[steps % instructions.size])
      currentNode = nodes.getValue(nextId)
      steps++
      if (nextId == endId) {
        return steps
      }
    }
  }
}

enum class Instruction { Left, Right }

fun String.toInstructionList(): List<Instruction> = this
  .map {
    when (it) {
      'L' -> Left
      else -> Right
    }
  }
  .toList()

fun String.toNode(): Node {
  val (id, left, right) = """(.+) = \((.+), (.+)\)""".toRegex().matchEntire(this)?.destructured!!
  return Node(id = NodeId(id), left = NodeId(left), right = NodeId(right))
}

data class Node(val id: NodeId, val left: NodeId, val right: NodeId) {
  fun doStep(instruction: Instruction): NodeId = when (instruction) {
    Left -> left
    Right -> right
  }

  constructor(id: String, left: String, right: String) : this(NodeId(id), NodeId(left), NodeId(right))
}

data class NodeId(val value: String)
