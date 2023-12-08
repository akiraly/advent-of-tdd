package day08p1

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class Day08p1Tests: FunSpec({
  test("""Given a reference as "ABC" then it should be possible to represent as a NodeId""") {
    val nodeId = NodeId("ABC")
    nodeId.value shouldBe "ABC"
  }

  test("""Given a "AAA = (BBB, CCC)"then it should be possible to parse it as Node""") {
    "AAA = (BBB, CCC)".toNode() shouldBe Node(id = NodeId("AAA"), left = NodeId("BBB"), right = NodeId("CCC"))
  }
})

fun String.toNode(): Node {
  val (id, left, right) = """(.+) = \((.+), (.+)\)""".toRegex().matchEntire(this)?.destructured ?: error("can't parse $this")
  return Node(id = NodeId(id), left = NodeId(left), right = NodeId(right))
}

data class Node(val id: NodeId, val left: NodeId, val right: NodeId)

data class NodeId(val value: String)
