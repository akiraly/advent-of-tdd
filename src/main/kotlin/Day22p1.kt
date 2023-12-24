package day22p1

fun String.numberOfBricksThatCanBeDisintegrated(): Int = toSnapshot().settle().numberOfBricksThatCanBeDisintegrated()

fun String.toSnapshot(): Snapshot = Snapshot(
  this
    .lineSequence()
    .map { line -> line.split("~").map { it.split(",").map { n -> n.toInt() } } }
    .mapIndexed { index, parts ->
      Brick(index + 1, parts[0][0], parts[0][1], parts[0][2], parts[1][0], parts[1][1], parts[1][2])
    }
    .associateBy { it.id }
)

data class Snapshot(val bricks: Map<BrickId, Brick>) {
  constructor(vararg bricks: Brick) : this(bricks.associateBy { it.id })

  val maxX: Int = bricks.values.maxOf { it.end.x }
  val maxY: Int = bricks.values.maxOf { it.end.y }
  val maxZ: Int = bricks.values.maxOf { it.end.z }

  fun settle(): Snapshot {

    val bricksByMinZ = bricks.values.asSequence().sortedBy { it.start.z }.toMutableList()

    val topDownXYView: MutableMap<Coordinate2D, Pair<Int, Brick>?> =
      (0..maxX).flatMap { x -> (0..maxY).asSequence().map { y -> Coordinate2D(x, y) to null } }
        .toMap(mutableMapOf())

    val settledBricks = mutableListOf<Brick>()

    while (bricksByMinZ.isNotEmpty()) {
      val current = bricksByMinZ.removeFirst()
      val supporterCandidates = current.xyCoordinates().mapNotNull { topDownXYView[it] }

      val (supporterZ, supporters) = supporterCandidates.maxOfOrNull { it.first }?.let { max ->
        max to supporterCandidates.asSequence().filter { it.first == max }.map { it.second.id }.toSet()
      } ?: (0 to emptySet())

      val updated = current.copy(
        start = current.start.copy(z = supporterZ + 1),
        end = current.end.copy(z = supporterZ + 1 + current.end.z - current.start.z),
        supporters = supporters
      )

      settledBricks.add(updated)

      updated.xyCoordinates().forEach { xy -> topDownXYView[xy] = updated.end.z to updated }
    }

    return Snapshot(settledBricks.associateBy { it.id })
  }

  fun bricksThatCanBeDisintegrated(): Set<BrickId> {
    val result = bricks.keys.toMutableSet()
    bricks.values.forEach { brick ->
      if (brick.supporters.size == 1) {
        result.remove(brick.supporters.single())
      }
    }
    return result
  }

  fun numberOfBricksThatCanBeDisintegrated(): Int = bricksThatCanBeDisintegrated().size
}

data class BrickId(val id: Int)

data class Brick(
  val id: BrickId,
  val start: Coordinate3D,
  val end: Coordinate3D,
  val supporters: Set<BrickId> = emptySet()
) {

  constructor(id: Int, sx: Int, sy: Int, sz: Int, ex: Int, ey: Int, ez: Int, vararg supporters: Int) : this(
    BrickId(id),
    Coordinate3D(sx, sy, sz),
    Coordinate3D(ex, ey, ez),
    supporters.asSequence().map { BrickId(it) }.toSet()
  )

  init {
    require(start.x <= end.x)
    require(start.y <= end.y)
    require(start.z <= end.z)
    require(0 <= start.x)
    require(0 <= start.y)
    require(1 <= start.z)
  }

  fun xyCoordinates(): Set<Coordinate2D> =
    (start.x..end.x).flatMap { x -> (start.y..end.y).map { y -> Coordinate2D(x, y) } }.toSet()
}

data class Coordinate2D(val x: Int, val y: Int)

data class Coordinate3D(val x: Int, val y: Int, val z: Int)
