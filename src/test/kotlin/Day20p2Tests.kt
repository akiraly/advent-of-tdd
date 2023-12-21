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
  }
})

fun String.pressButtonAndCountNumberOfPulses(): Long {
  val modules = toModules()
  return modules.pressButtonAndCountNumberOfPulses(1000)
}

private fun String.toModules(): Modules {
  val builders = mutableMapOf(buttonModuleName to ModuleBuilder(buttonModuleName))
  lineSequence().forEach { line ->
    val (typedName, outputs) = line.split(" -> ").let { (name, outputs) -> name to outputs.split(", ").map { it.md } }
    val (name, builder) = if (typedName.startsWith("%")) {
      val n = typedName.substring(1).md
      n to builders.moduleBuilder(n).asFlipFlop()
    } else if (typedName.startsWith("&")) {
      val n = typedName.substring(1).md
      n to builders.moduleBuilder(n).asConjunction()
    } else {
      val n = typedName.md
      n to builders.moduleBuilder(n)
    }
    builder.addOutputs(outputs)
    outputs.forEach { output -> builders.moduleBuilder(output).addInput(name) }
  }
  return Modules(builders.mapValues { it.value.build() })
}

private fun MutableMap<ModuleName, ModuleBuilder>.moduleBuilder(name: ModuleName): ModuleBuilder =
  computeIfAbsent(name) { ModuleBuilder(name) }

data class ModuleBuilder(val name: ModuleName) {
  private val inputs = mutableSetOf<ModuleName>()
  private val outputs = mutableListOf<ModuleName>()
  private var builderFn: (ModuleBuilder.() -> Module)? = when (name) {
    buttonModuleName -> {
      {
        require(inputs.isEmpty())
        require(outputs.isEmpty())
        ButtonModule
      }
    }

    broadcasterModuleName -> {
      {
        require(inputs.isEmpty())
        require(outputs.isNotEmpty())
        Broadcaster(outputs)
      }
    }

    else -> {
      {
        require(inputs.isNotEmpty())
        require(outputs.isEmpty())
        UntypedModule(name, inputs)
      }
    }
  }

  fun asFlipFlop(): ModuleBuilder {
    builderFn = {
      require(inputs.isNotEmpty())
      require(outputs.isNotEmpty())
      FlipFlop(name, inputs, outputs)
    }
    return this
  }

  fun asConjunction(): ModuleBuilder {
    builderFn = {
      require(inputs.isNotEmpty())
      require(outputs.isNotEmpty())
      Conjunction(name, inputs, outputs)
    }
    return this
  }

  fun addInput(name: ModuleName): ModuleBuilder {
    inputs.add(name)
    return this
  }

  fun addOutputs(outputs: List<ModuleName>): ModuleBuilder {
    this.outputs.addAll(outputs)
    return this
  }

  fun build(): Module = (builderFn ?: error("$this"))()
}

data class Modules(val modules: Map<ModuleName, Module>) {
  val buttonModule = modules.getValue(buttonModuleName) as ButtonModule

  fun pressButton(times: Int = 1): Pair<List<PulseMessage>, Modules> {
    var current = this
    var eventLog = emptyList<PulseMessage>()
    repeat(times) {
      val updatedModules = current.deepCopy()
      eventLog = updatedModules.doPressButton()
      current = updatedModules
    }
    return eventLog to current
  }

  fun pressButtonAndCountNumberOfPulses(times: Int): Long {
    var current = this
    var countHigh = 0L
    var countLow = 0L
    repeat(times) {
      val (eventLog, updatedModules) = current.pressButton()
      current = updatedModules
      eventLog.forEach {
        if (it.value == Pulse.High) countHigh++ else countLow++
      }
    }
    return countHigh * countLow
  }

