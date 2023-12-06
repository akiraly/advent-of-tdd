package day05p2

import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSortedMap


fun String.toAlmanac(): Almanac {
  val lines = lineSequence().toMutableList()
  val seeds = lines.removeFirst().toSeedRangesSet()
  require(lines.removeFirst().isEmpty())
  val maps = ImmutableMap.builder<Category, AlmanacMap>()
  while (lines.isNotEmpty()) {
    val map = lines.toAlmanacMap()
    maps.put(map.name.source, map)
  }
  return Almanac(seeds, maps.build())
}

fun String.toAlmanacMap(): AlmanacMap = lineSequence().toMutableList().toAlmanacMap()


fun MutableList<String>.toAlmanacMap(): AlmanacMap {
  val name = removeFirst().toMapName()
  val ranges = ImmutableSortedMap.naturalOrder<Long, AlmanacMapRange>()
  while (isNotEmpty()) {
    val line = removeFirst()
    if (line.isBlank()) {
      break
    }
    val range = line.toMapRange(name)
    ranges.put(range.sourceRange.start, range)
  }
  return AlmanacMap(name, ranges.build())
}

fun String.toMapRange(name: AlmanacMapName): AlmanacMapRange {
  val (destinationRangeStart, sourceRangeStart, rangeLength) = split(" ")
  return AlmanacMapRange(
    name,
    sourceRangeStart.toLong(),
    destinationRangeStart.toLong(),
    rangeLength = rangeLength.toLong()
  )
}

fun String.toMapName(): AlmanacMapName =
  substringBeforeLast(" map:")
    .split("-to-")
    .let { (source, destination) -> AlmanacMapName(source, destination) }

private val intRegex = """(\d+) (\d+)""".toRegex()
fun String.toSeedRangesSet(): Set<CategoryNumberRange> = substringAfter("seeds: ").let { seeds ->
  intRegex.findAll(seeds).map { m ->
    m.groupValues.drop(1).let { (start, count) ->
      seedCategory.range(start.toLong(), count.toLong().asCount())
    }
  }.toSet()
}

data class Almanac(val seeds: Set<CategoryNumberRange>, val maps: Map<Category, AlmanacMap>) {
  init {
    maps.forEach { (source, map) -> require(source == map.name.source) }
  }

  fun findLowestLocationNumber(): Long = map(seeds.toMutableList(), locationCategory).minOf { it.start }

  fun map(source: MutableList<CategoryNumberRange>, destinationCat: Category): MutableList<CategoryNumberRange> {
    var value = source
    while (true) {
      require(value.isNotEmpty())
      val category = value[0].category
      if (category == destinationCat) return value
      value = maps.getValue(category).map(value)
    }
  }
}

data class AlmanacMap(val name: AlmanacMapName, val ranges: ImmutableSortedMap<Long, AlmanacMapRange>) {

  fun map(sourceRanges: MutableList<CategoryNumberRange>): MutableList<CategoryNumberRange> {
    val result = mutableListOf<CategoryNumberRange>()
    while (sourceRanges.isNotEmpty()) {
      val sourceRange = sourceRanges.removeFirst()
      var mappingResult = ranges.floorEntry(sourceRange.start)?.value?.map(sourceRange)
      if (mappingResult == null ||
        (mappingResult.mappedDestinationRange == null && mappingResult.notMappedSourceRanges.contains(sourceRange))
      ) {
        mappingResult = ranges.floorEntry(sourceRange.end)?.value?.map(sourceRange)
      }
      val mappedDestinationRange = mappingResult?.mappedDestinationRange
      if (mappedDestinationRange != null) {
        result.add(mappedDestinationRange)
        if (mappingResult != null) sourceRanges.addAll(mappingResult.notMappedSourceRanges)
      } else {
        result.add(sourceRange.copy(category = name.destination))
      }
    }
    return result
  }
}

data class AlmanacMapName(val source: Category, val destination: Category) {
  constructor(source: String, destination: String) : this(Category(source), Category(destination))
}

data class AlmanacMapRange(
  val sourceRange: CategoryNumberRange,
  val destinationRange: CategoryNumberRange
) {
  constructor(name: AlmanacMapName, sourceRangeStart: Long, destinationRangeStart: Long, rangeLength: Long) :
    this(
      name.source.range(sourceRangeStart, rangeLength.asCount()),
      name.destination.range(destinationRangeStart, rangeLength.asCount())
    )

  private fun map(source: Long): Long {
    require(sourceRange.contains(source))
    return destinationRange.start + source - sourceRange.start
  }

  fun map(source: CategoryNumberRange): MappingResult {
    val overlappingResult = sourceRange.overlapOn(source)
    val mappedDestinationRange = if (overlappingResult.overlapped == null) null else {
      destinationRange.category.range(map(overlappingResult.overlapped.start), map(overlappingResult.overlapped.end))
    }
    return MappingResult(
      mappedDestinationRange,
      overlappingResult.notOverlappedRanges()
    )
  }

  data class MappingResult(
    val mappedDestinationRange: CategoryNumberRange?,
    val notMappedSourceRanges: Set<CategoryNumberRange>
  )
}

data class CategoryNumberRange(val category: Category, val start: Long, val end: Long) {

  init {
    require(start <= end)
  }

  fun contains(value: Long): Boolean = value in start..end

  fun overlapOn(range: CategoryNumberRange): OverlappingResult {
    require(category == range.category)

    // no overlap
    if (start > range.end) {
      return OverlappingResult(range, null, null)
    }
    if (end < range.start) {
      return OverlappingResult(null, null, range)
    }

    // some overlap
    val left = if (start <= range.start) null else category.range(range.start, start - 1)
    val right = if (end >= range.end) null else category.range(end + 1, range.end)
    val overlapped = category.range(maxOf(start, range.start), minOf(end, range.end))
    return OverlappingResult(left, overlapped, right)
  }

  data class OverlappingResult(
    val left: CategoryNumberRange?,
    val overlapped: CategoryNumberRange?,
    val right: CategoryNumberRange?
  ) {
    init {
      require(left != null || overlapped != null || right != null)
      require(sequenceOf(left, overlapped, right).filterNotNull().map { it.category }.distinct().count() == 1)
    }

    fun notOverlappedRanges() = sequenceOf(left, right).filterNotNull().toSet()
  }
}

data class Count(val value: Long)

fun Long.asCount() = Count(this)

val seedCategory = Category("seed")
val soilCategory = Category("soil")
val locationCategory = Category("location")

data class Category(val value: String) {
  fun range(start: Long, end: Long) = CategoryNumberRange(this, start, end)
  fun range(start: Long, count: Count) = range(start, start + count.value - 1)
}
