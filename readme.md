## Morgan Stanley Advent of TDD (Advent of Code 2023 solutions in TDD style)

Reports available: [test report](https://akiraly.github.io/advent-of-tdd/test_report/index.html)
and [test coverage report](https://akiraly.github.io/advent-of-tdd/test_coverage_report/index.html).

* Kotlin 1.9.21
* [Kotest](https://kotest.io/) for testing
* JVM 21
* Gradle 8.5
* Intellij 2023.3 with latest stable Kotlin and Kotest plugins

Kotest has a rich set of features including multiple
different [testing styles](https://kotest.io/docs/framework/testing-styles.html).  
Different days sometimes use different styles because I was experimenting with them.  
After a while I settled for [the default style](https://kotest.io/docs/framework/testing-styles.html#fun-spec).  
Also using [Table-driven testing](https://kotest.io/docs/5.4/assertions/table_driven_testing/) when running the same
test with multiple data sets.
