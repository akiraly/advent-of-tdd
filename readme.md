## Morgan Stanley Advent of TDD (Advent of Code 2023 solutions in TDD style)

This solution takes Test-Driven Design to heart:

* tests are written first - even if they don't compile initially (because classes, methods, etc. do not exist yet)
* non-test code is only written because either it fixes a failing test or as part of a refactor step
* there are no `main` methods/entry points: even the solutions (for the AoC private/custom inputs) are generated as test
  cases (however the private inputs are not stored in this public repo because
  of [this](https://old.reddit.com/r/adventofcode/wiki/faqs/copyright/inputs))

Some quick test stats (after day 25):  
Number of tests: 1245  
Code coverage: 98%<  
Time to run tests: ~3.5 minutes  
(as mentioned above these include even test cases that generate the actual solutions for the AoC inputs)

The full reports are also available: [test report](https://akiraly.github.io/advent-of-tdd/test_report/index.html)
and [test coverage report](https://akiraly.github.io/advent-of-tdd/test_coverage_report/index.html).

About the tech stack:

* [Kotlin](https://kotlinlang.org) 1.9.21
* [Kotest](https://kotest.io) for testing
* JVM 21
* [Gradle](https://gradle.org) 8.5
* Intellij 2023.3.2 with latest stable Kotlin and Kotest plugins

Kotest has a rich set of features including multiple
different [testing styles](https://kotest.io/docs/framework/testing-styles.html).  
Different days sometimes use different styles because I was experimenting with them.  
After a while I settled for [the default style](https://kotest.io/docs/framework/testing-styles.html#fun-spec).  
Also using [Table-driven testing](https://kotest.io/docs/5.4/assertions/table_driven_testing/) when running the same
test with multiple data sets.
