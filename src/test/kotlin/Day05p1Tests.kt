package day05p1

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.readInput

class Day05p1Tests : FunSpec({
  test("The basic entities like seed and category can be properly represented") {
    val category = Category("seed")
    category.value shouldBe "seed"

    val categoryNumber = CategoryNumber(category, 79)
    categoryNumber.value shouldBe 79
  }

  test("An Almanac map name can be properly represented") {
    val mapName = AlmanacMapName(source = "seed", destination = "soil")
    mapName.source shouldBe Category("seed")
    mapName.destination shouldBe Category("soil")
  }

  test("""Given "seeds: 41" as input when we parse it then it should be setOfSeeds(42)""") {
    "seeds: 41".toSeedSet() shouldBe setOf(41).map { CategoryNumber("seed", it.toLong()) }
  }

  test("""Given "seeds: 83 86  6 31 17  9 48 53" as input when we parse it then it should be setOfSeeds(6, 9, 17, 31, 48, 53, 83, 86)""") {
    "seeds: 83 86  6 31 17  9 48 53".toSeedSet() shouldContainExactlyInAnyOrder
      setOf(6, 9, 17, 31, 48, 53, 83, 86).map { CategoryNumber("seed", it.toLong()) }
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
      setOf(79, 14, 55, 13).map { CategoryNumber("seed", it.toLong()) }

    almanac.maps shouldHaveSize 7
    val humidityMap = almanac.maps.getValue(Category("humidity"))
    humidityMap.name shouldBe AlmanacMapName("humidity", "location")
    humidityMap.ranges shouldHaveSize 2
    humidityMap.ranges[56] shouldBe AlmanacMapRange(humidityMap.name, 56, 60, 37)
    humidityMap.ranges[93] shouldBe AlmanacMapRange(humidityMap.name, 93, 56, 4)
  }

  context("Test AlmanacMapRange mapping between category numbers") {
    val range = "52 50 48".toMapRange(AlmanacMapName("seed", "soil"))

    table(
      headers("seed number", "soil number"),
      row(0, null),
      row(1, null),
      row(48, null),
      row(49, null),
      row(50, 52),
      row(51, 53),
      row(96, 98),
      row(97, 99),
      row(98, null),
      row(99, null)
    ).forAll { seedNumber, soilNumber ->
      test("Mapping $seedNumber with AlmanacMapRange should be $soilNumber") {
        val seed = CategoryNumber("seed", seedNumber.toLong())
        val soil = if (soilNumber == null) null else CategoryNumber("soil", soilNumber.toLong())
        range.map(seed) shouldBe soil
      }
    }
  }

  context("Test AlmanacMap mapping between category numbers") {
    val map = """
seed-to-soil map:
50 98 2
52 50 48
    """.trimIndent().toAlmanacMap()

    table(
      headers("seed number", "soil number"),
      row(79, 81),
      row(14, 14),
      row(55, 57),
      row(13, 13),
      row(0, 0),
      row(1, 1),
      row(48, 48),
      row(49, 49),
      row(50, 52),
      row(51, 53),
      row(96, 98),
      row(97, 99),
      row(98, 50),
      row(99, 51),
      row(100, 100),
      row(101, 101)
    ).forAll { seedNumber, soilNumber ->
      test("Mapping $seedNumber with AlmanacMap should be $soilNumber") {
        val seed = CategoryNumber("seed", seedNumber.toLong())
        val soil = CategoryNumber("soil", soilNumber.toLong())
        map.map(seed) shouldBe soil
      }
    }
  }

  context("Given the example Almanac test the chained mapping between category numbers") {
    val almanac = almanacExampleInput.toAlmanac()
    val seedCat = Category("seed")
    val locationCat = Category("location")
    table(
      headers("seed number", "location number"),
      row(79, 82),
      row(14, 43),
      row(55, 86),
      row(13, 35)
    ).forAll { seedNumber, locationNumber ->
      test("Mapping $seedNumber with example Almanac should be $locationNumber") {
        val seed = CategoryNumber(seedCat, seedNumber.toLong())
        val location = CategoryNumber(locationCat, locationNumber.toLong())
        almanac.map(seed, locationCat) shouldBe location
      }
    }
  }

  test("Given the example Almanac the lowest location number should be 35") {
    val almanac = almanacExampleInput.toAlmanac()
    almanac.findLowestLocationNumber() shouldBe 35
  }

  test("Given our custom Almanac the lowest location number should be 199602917") {
    val almanac = readInput("day05p1").toAlmanac()
    almanac.findLowestLocationNumber() shouldBe 199602917
  }
})
