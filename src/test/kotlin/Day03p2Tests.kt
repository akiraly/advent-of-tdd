package day03p2

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.readInput

class Day03p2Tests : StringSpec({
    "Given a part number of 467 with row 0 and range [0..2] can be represented" {
        val partNumber = PartNumber(467, Position(0, 0..2))
        partNumber.value shouldBe 467
        partNumber.position.row shouldBe 0
        partNumber.position.range shouldBe 0..2
    }

    "Given a schematic of `.` when parsed then it will have 0 part numbers" {
        Schematic.fromString(".").partNumbers shouldHaveSize 0
    }

    "Given a schematic of `467*` when parsed then it will have 1 part number" {
        Schematic.fromString("467*").partNumbers shouldBe listOf(PartNumber(467, Position(0, 0..2)))
    }

    "Given a schematic of `467` when parsed then it will have 0 part numbers" {
        Schematic.fromString("467").partNumbers shouldHaveSize 0
    }

    "Given a schematic of `467*` and row `0` and int rage `[0, 2]` when queried then it will return adjacent" {
        Schematic.fromString("467*").isAdjacentToSymbol(0, 0..2) shouldBe true
    }

    "Given a schematic of `467` and row `0` and int rage `[0, 2]` when queried then it will return not adjacent" {
        Schematic.fromString("467").isAdjacentToSymbol(0, 0..2) shouldBe false
    }

    "Given a schematic of `467.` and row `0` and int rage `[0, 2]` when queried then it will return not adjacent" {
        Schematic.fromString("467.").isAdjacentToSymbol(0, 0..2) shouldBe false
    }

    "Given a schematic of `*467` and row `0` and int rage `[1, 3]` when queried then it will return adjacent" {
        Schematic.fromString("*467").isAdjacentToSymbol(0, 1..3) shouldBe true
    }

    "Given a schematic of `.467` and row `0` and int rage `[1, 3]` when queried then it will return not adjacent" {
        Schematic.fromString(".467").isAdjacentToSymbol(0, 1..3) shouldBe false
    }

    """
        Given a schematic of
        
        467.
        ...*
        
        and row 0 and int range `[0, 2]`
        
        when queried then it will return adjacent
        """ {
        Schematic.fromString(
            """
        467.
        ...*
        """.trimIndent()
        ).isAdjacentToSymbol(0, 0..2) shouldBe true
    }

    """
        Given a schematic of
        
        467..
        ....*
        
        and row 0 and int range `[0, 2]`
        
        when queried then it will return not adjacent
        """ {
        Schematic.fromString(
            """
        467..
        ....*
        """.trimIndent()
        ).isAdjacentToSymbol(0, 0..2) shouldBe false
    }

    """
        Given a schematic of
        
        ...*
        467.
        
        and row 1 and int range `[0, 2]`
        
        when queried then it will return adjacent
        """ {
        Schematic.fromString(
            """
        ...*
        467.
        """.trimIndent()
        ).isAdjacentToSymbol(1, 0..2) shouldBe true
    }

    """
        Given a schematic of
        
        ....*
        467..
        
        and row 1 and int range `[0, 2]`
        
        when queried then it will return adjacent
        """ {
        Schematic.fromString(
            """
        ....*
        467..
        """.trimIndent()
        ).isAdjacentToSymbol(1, 0..2) shouldBe false
    }

    """
       Given an example schematic of
        
467..114..
...*......
..35..633.
......#...
617*......
.....+.58.
..592.....
......755.
...\$.*....
.664.598..

       when summing the part numbers we get 4361
    """ {
        Schematic.fromString(
            """
467..114..
...*......
..35..633.
......#...
617*......
.....+.58.
..592.....
......755.
...\$.*....
.664.598..
"""
        ).partNumbers.sumOf { it.value } shouldBe 4361
    }

    "Given our custom input schematic when summing the part numbers we get 527364" {
        Schematic.fromString(readInput("day03p1")).partNumbers.sumOf { it.value } shouldBe 527364
    }

    "A char of `*` is a gear candidate" {
        '*'.isGearCandidate() shouldBe true
    }

    "None of `.?%!$` are gear candidates" {
        ".?%!\$".all { it.isGearCandidate() shouldBe false }
    }

    "Given a schematic of `467*114` when parsed then it will have 1 gear" {
        val schematic = Schematic.fromString("467*114")
        schematic.partNumbers shouldHaveSize 2
        schematic.findGears().size shouldBe 1
        schematic.sumOfGearRatios() shouldBe 53238
    }

    """
       Given an example schematic of
        
467..114..
...*......
..35..633.
......#...
617*......
.....+.58.
..592.....
......755.
...\$.*....
.664.598..

       when summing the gear ratios we get 467835
    """ {
        Schematic.fromString(
            """
467..114..
...*......
..35..633.
......#...
617*......
.....+.58.
..592.....
......755.
...\$.*....
.664.598..
"""
        ).sumOfGearRatios() shouldBe 467835
    }

    "Given our custom input schematic when summing the gear ratios we get 79026871" {
        Schematic.fromString(readInput("day03p1")).sumOfGearRatios() shouldBe 79026871
    }
})
