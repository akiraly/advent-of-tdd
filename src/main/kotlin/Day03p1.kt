package day03p1

data class Schematic(val lines: List<String>) {
    private val numberRegex = """(\d+)""".toRegex()

    fun partNumbers(): List<PartNumber> {
        return lines.asSequence().flatMapIndexed { row, line ->
            numberRegex.findAll(line)
                .mapNotNull {
                    val group = it.groups[1] ?: error("no group")
                    if (!isAdjacentToSymbol(row, group.range)) {
                        return@mapNotNull null
                    }
                    group.value.toInt()
                }
                .map { PartNumber(it) }
        }
            .toList()
    }

    fun isAdjacentToSymbol(row: Int, indexRange: IntRange): Boolean {
        val encapsulatingRange = indexRange.first - 1..indexRange.last + 1
        if (isSymbol(row, encapsulatingRange.first) || isSymbol(row, encapsulatingRange.last)) {
            return true
        }
        if (row != 0 && encapsulatingRange.any { isSymbol(row - 1, it) }) {
            return true
        }
        if (row < lines.size && encapsulatingRange.any { isSymbol(row + 1, it) }) {
            return true
        }
        return false
    }

    private fun isSymbol(row: Int, col: Int): Boolean {
        if (row < 0 || row >= lines.size) return false
        val line = lines[row]
        if (col < 0 || col >= line.length) return false
        val char = line[col]
        return char != '.' && !char.isDigit()
    }

    companion object {
        fun fromString(raw: String): Schematic {
            return Schematic(raw.lines())
        }
    }

}

data class PartNumber(val value: Int)