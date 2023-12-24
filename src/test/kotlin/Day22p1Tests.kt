package day22p1

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.readInput

class Day22p1Tests : FunSpec({
  val exampleSnapshotInput = """
1,0,1~1,2,1
0,0,2~2,0,2
0,2,3~2,2,3
0,0,4~0,2,4
2,0,5~2,2,5
0,1,6~2,1,6
1,1,8~1,1,9
  """.trimIndent()

  val customSnapshotInput = readInput("day22p1")

  context("parsing") {
    test(""" Given the example snapshot input then it can be parsed as expected""") {
      val snapshot = exampleSnapshotInput.toSnapshot()

      snapshot shouldBe Snapshot(
        Brick(1, 1, 0, 1, 1, 2, 1),
        Brick(2, 0, 0, 2, 2, 0, 2),
        Brick(3, 0, 2, 3, 2, 2, 3),
        Brick(4, 0, 0, 4, 0, 2, 4),
        Brick(5, 2, 0, 5, 2, 2, 5),
        Brick(6, 0, 1, 6, 2, 1, 6),
        Brick(7, 1, 1, 8, 1, 1, 9),
      )

      snapshot.bricks.size shouldBe 7
      snapshot.maxX shouldBe 2
      snapshot.maxY shouldBe 2
      snapshot.maxZ shouldBe 9
    }

    test(""" Given the custom snapshot input then it can be parsed as expected""") {
      val snapshot = customSnapshotInput.toSnapshot()

      snapshot.bricks.size shouldBe 1499
      snapshot.maxX shouldBe 9
      snapshot.maxY shouldBe 9
      snapshot.maxZ shouldBe 376
    }
  }

  test(""" Given the example snapshot input when settled then the bricks will be settled as expected""") {
    val snapshot = exampleSnapshotInput.toSnapshot()
    val settled = snapshot.settle()

    settled.bricks.size shouldBe snapshot.bricks.size

    settled shouldBe Snapshot(
      Brick(1, 1, 0, 1, 1, 2, 1),
      Brick(2, 0, 0, 2, 2, 0, 2, 1),
      Brick(3, 0, 2, 2, 2, 2, 2, 1),
      Brick(4, 0, 0, 3, 0, 2, 3, 2, 3),
      Brick(5, 2, 0, 3, 2, 2, 3, 2, 3),
      Brick(6, 0, 1, 4, 2, 1, 4, 4, 5),
      Brick(7, 1, 1, 5, 1, 1, 6, 6),
    )
  }

  test(""" Given the custom snapshot input when settled then the bricks will be settled as expected""") {
    val snapshot = customSnapshotInput.toSnapshot()
    val settled = snapshot.settle()

    settled.bricks.size shouldBe snapshot.bricks.size
  }

  test(""" Given the example snapshot input then the bricks that can be disintegrated can be calculated correctly""") {
    exampleSnapshotInput.toSnapshot().settle().bricksThatCanBeDisintegrated() shouldBe setOf(2, 3, 4, 5, 7).asSequence()
      .map { BrickId(it) }.toSet()
  }

  test(""" Given the example snapshot input then the number of bricks that can be disintegrated is 5""") {
    exampleSnapshotInput.numberOfBricksThatCanBeDisintegrated() shouldBe 5
  }

  test(""" Given the custom snapshot input then the number of bricks that can be disintegrated is 512""") {
    customSnapshotInput.numberOfBricksThatCanBeDisintegrated() shouldBe 512
  }
})
