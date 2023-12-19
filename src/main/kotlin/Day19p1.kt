package day19p1

fun String.sumOfRatingsOfSortedParts(): Long {
  val (workflows, parts) = toWorkflowsAndParts()
  return parts.asSequence()
    .map { it to workflows.sortPart(it) }
    .filter { it.second == Workflow.A }
    .sumOf { it.first.total }
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
}

private val workflowRegex = """(\w+)\{(.+)}""".toRegex()
fun String.toWorkflow(): RuleBasedWorkflow = workflowRegex.matchEntire(this)!!.destructured.let { (name, rules) ->
  RuleBasedWorkflow(
    WorkflowName(name),
    rules.split(',').map { it.toRule() }
  )
}

private val ruleRegex = """([xmas])([><])(\d+):(\w+)""".toRegex()
fun String.toRule(): Rule = when (this) {
  "A" -> Workflow.A
  "R" -> Workflow.R
  else -> {
    ruleRegex.matchEntire(this)?.destructured?.let { (cat, op, limit, next) ->
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
  }
}

private fun String.toRateCategory(): RateCategory = when (this) {
  "x" -> RateCategory.X
  "m" -> RateCategory.M
  "a" -> RateCategory.A
  else -> {
    require(this == "s")
    RateCategory.S
  }
}

sealed interface Workflow {
  fun sortPart(part: Part): RuleOutcome

  data object A : Workflow, Rule, RuleOutcome, EndState {
    override fun sortPart(part: Part): RuleOutcome = this
  }

  data object R : Workflow, Rule, RuleOutcome, EndState {
    override fun sortPart(part: Part): RuleOutcome = this
  }
}

data class RuleBasedWorkflow(val name: WorkflowName, val rules: List<Rule>) : Workflow {
  override fun sortPart(part: Part): RuleOutcome = rules.asSequence().mapNotNull { it.sortPart(part) }.first()
}

sealed interface RuleOutcome : Rule

sealed interface EndState : RuleOutcome

sealed interface Rule {
  fun sortPart(part: Part): RuleOutcome?

  data class IntervalRule(val category: RateCategory, val range: IntRange, val next: RuleOutcome) : Rule {
    override fun sortPart(part: Part): RuleOutcome? = if (range.contains(category(part).value)) next else null
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
  }
}

private fun String.toRuleOutcome(): RuleOutcome = when (this) {
  "A" -> Workflow.A
  "R" -> Workflow.R
  else -> WorkflowName(this)
}

data class WorkflowName(val name: String) : Rule, RuleOutcome {
  override fun sortPart(part: Part): RuleOutcome = this
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

const val MAX_RATING_VALUE = 9999

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
