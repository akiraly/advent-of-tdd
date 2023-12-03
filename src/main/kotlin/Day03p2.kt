package day03p2

data class Schematic(val lines: List<String>) {
    private val numberRegex = """(\d+)""".toRegex()

    val partNumbers: List<PartNumber>

    init {
        partNumbers = lines.asSequence().flatMapIndexed { row, line ->
            numberRegex.findAll(line)
                .mapNotNull {
                    val group = it.groups[1] ?: error("no group")
                    val partNumber = PartNumber(group.value.toInt(), row, group.range)
                    if (!isBorderedBySymbol(partNumber.row, partNumber.encapsulatingRange)) {
                        return@mapNotNull null
                    }
                    partNumber
                }
        }
            .toList()
    }

    fun isAdjacentToSymbol(row: Int, indexRange: IntRange): Boolean =
        isBorderedBySymbol(row, indexRange.start - 1..indexRange.last + 1)

    fun isBorderedBySymbol(row: Int, encapsulatingRange: IntRange): Boolean {
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

    private fun isSymbol(row: Int, col: Int): Boolean = isMatching(row, col) { char ->
        char.isSymbol()
    }

    private fun isGearCandidate(row: Int, col: Int): Boolean = isMatching(row, col) { char ->
        char.isGearCandidate()
    }

    private fun isMatching(row: Int, col: Int, matcher: (Char) -> Boolean): Boolean {
        if (row < 0 || row >= lines.size) return false
        val line = lines[row]
        if (col < 0 || col >= line.length) return false
        val char = line[col]
        return matcher(char)
    }

    fun findGears(): Map<Coordinate, Set<PartNumber>> {
        val gearCandidats = mutableMapOf<Coordinate, MutableSet<PartNumber>>()
        for (partNumber in partNumbers) {
            (sequenceOf(
                Coordinate(partNumber.row, partNumber.encapsulatingRange.first),
                Coordinate(partNumber.row, partNumber.encapsulatingRange.last)
            ) + partNumber.encapsulatingRange.asSequence().map {
                Coordinate(partNumber.row - 1, it)
            } + partNumber.encapsulatingRange.asSequence().map {
                Coordinate(partNumber.row + 1, it)
            }).distinct().forEach { coordinate ->
                if (isGearCandidate(coordinate.row, coordinate.col)) {
                    gearCandidats.computeIfAbsent(coordinate) { mutableSetOf() }.add(partNumber)
                }
            }
        }

        return gearCandidats.filterValues { it.size == 2 }
    }

    fun sumOfGearRatios(): Int = findGears().asSequence()
        .sumOf { pns -> pns.value.asSequence().map { it.value }.reduce { acc, i -> acc * i } }

    companion object {
        fun fromString(raw: String): Schematic {
            return Schematic(raw.lines())
        }
    }

}

internal fun Char.isSymbol(): Boolean = this != '.' && !isDigit()

internal fun Char.isGearCandidate(): Boolean = this == '*'

data class PartNumber(val value: Int, val row: Int, val range: IntRange) {
    val encapsulatingRange = range.first - 1..range.last + 1
}

data class Coordinate(val row: Int, val col: Int)
