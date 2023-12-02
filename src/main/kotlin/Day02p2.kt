package day02p2

fun List<Game>.idSumOfPossibleGames(bag: Cubes): Int = filter { it.isPossible(bag) }.sumOf { it.id }

fun List<Game>.sumOfMinimumBagPowers(): Int =
    asSequence().map { it.minimumBagOfCubes().power }.sum()

data class Game(val id: Int, val handfuls: List<Cubes>) {
    fun isPossible(bag: Cubes): Boolean = handfuls.all { bag.canContain(it) }
    fun minimumBagOfCubes(): Cubes = Cubes(
        red = handfuls.maxOf { it.red },
        green = handfuls.maxOf { it.green },
        blue = handfuls.maxOf { it.blue }
    )

    companion object {
        fun from(raw: String): Game {
            val (gameText, handfulTexts) = raw.split(": ")
            val id = gameText.split(" ")[1].toInt()
            val handfuls = handfulTexts.split("; ").map { Cubes.from(it) }
            return Game(id, handfuls)
        }

        fun gameListFrom(raw: String): List<Game> = raw.lines().map { from(it) }
    }
}

data class Cubes(val red: Int = 0, val green: Int = 0, val blue: Int = 0) {
    val power: Int = red * green * blue

    fun canContain(other: Cubes): Boolean =
        red >= other.red && green >= other.green && blue >= other.blue

    companion object {
        private val reds = """(\d+) red""".toRegex()

        private val greens = """(\d+) green""".toRegex()

        private val blues = """(\d+) blue""".toRegex()

        private fun Regex.parseNumOfCubes(raw: String) = find(raw)?.let {
            it.groupValues[1].toInt()
        } ?: 0

        fun from(raw: String): Cubes = Cubes(
            red = reds.parseNumOfCubes(raw),
            green = greens.parseNumOfCubes(raw),
            blue = blues.parseNumOfCubes(raw)
        )
    }
}