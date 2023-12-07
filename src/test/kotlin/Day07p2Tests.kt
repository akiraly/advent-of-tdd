package day07p2

import day07p2.Card.*
import day07p2.HandType.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.readInput


class Day07p2Tests : FunSpec({
  context("Given cards we can compare them based on their relative strength") {

    test("Smoke test card comparisons") {
      Ace shouldBeGreaterThan Two
      Ten shouldBeGreaterThan Nine
      Three shouldBeLessThan Four

      (Ace > Two) shouldBe true
    }

    val allCardsInOrder = listOf(
      Ace,
      King,
      Queen,
      Ten,
      Nine,
      Eight,
      Seven,
      Six,
      Five,
      Four,
      Three,
      Two,
      Joker
    )

    test("Given all cards when we sort them then they should be in order") {
      allCardsInOrder.sortedDescending() shouldBe allCardsInOrder
    }

    test("All cards should really be all cards") {
      val cards = Card.allCards()
      cards.size shouldBe 13

      allCardsInOrder shouldContainExactlyInAnyOrder cards
    }

    for (i in allCardsInOrder.indices) {
      val card = allCardsInOrder[i]
      test("Given $card when compared to itself it should be equal") {
        card shouldBe card
        card shouldBeEqualComparingTo card
      }
      for (j in i + 1 until allCardsInOrder.size) {
        val otherCard = allCardsInOrder[j]
        test("Given $card and $otherCard when we compare them then $card should be greater than $otherCard") {
          card shouldBeGreaterThan otherCard
          otherCard shouldBeLessThan card
        }
      }
    }
  }

  context("Cards should be accessible by their shortIds") {
    Card.allCards().forEach {
      test("Card by ${it.shortId} should be $it") {
        Card[it.shortId] shouldBe it
        it.shortId.toCard() shouldBe it
      }
    }
  }

  context("Test Hands") {
    table(
      headers("Hand", "Cards"),
      row("T32KQ", listOf(Ten, Three, Two, King, Queen)),
      row("32T3K", listOf(Three, Two, Ten, Three, King)),
      row("T55J5", listOf(Ten, Five, Five, Joker, Five)),
      row("KK677", listOf(King, King, Six, Seven, Seven)),
      row("KTJJT", listOf(King, Ten, Joker, Joker, Ten)),
      row("QQQJA", listOf(Queen, Queen, Queen, Joker, Ace)),
    ).forAll { rawHand, cards ->
      test("""Given $rawHand it should be possible to parse it into a Hand with expected cards""") {
        val hand: Hand = rawHand.toHand()
        hand.cards shouldBe cards
      }
    }
  }

  context("Test Hand types") {
    test("Smoke test card comparisons") {
      FiveOfAKind shouldBeGreaterThan HighCard
      FourOfAKind shouldBeGreaterThan FullHouse
      OnePair shouldBeLessThan ThreeOfAKind

      (TwoPair > HighCard) shouldBe true
    }

    val allHandTypesInOrder = listOf(
      FiveOfAKind,
      FourOfAKind,
      FullHouse,
      ThreeOfAKind,
      TwoPair,
      OnePair,
      HighCard
    )

    test("Given all hand types when we sort them then they should be in order") {
      allHandTypesInOrder.sortedDescending() shouldBe allHandTypesInOrder
    }

    test("All hand types should really be all hand types") {
      val types = HandType.allHandTypes()
      types.size shouldBe 7

      allHandTypesInOrder shouldContainExactlyInAnyOrder types
    }

    for (i in allHandTypesInOrder.indices) {
      val handType = allHandTypesInOrder[i]
      test("Given $handType when compared to itself it should be equal") {
        handType shouldBe handType
        handType shouldBeEqualComparingTo handType
      }
      for (j in i + 1 until allHandTypesInOrder.size) {
        val otherHandType = allHandTypesInOrder[j]
        test("Given $handType and $otherHandType when we compare them then $handType should be greater than $otherHandType") {
          handType shouldBeGreaterThan otherHandType
          otherHandType shouldBeLessThan handType
        }
      }
    }
  }

  context("It should be possible to calc the type of a Hand") {
    table(
      headers("Hand", "Type"),
      row("QJJQ2", FourOfAKind),
      row("JKKK2", FourOfAKind),
      row("AAAAA", FiveOfAKind),
      row("AA8AA", FourOfAKind),
      row("23332", FullHouse),
      row("TTT98", ThreeOfAKind),
      row("23432", TwoPair),
      row("A23A4", OnePair),
      row("23456", HighCard),
      row("T32KQ", HighCard),
      row("32T3K", OnePair),
      row("T55J5", FourOfAKind),
      row("KK677", TwoPair),
      row("KTJJT", FourOfAKind),
      row("QQQJA", FourOfAKind),
      row("JJJJJ", FiveOfAKind),
      row("JJJJ2", FiveOfAKind),
      row("JJJ23", FourOfAKind),
      row("JJ234", ThreeOfAKind),
      row("J2345", OnePair),
    ).forAll { rawHand, handType ->
      test("Given $rawHand it should be possible to calc its type as $handType") {
        val hand: Hand = rawHand.toHand()
        hand.handType shouldBe handType
      }
    }
  }

  context("Hands should be comparable") {
    context("Hands with different types should be compared by type") {
      val hands = listOf(
        "AAAAA",
        "AA8AA",
        "23332",
        "TTT98",
        "23432",
        "A23A4",
        "23456"
      ).map { it.toHand() }

      val handTypes = hands.map { it.handType }

      test("All hand types are covered by the test case") {
        hands.size shouldBe handTypes.size

        handTypes.sortedDescending() shouldBe handTypes

        handTypes shouldContainExactlyInAnyOrder HandType.allHandTypes()
      }

      for (i in hands.indices) {
        val hand = hands[i]
        test("Hand $hand should be equal to itself") {
          hand shouldBe hand
          hand shouldBeEqualComparingTo hand
        }
        for (j in i + 1 until hands.size) {
          val otherHand = hands[j]
          test("Hand $hand should be greater than $otherHand") {
            hand shouldBeGreaterThan otherHand
            otherHand shouldBeLessThan hand
          }
        }
      }
    }

    context("Hands with same types should be compared by their cards") {
      table(
        headers("Hand1", "Hand2", "Type"),
        row("33332", "2AAAA", FourOfAKind),
        row("77888", "77788", FullHouse),
        row("33334", "33332", FourOfAKind),
        row("QQQQ2", "JKKK2", FourOfAKind),
        row("KTJJT", "QQQJA", FourOfAKind),
        row("QQQJA", "T55J5", FourOfAKind),
        row("KTJJT", "T55J5", FourOfAKind),
      ).forAll { hand1, hand2, handType ->
        val strongerHand = hand1.toHand()
        val weakerHand = hand2.toHand()
        test("Hand $hand1 should be greater than $hand2 and they should have the same type") {
          strongerHand shouldBeGreaterThan weakerHand
          weakerHand shouldBeLessThan strongerHand
          strongerHand.handType shouldBe weakerHand.handType
          strongerHand.handType shouldBe handType
        }
      }
    }
  }

  test("Hand with bid can be parsed") {
    "32T3K 765".toHandBid() shouldBe HandBid(Hand(listOf(Three, Two, Ten, Three, King)), 765)
  }

  val exampleInput = """
32T3K 765
T55J5 684
KK677 28
KTJJT 220
QQQJA 483
  """.trimIndent()

  test("The example input can be parsed into a list of HandBids") {
    exampleInput.toHandBidList() shouldBe listOf(
      HandBid(Hand(listOf(Three, Two, Ten, Three, King)), 765),
      HandBid(Hand(listOf(Ten, Five, Five, Joker, Five)), 684),
      HandBid(Hand(listOf(King, King, Six, Seven, Seven)), 28),
      HandBid(Hand(listOf(King, Ten, Joker, Joker, Ten)), 220),
      HandBid(Hand(listOf(Queen, Queen, Queen, Joker, Ace)), 483)
    )
  }

  test("Given the example input then we can calculate the total winning as 5905") {
    exampleInput.toHandBidList().totalWinning() shouldBe 5905
  }

  test("Given our custom input then we can calculate the total winning as 246424613") {
    readInput("day07p1").toHandBidList().totalWinning() shouldBe 248256639
  }
})
