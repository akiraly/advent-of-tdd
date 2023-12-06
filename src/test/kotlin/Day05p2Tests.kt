package day05p2

import day05p2.AlmanacMapRange.MappingResult
import day05p2.CategoryNumberRange.OverlappingResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.readInput

class Day05p2Tests : FunSpec({
  test("The basic entities like seed and category can be properly represented") {
    val category = Category("seed")
    category.value shouldBe "seed"

    val categoryNumberRange = category.range(79, 82)
    categoryNumberRange.start shouldBe 79
    categoryNumberRange.end shouldBe 82
  }

  test("An Almanac map name can be properly represented") {
    val mapName = AlmanacMapName(source = "seed", destination = "soil")
    mapName.source shouldBe seedCategory
    mapName.destination shouldBe Category("soil")
  }

  test("""Given "seeds: 41 3" as input when we parse it then it should be setOfSeedRanges(41..43)""") {
    "seeds: 41 3".toSeedRangesSet() shouldContainExactlyInAnyOrder
      setOf(seedCategory.range(41, 43))
  }

  test("""Given "seeds: seeds: 79 3 55 4" as input when we parse it then it should be setOfSeedRanges(79..81, 55..58)""") {
    "seeds: 79 3 55 4".toSeedRangesSet() shouldContainExactlyInAnyOrder
      setOf(
        seedCategory.range(79, 81),
        seedCategory.range(55, 58)
      )
  }

  test("""Given a CategoryNumberRange("seed", 79, 81) contains should work as expected""") {
    val range = seedCategory.range(79, 81)
    range.contains(78) shouldBe false
    range.contains(79) shouldBe true
    range.contains(80) shouldBe true
    range.contains(81) shouldBe true
    range.contains(82) shouldBe false
  }

  test("""Given a CategoryNumberRange("seed", 75, 82) then we should be able to compare it with other ranges""") {
    val range = seedCategory.range(75, 82)

    range.overlapOn(range) shouldBe OverlappingResult(left = null, overlapped = range, right = null)

    // contains
    range.overlapOn(seedCategory.range(75, 75)) shouldBe OverlappingResult(
      left = null,
      overlapped = seedCategory.range(75, 75),
      right = null
    ).also { it.notOverlappedRanges() shouldBe emptySet() }
    range.overlapOn(seedCategory.range(77, 80)) shouldBe OverlappingResult(
      left = null,
      overlapped = seedCategory.range(77, 80),
      right = null
    )
    range.overlapOn(seedCategory.range(82, 82)) shouldBe OverlappingResult(
      left = null,
      overlapped = seedCategory.range(82, 82),
      right = null
    )

    // disjunct
    range.overlapOn(seedCategory.range(74, 74)) shouldBe OverlappingResult(
      left = seedCategory.range(74, 74),
      overlapped = null,
      right = null
    ).also { it.notOverlappedRanges() shouldBe setOf(seedCategory.range(74, 74)) }
    range.overlapOn(seedCategory.range(83, 83)) shouldBe OverlappingResult(
      left = null,
      overlapped = null,
      right = seedCategory.range(83, 83)
    ).also { it.notOverlappedRanges() shouldBe setOf(seedCategory.range(83, 83)) }

    // overlap on the left
    range.overlapOn(seedCategory.range(74, 75)) shouldBe OverlappingResult(
      left = seedCategory.range(74, 74),
      overlapped = seedCategory.range(75, 75),
      right = null
    )
    range.overlapOn(seedCategory.range(70, 80)) shouldBe OverlappingResult(
      left = seedCategory.range(70, 74),
      overlapped = seedCategory.range(75, 80),
      right = null
    )

    // overlap on the right
    range.overlapOn(seedCategory.range(82, 83)) shouldBe OverlappingResult(
      left = null,
      overlapped = seedCategory.range(82, 82),
      right = seedCategory.range(83, 83)
    )
    range.overlapOn(seedCategory.range(80, 90)) shouldBe OverlappingResult(
      left = null,
      overlapped = seedCategory.range(80, 82),
      right = seedCategory.range(83, 90)
    )

    range.overlapOn(seedCategory.range(74, 83)) shouldBe OverlappingResult(
      left = seedCategory.range(74, 74),
      overlapped = seedCategory.range(75, 82),
      right = seedCategory.range(83, 83)
    ).also {
      it.notOverlappedRanges() shouldContainExactlyInAnyOrder
        setOf(seedCategory.range(74, 74), seedCategory.range(83, 83))
    }

    range.overlapOn(seedCategory.range(70, 90)) shouldBe OverlappingResult(
      left = seedCategory.range(70, 74),
      overlapped = seedCategory.range(75, 82),
      right = seedCategory.range(83, 90)
    )
  }


  test("""Given a map name line of "seed-to-soil map:" as input when we parse it then it should be AlmanacMapName("seed", "soil")""") {
    "seed-to-soil map:".toMapName() shouldBe AlmanacMapName("seed", "soil")
  }

  test("""Given a map range line of "50 98 2" as input when we parse it then it should be AlmanacMapRange(98, 50, 2)""") {
    val name = AlmanacMapName("seed", "soil")
    "50 98 2".toMapRange(name) shouldBe AlmanacMapRange(
      name = name,
      sourceRangeStart = 98,
      destinationRangeStart = 50,
      rangeLength = 2
    )
  }

  test("""Given a map range of "50 98 2" we should be able to map ranges""") {
    val name = AlmanacMapName("seed", "soil")
    val range = "50 98 10".toMapRange(name)
    range.map(seedCategory.range(98, 99)) shouldBe MappingResult(
      mappedDestinationRange = soilCategory.range(50, 51),
      notMappedSourceRanges = emptySet()
    )

    range.map(seedCategory.range(98, 98)) shouldBe MappingResult(
      mappedDestinationRange = soilCategory.range(50, 50),
      notMappedSourceRanges = emptySet()
    )

    range.map(seedCategory.range(107, 107)) shouldBe MappingResult(
      mappedDestinationRange = soilCategory.range(59, 59),
      notMappedSourceRanges = emptySet()
    )

    range.map(seedCategory.range(98, 107)) shouldBe MappingResult(
      mappedDestinationRange = soilCategory.range(50, 59),
      notMappedSourceRanges = emptySet()
    )

    range.map(seedCategory.range(100, 105)) shouldBe MappingResult(
      mappedDestinationRange = soilCategory.range(52, 57),
      notMappedSourceRanges = emptySet()
    )

    range.map(seedCategory.range(95, 110)) shouldBe MappingResult(
      mappedDestinationRange = soilCategory.range(50, 59),
      notMappedSourceRanges = setOf(seedCategory.range(95, 97), seedCategory.range(108, 110))
    )
  }

  test(
    """Given a map as
    |temperature-to-humidity map:
    |
    |0 69 1
    |1 0 69
    |
    |as input when we parse it then it should be an AlmanacMap with proper name and ranges
  """.trimMargin()
  ) {
    val origInput = """
      temperature-to-humidity map:
      0 69 1
      1 0 69
    """.trimIndent()

    listOf(
      origInput,
      origInput + "\n"
    ).forEach { input ->
      val map = input.toAlmanacMap()
      map.name shouldBe AlmanacMapName("temperature", "humidity")
      map.ranges shouldHaveSize 2
      map.ranges[69] shouldBe AlmanacMapRange(map.name, 69, 0, 1)
      map.ranges[0] shouldBe AlmanacMapRange(map.name, 0, 1, 69)
    }
  }

  val almanacExampleInput = """
seeds: 79 14 55 13

seed-to-soil map:
50 98 2
52 50 48

soil-to-fertilizer map:
0 15 37
37 52 2
39 0 15

fertilizer-to-water map:
49 53 8
0 11 42
42 0 7
57 7 4

water-to-light map:
88 18 7
18 25 70

light-to-temperature map:
45 77 23
81 45 19
68 64 13

temperature-to-humidity map:
0 69 1
1 0 69

humidity-to-location map:
60 56 37
56 93 4
  """.trimIndent()

  test(
    """
      Given an Almanac as

      $almanacExampleInput

      as input when we parse it then it should be an Almanac with proper seeds and maps
    """.trimIndent()
  ) {
    val almanac = almanacExampleInput.toAlmanac()

    almanac.seeds shouldContainExactlyInAnyOrder
      setOf((79..92), (55..67))
        .map { seedCategory.range(it.first.toLong(), it.last.toLong()) }

    almanac.maps shouldHaveSize 7
    val humidityMap = almanac.maps.getValue(Category("humidity"))
    humidityMap.name shouldBe AlmanacMapName("humidity", "location")
    humidityMap.ranges shouldHaveSize 2
    humidityMap.ranges[56] shouldBe AlmanacMapRange(humidityMap.name, 56, 60, 37)
    humidityMap.ranges[93] shouldBe AlmanacMapRange(humidityMap.name, 93, 56, 4)
  }

  test("Test AlmanacMap mapping between category number ranges") {
    val map = """
seed-to-soil map:
50 98 2
52 50 48
    """.trimIndent().toAlmanacMap()

    map.map(mutableListOf(seedCategory.range(0, 101))) shouldContainExactlyInAnyOrder mutableListOf(
      soilCategory.range(0, 49),
      soilCategory.range(50, 51),
      soilCategory.range(52, 99),
      soilCategory.range(100, 101)
    )
  }

  test("Given the example Almanac test the chained mapping between category number ranges") {
    val almanac = almanacExampleInput.toAlmanac()
    almanac.map(
      mutableListOf(seedCategory.range(0, 101)),
      locationCategory
    ) shouldContainExactlyInAnyOrder mutableListOf(
      locationCategory.range(0, 0),
      locationCategory.range(1, 18),
      locationCategory.range(19, 19),
      locationCategory.range(20, 21),
      locationCategory.range(22, 35),
      locationCategory.range(36, 42),
      locationCategory.range(43, 43),
      locationCategory.range(44, 45),
      locationCategory.range(46, 55),
      locationCategory.range(56, 59),
      locationCategory.range(60, 60),
      locationCategory.range(61, 66),
      locationCategory.range(67, 67),
      locationCategory.range(68, 72),
      locationCategory.range(73, 73),
      locationCategory.range(74, 84),
      locationCategory.range(85, 89),
      locationCategory.range(90, 93),
      locationCategory.range(94, 96),
      locationCategory.range(97, 99),
      locationCategory.range(100, 101)
    )
  }

  test("Given the example Almanac the lowest location number should be 46") {
    val almanac = almanacExampleInput.toAlmanac()
    almanac.findLowestLocationNumber() shouldBe 46
  }

  test("Given our custom Almanac the lowest location number should be 2254686") {
    val almanac = readInput("day05p1").toAlmanac()
    almanac.findLowestLocationNumber() shouldBe 2254686
  }
})
