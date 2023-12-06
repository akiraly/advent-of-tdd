package day05p1

import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSortedMap


fun String.toAlmanac(): Almanac {
  val lines = lineSequence().toMutableList()
  val seeds = lines.removeFirst().toSeedSet()
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
    ranges.put(range.sourceRangeStart.value, range)
  }
  return AlmanacMap(name, ranges.build())
}

fun String.toMapRange(name: AlmanacMapName): AlmanacMapRange {
  val (destinationRangeStart, sourceRangeStart, rangeLength) = split(" ")
  return AlmanacMapRange(
    sourceRangeStart = CategoryNumber(name.source, sourceRangeStart.toLong()),
    destinationRangeStart = CategoryNumber(name.destination, destinationRangeStart.toLong()),
    rangeLength = rangeLength.toLong()
  )
}

fun String.toMapName(): AlmanacMapName =
  substringBeforeLast(" map:")
    .split("-to-")
    .let { (source, destination) -> AlmanacMapName(source, destination) }

private val intRegex = """(\d+)""".toRegex()
private val seedCategory = Category("seed")
fun String.toSeedSet(): Set<CategoryNumber> = substringAfter("seeds: ").let {
  intRegex.findAll(it).map { n -> CategoryNumber(seedCategory, n.value.toLong()) }.toSet()
}

private val locationCategory = Category("location")

data class Almanac(val seeds: Set<CategoryNumber>, val maps: Map<Category, AlmanacMap>) {
  fun map(source: CategoryNumber, destinationCat: Category): CategoryNumber {
    var value = source
    while (true) {
      if (value.category == destinationCat) return value
      value = maps.getValue(value.category).map(value)
    }
  }

  fun findLowestLocationNumber(): Long = seeds.asSequence().map { map(it, locationCategory).value }.min()

  init {
    maps.forEach { (source, map) -> require(source == map.name.source) }
  }
}

data class AlmanacMap(val name: AlmanacMapName, val ranges: ImmutableSortedMap<Long, AlmanacMapRange>) {
  fun map(source: CategoryNumber): CategoryNumber {
    require(source.category == name.source)
    return ranges.floorEntry(source.value)?.value?.map(source) ?: CategoryNumber(name.destination, source.value)
  }
}

data class AlmanacMapName(val source: Category, val destination: Category) {
  constructor(source: String, destination: String) : this(Category(source), Category(destination))
}

data class AlmanacMapRange(
  val sourceRangeStart: CategoryNumber,
  val destinationRangeStart: CategoryNumber,
  val rangeLength: Long
) {
  fun map(source: CategoryNumber): CategoryNumber? {
    require(source.category == sourceRangeStart.category)
    if (source.value < sourceRangeStart.value) {
      return null
    }
    if (source.value >= sourceRangeStart.value + rangeLength) {
      return null
    }
    return CategoryNumber(
      destinationRangeStart.category,
      destinationRangeStart.value + source.value - sourceRangeStart.value
    )
  }

  constructor(name: AlmanacMapName, sourceRangeStart: Long, destinationRangeStart: Long, rangeLength: Long) :
    this(
      CategoryNumber(name.source, sourceRangeStart),
      CategoryNumber(name.destination, destinationRangeStart),
      rangeLength
    )
}

data class CategoryNumber(val category: Category, val value: Long) {
  constructor(category: String, value: Long) : this(Category(category), value)
}

data class Category(val value: String)
