package day03p1

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.readInput

class Day03p1Tests : StringSpec({
    "Given a part number of 467 can be represented" {
        PartNumber(467).value shouldBe 467
    }

    "Given a schematic of `.` when parsed then it will have 0 part numbers" {
        Schematic.fromString(".").partNumbers() shouldHaveSize 0
    }

    "Given a schematic of `467*` when parsed then it will have 1 part number" {
        Schematic.fromString("467*").partNumbers() shouldBe listOf(PartNumber(467))
    }

    "Given a schematic of `467` when parsed then it will have 0 part numbers" {
        Schematic.fromString("467").partNumbers() shouldHaveSize 0
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
        ).partNumbers().sumOf { it.value } shouldBe 4361
    }

    "Given our custom input schematic when summing the part numbers we get 527364" {
        Schematic.fromString(readInput("day03p1")).partNumbers().sumOf { it.value } shouldBe 527364
    }
})
