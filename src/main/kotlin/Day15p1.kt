package day15p1

fun String.sumOfInitSeqHashes(): Long = parseInitSeq().sumOf { HASH(it).toLong() }

fun String.parseInitSeq(): List<String> = filter { it != '\n' }.split(',')

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
