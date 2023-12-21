package day19p2

fun String.numberOfAcceptedPartCombinations(): Long {
  val (workflows, _) = toWorkflowsAndParts()
  return workflows.numberOfAcceptedPartCombinations()
}

fun String.sumOfRatingsOfSortedParts(): Long {
  val (workflows, parts) = toWorkflowsAndParts()
  return workflows.sumOfRatingsOfSortedParts(parts)
}

fun String.simplifiedSumOfRatingsOfSortedParts(): Long {
  val (workflows, parts) = toWorkflowsAndParts()
  return workflows.simplify().sumOfRatingsOfSortedParts(parts)
}

fun String.toWorkflowsAndParts(): Pair<Workflows, List<Part>> {
  val (workflows, parts) = split("\n\n")
  return workflows.toWorkflows() to parts.toParts()
}

fun String.toWorkflows(): Workflows = Workflows(lineSequence().map { it.toWorkflow() }.associateBy { it.name })

data class Workflows(val workflows: Map<WorkflowName, Workflow>) {
  private val start = workflows.getValue(WorkflowName("in"))
  fun sortPart(part: Part): EndState {
    var current = start
    while (true) {
      val outcome = current.sortPart(part)
      if (outcome is EndState) return outcome
      require(outcome is WorkflowName)
      current = workflows.getValue(outcome)
    }
  }

  fun sumOfRatingsOfSortedParts(
    parts: List<Part>
  ) = parts.asSequence()
    .map { it to sortPart(it) }
    .filter { it.second == EndState.A }
    .sumOf { it.first.total }

  fun putWorkflowsInDAGOrder(): List<Workflow> {
    val ordered = mutableListOf<Workflow>()
    val toVisit = LinkedHashSet(listOf(start))
    val visited = mutableSetOf<Workflow>()
    while (toVisit.isNotEmpty()) {
      val current = toVisit.removeFirst()!!
      require(current !in visited)
      visited += current
      ordered.addFirst(current)
      current.rules.forEach {
        val outcome = it.outcome
        if (outcome is WorkflowName) {
          toVisit += workflows.getValue(outcome)
        }
      }
    }
    return ordered
  }

  fun simplify(): Workflows {
    val ordered = putWorkflowsInDAGOrder()
    val simplifiedWorkflows = LinkedHashMap<WorkflowName, Workflow>()
    val inlineables = mutableMapOf<WorkflowName, RuleOutcome>()
    ordered.forEach { w ->
      val simplified = w.simplify(inlineables)
      val replacement = simplified.inlinedReplacement()
      if (replacement != null) {
        inlineables[w.name] = replacement
      } else {
        simplifiedWorkflows[w.name] = simplified
      }
    }
    return Workflows(simplifiedWorkflows)
  }

  override fun toString(): String = workflows.values.joinToString("\n")

  fun numberOfAcceptedPartCombinations(): Long {
    var count = 0L
    visitAllRulePathsToA { path ->
      val tracker = RateCategory.entries.associateWith { Array(MAX_RATING_VALUE) { true } }
      path.forEach { (rule, matched) ->
        if (rule !is Rule.IntervalRule) return@forEach

        val t = tracker.getValue(rule.category)

        if (matched) {
          ((1..<rule.range.first).asSequence() + (rule.range.last + 1..MAX_RATING_VALUE).asSequence())
            .forEach {
              t[it - 1] = false
            }
        } else {
          rule.range.forEach { t[it - 1] = false }
        }
      }

      val countedTracker = tracker.mapValues { c -> c.value.count { it }.toLong() }
      val combinations = countedTracker.values.reduce { acc, i -> acc * i }

      count += combinations
    }

    return count
  }

  fun visitAllRulePathsToA(
    path: Sequence<VisitedRule> = emptySequence(),
    current: Workflow = start,
    successFn: (Sequence<VisitedRule>) -> Unit
  ) {
    var updatedPath = path
    current.rules.forEach { rule ->
      val outcome = rule.outcome
      if (rule == EndState.A) {
        successFn(updatedPath)
      } else if (outcome == EndState.A) {
        successFn(updatedPath + VisitedRule(rule, true))
      } else if (outcome is WorkflowName) {
        visitAllRulePathsToA(updatedPath + VisitedRule(rule, true), workflows.getValue(outcome), successFn)
      }
      updatedPath += VisitedRule(rule, false)
    }
  }

  data class VisitedRule(val rule: Rule, val matched: Boolean) {
    override fun toString(): String = (if (matched) "" else "!") + rule
  }
}

private val workflowRegex = """(\w+)\{(.+)}""".toRegex()
fun String.toWorkflow(): Workflow = workflowRegex.matchEntire(this)!!.destructured.let { (name, rules) ->
  Workflow(
    WorkflowName(name),
    rules.split(',').map { it.toRule() }
  )
}

private val ruleRegex = """([xmas])([><])(\d+):(\w+)""".toRegex()
fun String.toRule(): Rule = ruleRegex.matchEntire(this)?.destructured?.let { (cat, op, limit, next) ->
  Rule.IntervalRule(
    cat.toRateCategory(),
    when (op) {
      ">" -> limit.toInt() + 1..MAX_RATING_VALUE
      else -> {
        require(op == "<")
        1..<limit.toInt()
      }
    },
    next.toRuleOutcome()
  )
} ?: this.toRuleOutcome()

