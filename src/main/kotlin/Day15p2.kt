package day15p2

import day15p2.Step.AddLens
import day15p2.Step.RemoveLens

class Boxes {
  val boxes: List<Box> = (0..255).map { Box(it) }

  fun processInitSeq(initSeqInput: String): Long {
    initSeqInput.parseSteps().forEach { step ->
      when (step) {
        is AddLens -> boxes[step.lens.hash].addLens(step.lens, step.focalLength)
        else -> boxes[step.lens.hash].removeLens(step.lens)
      }
    }

    return boxes.sumOf { it.focusingPower() }
  }
}

data class Box(val id: Int) {
  val lenses: LinkedHashMap<String, Int> = LinkedHashMap()

  fun addLens(lens: Lens, focalLength: Int) {
    lenses[lens.label] = focalLength
  }

  fun removeLens(lens: Lens) {
    lenses.remove(lens.label)
  }

  fun focusingPower(): Long = lenses.entries.asSequence().withIndex().sumOf { (index, entry) ->
    val (_, focusingPower) = entry

    (id + 1L) * (index + 1) * focusingPower
  }
}

fun String.parseSteps(): List<Step> = parseInitSeq().map { input ->
  val addLensParts = input.split('=')
  return@map if (addLensParts.size == 2) {
    AddLens(Lens(addLensParts[0]), addLensParts[1].toInt())
  } else {
    RemoveLens(Lens(input.removeSuffix("-")))
  }
}

data class Lens(val label: String, val hash: Int) {
  constructor(label: String) : this(label, HASH(label))
}

sealed interface Step {
  val lens: Lens

  fun toText(): String

  data class AddLens(override val lens: Lens, val focalLength: Int) : Step {
    override fun toText() = "${lens.label}=$focalLength"
  }

  data class RemoveLens(override val lens: Lens) : Step {
    override fun toText() = "${lens.label}-"
  }
}

private fun String.parseInitSeq(): List<String> = filter { it != '\n' }.split(',')

fun HASH(input: String): Int = 0.HASH(input)

fun Int.HASH(input: String): Int {
  var result = this
  for (i in input.indices) {
    result += input[i].code
    result *= 17
    result %= 256
  }

  return result
}
