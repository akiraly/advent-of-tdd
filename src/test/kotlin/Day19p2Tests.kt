package day19p2

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.readInput

class Day19p2Tests : FunSpec({
  val exampleInput = """
px{a<2006:qkq,m>2090:A,rfg}
pv{a>1716:R,A}
lnx{m>1548:A,A}
rfg{s<537:gd,x>2440:R,A}
qs{s>3448:A,lnx}
qkq{x<1416:A,crn}
crn{x>2662:A,R}
in{s<1351:px,qqz}
qqz{s>2770:qs,m<1801:hdj,R}
gd{a>3333:R,R}
hdj{m>838:A,pv}

{x=787,m=2655,a=1222,s=2876}
{x=1679,m=44,a=2067,s=496}
{x=2036,m=264,a=79,s=2244}
{x=2461,m=1339,a=466,s=291}
{x=2127,m=1623,a=2188,s=1013}
  """.trimIndent()

  val customInput = readInput("day19p1")

  context("parsing") {
    table(
      headers("part rating", "expected", "total"),
      row("{x=787,m=2655,a=1222,s=2876}", Part(787, 2655, 1222, 2876), 7540),
      row("{x=1679,m=44,a=2067,s=496}", Part(1679, 44, 2067, 496), 4286),
      row("{x=2036,m=264,a=79,s=2244}", Part(2036, 264, 79, 2244), 4623),
      row("{x=2461,m=1339,a=466,s=291}", Part(2461, 1339, 466, 291), 4557),
      row("{x=2127,m=1623,a=2188,s=1013}", Part(2127, 1623, 2188, 1013), 6951),
    ).forAll { partRating, expected, total ->

      test(""" Given a part rating of "$partRating" then it can be correctly parsed as $expected with a total of $total""") {
        val part = partRating.toPart()
        part shouldBe expected
        part.total shouldBe total
      }
    }

    test(""" Given the example list of part ratings when parsed then the total of all ratings is 27957 """) {
      val parts = """
{x=787,m=2655,a=1222,s=2876}
{x=1679,m=44,a=2067,s=496}
{x=2036,m=264,a=79,s=2244}
{x=2461,m=1339,a=466,s=291}
{x=2127,m=1623,a=2188,s=1013}
      """.trimIndent().toParts()

      parts shouldBe listOf(
        Part(787, 2655, 1222, 2876),
        Part(1679, 44, 2067, 496),
        Part(2036, 264, 79, 2244),
        Part(2461, 1339, 466, 291),
        Part(2127, 1623, 2188, 1013)
      )

      parts.sumOfRatings() shouldBe 27957
    }

    table(
      headers("rule input", "expected"),
      row("x>10:one", Rule.xGt(10, "one")),
      row("m<20:two", Rule.mLt(20, "two")),
      row("a>30:R", Rule.aGt(30, "R")),
      row("A", EndState.A),
      row("R", EndState.R),
    ).forAll { ruleInput, expected ->
      test(""" Given a rule input of "$ruleInput" then it can be correctly parsed as $expected """) {
        val rule = ruleInput.toRule()
        rule shouldBe expected
      }
    }

    table(
      headers("workflow input", "expected"),
      row(
        "px{a<2006:qkq,m>2090:A,rfg}", Workflow(
          WorkflowName("px"), listOf(
            Rule.aLt(2006, "qkq"),
            Rule.mGt(2090, "A"),
            WorkflowName("rfg")
          )
        )
      ),
      row(
        "pv{a>1716:R,A}", Workflow(
          WorkflowName("pv"), listOf(
            Rule.aGt(1716, "R"),
            EndState.A
          )
        )
      )
    ).forAll { input, expected ->
      test(""" Given a workflow input of "$input" then it can be correctly parsed as $expected """) {
        val workflow = input.toWorkflow()
        workflow shouldBe expected
      }
    }

    test(""" Given the example input of workflows and parts when parsed then the number of workflows is 2 and the number of parts is 5 """) {
      val (workflows, parts) = exampleInput.toWorkflowsAndParts()
      workflows.workflows.size shouldBe 11
      parts.size shouldBe 5
    }
  }

  context("part processing") {
    table(
      headers("part input", "expected outcome"),
      row("{x=787,m=2655,a=1222,s=2876}", EndState.A),
      row("{x=1679,m=44,a=2067,s=496}", EndState.R),
      row("{x=2036,m=264,a=79,s=2244}", EndState.A),
      row("{x=2461,m=1339,a=466,s=291}", EndState.R),
      row("{x=2127,m=1623,a=2188,s=1013}", EndState.A),
    ).forAll { partInput, expected ->
      val (workflows, _) = exampleInput.toWorkflowsAndParts()
      test(""" Given an example part input of "$partInput" when sorted with the example workflows then the eventual outcome is $expected """) {
        val part = partInput.toPart()
        workflows.sortPart(part) shouldBe expected
      }
    }

    test(""" Given the example input of workflows and parts when sorting the parts with the workflows then the total is 19114 """) {
      exampleInput.sumOfRatingsOfSortedParts() shouldBe 19114
    }

    test(""" Given the custom input of workflows and parts when sorting the parts with the workflows then the total is 575412 """) {
      customInput.sumOfRatingsOfSortedParts() shouldBe 575412
    }

    test(""" Given the example input of workflows and parts when sorting the parts with the simplified workflows then the total is 19114 """) {
      exampleInput.simplifiedSumOfRatingsOfSortedParts() shouldBe 19114
    }

    test(""" Given the custom input of workflows and parts when sorting the parts with the simplified workflows then the total is 575412 """) {
      customInput.simplifiedSumOfRatingsOfSortedParts() shouldBe 575412
    }
  }

  context("Part 2, counting possibilities") {
    table(
      headers("workflow input", "expected workflow", "expected simplified workflow"),
      row(
        "zr{m>522:vtc,a>1052:A,s>2877:R,R}",
        Workflow(
          WorkflowName("zr"), listOf(
            Rule.mGt(522, "vtc"),
            Rule.aGt(1052, "A"),
            Rule.sGt(2877, "R"),
            EndState.R
          )
        ),
        Workflow(
          WorkflowName("zr"), listOf(
            Rule.mGt(522, "vtc"),
            Rule.aGt(1052, "A"),
            EndState.R
          )
        )
      )
    ).forAll { workflowInput, expectedWorkflow, expectedSimplifiedWorkflow ->
      test(""" Given a workflow input of "$workflowInput" then it can be correctly parsed as $expectedWorkflow and simplified to $expectedSimplifiedWorkflow """) {
        val workflow = workflowInput.toWorkflow()
        workflow shouldBe expectedWorkflow
        workflow.simplify() shouldBe expectedSimplifiedWorkflow
      }
    }

    test(""" Given the example input of workflows then the workflows can be DAG ordered""") {
      val (workflows, _) = exampleInput.toWorkflowsAndParts()
      val orderedRules = workflows.putWorkflowsInDAGOrder()
      orderedRules.joinToString("\n") shouldBe """
pv{1717<=A<=4000:R,A}
lnx{1549<=M<=4000:A,A}
gd{3334<=A<=4000:R,R}
crn{2663<=X<=4000:A,R}
hdj{839<=M<=4000:A,pv}
qs{3449<=S<=4000:A,lnx}
rfg{1<=S<=536:gd,2441<=X<=4000:R,A}
qkq{1<=X<=1415:A,crn}
qqz{2771<=S<=4000:qs,1<=M<=1800:hdj,R}
px{1<=A<=2005:qkq,2091<=M<=4000:A,rfg}
in{1<=S<=1350:px,qqz}
      """.trimIndent()
    }

    test(""" Given the example input of workflows then the whole workflow can be simplified as expected""") {
      val (workflows, _) = exampleInput.toWorkflowsAndParts()
      val simplified = workflows.simplify()
      simplified.toString() shouldBe """
pv{1717<=A<=4000:R,A}
crn{2663<=X<=4000:A,R}
hdj{839<=M<=4000:A,pv}
rfg{1<=S<=536:R,2441<=X<=4000:R,A}
qkq{1<=X<=1415:A,crn}
qqz{2771<=S<=4000:A,1<=M<=1800:hdj,R}
px{1<=A<=2005:qkq,2091<=M<=4000:A,rfg}
in{1<=S<=1350:px,qqz}
      """.trimIndent()
    }

    test(""" Given the custom input of workflows then the workflows can be DAG ordered""") {
      val (workflows, _) = customInput.toWorkflowsAndParts()
      val orderedRules = workflows.putWorkflowsInDAGOrder()
      orderedRules shouldContainExactlyInAnyOrder workflows.workflows.values
    }

    test(""" Given the custom input of workflows then the whole workflow can be simplified as expected""") {
      val (workflows, _) = customInput.toWorkflowsAndParts()
      val simplifiedWorkflows = workflows.simplify()

      simplifiedWorkflows.workflows shouldHaveSize 435
    }

    test(""" Given the example input of workflows then the number of accepted part combinations is 167409079868000 """) {
      exampleInput.numberOfAcceptedPartCombinations() shouldBe 167409079868000
    }

    test(""" Given the custom input of workflows then the number of accepted part combinations is 126107942006821 """) {
      customInput.numberOfAcceptedPartCombinations() shouldBe 126107942006821
    }
  }
})
