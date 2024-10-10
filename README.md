# ecse429-project-partA

### Excel session notes are in the subdirectories of ExploratorySessionNotes

## Tool Versions
- **Gradle:** 8.8
- **Groovy:** 3.0.21
- **JVM:** 21 (Oracle Corporation 21+35-LTS-2513)

## Running the Tests

To run all the tests, execute the following commands sequentially inside the `UnitTestSuite` project folder:

```bash
gradlew test --tests 'TodoTest' --tests 'ProjectTest' --tests 'CategoryTest'
gradlew test --tests 'ShutdownTest'

