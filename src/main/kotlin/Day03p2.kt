package day03p2

data class Schematic(val lines: List<String>) {
    private val numberRegex = """(\d+)""".toRegex()

    val partNumbers: List<PartNumber>

    init {
        partNumbers = lines.asSequence().flatMapIndexed { row, line ->
            numberRegex.findAll(line)
                .mapNotNull {
                    val group = it.groups[1] ?: error("no group")
                    val position = Position(row, group.range)
                    if (!isAdjacentToSymbol(position)) {
                        return@mapNotNull null
                    }
                    PartNumber(group.value.toInt(), position)
                }
        }
            .toList()
    }

    fun isAdjacentToSymbol(row: Int, indexRange: IntRange): Boolean =
        isAdjacentToSymbol(Position(row, indexRange))

    private fun isAdjacentToSymbol(position: Position): Boolean =
        adjacentCoordinates(position).any { isSymbol(it) }

    private fun isSymbol(coordinate: Coordinate): Boolean = isMatching(coordinate) { char ->
        char.isSymbol()
    }

    private fun isGearCandidate(coordinate: Coordinate): Boolean = isMatching(coordinate) { char ->
        char.isGearCandidate()
    }

    private fun isMatching(coordinate: Coordinate, matcher: (Char) -> Boolean): Boolean {
        val line = lines.getOrNull(coordinate.row) ?: return false
        val char = line.getOrNull(coordinate.col) ?: return false
        return matcher(char)
    }

    fun findGears(): Map<Coordinate, Set<PartNumber>> {
        val gearCandidats = mutableMapOf<Coordinate, MutableSet<PartNumber>>()
        for (partNumber in partNumbers) {
            adjacentCoordinates(partNumber.position).forEach { coordinate ->
                if (isGearCandidate(coordinate)) {
                    gearCandidats.computeIfAbsent(coordinate) { mutableSetOf() }.add(partNumber)
                }
            }
        }

        return gearCandidats.filterValues { it.size == 2 }
    }

    private fun adjacentCoordinates(position: Position): Sequence<Coordinate> {
        val encapsulatingRange = position.range.encapsulatingRange()
        val leftAndRight = sequenceOf(
            Coordinate(position.row, encapsulatingRange.first),
            Coordinate(position.row, encapsulatingRange.last)
        )
        val previousRow = if (position.row > 0) {
            encapsulatingRange.asSequence().map { Coordinate(position.row - 1, it) }
        } else emptySequence()
        val nextRow = if (position.row < lines.size - 1) {
            encapsulatingRange.asSequence().map { Coordinate(position.row + 1, it) }
        } else emptySequence()

        return leftAndRight + previousRow + nextRow
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

internal fun IntRange.encapsulatingRange(): IntRange = first - 1..last + 1

data class PartNumber(val value: Int, val position: Position)

data class Position(val row: Int, val range: IntRange)

data class Coordinate(val row: Int, val col: Int)
