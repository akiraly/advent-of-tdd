package day14p1

fun String.toPlatform(): Platform = Platform(lineSequence().map { it.toMutableList() }.toList())

data class Platform(val value: List<MutableList<Char>>) {
  fun tiltNorth(): Platform {
    val copy = copy()

    val numOfColumns = copy.value.first().size

    for (col in (0..<numOfColumns)) {
      for (row in copy.value.indices) {
        var currentRow = row
        while (currentRow > 0 && copy.value[currentRow - 1][col] == '.' && copy.value[currentRow][col] == 'O') {
          copy.value[currentRow - 1][col] = 'O'
          copy.value[currentRow][col] = '.'
          currentRow--
        }
      }
    }

    return copy
  }

  fun look(): String = value.joinToString("\n") { it.joinToString("") }
  fun calcTotalLoad(): Long {
    val numOfRows = value.size.toLong()

    return value.asSequence().mapIndexed { rowIndex, row ->
      val load = numOfRows - rowIndex
      row.sumOf { if (it == 'O') load else 0 }
    }.sum()
  }
}
