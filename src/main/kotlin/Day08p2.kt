package day08p2

import day08p2.Instruction.Left
import day08p2.Instruction.Right
import org.apache.commons.math3.util.ArithmeticUtils

fun String.toInstructionListAndNetwork(): Pair<List<Instruction>, Network> {
  val lines = lineSequence()
  return lines.first().toInstructionList() to lines.drop(2).toNetwork()
}

fun Sequence<String>.toNetwork(): Network = Network(
  map { it.toNode() }.associateBy { it.id }
)

fun String.toNetwork(): Network = lineSequence().toNetwork()

data class Network(val nodes: Map<NodeId, Node>) {
  fun calculateNumberOfSteps(instructions: List<Instruction>): Long =
    nodes.asSequence().filter { (id, _) -> id.starting }.map { (id, _) -> calculateNumberOfSteps(instructions, id) }
      .reduce(ArithmeticUtils::lcm)

  private fun calculateNumberOfSteps(instructions: List<Instruction>, start: NodeId): Long {
    var currentNode = nodes.getValue(start)
    var steps = 0
    while (true) {
      val nextId = currentNode.doStep(instructions[steps % instructions.size])
      currentNode = nodes.getValue(nextId)
      steps++
      if (nextId.ending) {
        return steps.toLong()
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

data class NodeId(val value: String) {
  val starting: Boolean = value.last() == 'A'
  val ending: Boolean = value.last() == 'Z'
}