  private fun doPressButton(): List<PulseMessage> {
    val messages = mutableListOf<PulseMessage>()
    val eventLog = mutableListOf<PulseMessage>()

    buttonModule.press(messages)
    while (messages.isNotEmpty()) {
      val message = messages.removeFirst()
      eventLog += message
      val toModule = modules.getValue(message.to)
      require(toModule is ModuleWithInput)
      toModule.send(message, messages)
    }
    return eventLog
  }

  private fun deepCopy(): Modules = Modules(modules.mapValues { it.value.deepCopy() })
}

data class PulseMessage(val from: ModuleName, val to: ModuleName, val value: Pulse) {
  override fun toString(): String = "$from -${value.toString().lowercase()}-> $to"
}

data class ModuleName(val name: String) {
  override fun toString(): String = name
}

sealed interface Module {
  val name: ModuleName

  fun toNamedPair(): Pair<ModuleName, Module> = name to this
  fun deepCopy(): Module = this
}

sealed interface ModuleWithInput : Module {
  fun send(message: PulseMessage, messageBus: MutableList<PulseMessage>)
}

sealed interface SingleInputModule : ModuleWithInput {
  val input: ModuleName
}

sealed interface MultipleInputModule : ModuleWithInput {
  val inputs: Set<ModuleName>
}


sealed interface SingleOutputModule : Module {
  val output: ModuleName
}

sealed interface MultipleOutputModule : Module {
  val outputs: List<ModuleName>
}

val String.md: ModuleName
  get() = ModuleName(this)

fun moduleNames(vararg names: String) = names.map { it.md }

val outputModuleName = ModuleName("output")
val buttonModuleName = ModuleName("button")
val broadcasterModuleName = ModuleName("broadcaster")

data class UntypedModule(override val name: ModuleName, override val inputs: Set<ModuleName>) : MultipleInputModule {
  override fun send(message: PulseMessage, messageBus: MutableList<PulseMessage>) {
    // ignored
  }
}

data object ButtonModule : SingleOutputModule {
  fun press(messages: MutableList<PulseMessage>) {
    messages += PulseMessage(buttonModuleName, broadcasterModuleName, Pulse.Low)
  }

  override val name = buttonModuleName
  override val output = broadcasterModuleName
}

data class Broadcaster(override val outputs: List<ModuleName>) : SingleInputModule, MultipleOutputModule {
  override val name = broadcasterModuleName
  override val input = buttonModuleName

  override fun send(message: PulseMessage, messageBus: MutableList<PulseMessage>) {
    outputs.forEach { output -> messageBus += PulseMessage(broadcasterModuleName, output, message.value) }
  }
}

data class FlipFlop(
  override val name: ModuleName,
  override val inputs: Set<ModuleName>,
  override val outputs: List<ModuleName>,
  var on: Boolean = false
) : MultipleInputModule, MultipleOutputModule {
  override fun deepCopy(): FlipFlop = copy()

  override fun send(message: PulseMessage, messageBus: MutableList<PulseMessage>) {
    if (message.value == Pulse.High) return

    if (!on) {
      on = true
      outputs.forEach { output -> messageBus += PulseMessage(name, output, Pulse.High) }
    } else {
      on = false
      outputs.forEach { output -> messageBus += PulseMessage(name, output, Pulse.Low) }
    }
  }
}

data class Conjunction(
  override val name: ModuleName,
  override val inputs: Set<ModuleName>,
  override val outputs: List<ModuleName>,
  val lastPulses: MutableMap<ModuleName, Pulse> = inputs.associateWith { Pulse.Low }.toMutableMap()
) : MultipleInputModule, MultipleOutputModule {
  override fun deepCopy(): Conjunction = copy(lastPulses = lastPulses.toMutableMap())

  override fun send(message: PulseMessage, messageBus: MutableList<PulseMessage>) {
    lastPulses[message.from] = message.value
    val pulseToSend = if (lastPulses.values.all { it == Pulse.High }) Pulse.Low else Pulse.High
    outputs.forEach { output -> messageBus += PulseMessage(name, output, pulseToSend) }
  }
}

enum class Pulse {
  Low, High
}