private fun String.toRateCategory(): RateCategory = when (this) {
  "x" -> RateCategory.X
  "m" -> RateCategory.M
  "a" -> RateCategory.A
  else -> {
    require(this == "s")
    RateCategory.S
  }
}

data class Workflow(val name: WorkflowName, val rules: List<Rule>) {
  fun sortPart(part: Part): RuleOutcome = rules.asSequence().mapNotNull { it.sortPart(part) }.first()
  fun simplify(inlineables: Map<WorkflowName, RuleOutcome> = emptyMap()): Workflow {
    val simplifiedRules = rules.toMutableList()

    simplifiedRules.replaceAll { rule ->
      val outcome = rule.outcome
      if (outcome !is WorkflowName) return@replaceAll rule

      val replacement = inlineables[outcome] ?: return@replaceAll rule

      when (rule) {
        is Rule.IntervalRule -> rule.copy(outcome = replacement)
        else -> {
          require(rule is WorkflowName)
          replacement
        }
      }
    }

    simplifiedRules.asReversed().iterator().also {
      val last = it.next()
      require(last is RuleOutcome)
      while (it.hasNext()) {
        val next = it.next()
        if (next.outcome != last) {
          break
        }
        it.remove()
      }
    }
    return Workflow(name, simplifiedRules)
  }

  fun inlinedReplacement(): RuleOutcome? {
    if (rules.size != 1) return null
    val rule = rules.single()
    require(rule is RuleOutcome)
    return rule
  }

  override fun toString(): String = "$name{${rules.joinToString(",")}}"
}

sealed interface RuleOutcome : Rule {
  override val outcome: RuleOutcome get() = this
}

sealed interface EndState : RuleOutcome {
  override fun sortPart(part: Part): RuleOutcome = this

  data object A : EndState

  data object R : EndState
}

sealed interface Rule {
  val outcome: RuleOutcome
  fun sortPart(part: Part): RuleOutcome?

  data class IntervalRule(val category: RateCategory, val range: IntRange, override val outcome: RuleOutcome) : Rule {
    override fun sortPart(part: Part): RuleOutcome? = if (range.contains(category(part).value)) outcome else null

    override fun toString(): String = "${range.first}<=$category<=${range.last}:$outcome"
  }

  companion object {
    fun xGt(limit: Int, next: String): Rule =
      IntervalRule(RateCategory.X, limit + 1..MAX_RATING_VALUE, next.toRuleOutcome())

    fun mLt(limit: Int, next: String): Rule = IntervalRule(RateCategory.M, 1..<limit, next.toRuleOutcome())

    fun aGt(limit: Int, next: String): Rule =
      IntervalRule(RateCategory.A, limit + 1..MAX_RATING_VALUE, next.toRuleOutcome())

    fun aLt(limit: Int, next: String): Rule =
      IntervalRule(RateCategory.A, 1..<limit, next.toRuleOutcome())

    fun mGt(limit: Int, next: String): Rule =
      IntervalRule(RateCategory.M, limit + 1..MAX_RATING_VALUE, next.toRuleOutcome())

    fun sGt(limit: Int, next: String): Rule =
      IntervalRule(RateCategory.S, limit + 1..MAX_RATING_VALUE, next.toRuleOutcome())
  }
}

private fun String.toRuleOutcome(): RuleOutcome = when (this) {
  "A" -> EndState.A
  "R" -> EndState.R
  else -> WorkflowName(this)
}

data class WorkflowName(val name: String) : Rule, RuleOutcome {
  override fun sortPart(part: Part): RuleOutcome = this

  override fun toString(): String = name
}

enum class RateCategory {
  X {
    override fun invoke(part: Part) = part.x
  },
  M {
    override fun invoke(part: Part) = part.m
  },
  A {
    override fun invoke(part: Part) = part.a
  },
  S {
    override fun invoke(part: Part) = part.s
  };

  abstract operator fun invoke(part: Part): Rating
}

fun List<Part>.sumOfRatings(): Long = sumOf { it.total }

fun String.toParts(): List<Part> = lines().map { it.toPart() }

val partRegex = """\{x=(\d+),m=(\d+),a=(\d+),s=(\d+)}""".toRegex()

fun String.toPart(): Part {
  val (x, m, a, s) = partRegex.find(this)!!.destructured
  return Part(x.toInt(), m.toInt(), a.toInt(), s.toInt())
}

const val MAX_RATING_VALUE = 4000

data class Rating(val value: Int) {
  init {
    require(value in 1..MAX_RATING_VALUE)
  }

  operator fun plus(other: Rating): Long = this.value.toLong() + other.value
}

data class Part(val x: Rating, val m: Rating, val a: Rating, val s: Rating) {
  constructor(x: Int, m: Int, a: Int, s: Int) : this(Rating(x), Rating(m), Rating(a), Rating(s))

  val total = x + m + a + s
}

private operator fun Long.plus(rating: Rating): Long = this + rating.value
