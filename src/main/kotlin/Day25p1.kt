package day25p1

import org.jgrapht.alg.clustering.GirvanNewmanClustering
import org.jgrapht.graph.SimpleGraph

fun solve(diagramInput: String): Long {
  val edges = diagramInput.lineSequence()
    .map { it.split(": ") }
    .map { (left, rights) -> left to rights.split(' ') }
    .flatMap { (left, rights) -> rights.asSequence().map { if (left < it) left to it else it to left } }
    .sortedWith(compareBy({ it.first }, { it.second }))
    .toSet()
  val nodes = edges.asSequence().flatMap { sequenceOf(it.first, it.second) }.distinct().sorted().toList()

  val graph = SimpleGraph.createBuilder<String, Edge>(Edge::class.java).let { builder ->
    nodes.forEach { builder.addVertex(it) }
    edges.forEach { builder.addEdge(it.first, it.second, Edge(it.first, it.second)) }

    builder.build()
  }

  return GirvanNewmanClustering(graph, 2).clustering.clusters.fold(1L) { acc, cluster -> acc * cluster.size }
}

data class Edge(val from: String, val to: String)
