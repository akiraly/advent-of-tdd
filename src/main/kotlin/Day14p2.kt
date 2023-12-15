package day14p2

fun String.toPlatform(): Platform = Platform(lineSequence().map { it.toMutableList() }.toList())

data class Platform(val value: List<MutableList<Char>>) {
  private val numOfColumns = value.first().size
  private val numOfRows = value.size

  fun deepCopy(): Platform = Platform(value.map { it.toMutableList() })

  fun tiltNorth(): Platform {
    for (col in (0..<numOfColumns)) {
      for (row in value.indices) {
        var currentRow = row
        while (currentRow > 0 && value[currentRow - 1][col] == '.' && value[currentRow][col] == 'O') {
          this.value[currentRow - 1][col] = 'O'
          this.value[currentRow][col] = '.'
          currentRow--
        }
      }
    }

    return this
  }

  fun tiltWest(): Platform {
    for (row in value.indices) {
      for (col in (0..<numOfColumns)) {
        var currentCol = col
        while (currentCol > 0 && value[row][currentCol - 1] == '.' && value[row][currentCol] == 'O') {
          this.value[row][currentCol - 1] = 'O'
          this.value[row][currentCol] = '.'
          currentCol--
        }
      }
    }

    return this
  }

  fun tiltSouth(): Platform {
    for (col in (0..<numOfColumns)) {
      for (row in value.indices.reversed()) {
        var currentRow = row
        while (currentRow < numOfRows - 1 && value[currentRow + 1][col] == '.' && value[currentRow][col] == 'O') {
          this.value[currentRow + 1][col] = 'O'
          this.value[currentRow][col] = '.'
          currentRow++
        }
      }
    }

    return this
  }

  fun tiltEast(): Platform {
    for (row in value.indices) {
      for (col in (0..<numOfColumns).reversed()) {
        var currentCol = col
        while (currentCol < numOfColumns - 1 && value[row][currentCol + 1] == '.' && value[row][currentCol] == 'O') {
          value[row][currentCol + 1] = 'O'
          value[row][currentCol] = '.'
          currentCol++
        }
      }
    }

    return this
  }

  fun spin(cycles: Int, cache: MutableMap<Pair<Platform, Int>, Platform> = mutableMapOf()): Platform {
    if (cycles == 0) return this

    val key = this to cycles
    cache[key]?.let {
      return it
    }

    val newPlatform = if (cycles == 1) spinOnce() else {
      val half = cycles / 2
      val remaining = cycles - half

      spin(half, cache).spin(remaining, cache)
    }

    cache[key] = newPlatform

    return newPlatform
  }

  private fun spinOnce(): Platform = deepCopy().tiltNorth().tiltWest().tiltSouth().tiltEast()

  fun look(): String = value.joinToString("\n") { it.joinToString("") }
  fun calcTotalLoad(): Long {
    val numOfRows = value.size.toLong()

    return value.asSequence().mapIndexed { rowIndex, row ->
      val load = numOfRows - rowIndex
      row.sumOf { if (it == 'O') load else 0 }
    }.sum()
  }
}

