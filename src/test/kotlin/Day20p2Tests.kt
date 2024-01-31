package day20p2

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.readInput

class Day20p2Tests : FunSpec({
  val exampleInput1 = """
broadcaster -> a, b, c
%a -> b
%b -> c
%c -> inv
&inv -> a
  """.trimIndent()

  val exampleInput2 = """
broadcaster -> a
%a -> inv, con
&inv -> b
%b -> con
&con -> output
  """.trimIndent()

  val customInput = readInput("day20p1")

  context("parsing modules") {
    test(""" Given the first example modules input then the modules can be parsed as expected""") {
      exampleInput1.toModules() shouldBe Modules(
        listOf(
          ButtonModule,
          Broadcaster(moduleNames("a", "b", "c")),
          FlipFlop("a".md, setOf(broadcasterModuleName, "inv".md), moduleNames("b")),
          FlipFlop("b".md, setOf(broadcasterModuleName, "a".md), moduleNames("c")),
          FlipFlop("c".md, setOf(broadcasterModuleName, "b".md), moduleNames("inv")),
          Conjunction("inv".md, moduleNames("c").toSet(), moduleNames("a")),
        ).associate { it.toNamedPair() }
      )
    }

    test(""" Given the second example modules input then the modules can be parsed as expected""") {
      exampleInput2.toModules() shouldBe Modules(
        listOf(
          ButtonModule,
          Broadcaster(moduleNames("a")),
          FlipFlop("a".md, setOf(broadcasterModuleName), moduleNames("inv", "con")),
          Conjunction("inv".md, setOf("a".md), moduleNames("b")),
          Conjunction("con".md, setOf("a".md, "b".md), listOf(outputModuleName)),
          FlipFlop("b".md, setOf("inv".md), moduleNames("con")),
          UntypedModule(outputModuleName, moduleNames("con").toSet()),
        ).associate { it.toNamedPair() }
      )
    }
  }

  context("pressing the button") {
    test(""" Given the first example modules input when the button is pressed then the pulses are sent in the correct order""") {
      val modules = exampleInput1.toModules()
      val (eventLog, updatedModules) = modules.pressButton(2)

      eventLog.joinToString("\n") shouldBe """
        button -low-> broadcaster
        broadcaster -low-> a
        broadcaster -low-> b
        broadcaster -low-> c
        a -high-> b
        b -high-> c
        c -high-> inv
        inv -low-> a
        a -low-> b
        b -low-> c
        c -low-> inv
        inv -high-> a
      """.trimIndent()

      modules shouldBe updatedModules
    }

    test(""" Given the second example modules input when the button is pressed multiple times then the pulses are sent in the correct order""") {
      val modules = exampleInput2.toModules()
      val (eventLog1, updatedModules1) = modules.pressButton()

      eventLog1.joinToString("\n") shouldBe """
button -low-> broadcaster
broadcaster -low-> a
a -high-> inv
a -high-> con
inv -low-> b
con -high-> output
b -high-> con
con -low-> output
      """.trimIndent()

      modules shouldNotBe updatedModules1

      val (eventLog2, updatedModules2) = updatedModules1.pressButton()

      eventLog2.joinToString("\n") shouldBe """
button -low-> broadcaster
broadcaster -low-> a
a -low-> inv
a -low-> con
inv -high-> b
con -high-> output
      """.trimIndent()

      modules shouldNotBe updatedModules2
      updatedModules1 shouldNotBe updatedModules2

      val (eventLog3, updatedModules3) = updatedModules2.pressButton()

      eventLog3.joinToString("\n") shouldBe """
button -low-> broadcaster
broadcaster -low-> a
a -high-> inv
a -high-> con
inv -low-> b
con -low-> output
b -low-> con
con -high-> output
      """.trimIndent()

      modules shouldNotBe updatedModules3
      updatedModules1 shouldNotBe updatedModules3
      updatedModules2 shouldNotBe updatedModules3

      val (eventLog4, updatedModules4) = updatedModules3.pressButton()

      eventLog4.joinToString("\n") shouldBe """
button -low-> broadcaster
broadcaster -low-> a
a -low-> inv
a -low-> con
inv -high-> b
con -high-> output
      """.trimIndent()

      modules shouldBe updatedModules4
    }

    test(""" Given the first example modules input when the button is pressed 1000 times then the product of the number of pulses is 32000000""") {
      exampleInput1.pressButtonAndCountNumberOfPulses() shouldBe 32000000
    }
    test(""" Given the second example modules input when the button is pressed 1000 times then the product of the number of pulses is 11687500""") {
      exampleInput2.pressButtonAndCountNumberOfPulses() shouldBe 11687500
    }
    test(""" Given the custom example modules input when the button is pressed 1000 times then the product of the number of pulses is 821985143""") {
      customInput.pressButtonAndCountNumberOfPulses() shouldBe 821985143
    }
    test(""" Given the custom example modules input the rx output module first gets a low pulse with 240853834793347 number of button presses """) {
      customInput.numberOfButtonPressesForFirstLowPulseToRx() shouldBe 240853834793347
    }
  }
})
